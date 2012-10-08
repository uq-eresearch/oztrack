# Author: Ross Dwyer
# Email: ross.dwyer@uq.edu.au
# Project: OzTrack
# Date: 08th August 2012
# Purpose: 
# Generates polygon of point density
# Generates polygon of line density
# Generates kml from polygon heatmap

# Generates polygon of point density
fpdens2kml <- function(sdata,igrid,ssrs,scol,labsent=FALSE)
{
#sdata=positionFix1
#igrid=100
#ssrs="+init=epsg:3577"
#scol="Blues"
#labsent=FALSE

  if(igrid < 10)
   stop('Grid size too small, try increasing grid number.')

  sdata.proj <- spTransform(sdata, CRS(ssrs))
  
  mybb <- bbox(sdata.proj)
  mybb1 <- c(round_any(mybb[,1],igrid, f = floor),round_any(mybb[,2], igrid, f = ceiling))

  pF1.p <- data.frame(sdata.proj)  
  myppp <- ppp(pF1.p$X,pF1.p$Y,
               window=owin(c(mybb1[1],mybb1[3]),c(mybb1[2],mybb1[4])))
  
  Z <- pixellate(myppp, eps=igrid) ##
#  image(Z)
#  points(pF1.p$X,pF1.p$Y,cex=0.1,pch=16)

  if(labsent==FALSE)
    Z[][Z[]==0] <- NA
  Z_ppp_SGDF <- as.SpatialGridDataFrame.im(Z)
# image(Z_ppp_SGDF,col=rev(heat.colors(10)))
  
  Z_ppp_SPDF <- Grid2PolygonsRD(Z_ppp_SGDF, "v", level = FALSE)
  z <- Z_ppp_SPDF$z
  proj4string(Z_ppp_SPDF) <- CRS(ssrs)
  Z_ppp_SPDF <- spTransform(Z_ppp_SPDF,CRS("+init=epsg:4326"))
  #Z_ppp_SPDF <- spTransform(Z_ppp_SPDF,CRS(" +proj=longlat +datum=WGS84 +ellps=WGS84 +towgs84=0,0,0"))
  
  col.idxs <- findInterval(z, sort(unique(na.omit(z))))
  cols <- colorRampPalette(brewer.pal(9,scol))(max(col.idxs))[col.idxs]
  Z_ppp_SPDF$cols <- cols
  Z_ppp_SPDF$ID <- Z_ppp_SPDF$name <- data.frame(sdata)[1,1]
  return(Z_ppp_SPDF) 
}

###
# Generates polygon of line density
fldens2kml <- function(sdata,igrid,ssrs,scol,labsent=FALSE)
{
  if(igrid < 10)
    stop('Grid size too small, try increasing grid number.')
  
  sdata.proj <- spTransform(sdata,CRS(ssrs)) 
  
  mybb <- bbox(sdata.proj)
  mybb1 <- c(round_any(mybb[,1],igrid, f = floor),round_any(mybb[,2], igrid, f = ceiling))
  pF1.p <- data.frame(sdata.proj)
  mypsp <- psp(pF1.p$X[1:length(pF1.p$X)-1],pF1.p$Y[1:length(pF1.p$Y)-1],
               pF1.p$X[2:length(pF1.p$X)],pF1.p$Y[2:length(pF1.p$Y)],
               window=owin(c(mybb1[1],mybb1[3]),c(mybb1[2],mybb1[4])))
  
  Z <- pixellate(mypsp, eps=igrid) 
  if(labsent==FALSE)
    Z[][Z[]==0] <- NA
  Z_psp_SGDF <- as.SpatialGridDataFrame.im(Z)
  Z_psp_SGDF$v2 <- round(Z_psp_SGDF$v) #R doesnt like non integer values here
  
  Z_psp_SPDF <- Grid2PolygonsRD(Z_psp_SGDF, "v2", level = FALSE)
  z <- Z_psp_SPDF$z
  proj4string(Z_psp_SPDF) <- CRS(ssrs)
  Z_psp_SPDF <- spTransform(Z_psp_SPDF,CRS("+init=epsg:4326"))
  #Z_ppp_SPDF <- spTransform(Z_ppp_SPDF,CRS(" +proj=longlat +datum=WGS84 +ellps=WGS84 +towgs84=0,0,0"))
  
  col.idxs <- findInterval(z, sort(unique(na.omit(z))))
  cols <- colorRampPalette(brewer.pal(9,scol))(max(col.idxs))[col.idxs]
  Z_psp_SPDF$cols <- cols
  Z_psp_SPDF$ID <- Z_psp_SPDF$name <- data.frame(sdata)[1,1]
  return(Z_psp_SPDF) 
}

