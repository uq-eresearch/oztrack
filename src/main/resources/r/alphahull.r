## This function converts an alpha hull object into a SpatialPolygonsDataFrame object
ah2sp <- function(x, increment=360, rnd=10, proj4string=CRS(as.character(NA))){ 
  require(sp) 
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
    areas <- sapply(slot(sppolys, "polygons"), function(x) slot(x, "area")) 
    df <- data.frame(hid,areas) 
    names(df) <- c("ID","area") 
    rownames(df) <- df$ID 
    res <- SpatialPolygonsDataFrame(sppolys, data=df) 
    res <- res[which(res@data$area > 0),] 
  }   
  return(res) 
}

## This function generates the alpha hull SPDF for all locations
fmyalphahullP <- function(sinputfile,sinputssrs,ialpha)
{  
  require(alphahull) 
  require(maptools)
  require(sp)
  require(rgdal)
 
  #sinputfile=positionFix;sinputssrs='+init=epsg:3577';ialpha=100
  
  myids <- data.frame(ID=as.character(sinputfile$ID))
  iids <- unique(as.character(sinputfile$ID))
  mycoords <- SpatialPoints(sinputfile[,c("X","Y")])
  proj4string(mycoords) <- CRS("+proj=longlat +datum=WGS84")
  positionFix.xy <- SpatialPointsDataFrame(mycoords,myids)
  positionFix.proj <- spTransform(positionFix.xy,CRS(sinputssrs))
  
  alphaHRD <- function(ialpha,iepsg)
  {
    id.alpha <- function(i) # Need to run for each animal ID
    {
      positionFix.proj1 <- subset(positionFix.proj,positionFix.proj$ID==iids[i])
      positionFix.proj1d<- coordinates(positionFix.proj1)[!duplicated(coordinates(positionFix.proj1)),]
      
      a <- ahull(coordinates(positionFix.proj1d), alpha = ialpha) 
      Ahull.proj <- ah2sp(a,rnd=2)
      #Ahull.proj$id <- unique(positionFix.proj1$ID)
      Ahull.proj$alpha <- ialpha
      row.names(Ahull.proj) <- iids[i]
      return(Ahull.proj)
    }
    all.alpha <- do.call(rbind,lapply(1:length(iids),id.alpha))
    proj4string(all.alpha) <- sinputssrs 
    Ahull <- spTransform(all.alpha,CRS("+proj=longlat +datum=WGS84"))
    return(Ahull)
  }
  myAhull <- try(
{
  myAhull <- alphaHRD(ialpha,sinputssrs)
},silent=TRUE)
  if (class(myAhull) == 'try-error'){
    print(myAhull)
    stop('Alpha hull unable to generate under these parameters. Try increasing the alpha value.') 
  }else{
    myAhull@data <- data.frame(id=iids, area=myAhull@data$area / (1000 * 1000), alpha=myAhull@data$alpha) # Convert from m2 to km2
    return(myAhull)
  }
}


