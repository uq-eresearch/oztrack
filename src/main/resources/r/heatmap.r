# Author: Ross Dwyer
# Email: ross.dwyer@uq.edu.au
# Project: OzTrack
# Date: 08th August 2012
# Purpose:
# Generates polygon of point density
# Generates polygon of line density
# Generates kml from polygon heatmap

library(adehabitatHR)
library(rgdal)
library(alphahull)
library(sp)
library(raster)
library(plyr)
library(spatstat)
library(maptools)
library(Grid2Polygons)
library(RColorBrewer)

# Generates polygon of point density
fpdens2kml <- function(sdata,igrid,ssrs,scol,labsent=FALSE)
{
  sdata.proj <- spTransform(sdata,CRS(ssrs))

  mybb <- bbox(sdata.proj)
  mybb1 <- c(round_any(mybb[,1],100, f = floor),round_any(mybb[,2], 100, f = ceiling))
  pF1.p <- data.frame(sdata.proj)
  myppp <- ppp(pF1.p$X,pF1.p$Y,
               window=owin(c(mybb1[1],mybb1[3]),c(mybb1[2],mybb1[4])))

  Z <- pixellate(myppp, eps=igrid)
  #image(Z)
  if(labsent==FALSE)
    Z[][Z[]==0] <- NA
  Z_ppp_SGDF <- as.SpatialGridDataFrame.im(Z)
  #image(Z_ppp_SGDF,col=rev(heat.colors(10)))

  Z_ppp_SPDF <- try({Grid2Polygons(Z_ppp_SGDF, "v", level = FALSE)}, silent=TRUE)
  if (class(Z_ppp_SPDF) == 'try-error') {
    stop('Error converting to polygon')
  }
  
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
  sdata.proj <- spTransform(sdata,CRS(ssrs))

  mybb <- bbox(sdata.proj)
  mybb1 <- c(round_any(mybb[,1],100, f = floor),round_any(mybb[,2], 100, f = ceiling))
  pF1.p <- data.frame(sdata.proj)
  mypsp <- psp(pF1.p$X[1:length(pF1.p$X)-1],pF1.p$Y[1:length(pF1.p$Y)-1],
               pF1.p$X[2:length(pF1.p$X)],pF1.p$Y[2:length(pF1.p$Y)],
               window=owin(c(mybb1[1],mybb1[3]),c(mybb1[2],mybb1[4])))

  Z <- pixellate(mypsp, eps=igrid)
  if(labsent==FALSE)
    Z[][Z[]==0] <- NA
  Z_psp_SGDF <- as.SpatialGridDataFrame.im(Z)
  Z_psp_SGDF$v2 <- round(Z_psp_SGDF$v) #R doesnt like non integer values here

  Z_psp_SPDF <- try({Grid2Polygons(Z_psp_SGDF, "v2", level = FALSE)}, silent=TRUE)
  if (class(Z_psp_SPDF) == 'try-error') {
    stop('Error converting to polygon')
  }
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
polykml <- function(sw, filename, kmlname, namefield, kmldesc="Made with OzTrack" )
{
  #sw=Z_ppp_SPDF
  #filename=paste(KMLdir,"pointpattern5.kml",sep="")
  #kmlname="myRpolygon"
  #namefield=6
  #kmldesc="Made with R"
  out <- sapply(slot(sw, "polygons"),
                function(x)
                {
                  #x=slot(sw, "polygons")[1]
                  kmlPolygon(x,
                             name=as(sw, "data.frame")[slot(x, "ID"), 1],
                             col=as(sw, "data.frame")[slot(x, "ID"), 2],
                             #lwd=1.5, border='white')#,
                             lwd=0, border='white')#,
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
  #    ext(kmz) <- '.kmz'
  #    com <- paste("7za a -tzip", kmz, " ", fn, sep="")
  #    sss <- system(com, intern=TRUE)
  return(TRUE)
}
