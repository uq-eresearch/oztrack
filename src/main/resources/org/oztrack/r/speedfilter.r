# Code adapted from Mike Sumner's trip package
# Uses a moving window rather than a simple speed filter
# Uses Great Circle Distance rather than Euclidean (more accuate)
# Automatically updates data to antimeridian 

# Generate the fdist funtion in KILOMETERS
fdist <- function(x1,y1,x2,y2)  sqrt((x2 - x1)^2 + (y2 - y1)^2)/1000
  
# Generate the gcdist.c funtion (great circle distance) in KILOMETERS
gcdist.c <- function (lon1, lat1, lon2, lat2) 
{
  DE2RA = pi/180
  a = 6378.137
  f = 1/298.257223563
  lat1R = lat1 * DE2RA
  lat2R = lat2 * DE2RA
  lon1R = lon1 * DE2RA
  lon2R = lon2 * DE2RA
  F = (lat1R + lat2R)/2
  G = (lat1R - lat2R)/2
  L = (lon1R - lon2R)/2
  sinG2 = sin(G)^2
  cosG2 = cos(G)^2
  sinF2 = sin(F)^2
  cosF2 = cos(F)^2
  sinL2 = sin(L)^2
  cosL2 = cos(L)^2
  S = sinG2 * cosL2 + cosF2 * sinL2
  C = cosG2 * cosL2 + sinF2 * sinL2
  w = atan(sqrt(S/C))
  R = sqrt(S * C)/w
  D = 2 * w * a
  H1 = (3 * R - 1)/(2 * C)
  H2 = (3 * R + 2)/(2 * S)
  dist <- D * (1 + f * H1 * sinF2 * cosG2 - f * H2 * cosF2 * sinG2)
  dist <- ifelse(abs(lat1 - lat2) < .Machine$double.eps, 0, dist)
  dist <- ifelse(abs(lon1 - lon2) < .Machine$double.eps, 0, dist)
  dist <- ifelse(abs((abs(lon1) + abs(lon2)) - 360) < .Machine$double.eps, 0, dist)
  dist
}