## This function generates the alpha hull SPDF for all locations when crossing antimeridian
fmyalphahullAMindegrees <- function(sinputfile,sinputssrs,ialpha)
{  
  require(sp)
  require(rgdal)
  require(alphahull) 
  require(maptools)
  require(rgeos)
  
  
 #sinputfile=positionFix;sinputssrs='+init=epsg:4326';ialpha=100
  
  myids <- data.frame(ID=as.character(sinputfile$ID))
  iids <- unique(as.character(sinputfile$ID))
  
  sinputfile$newXE <- ifelse(sinputfile$X < 0,sinputfile$X+360,sinputfile$X)
  mycoordsE <- SpatialPoints(sinputfile[,c("newXE","Y")])
  positionFix.xyE <- SpatialPointsDataFrame(mycoordsE,myids)
  sinputfile$newXW <- ifelse(sinputfile$X > 0,sinputfile$X-360,sinputfile$X)
  mycoordsW <- SpatialPoints(sinputfile[,c("newXW","Y")])
  positionFix.xyW <- SpatialPointsDataFrame(mycoordsW,myids)
  
  # Function to generate alpha SP for East hemis
  id.alphaE <- function(i) # Need to run for each animal ID
  {
    positionFix.xy1 <- subset(positionFix.xyE,positionFix.xyE$ID==iids[i])
    positionFix.xy2 <- coordinates(positionFix.xy1)[!duplicated(coordinates(positionFix.xy1)),]
    
    a <- ahull(coordinates(positionFix.xy2), alpha = ialpha) 
    Ahull.xy <- ah2sp(a,rnd=2)
    
    Ahull.xy <- try(
{
  Ahull.xy <- ah2sp(a,rnd=2)
},silent=TRUE)
    if (class(Ahull.xy) == 'try-error'){
      stop('Alpha hull unable to generate under these parameters. Try increasing the alpha value.') 
    }else{
      Ahull.xy <- Ahull.xy
    }
    
    row.names(Ahull.xy) <- iids[i]
    bb1a <- bbox(Ahull.xy) #Create bounding box polygon around HR which clips at the antemeridian
    pts1a <- rbind(c(bb1a[1],bb1a[2]),c(bb1a[1],bb1a[4]),c(180,bb1a[4]),c(180,bb1a[2]))
    pts1a <- rbind(pts1a , pts1a[1,]) # ensure that the last point and first coincide
    poly1a <- SpatialPolygons(list(Polygons(list(Polygon(pts1a)),ID=as.character(i))))
    hr1a <- gIntersection(Ahull.xy,poly1a, byid=TRUE)
    row.names(hr1a) <- iids[i]
    return(hr1a)
  }
  
  # Function to generate alpha SP for West hemis
  id.alphaW <- function(i) # Need to run for each animal ID
  {
    positionFix.xy1 <- subset(positionFix.xyW,positionFix.xyW$ID==iids[i])
    positionFix.xy2 <- coordinates(positionFix.xy1)[!duplicated(coordinates(positionFix.xy1)),]
    
    a <- ahull(coordinates(positionFix.xy2), alpha = ialpha) 
    Ahull.xy <- ah2sp(a,rnd=2)
    Ahull.xy$ID <- Ahull.xy$name <- as.character(iids[i])
    Ahull.xy$alpha <- ialpha
    row.names(Ahull.xy) <- as.character(iids[i])
    
    bb1b <- bbox(Ahull.xy) #Create bounding box polygon around HR which clips at the antemeridian
    pts1b <- rbind(c(-180,bb1b[2]),c(-180,bb1b[4]),c(bb1b[3],bb1b[4]),c(bb1b[3],bb1b[2]))
    pts1b <- rbind(pts1b , pts1b[1,]) # ensure that the last point and first coincide
    poly1b <- SpatialPolygons(list(Polygons(list(Polygon(pts1b)),ID=as.character(i))))
    hr1b <- gIntersection(Ahull.xy,poly1b, byid=TRUE)
    row.names(hr1b) <- as.character(i)
    return(hr1b)
  }
  
  # Combine Easterlies and Westerlies
  id.alphaEW <- function(i)
  {
    hr1a <- try({
      hr1a <- id.alphaE(i)
    },silent=TRUE)
    if (class(hr1a) == 'try-error'){
      print(hr1a)
      hr1a <- NULL
    }else{
      hr1a <- hr1a
    } 
    
    hr1b <- try({
      hr1b <- id.alphaW(i)
    },silent=TRUE)
    if (class(hr1b) == 'try-error'){
      print(hr1b)
      hr1b <- NULL
    }else{
      hr1b <- hr1b
    } 
    
    # Error checking to ensure East and West match up
    if(is.null(hr1a)==TRUE & is.null(hr1b)==FALSE)
    {
      if(class(hr1b)=="SpatialPolygons")
      {
        hr1ab <- hr1b
        hrspdf <- SpatialPolygonsDataFrame(hr1ab,data.frame(id=iids[i],row.names = as.character(i)))
      }
      else {
        print('hr1b not spatial polygon')
      }
    }
    else if(is.null(hr1a)==FALSE & is.null(hr1b)==TRUE) {
      if(class(hr1a)=="SpatialPolygons")
      {
        hr1ab <- hr1a
        hrspdf <- SpatialPolygonsDataFrame(hr1ab,data.frame(id=iids[i],row.names = as.character(i)))
      }
      else {
        print('hr1a not spatial polygon')
      }
    }
    else if(is.null(hr1a)==FALSE & is.null(hr1b)==FALSE)
    {
      if(class(hr1a)=="SpatialPolygons" & class(hr1b)=="SpatialPolygons")
      {
        hr1a <- sapply(1:length(hr1a@polygons[[1]]@Polygons),function(x) hr1a@polygons[[1]]@Polygons[[x]])
        hr1b <- sapply(1:length(hr1b@polygons[[1]]@Polygons),function(x) hr1b@polygons[[1]]@Polygons[[x]])
        hr1ab <- append(hr1a,hr1b)
        
        poly2b <- SpatialPolygons(list(Polygons(hr1ab,ID=as.character(i))))
        hrspdf <- SpatialPolygonsDataFrame(poly2b,data.frame(id=iids[i],row.names = as.character(i)))
      }
      else if(class(hr1a)=="SpatialPolygons" & class(hr1b)!="SpatialPolygons")
      {
        hr1ab <- hr1a
        hrspdf <- SpatialPolygonsDataFrame(hr1ab,data.frame(id=iids[i],row.names = as.character(i)))
      }
      else if(class(hr1a)!="SpatialPolygons" & class(hr1b)=="SpatialPolygons")
      {
        hr1ab <- hr1b
        hrspdf <- SpatialPolygonsDataFrame(hr1ab,data.frame(id=iids[i],row.names = as.character(i)))
      }
      else {
        print('neither hr1a nor hr1b are spatial polygons')
      }
    }
    else {
      print('both hr1a and hr1b are null')
    }
    return(hrspdf)
  }
  # Run the function for all ids
  Ahull <- do.call(rbind,lapply(1:length(iids),id.alphaEW))
  # change projections so we can work out areas
  proj4string(Ahull) <- CRS("+proj=longlat +datum=WGS84")
  Ahull.proj <- spTransform(Ahull,CRS(sinputssrs))  
  polygonareas <- sapply(Ahull.proj@polygons,function(x) x@area)  
  #Attach areas and alpha to SPDF
  Ahull$area <- polygonareas / (1000 * 1000) # Convert from m2 to km2
  Ahull$alpha <- ialpha
  return(Ahull)
}

## This code converts SPDF to kml
oztrack_alphahull <- function(srs, alpha, kmlFile, is180=FALSE) {
  if(is180==FALSE)
    myAhull <- fmyalphahullP(sinputfile=positionFix, sinputssrs=paste('+init=', srs, sep=''), ialpha=alpha)
  if(is180==TRUE)
    myAhull <- fmyalphahullAMindegrees(sinputfile=positionFix, sinputssrs=paste('+init=', srs, sep=''), ialpha=alpha)
    
  fOZkmlPolygons(OzSPDF=myAhull,kmlFileName=kmlFile,folderName='AHULL')
}
