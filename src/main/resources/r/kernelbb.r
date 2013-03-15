fmykBB <- function(sinputfile,sinputssrs,isig1,isig2,imypercent,imygrid,imyextent) {
  
  require(adehabitatHR)
  require(rgdal)
  require(sp)
  require(maptools)

  myids <- data.frame(ID=as.character(sinputfile$ID))
  mycoords <- SpatialPoints(sinputfile[,c("X","Y")])
  proj4string(mycoords) <- CRS("+proj=longlat +datum=WGS84")
  positionFix.xy <- SpatialPointsDataFrame(mycoords,myids)
  positionFix.proj <- spTransform(positionFix.xy,CRS(sinputssrs))  
  ltraj.obj <- as.ltraj(xy=coordinates(positionFix.proj), date=positionFix$Date, id=positionFix$ID, typeII=TRUE)

  # Calculate kernel using projected coordinate system
  KerBBud <- try({
    kernelbb(ltraj.obj,sig1=isig1,sig2=isig2,grid=imygrid,extent=imyextent)
  },silent=TRUE)
  if (class(KerBBud) == 'try-error') 
  {
    stop('Kernel unable to generate under these parameters. Try increasing sig1.')
  }
  
  # Generate the polygon at the chosen percentile
  hr.proj <- try({
     getverticeshr(KerBBud, percent=imypercent, unin=c('m'), unout=c('km2'))
  },silent=TRUE)
  
  if (class(hr.proj) == 'try-error') 
  {
    hr.xy <- stop('Kernel polygon unable to generate under these parameters. Try increasing the grid size or change the percentile.')
  }else{  
  if (nrow(hr.proj) == 1) {hr.proj$id <- positionFix[1,'ID']} # Fix: puts 'homerange' instead of animal ID when only one animal
  proj4string(hr.proj) <- proj4string(positionFix.proj)
  hr.xy <- spTransform(hr.proj, CRS('+proj=longlat +datum=WGS84'))
  #hr.xy$area <- hr.proj$area #No longer needed !
  }
  return(hr.xy) 
}


fmykBBAM <- function(sinputfile,sinputssrs,isig1,isig2,imypercent,imygrid,imyextent) {
  
  require(adehabitatHR)
  require(rgdal)
  require(sp)
  require(maptools)

  myids <- data.frame(ID=as.character(sinputfile$ID))
  
  # Create polygon for Easterlies
  # Add 360 to negatives to make all xys on same plane
  sinputfile$newX <- ifelse(sinputfile$X < 0,sinputfile$X+360,sinputfile$X)
  mycoords<- SpatialPoints(sinputfile[,c("newX","Y")]) 
  proj4string(mycoords) <- CRS("+proj=longlat +datum=WGS84 +over")
  positionFix.xy <- SpatialPointsDataFrame(mycoords,myids)
  
  positionFix.proj <- spTransform(positionFix.xy,CRS(sinputssrs))  
  ltraj.obj <- as.ltraj(xy=coordinates(positionFix.proj), date=positionFix$Date, id=positionFix$ID, typeII=TRUE)
  
  # Calculate kernel using projected coordinate system
  KerBBud <- try({
    kernelbb(ltraj.obj,sig1=isig1,sig2=isig2,grid=imygrid,extent=imyextent)
  },silent=TRUE)
  if (class(KerBBud) == 'try-error') 
  {
    stop('Kernel unable to generate under these parameters. Try increasing sig1.')
  }
  
  # Generate the polygon at the chosen percentile
  hr.proj <- try({
    getverticeshr(KerBBud, percent=imypercent, unin=c('m'), unout=c('km2'))
  },silent=TRUE)
  
  if (class(hr.proj) == 'try-error') 
  {
    hr.xy <- stop('Kernel polygon unable to generate under these parameters. Try increasing the grid size or change the percentile.')
  }else{  
    if (nrow(hr.proj) == 1) {hr.proj$id <- positionFix[1,'ID']} # Fix: puts 'homerange' instead of animal ID when only one animal
    proj4string(hr.proj) <- proj4string(positionFix.proj)
    hr.xy <- spTransform(hr.proj, CRS('+proj=longlat +datum=WGS84 +over'))
    # This ensures all coordinates 0 - 360   
    hr.xy <- elide.polygons2(hr.xy, shift = c(360.0, 0.0))
    hr.xy <- SpatialPolygonsDataFrame(hr.xy,hr.proj@data)
  }
  return(hr.xy) 
}




## This code converts SPDF to kml
oztrack_kernelbb <- function(srs, sig1, sig2, gridSize, extent, percent, kmlFile, is180=FALSE) {
  if(is180==FALSE)
    myKerBB <- fmykBB(sinputfile=positionFix, 
                      sinputssrs=paste('+init=', srs, sep=''), 
                      imypercent=percent,isig1=sig1,isig2=sig2,
                      imygrid=gridSize,imyextent=extent)
  if(is180==TRUE)
    myKerBB <- fmykBBAM(sinputfile=positionFix, 
                        sinputssrs=paste('+init=', srs, sep=''), 
                        imypercent=percent,isig1=sig1,isig2=sig2,
                        imygrid=gridSize,imyextent=extent)
  if(class(myKerBB)=='SpatialPolygonsDataFrame'){
    fOZkmlPolygons(OzSPDF=myKerBB, kmlFileName=kmlFile, folderName='KBB')
  }else{ stop('KBB unable to generate under these parameters. Try increasing the alpha value.')   
  }
}