fspeedfilter2.AM <- function(sinputfile,sinputssrs,max.speed,great.circ=TRUE) 
{
     
  # Convert data frame into SPDF and project
  mydf <- data.frame(ID=as.character(sinputfile$ID),Date=as.POSIXct(sinputfile$Date))

  # Add 360 to negatives to make all xys on same plane
  sinputfile$X <- ifelse(sinputfile$X < 0,sinputfile$X+360,sinputfile$X)

  mycoords<- SpatialPoints(sinputfile[,c("X","Y")]) 
  proj4string(mycoords) <- CRS("+proj=longlat +datum=WGS84 +over")
  dat.xy <- SpatialPointsDataFrame(mycoords,mydf)
  ids <- unique(as.character(dat.xy@data$ID))
  
  if(great.circ!=TRUE){
    dat.proj <- spTransform(dat.xy,CRS(sinputssrs)) 
    dat.proj <- na.omit(dat.proj)
    fdistfun <- fdist 
  }else{
    dat.proj <- dat.xy
    fdistfun <- gcdist.c
  }

  pprm <- 3 # Points per running mean should be odd and greater than this number
  
  ## SPEED FILTER function
  fspeedfiltID <- function(j)
  { 
    #j=1
    myid <- ids[j]
    xysub <- subset(dat.proj,dat.proj$ID==myid)
    xy <- coordinates(xysub) # extract id's locations
    tms <- xysub@data$Date # extract vector of datetime for that id
    npts <- nrow(xy) # no locations for that id
    index <- 1:npts # Generate row index
    
    # Prepare while loop
    RMS <- rep(max.speed, npts)
    new.index <- index # Generate row index
    ok <- FALSE  
    stopfun <- FALSE
    xy1 <- xy
    iter <- 1
    res <- list(speed = numeric(0), rms = numeric(0)) # Generate an empty list object
   
    # Whle there is at least one point which doesn't meet the speed limit threshold
    while (any(ok==FALSE, na.rm = TRUE)) 
    {     
      # Run the specified distance function to extract the track distance in meters for all the points where
      # function(x1y1=all xy coords minus last coord,x2y2=all xy cords minus first coord)
      # then divide by time difference/3600 to obtain speed in km/h 
      
      if(iter==1){
        n <- length(new.index)
        ok <- rep(TRUE,length(new.index))
      }else{
      n <- length(which(ok))}
           
      dist1 <- fdistfun(xy1[-nrow(xy1), 1], xy1[-nrow(xy1),2],# calculate dist #1 to #2, (#n-1 to #n)
                        xy1[-1, 1], xy1[-1, 2])
      duration1 <- as.vector(diff(unclass(tms[new.index]))/3600)
      speed1 <- dist1/duration1
      
      dist2 <- fdistfun(xy1[-((nrow(xy1) - 1):nrow(xy1)),1], # # calculate dist #1 to #3, (#n-2 to #n)
                        xy1[-((nrow(xy1) - 1):nrow(xy1)), 2],
                        xy1[-(1:2),1], 
                        xy1[-(1:2), 2])
      duration2 <- as.vector(((unclass(tms[new.index][-c(1,2)]) - unclass(tms[new.index][-c(n - 1, n)]))/3600))
      # excluding last 2 points
      speed2 <- dist2/duration2
      
      # Now generate the matrix to extract the previous 2 rates of travel and the subsequent 2 rates of travel
      
      sub1 <- rep(0:1,n-1) + rep(1:(n-1),each=2) 
      sub2 <- rep(c(0,2),n-1) + rep(0:(n-2),each=2)
      sub1[which(sub1<=0 | sub1 > length(speed1))] <- NA
      sub2[which(sub2<=0 | sub2 > length(speed2))] <- NA
     
      # Extract speeds 1 and 2 from location on n - 1 and n - 2 vectors
      subspeed1 <- speed1[sub1]  
      subspeed2 <- speed2[sub2]
      
      # convert into a matrix object, bind then extract row means
      matspeed1 <- matrix(subspeed1,ncol=2,byrow = TRUE)
      matspeed2 <- matrix(subspeed2,ncol=2,byrow = TRUE)
      rmsRows <- cbind(matspeed1,matspeed2)
      nodist <- rowSums(is.na(rmsRows))
      RMS <- c(0, sqrt(rowSums(rmsRows^2, na.rm = TRUE)/(4-nodist)))

      ##### Now ensure that only largest error is removed and not those locations adjacent to erronious locations 
      # Which locations have a sum of squares greater or equal to the max speed threshold?
      bad <- RMS >= max.speed
      if(any(bad==TRUE))
      {
        segs <- cumsum(c(0, abs(diff(bad))))
        rmsFlag <- unlist(lapply(split(RMS, segs), 
                                 function(x) ifelse((1:length(x)) == which.max(x), TRUE, FALSE)), 
                          use.names = FALSE)
        # Reset those locations which were originally ok
        rmsFlag[!bad] <- FALSE
        ok <- !rmsFlag
      }else{
        ok <- !bad 
      }
        
      new.index <- new.index[ok] # Extract good row numbers from the data
      xy1 <- xy1[ok, ] # Extract good coordinates from the data frame
      
      # Break function if only 3 locations remains
      if(nrow(xy1)<=3)
      {
        newsub <- data.frame(xysub)[1,]
        #       newsub$speed <- 0 # Blank these out if they're not to be returned to OzTrack
        #       newsub$duration <- 0  # Blank these out if they're not to be returned to OzTrack
        warning("No longer enough locations to run filter, try increasing max speed threshold")
        ok <- TRUE
      }
      
      # Run function again if more than 3 locations
      if(nrow(xy1) > 3)
      {
        newsub <- data.frame(xysub)[new.index,]
        #newsub$duration <- c(0,duration1)[ok]  # Blank these out if they're not to be returned to OzTrack
        #newsub$speed <- c(0,speed1)[ok]  # Blank these out if they're not to be returned to OzTrack
      }
      iter <- iter+1
    }# End of while loop
    return(newsub)
  }
    
  # Apply the function to all IDs one at a time
  newxy.proj <- do.call(rbind,lapply(1:length(ids),fspeedfiltID))
  # Convert data frame to SPDF then re-project for upload into Google Earth
  coordinates(newxy.proj) <- ~X+Y

  if(great.circ!=TRUE){
    proj4string(newxy.proj) <- CRS(sinputssrs)
    newxy <- spTransform(newxy.proj,CRS("+proj=longlat +datum=WGS84 +over"))
  }else{
    proj4string(newxy.proj) <- CRS("+proj=longlat +datum=WGS84 +over")
    newxy <- newxy.proj
  }
  return(newxy)
}