###
# Generates kml from polygon heatmap
polykml <- function(sw, filename, kmlname, namefield, kmldesc="Created using OzTrack" ) 
{
 # sw=Z_ppp_SPDF
#  filename=paste(KMLdir,"pointpatternplay.kml",sep="")
#  kmlname="myRpolygon"
#  namefield=6
#  kmldesc="Created using OzTrack"  
  out <- sapply(slot(sw, "polygons"),
                function(x) 
                {
                  #x=slot(sw, "polygons")[1]
                  kmlPolygonRD(x,
                             name=as(sw, "data.frame")[slot(x, "ID"), 1],
                             col=as(sw, "data.frame")[slot(x, "ID"), 2], 
                             lwd=1, border='white')#, 
                  #description=as(sw,"data.frame")[slot(x, "ID"), desc] )
                }
  )
  kmlFile <- file(filename, "w")
  cat(kmlPolygon(kmlname=kmlname, kmldescription=kmldesc)$header,
      file=kmlFile, sep="\n")
  cat(unlist(out["style",]), file=kmlFile, sep="\n")
  cat(unlist(out["content",]), file=kmlFile, sep="\n")
  cat(kmlPolygon()$footer, file=kmlFile, sep="\n")
  close(kmlFile)
  # optional, to make a kmz, I do not know how to make this generic for all systems
  #  kmz <- basename(filename)
  # change file extension, from raster package  
  # ext(kmz) <- '.kmz'
  # com <- paste("7za a -tzip", kmz, " ", fn, sep="")
  # sss <- system(com, intern=TRUE)
  return(TRUE)
}

