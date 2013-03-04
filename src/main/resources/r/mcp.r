## Generate minimum convex polygons for locations not crossing the antimeridian
fmymcp <- function(sinputfile,sinputssrs,imypercent){
  require(adehabitatHR)
  require(rgdal)
  require(sp)
  require(maptools)
  
  myids <- data.frame(ID=as.character(sinputfile$ID))
  mycoords <- SpatialPoints(sinputfile[,c("X","Y")])
  proj4string(mycoords) <- CRS("+proj=longlat +datum=WGS84")
  positionFix.xy <- SpatialPointsDataFrame(mycoords,myids)
  positionFix.proj <- spTransform(positionFix.xy,CRS(sinputssrs))  
  
  # Calculate MCP using X/Ys
  MCP_oz <- try({
    mcp(positionFix.xy,percent=imypercent) 
  },silent=TRUE)
  
  # Error handling
  if (class(MCP_oz) == 'try-error') 
  {
    stop('At least 5 relocations are required to fit a home range. Please ensure all animals have >5 locations.')
  }else{
    MCP_oz$area <- mcp(positionFix.proj,percent=imypercent,
                       unin = c("m"),
                       unout = c("km2"))$area
  }
  return(MCP_oz) 
}

## Generate minimum convex polygons for locations crossing antimeridian 
fmymcpAM <- function(sinputfile,sinputssrs,imypercent){
  require(adehabitatHR)
  require(rgdal)
  require(sp)
  require(maptools)
  require(rgeos)
  
  myids <- data.frame(ID=as.character(sinputfile$ID))
  iids <- unique(as.character(sinputfile$ID))
  
  id.mcp <- function(i) # Need to run for each animal ID
  {
    sinputfile1 <- subset(sinputfile,sinputfile$ID==iids[i]) 
    
    # Create polygon for Easterlies
    # Add 360 to negatives to make all xys on same plane
    sinputfile1$newX <- ifelse(sinputfile1$X < 0,sinputfile1$X+360,sinputfile1$X)
    mylocs2<- SpatialPoints(sinputfile1[,c("newX","Y")]) 
    mymcp1a <- mcp(mylocs2,percent=100)
    bb1a <- bbox(mymcp1a) #Create bounding box polygon around HR which clips at the antemeridian 
    pts1a <- rbind(c(bb1a[1],bb1a[2]),c(bb1a[1],bb1a[4]),c(180,bb1a[4]),c(180,bb1a[2]))
    pts1a <- rbind(pts1a , pts1a[1,]) # ensure that the last point and first coincide
    poly1a <- SpatialPolygons(list(Polygons(list(Polygon(pts1a)),ID=i)))
    hr1a  <- gIntersection(mymcp1a,poly1a, byid=TRUE)
    
    # Create polygon for Westerlies
    # Subtract 360 from positives to make all xys on same plane
    sinputfile1$newX2 <- ifelse(sinputfile1$X > 0,sinputfile1$X-360,sinputfile1$X)
    mylocs2<- SpatialPoints(sinputfile1[,c("newX2","Y")]) 
    mymcp1b <- mcp(mylocs2,percent=100)
    bb1b <- bbox(mymcp1b)
    pts1b <- rbind(c(-180,bb1b[2]),c(-180,bb1b[4]),c(bb1b[3],bb1b[4]),c(bb1b[3],bb1b[2]))
    pts1b <- rbind(pts1b , pts1b[1,]) # ensure that the last point and first coincide
    poly1b <- SpatialPolygons(list(Polygons(list(Polygon(pts1b)),ID=i)))
    hr1b  <- gIntersection(mymcp1b,poly1b, byid=TRUE)

    # Combine Westerlies and Easterlies
    hr1abP <- c()
    if (class(hr1a) == 'SpatialPolygons')
    {
      hr1aP <- sapply(1:length(hr1a@polygons[[1]]@Polygons),function(x) hr1a@polygons[[1]]@Polygons[[x]])
      hr1abP <- append(hr1abP, hr1aP)
    }
    if (class(hr1b) == 'SpatialPolygons')
    {
      hr1bP <- sapply(1:length(hr1b@polygons[[1]]@Polygons),function(x) hr1b@polygons[[1]]@Polygons[[x]])
      hr1abP <- append(hr1abP, hr1bP)
    }
    
    poly2b <- SpatialPolygons(list(Polygons(hr1abP,ID=i)))
    hrspdf <- SpatialPolygonsDataFrame(poly2b,data.frame(id=iids[i],row.names = as.character(i)))       
  }
  
  ####
  all.mcp <- do.call(rbind,lapply(1:length(iids),id.mcp))
  proj4string(all.mcp) <- CRS("+proj=longlat +datum=WGS84")
  all.mcp.proj <- spTransform(all.mcp,CRS(sinputssrs))
  polygonareas <- sapply(all.mcp.proj@polygons,function(x) x@area)
  all.mcp$area <- polygonareas / (1000 * 1000) # Convert from m2 to km2
  return(all.mcp)
}

## This code converts SPDF to kml
oztrack_mcp <- function(srs, percent, kmlFile, is180=FALSE) {
  if(is180==FALSE)
    mcp.obj <- fmymcp(sinputfile=positionFix, sinputssrs=paste('+init=', srs, sep=''),imypercent=percent)
  if(is180==TRUE)
    mcp.obj <- fmymcpAM(sinputfile=positionFix, sinputssrs=paste('+init=', srs, sep=''),imypercent=percent)
    
  fOZkmlPolygons(OzSPDF=mcp.obj, kmlFileName=kmlFile, folderName='MCP')
}
