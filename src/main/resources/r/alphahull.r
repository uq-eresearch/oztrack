ah2sp <- function(x, increment=360, rnd=10, proj4string=CRS(as.character(NA))){ 
  require(alphahull) 
  require(maptools) 
  if (class(x) != "ahull"){ 
    stop("x needs to be an ahull class object") 
  } 
  # Extract the edges from the ahull object as a dataframe 
  xdf <- as.data.frame(x$arcs) 
  # Remove all cases where the coordinates are all the same       
  xdf <- subset(xdf,xdf$r > 0) 
  res <- NULL 
  if (nrow(xdf) > 0){ 
    # Convert each arc to a line segment 
    linesj <- list() 
    prevx<-NULL 
    prevy<-NULL 
    j<-1 
    for(i in 1:nrow(xdf)){ 
      rowi <- xdf[i,] 
      v <- c(rowi$v.x, rowi$v.y) 
      theta <- rowi$theta 
      r <- rowi$r 
      cc <- c(rowi$c1, rowi$c2) 
      # Arcs need to be redefined as strings of points. Work out the number of points to allocate in this arc segment. 
      ipoints <- 2 + round(increment * (rowi$theta / 2),0) 
      # Calculate coordinates from arc() description for ipoints along the arc. 
      angles <- anglesArc(v, theta) 
      seqang <- seq(angles[1], angles[2], length = ipoints) 
      x <- round(cc[1] + r * cos(seqang),rnd) 
      y <- round(cc[2] + r * sin(seqang),rnd) 
      # Check for line segments that should be joined up and combine their coordinates 
      if (is.null(prevx)){ 
        prevx<-x 
        prevy<-y 
      } else if (x[1] == round(prevx[length(prevx)],rnd) && y[1] == round(prevy[length(prevy)],rnd)){ 
        if (i == nrow(xdf)){ 
          #We have got to the end of the dataset 
          prevx<-append(prevx,x[2:ipoints]) 
          prevy<-append(prevy,y[2:ipoints]) 
          prevx[length(prevx)]<-prevx[1] 
          prevy[length(prevy)]<-prevy[1] 
          coordsj<-cbind(prevx,prevy) 
          colnames(coordsj)<-NULL 
          # Build as Line and then Lines class 
          linej <- Line(coordsj) 
          linesj[[j]] <- Lines(linej, ID = as.character(j)) 
        } else { 
          prevx<-append(prevx,x[2:ipoints]) 
          prevy<-append(prevy,y[2:ipoints]) 
        } 
      } else { 
        # We have got to the end of a set of lines, and there are several such sets, so convert the whole of this one to a line segment and reset. 
        prevx[length(prevx)]<-prevx[1] 
        prevy[length(prevy)]<-prevy[1] 
        coordsj<-cbind(prevx,prevy) 
        colnames(coordsj)<-NULL 
        # Build as Line and then Lines class 
        linej <- Line(coordsj) 
        linesj[[j]] <- Lines(linej, ID = as.character(j)) 
        j<-j+1 
        prevx<-NULL 
        prevy<-NULL 
      } 
    } 
    # Promote to SpatialLines 
    lspl <- SpatialLines(linesj) 
    # Convert lines to polygons 
    # Pull out Lines slot and check which lines have start and end points that are the same 
    lns <- slot(lspl, "lines") 
    polys <- sapply(lns, function(x) { 
      crds <- slot(slot(x, "Lines")[[1]], "coords") 
      identical(crds[1, ], crds[nrow(crds), ]) 
    }) 
    # Select those that do and convert to SpatialPolygons 
    polyssl <- lspl[polys] 
    list_of_Lines <- slot(polyssl, "lines") 
    sppolys <- SpatialPolygons(list(Polygons(lapply(list_of_Lines, function(x) { Polygon(slot(slot(x, "Lines")[[1]], "coords")) }), ID = "1")), proj4string=proj4string) 
    # Create a set of ids in a dataframe, then promote to SpatialPolygonsDataFrame 
    hid <- sapply(slot(sppolys, "polygons"), function(x) slot(x, "ID")) 
    areas <- sapply(slot(sppolys, "polygons"), function(x) {slot(x, "area") / (1000.0  * 1000.0)}) 
    df <- data.frame(hid,areas) 
    names(df) <- c("HID","area") 
    rownames(df) <- df$HID 
    res <- SpatialPolygonsDataFrame(sppolys, data=df) 
    res <- res[which(res@data$area > 0),] 
  }   
  return(res) 
} 

myalphahullP <- function(positionFix.proj, sinputssrs, ialpha) {
  iids <- unique(as.character(positionFix.proj$ID))
  alphaHRD <- function(ialpha,iepsg)
  {
    id.alpha <- function(i) # Need to run for each animal ID
    {
      positionFix.proj1 <- subset(positionFix.proj,positionFix.proj$ID==iids[i])
      positionFix.xy <- coordinates(positionFix.proj1)[!duplicated(coordinates(positionFix.proj1)),]
  
      a <- ahull(coordinates(positionFix.xy), alpha = ialpha) 
      Ahull.proj <- ah2sp(a,rnd=2)
      Ahull.proj$id <- Ahull.proj$name <- unique(positionFix.proj1$ID)
      Ahull.proj$alpha <- ialpha
      row.names(Ahull.proj) <- as.character(i)
      return(Ahull.proj)
    }
    all.alpha <- do.call(rbind,lapply(1:length(iids),id.alpha))
    proj4string(all.alpha) <- sinputssrs 
    Ahull <- spTransform(all.alpha,CRS("+proj=longlat +datum=WGS84"))
  }
  myAhull <- try({myAhull <- alphaHRD(ialpha,sinputssrs)},silent=TRUE)
  if (class(myAhull) == 'try-error') {
    stop('Alpha hull unable to generate under these parameters. Try increasing the alpha value.') 
  }
  return(myAhull)
}

oztrack_alphahull <- function(srs, alpha, kmlFile) {
  myAhull <- myalphahullP(positionFix.proj, sinputssrs=paste('+init=', srs, sep=''), ialpha=alpha)
  fOZkmlPolygons(OzSPDF=myAhull, kmlFileName=kmlFile, folderName='AHULL')
}