##########################################

fspeedfilter2 <- function(sinputfile,sinputssrs,max.speed,great.circ=TRUE) 
{
  
  # Convert data frame into SPDF and project
  mydf <- data.frame(ID=as.character(sinputfile$ID),Date=as.POSIXct(sinputfile$Date))
  
  mycoords<- SpatialPoints(sinputfile[,c("X","Y")]) 
  proj4string(mycoords) <- CRS("+proj=longlat +datum=WGS84")
  dat.xy <- SpatialPointsDataFrame(mycoords,mydf)
  ids <- unique(as.character(dat.xy@data$ID))
  
  if(great.circ!=TRUE){
    dat.proj <- spTransform(dat.xy,CRS(sinputssrs)) 
    dat.proj <- na.omit(dat.proj)
    fdistfun <- fdist 
  }else{
    dat.proj <- dat.xy
    fdistfun <- gcdist.c
  }
  
  pprm <- 3 # Points per running mean should be odd and greater than this number
  
  ## SPEED FILTER function
  fspeedfiltID <- function(j)
  { 
    #j=1
    myid <- ids[j]
    xysub <- subset(dat.proj,dat.proj$ID==myid)
    xy <- coordinates(xysub) # extract id's locations
    tms <- xysub@data$Date # extract vector of datetime for that id
    npts <- nrow(xy) # no locations for that id
    index <- 1:npts # Generate row index
    
    # Prepare while loop
    RMS <- rep(max.speed, npts)
    new.index <- index # Generate row index
    ok <- FALSE  
    stopfun <- FALSE
    xy1 <- xy
    iter <- 1
    res <- list(speed = numeric(0), rms = numeric(0)) # Generate an empty list object
    
    # Whle there is at least one point which doesn't meet the speed limit threshold
    while (any(ok==FALSE, na.rm = TRUE)) 
    {     
      # Run the specified distance function to extract the track distance in meters for all the points where
      # function(x1y1=all xy coords minus last coord,x2y2=all xy cords minus first coord)
      # then divide by time difference/3600 to obtain speed in km/h 
      
      if(iter==1){
        n <- length(new.index)
        ok <- rep(TRUE,length(new.index))
      }else{
        n <- length(which(ok))}
      
      dist1 <- fdistfun(xy1[-nrow(xy1), 1], xy1[-nrow(xy1),2],# calculate dist #1 to #2, (#n-1 to #n)
                        xy1[-1, 1], xy1[-1, 2])
      duration1 <- as.vector(diff(unclass(tms[new.index]))/3600)
      speed1 <- dist1/duration1
      
      dist2 <- fdistfun(xy1[-((nrow(xy1) - 1):nrow(xy1)),1], # # calculate dist #1 to #3, (#n-2 to #n)
                        xy1[-((nrow(xy1) - 1):nrow(xy1)), 2],
                        xy1[-(1:2),1], 
                        xy1[-(1:2), 2])
      duration2 <- as.vector(((unclass(tms[new.index][-c(1,2)]) - unclass(tms[new.index][-c(n - 1, n)]))/3600))
      # excluding last 2 points
      speed2 <- dist2/duration2
      
      # Now generate the matrix to extract the previous 2 rates of travel and the subsequent 2 rates of travel
      
      sub1 <- rep(0:1,n-1) + rep(1:(n-1),each=2) 
      sub2 <- rep(c(0,2),n-1) + rep(0:(n-2),each=2)
      sub1[which(sub1<=0 | sub1 > length(speed1))] <- NA
      sub2[which(sub2<=0 | sub2 > length(speed2))] <- NA
      
      # Extract speeds 1 and 2 from location on n - 1 and n - 2 vectors
      subspeed1 <- speed1[sub1]  
      subspeed2 <- speed2[sub2]
      
      # convert into a matrix object, bind then extract row means
      matspeed1 <- matrix(subspeed1,ncol=2,byrow = TRUE)
      matspeed2 <- matrix(subspeed2,ncol=2,byrow = TRUE)
      rmsRows <- cbind(matspeed1,matspeed2)
      nodist <- rowSums(is.na(rmsRows))
      RMS <- c(0, sqrt(rowSums(rmsRows^2, na.rm = TRUE)/(4-nodist)))
      
      ##### Now ensure that only largest error is removed and not those locations adjacent to erronious locations 
      # Which locations have a sum of squares greater or equal to the max speed threshold?
      bad <- RMS >= max.speed
      if(any(bad==TRUE))
      {
        segs <- cumsum(c(0, abs(diff(bad))))
        rmsFlag <- unlist(lapply(split(RMS, segs), 
                                 function(x) ifelse((1:length(x)) == which.max(x), TRUE, FALSE)), 
                          use.names = FALSE)
        # Reset those locations which were originally ok
        rmsFlag[!bad] <- FALSE
        ok <- !rmsFlag
      }else{
        ok <- !bad 
      }
      
      new.index <- new.index[ok] # Extract good row numbers from the data
      xy1 <- xy1[ok, ] # Extract good coordinates from the data frame
      
      # Break function if only 3 locations remains
      if(nrow(xy1)<=3)
      {
        newsub <- data.frame(xysub)[1,]
        #       newsub$speed <- 0 # Blank these out if they're not to be returned to OzTrack
        #       newsub$duration <- 0  # Blank these out if they're not to be returned to OzTrack
        warning("No longer enough locations to run filter, try increasing max speed threshold")
        ok <- TRUE
      }
      
      # Run function again if more than 3 locations
      if(nrow(xy1) > 3)
      {
        newsub <- data.frame(xysub)[new.index,]
        #newsub$duration <- c(0,duration1)[ok]  # Blank these out if they're not to be returned to OzTrack
        #newsub$speed <- c(0,speed1)[ok]  # Blank these out if they're not to be returned to OzTrack
      }
      iter <- iter+1
    }# End of while loop
    return(newsub)
  }
  
  # Apply the function to all IDs one at a time
  newxy.proj <- do.call(rbind,lapply(1:length(ids),fspeedfiltID))
  # Convert data frame to SPDF then re-project for upload into Google Earth
  coordinates(newxy.proj) <- ~X+Y
  
  if(great.circ!=TRUE){
    proj4string(newxy.proj) <- CRS(sinputssrs)
    newxy <- spTransform(newxy.proj,CRS("+proj=longlat +datum=WGS84"))
  }else{
    proj4string(newxy.proj) <- CRS("+proj=longlat +datum=WGS84")
    newxy <- newxy.proj
  }
  return(newxy)
}

fspeedfilter <- function(sinputfile, sinputssrs, max.speed, is180=FALSE)
{
  if(is180==FALSE){
    return(fspeedfilter2(sinputfile, sinputssrs, max.speed))
  }else{
    return(fspeedfilter2.AM(sinputfile, sinputssrs, max.speed))
  }
}