Grid2PolygonsRD <- function (grd, zcol = 1, level = FALSE, at, cuts = 20, pretty = FALSE) 
{

#grd <- Z_ppp_SGDF
#zcol = "v"
#level = FALSE
  
  FindPolyNodes <- function(s) {
    id <- paste(apply(s, 1, min), apply(s, 1, max), sep = "")
    duplicates <- unique(id[duplicated(id)])
    s <- s[!id %in% duplicates, ]
    m <- nrow(s)
    out <- matrix(.C("define_polygons", as.integer(s[, 1]), 
                     as.integer(s[, 2]), as.integer(m), ans = integer(m * 
                       2))$ans, nrow = m, ncol = 2)
    poly.nodes <- lapply(unique(out[, 2]), function(i) out[out[, 
                                                               2] == i, 1])
    poly.nodes <- lapply(poly.nodes, function(i) c(i, i[1]))
    poly.nodes
  }
  require(sp)
  if (!inherits(grd, "SpatialGridDataFrame")) 
    stop("Grid object not of class SpatialGridDataFrame")
  if (is.character(zcol) && !(zcol %in% names(grd))) 
    stop("Column name not in attribute table")
  if (is.numeric(zcol) && zcol > ncol(slot(grd, "data"))) 
    stop("Column number outside bounds of attribute table")
  if (level) {
    if (missing(at)) {
      zlim <- range(grd[[zcol]], finite = TRUE)
      if (pretty) 
        at <- pretty(zlim, cuts)
      else at <- seq(zlim[1], zlim[2], length.out = cuts)
    }
    zc <- at[1:(length(at) - 1)] + diff(at)/2
    z <- zc[findInterval(grd[[zcol]], at, rightmost.closed = TRUE)]
  }  else {
    z <- grd[[zcol]]
  }
  grd.par <- gridparameters(grd)
  n <- grd.par$cells.dim[1]
  m <- grd.par$cells.dim[2]
  dx <- grd.par$cellsize[1]
  dy <- grd.par$cellsize[2]
  xmin <- grd.par$cellcentre.offset[1] - dx/2
  ymin <- grd.par$cellcentre.offset[2] - dy/2
  xmax <- xmin + n * dx
  ymax <- ymin + m * dy
  x <- seq(xmin, xmax, by = dx)
  y <- seq(ymin, ymax, by = dy)
  nnodes <- (m + 1) * (n + 1)
  nelems <- m * n
  nodes <- 1:nnodes
  elems <- 1:nelems
  coords <- cbind(x = rep(x, m + 1), y = rep(rev(y), each = n + 
    1))
  n1 <- c(sapply(1:m, function(i) seq(1, n) + (i - 1) * (n + 
    1)))
  n2 <- n1 + 1
  n4 <- c(sapply(1:m, function(i) seq(1, n) + i * (n + 1)))
  n3 <- n4 + 1
  elem.nodes <- cbind(n1, n2, n3, n4)
  nsegs <- nelems * 4
  segs <- matrix(data = NA, nrow = nsegs, ncol = 4, dimnames = list(1:nsegs, 
                                                                    c("elem", "a", "b", "z")))
  segs[, 1] <- rep(1:nelems, each = 4)
  segs[, 2] <- c(t(elem.nodes))
  segs[, 3] <- c(t(elem.nodes[, c(2, 3, 4, 1)]))
  segs[, 4] <- rep(z, each = 4)
  segs <- na.omit(segs)
  levs <- unique(na.omit(z))
  fun <- function(i) FindPolyNodes(segs[segs[, "z"] == i, c("a", 
                                                            "b")])
  poly.nodes <- sapply(levs, fun)
  fun <- function(i) lapply(i, function(j) Polygon(coords[j,]))

  ##RD improvement in polygon creation for oversized grid
  polytry <- try(lapply(poly.nodes, fun),  silent=TRUE)
  if (class(polytry) == 'try-error'){
    poly <- fun(poly.nodes)
    polys <- vector(mode="list", length=length(poly.nodes))
    # make somewhere to keep the output
    for (i in seq(along=polys)) polys[[i]] <- Polygons(list(poly[[i]]),ID=format(levs[i]))
  }else{
    poly <- lapply(poly.nodes, fun)
    fun <- function(i) Polygons(poly[[i]], ID = format(levs[i]))
    polys <- lapply(1:length(poly), fun)
  }##

  sp.polys <- SpatialPolygons(polys, proj4string = CRS(proj4string(grd)))
  d <- data.frame(z = levs, row.names = row.names(sp.polys))
  sp.polys.df <- SpatialPolygonsDataFrame(sp.polys, data = d, 
                                          match.ID = TRUE)
  sp.polys.df
}

