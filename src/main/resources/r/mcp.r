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
    # Calculate MCP area in hectares and write to MCP100
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
  
  # Create polygon for Easterlies
  # Add 360 to negatives to make all xys on same plane
  sinputfile$newX <- ifelse(sinputfile$X < 0,sinputfile$X+360,sinputfile$X)
  mycoords<- SpatialPoints(sinputfile[,c("newX","Y")]) 
  proj4string(mycoords) <- CRS("+proj=longlat +datum=WGS84 +over")
  positionFix.xy <- SpatialPointsDataFrame(mycoords,myids)
  
  # Calculate MCP using X/Ys
  MCP_oz <- try({
          mcp(positionFix.xy,percent=imypercent) 
      },silent=TRUE)
  
  ####
  proj4string(MCP_oz) <- CRS("+proj=longlat +datum=WGS84 +over")
  all.mcp.proj <- spTransform(MCP_oz,CRS(sinputssrs))
  polygonareas <- sapply(all.mcp.proj@polygons,function(x) x@area)
  MCP_oz$area <- polygonareas / 1000^2 # Convert m2 to km2
  return(MCP_oz)
}

## This code converts SPDF to kml
oztrack_mcp <- function(srs, percent, kmlFile, is180=FALSE) {
  if(is180==FALSE)
    mcp.obj <- fmymcp(sinputfile=positionFix, sinputssrs=paste('+init=', srs, sep=''),imypercent=percent)
  if(is180==TRUE)
    mcp.obj <- fmymcpAM(sinputfile=positionFix, sinputssrs=paste('+init=', srs, sep=''),imypercent=percent)
  
  fOZkmlPolygons(OzSPDF=mcp.obj, kmlFileName=kmlFile, folderName='MCP')
}
