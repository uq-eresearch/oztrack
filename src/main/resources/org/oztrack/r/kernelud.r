## This function generates the kernelUD SPDF for all locations when not crossing antimeridian
fmykernel <- function(sinputfile,sinputssrs,imypercent,smyh,imygrid,imyextent)
{
  require(adehabitatHR)
  require(rgdal)
  require(sp)
  require(maptools)
  
  myids <- data.frame(ID=as.character(sinputfile$ID))
  mycoords <- SpatialPoints(sinputfile[,c("X","Y")])
  proj4string(mycoords) <- CRS("+proj=longlat +datum=WGS84")
  positionFix.xy <- SpatialPointsDataFrame(mycoords,myids)
  positionFix.proj <- spTransform(positionFix.xy,CRS(sinputssrs))  
  
  # Calculate kernel using projected coordinate system
  KerHRud <- try({
    kernelUD(xy=positionFix.proj,h=smyh,grid=imygrid,extent=imyextent)
  },silent=TRUE)
  if (class(KerHRud) == 'try-error') 
  {
    if(smyh=='href')
      stop('Kernel unable to generate under these parameters. Try increasing the extent.')
    if(class(smyh)=='numeric')
      stop('Kernel unable to generate under these parameters. Try increasing the h smoothing parameter value.')
    if(smyh=='LSCV')
      stop('Kernel unable to generate under these parameters. Try increasing the extent and the grid size.')
  }else{
    # Extract the h values
    allh <- sapply(1:length(KerHRud),function(x)KerHRud[[x]]@h$h)
    
    # Generate the polygon at the chosen percentile
    myKerP <- try({
      getverticeshr(KerHRud,percent=imypercent,
                    unin = c("m"),
                    unout = c("km2"))
    },silent=TRUE)
    
    if (class(myKerP) == 'try-error') 
    {
      stop('Kernel polygon unable to generate under these parameters. Try increasing the grid size or change the percentile.')
    }else{      
      # Convert 95% kernel back to long lats (For plotting in Google Earth)
      myKer <- spTransform(myKerP,CRS("+proj=longlat +datum=WGS84"))
      myKer <- elide.polygons2(myKer, shift=c(360.0, 0.0))
      myKer <- SpatialPolygonsDataFrame(myKer, myKerP@data)
      myKer$hval <- allh
    }
  }
  return(myKer) 
}

## This function generates the kernelUD SPDF for all locations when not crossing antimeridian
fmykernelAM <- function(sinputfile,sinputssrs,imypercent,smyh,imygrid,imyextent)
{
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
  
  # Calculate kernel using projected coordinate system
  KerHRud <- try({
    kernelUD(xy=positionFix.proj,h=smyh,grid=imygrid,extent=imyextent)
  },silent=TRUE)
  if (class(KerHRud) == 'try-error') 
  {
    if(smyh=='href')
      stop('Kernel unable to generate under these parameters. Try increasing the extent.')
    if(class(smyh)=='numeric')
      stop('Kernel unable to generate under these parameters. Try increasing the h smoothing parameter value.')
    if(smyh=='LSCV')
      stop('Kernel unable to generate under these parameters. Try increasing the extent and the grid size.')
  }else{
    # Extract the h values
    allh <- sapply(1:length(KerHRud),function(x)KerHRud[[x]]@h$h)
    
    # Generate the polygon at the chosen percentile
    myKerP <- try({
      getverticeshr(KerHRud,percent=imypercent,
                    unin = c("m"),
                    unout = c("km2"))
    },silent=TRUE)
    
    if (class(myKerP) == 'try-error') 
    {
      stop('Kernel polygon unable to generate under these parameters. Try increasing the grid size or change the percentile.')
    }else{      
      # Convert 95% kernel back to long lats (For plotting in Google Earth)
      myKer <- spTransform(myKerP,CRS("+proj=longlat +datum=WGS84  +over"))
      # Write area in ha to the long/lat kernel
      myKer$area <- myKerP$area
      myKer$hval <- allh
    }
  }
  return(myKer) 
}



## This code converts SPDF to kml
oztrack_kernelud <- function(srs, h, gridSize, extent, percent, is180=FALSE) {
  if(is180==FALSE)
    myKer <- fmykernel(sinputfile=positionFix, 
                       sinputssrs=paste('+init=', srs, sep=''), 
                       imypercent=percent,smyh=h,
                       imygrid=gridSize,imyextent=extent)
  if(is180==TRUE)
    myKer <- fmykernelAM(sinputfile=positionFix, 
                         sinputssrs=paste('+init=', srs, sep=''), 
                         imypercent=percent,smyh=h,
                         imygrid=gridSize,imyextent=extent)
  
  kmlFile <- tempfile('kernelud', fileext='.kml')
  fOZkmlPolygons(OzSPDF=myKer, kmlFileName=kmlFile)
  return(kmlFile)
}