kmlPolygonRD <- function (obj = NULL, kmlfile = NULL, name = "R Polygon", description = "", 
                          col = NULL, visibility = 1, lwd = 1, border = 1, kmlname = "",
                          kmldescription = "") 
{
  if (is.null(obj)) 
    return(list(header = c("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", 
                           "<kml xmlns=\"http://earth.google.com/kml/2.2\">", 
                           "<Document>", paste("<name>", kmlname, "</name>", 
                                               sep = ""), paste("<description><![CDATA[", kmldescription, 
                                                                "]]></description>", sep = "")), footer = c("</Document>", 
                                                                                                            "</kml>")))
  if (class(obj) != "Polygons" && class(obj) != "SpatialPolygonsDataFrame") 
    stop("obj must be of class 'Polygons' or 'SpatialPolygonsDataFrame' [package 'sp']")
  if (class(obj) == "SpatialPolygonsDataFrame") {
    if (length(obj@polygons) > 1L) 
      warning(paste("Only the first Polygons object with the ID '", 
                    obj@polygons[[1]]@ID, "' is taken from 'obj'", 
                    sep = ""))
    obj <- obj@polygons[[1]]
  }
  col2kmlcolor <- function(col) paste(rev(sapply(col2rgb(col, 
                                                         TRUE), function(x) sprintf("%02x", x))), collapse = "")
  kml <- kmlStyle <- ""
  kmlHeader <- c("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", 
                 "<kml xmlns=\"http://earth.google.com/kml/2.2\">", "<Document>", 
                 paste("<name>", kmlname, "</name>", sep = ""), paste("<description><![CDATA[", 
                                                                      kmldescription, "]]></description>", sep = ""))
  kmlFooter <- c("</Document>", "</kml>")
  kmlStyle <- append(kmlStyle, paste("<Style id=\"", obj@ID, 
                                     "\">", sep = ""))
  kmlStyle <- append(kmlStyle, "<LineStyle>")
  kmlStyle <- append(kmlStyle, paste("<width>", lwd, "</width>", 
                                     sep = ""))
  kmlStyle <- append(kmlStyle, paste("<color>", col2kmlcolor(border), 
                                     "</color>", sep = ""))
  kmlStyle <- append(kmlStyle, "</LineStyle>")
  kmlStyle <- append(kmlStyle, "<PolyStyle>")
  if (is.null(col)) {
    kmlStyle <- append(kmlStyle, "<fill>0</fill>")
  }
  else {
    mycol <- col2kmlcolor(col)
    mycol <- sub("ff", "7f", mycol) # make 50% transparent
    kmlStyle <- append(kmlStyle, paste("<color>", mycol, 
                                       "</color>", sep = ""))
    kmlStyle <- append(kmlStyle, "<fill>1</fill>")
  }
  kmlStyle <- append(kmlStyle, "</PolyStyle>")
  kmlStyle <- append(kmlStyle, "</Style>")
  kml <- append(kml, "<Placemark>")
  kml <- append(kml, paste("<name>", name, "</name>", sep = ""))
  kml <- append(kml, paste("<description><![CDATA[", description, 
                           "]]></description>", sep = ""))
  kml <- append(kml, paste("<styleUrl>#", obj@ID, "</styleUrl>", 
                           sep = ""))
  kml <- append(kml, paste("<visibility>", as.integer(visibility), 
                           "</visibility>", sep = ""))
  kml <- append(kml, "<MultiGeometry>")
  holeFlag <- FALSE
  for (i in 1:length(obj@Polygons)) {
    if (!holeFlag) 
      kml <- append(kml, "<Polygon>")
    kml <- append(kml, ifelse(obj@Polygons[[i]]@hole, "<innerBoundaryIs>", 
                              "<outerBoundaryIs>"))
    kml <- append(kml, c("<LinearRing>", "<coordinates>"))
    kml <- append(kml, paste(coordinates(obj@Polygons[[i]])[, 
                                                            1], coordinates(obj@Polygons[[i]])[, 2], sep = ","))
    kml <- append(kml, c("</coordinates>", "</LinearRing>"))
    kml <- append(kml, ifelse(obj@Polygons[[i]]@hole, "</innerBoundaryIs>", 
                              "</outerBoundaryIs>"))
    holeFlag <- ifelse((i + 1L) <= length(obj@Polygons), 
                       obj@Polygons[[i + 1L]]@hole, FALSE)
    if (!holeFlag) 
      kml <- append(kml, "</Polygon>")
  }
  kml <- append(kml, "</MultiGeometry>")
  kml <- append(kml, "</Placemark>")
  if (!is.null(kmlfile)) 
    cat(paste(c(kmlHeader, kmlStyle, kml, kmlFooter), sep = "", 
              collapse = "\n"), "\n", file = kmlfile, sep = "")
  else list(style = kmlStyle, content = kml)
}
