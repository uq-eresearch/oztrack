fspeedfilter <- function(sinputfile, sinputssrs, max.speed) 
{
  # Convert data frame into SPDF and project
  mydf <- data.frame(ID=as.character(sinputfile$ID),Date=sinputfile$Date)
  mycoords <- SpatialPoints(sinputfile[,c("X","Y")])
  proj4string(mycoords) <- CRS("+proj=longlat +datum=WGS84")
  dat.xy <- SpatialPointsDataFrame(mycoords,mydf)
  dat.proj <- spTransform(dat.xy,CRS(sinputssrs)) 
  dat.proj <- na.omit(dat.proj)
  ids <- unique(as.character(dat.proj@data$ID))
  
  ## SPEED FILTER function
  fspeedfiltID <- function(j)
  { 
    myid <- ids[j]
    xysub <- subset(dat.proj,dat.proj$ID==myid)
    xy <- coordinates(xysub) # extract id's locations
    tms <- xysub@data$Date # extract vector of datetime for that id
    npts <- nrow(xy) # no locations for that id
    index <- 1:npts # Generate row index
    
    # Prepare while loop
    new.index <- index # Generate row index
    ok <- FALSE  
    stopfun <- FALSE
    xy1 <- xy  
      
    # Whle there is at least one point which doesn't meet the speed limit threshold
    while (any(ok==FALSE, na.rm = TRUE)) 
    {     
      # Run the fdist function to extract the track distance in meters for all the points where
      # function(x1y1=all xy coords minus last coord,x2y2=all xy cords minus first coord)
      # then divide by time difference/3600 to obtain speed in km/h 
      fdist <- function(x1,y1,x2,y2)  sqrt((x2 - x1)^2 + (y2 - y1)^2)
      
      dist1 <- fdist(xy1[-nrow(xy1), 1], xy1[-nrow(xy1),2],
        xy1[-1, 1], xy1[-1, 2])/1000
      duration1 <- diff(unclass(tms[new.index]))/3600
      speed1 <- dist1/duration1
      ok <- speed1 < max.speed
        
      if(length(ok)!=length(new.index))
        ok <- c(TRUE,ok)
      
      new.index <- new.index[ok] # Only include row numbers of original dataset where speed < max speed
      xy1 <- xy1[ok, ] # Remove any coordinate which is not < max speed
      
      # Break function if only 1 point remains
      if(length(xy1)==2)
      {
        newsub <- data.frame(xysub)[1,]
        newsub$speed <- 0 # Blank these out if they're not to be returned to OzTrack
        newsub$duration <- 0  # Blank these out if they're not to be returned to OzTrack
        ok <- TRUE
      }
      # Run function if more than 1 point
      if(length(xy1) > 2)
      {
        #speed <- c(0,speed1)[ok]
        newsub <- data.frame(xysub)[new.index,]
        newsub$duration <- c(0,duration1)[ok]  # Blank these out if they're not to be returned to OzTrack
        newsub$speed <- c(0,speed1)[ok]  # Blank these out if they're not to be returned to OzTrack
      }
    }
      return(newsub)
  }
  
  # Apply the function to all IDs one at a time
  newxy.proj <- do.call(rbind,lapply(1:length(ids),fspeedfiltID))
  # Convert data frame to SPDF then re-project
  coordinates(newxy.proj) <- ~X+Y
  proj4string(newxy.proj) <- CRS(sinputssrs)
  newxy <- spTransform(newxy.proj,CRS("+proj=longlat +datum=WGS84"))
  return(newxy)
}