## This function converts an alpha hull object into a SpatialPolygonsDataFrame object
ah2sp <- function(xah, increment=360, rnd=2, proj4string=CRS(as.character(NA))){ 
  require(sp) 
  require(maptools) 
  if (class(xah) != "ahull"){ 
    stop("xah needs to be an ahull class object") 
  }
  
  #xah=a;increment=360; rnd=2;proj4string=CRS(as.character(NA))
  
  # Extract the edges from the ahull object as a dataframe 
  xdf <- as.data.frame(xah$arcs) 
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
      x <- cc[1] + r * cos(seqang)
      y <- cc[2] + r * sin(seqang) 
      # Check for line segments that should be joined up and combine their coordinates 
      if (is.null(prevx)){ 
        prevx<-x 
        prevy<-y 
      } else if (round(x[1],rnd) == round(prevx[length(prevx)],rnd) && round(y[1],rnd) == round(prevy[length(prevy)],rnd)){ 
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
    areas <- sapply(slot(sppolys, "polygons"), function(x) slot(x, "area")) / 1000^2 # convert m2 to km2
    df <- data.frame(hid,areas) 
    names(df) <- c("id","area") 
    rownames(df) <- df$id 
    res <- SpatialPolygonsDataFrame(sppolys, data=df) 
    res <- res[which(res@data$area > 0),] 
  }   
  return(res) 
}

## This function generates the alpha hull SPDF for all locations
fmyalphahull <- function(sinputfile,sinputssrs,ialpha)
{  
  require(alphahull) 
  require(maptools)
  require(sp)
  require(rgdal)
 
#sinputfile=positionFix;sinputssrs='+init=epsg:3577';ialpha=10000
  
  myids <- data.frame(ID=as.character(sinputfile$ID))
  iids <- unique(as.character(sinputfile$ID))
  
  mycoords <- SpatialPoints(sinputfile[,c("X","Y")])
  proj4string(mycoords) <- CRS("+proj=longlat +datum=WGS84")
  positionFix.xy <- SpatialPointsDataFrame(mycoords,myids)
  positionFix.proj <- spTransform(positionFix.xy,CRS(sinputssrs))
  
  id.alpha <- function(i) # Need to run for each animal ID
  {
    positionFix.proj1 <- subset(positionFix.proj,positionFix.proj$ID==iids[i])
    positionFix.proj1d<- coordinates(positionFix.proj1)[!duplicated(coordinates(positionFix.proj1)),]
      
    a <- ahull(coordinates(positionFix.proj1d), alpha = ialpha) 
    Ahull.proj <- ah2sp(xah=a,rnd=2)
    
    if(class(Ahull.proj) == 'SpatialPolygonsDataFrame')
    {
      #Ahull.proj$id <- unique(positionFix.proj1$ID)
      Ahull.proj$alpha <- ialpha
      row.names(Ahull.proj) <- iids[i]
      Ahull.proj@data$id <- iids[i]
    }else{
      Ahull.proj <- print('Alpha hull unable to generate under these parameters. Try increasing the alpha value.')
    }
    return(Ahull.proj)
  }
  
  all.alpha <- try(
{
  do.call(rbind,lapply(1:length(iids),id.alpha))
},silent=TRUE)
  
  if (class(all.alpha) != 'try-error'){
    if(class(all.alpha)=='SpatialPolygonsDataFrame'){    
      proj4string(all.alpha) <- sinputssrs 
      myAhull <- spTransform(all.alpha,CRS("+proj=longlat +datum=WGS84"))
    }else{
      myAhull <- print('Alpha hull unable to generate under these parameters. Try increasing the alpha value.')
    }
  }else{
    myAhull <- print('Alpha hull unable to generate under these parameters. Try increasing the alpha value.')
  }
  return(myAhull)
}


## This function generates the alpha hull SPDF for all locations
fmyalphahullAM <- function(sinputfile,sinputssrs,ialpha,rnd=2)
{  
  require(alphahull) 
  require(maptools)
  require(sp)
  require(rgdal)
  
#  sinputfile=positionFix;sinputssrs='+init=epsg:3577';ialpha=10000; rnd=2
  
  myids <- data.frame(ID=as.character(sinputfile$ID))
  iids <- unique(as.character(sinputfile$ID))
  
  # Create polygon for Easterlies
  # Add 360 to negatives to make all xys on same plane
  sinputfile$newX <- ifelse(sinputfile$X < 0,sinputfile$X+360,sinputfile$X)
  mycoords<- SpatialPoints(sinputfile[,c("newX","Y")]) 
  proj4string(mycoords) <- CRS("+proj=longlat +datum=WGS84 +over")
  positionFix.xy <- SpatialPointsDataFrame(mycoords,myids)
  positionFix.proj <- spTransform(positionFix.xy,CRS(sinputssrs))
  
  id.alpha <- function(i) # Need to run for each animal ID
  {
    positionFix.proj1 <- subset(positionFix.proj,positionFix.proj$ID==iids[i])
    positionFix.proj1d<- coordinates(positionFix.proj1)[!duplicated(coordinates(positionFix.proj1)),]
      
    a <- ahull(coordinates(positionFix.proj1d), alpha = ialpha) 
    Ahull.proj <- ah2sp(xah=a,rnd=rnd)
    
    if(class(Ahull.proj) == 'SpatialPolygonsDataFrame')
    {
      #Ahull.proj$id <- unique(positionFix.proj1$ID)
      Ahull.proj$alpha <- ialpha
      row.names(Ahull.proj) <- iids[i]
      Ahull.proj@data$id <- iids[i]
    }else{
      Ahull.proj <- print('Alpha hull unable to generate under these parameters. Try increasing the alpha value.') 
    }
    return(Ahull.proj)
  }
  
  all.alpha <- try(
{
  do.call(rbind,lapply(1:length(iids),id.alpha))
},silent=TRUE)  

  if (class(all.alpha) != 'try-error'){
    if(class(all.alpha)=='SpatialPolygonsDataFrame'){
      proj4string(all.alpha) <- sinputssrs 
      Ahull <- spTransform(all.alpha,CRS("+proj=longlat +datum=WGS84 +over"))
      # This ensures all coordinates 0 - 360   
      myAhull <- elide.polygons2(Ahull, shift = c(360.0, 0.0))
      myAhull <- SpatialPolygonsDataFrame(myAhull,Ahull@data)
    }else{
      myAhull <- print('Alpha hull unable to generate under these parameters. Try increasing the alpha value.')
    }
  }else{
    myAhull <- print('Alpha hull unable to generate under these parameters. Try increasing the alpha value.')
  }
  return(myAhull)
}
    

## This code converts SPDF to kml
oztrack_alphahull <- function(srs, alpha, kmlFile, is180=FALSE,rnd=2) {
  if(is180==FALSE)
    myAhull <- fmyalphahull(sinputfile=positionFix, sinputssrs=paste('+init=', srs, sep=''), ialpha=alpha)
  if(is180==TRUE)
    myAhull <- fmyalphahullAM(sinputfile=positionFix, sinputssrs=paste('+init=', srs, sep=''), ialpha=alpha,rnd=rnd)
  
  if(class(myAhull)=='SpatialPolygonsDataFrame'){
    fOZkmlPolygons(OzSPDF=myAhull,kmlFileName=kmlFile,folderName='AHULL')
  }else{ print('Alpha hull unable to generate under these parameters. Try increasing the alpha value.')   
  }
}
