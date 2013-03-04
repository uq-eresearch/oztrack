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
                    unout = c("ha"))
    },silent=TRUE)
    
    if (class(myKerP) == 'try-error') 
    {
      stop('Kernel polygon unable to generate under these parameters. Try increasing the grid size or change the percentile.')
    }else{      
      # Convert 95% kernel back to long lats (For plotting in Google Earth)
      myKer <- spTransform(myKerP,CRS("+proj=longlat +datum=WGS84"))
      # Write area in ha to the long/lat kernel
      myKer$area <- myKerP$area
      myKer$hval <- allh
    }
  }
  return(myKer) 
}

## This function generates the kernelUD SPDF for all locations when crossing antimeridian
fmykernelAMindegrees <- function(sinputfile,sinputssrs,imypercent,smyh,imygrid,imyextent)
{
 
  myids <- data.frame(ID=as.character(sinputfile$ID))
  iids <- unique(as.character(sinputfile$ID))
  
  # Create polygon for western hemis
  # Add 360 to negatives to make all xys on same plane
  sinputfile$newX <- ifelse(sinputfile$X < 0,sinputfile$X+360,sinputfile$X)
  mycoords <- SpatialPoints(sinputfile[,c("newX","Y")])
  positionFix.xy <- SpatialPointsDataFrame(mycoords,myids)
  
  # Calculate kernel using projected coordinate system
  KerHRud <- try({
    KerHRud <- kernelUD(xy=positionFix.xy,h=smyh,grid=imygrid,extent=imyextent)
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
    myKer1 <- getverticeshr(KerHRud,percent=imypercent)  # Generate the polygon at the chosen percentile
    # Now clip by bounding box at the antemeridian
    bb1a <- bbox(myKer1) #Create bounding box polygon around HR
    pts1a <- rbind(c(bb1a[1],bb1a[2]),c(bb1a[1],bb1a[4]),c(180,bb1a[4]),c(180,bb1a[2]))
    pts1a <- rbind(pts1a , pts1a[1,]) # ensure that the last point and first coincide
    poly1a <- SpatialPolygons(list(Polygons(list(Polygon(pts1a)),ID=as.character(1))))

    # Create polygon for Eastern hemis   
    sinputfile$newX2 <- ifelse(sinputfile$X > 0,sinputfile$X-360,sinputfile$X)
    mycoords <- SpatialPoints(sinputfile[,c("newX2","Y")])
    positionFix.xy <- SpatialPointsDataFrame(mycoords,myids)
    KerHRud <- kernelUD(xy=positionFix.xy,h=smyh,grid=imygrid,extent=imyextent)
    myKer2 <- getverticeshr(KerHRud,percent=imypercent)  # Generate the polygon at the chosen percentile
    # Now clip by bounding box at the antemeridian    
    bb1b <- bbox(myKer2) #Create bounding box polygon
    pts1b <- rbind(c(-180,bb1b[2]),c(-180,bb1b[4]),c(bb1b[3],bb1b[4]),c(bb1b[3],bb1b[2]))
    pts1b <- rbind(pts1b , pts1b[1,]) # ensure that the last point and first coincide
    poly1b <- SpatialPolygons(list(Polygons(list(Polygon(pts1b)),ID=as.character(1))))
    
    # Combine Westerlies and Easterlies
    id.kernEW <- function(i)
    {
      hr1a <- try({
        hr1a  <- gIntersection(myKer1[i,],poly1a, byid=TRUE)
      },silent=TRUE)
      if (class(hr1a) == 'try-error'){
        hr1a <- NULL
      }else{
        hr1a <- hr1a
      } 
      
      hr1b <- try({   
        hr1b  <- gIntersection(myKer2[i,],poly1b, byid=TRUE)
      },silent=TRUE)
      if (class(hr1b) == 'try-error'){
        hr1b <- NULL
      }else{
        hr1b <- hr1b
      } 
      
      # Error checking to ensure polygons match up
      if(is.null(hr1a)==TRUE & is.null(hr1b)==FALSE)
        if(class(hr1b)=="SpatialPolygons")
        {
          hr1ab <- hr1b
          hrspdf <- SpatialPolygonsDataFrame(hr1ab,data.frame(id=iids[i], row.names = row.names(hr1ab)))
        }
      if(is.null(hr1a)==FALSE & is.null(hr1b)==TRUE)
        if(class(hr1a)=="SpatialPolygons")
        {
          hr1ab <- hr1a
          hrspdf <- SpatialPolygonsDataFrame(hr1ab,data.frame(id=iids[i], row.names = row.names(hr1ab)))
        }
      if(is.null(hr1a)==FALSE & is.null(hr1b)==FALSE)
      {
        if(class(hr1a)=="SpatialPolygons" & class(hr1b)=="SpatialPolygons")
        {
          hr1aP <- sapply(1:length(hr1a@polygons[[1]]@Polygons),function(x) hr1a@polygons[[1]]@Polygons[[x]])
          hr1bP <- sapply(1:length(hr1b@polygons[[1]]@Polygons),function(x) hr1b@polygons[[1]]@Polygons[[x]])
          hr1abP <- append(hr1aP,hr1bP)
          
          poly2b <- SpatialPolygons(list(Polygons(hr1abP,ID=i)))
          hrspdf <- SpatialPolygonsDataFrame(poly2b,data.frame(id=iids[i],row.names = row.names(poly2b)))       
        }
        if(class(hr1a)=="SpatialPolygons" & class(hr1b)!="SpatialPolygons")
        {
          hr1ab <- hr1a
          hrspdf <- SpatialPolygonsDataFrame(hr1ab,data.frame(id=iids[i],row.names = row.names(hr1ab)))
        }
        if(class(hr1a)!="SpatialPolygons" & class(hr1b)=="SpatialPolygons")
        {
          hr1ab <- hr1b
          hrspdf <- SpatialPolygonsDataFrame(hr1ab,data.frame(id=iids[i],row.names = row.names(hr1ab)))
        }
      }
      return(hrspdf)
    }
    
    myKer <- do.call(rbind,lapply(1:length(iids),id.kernEW))
    proj4string(myKer) <- CRS("+proj=longlat +datum=WGS84")
    myKer.proj <- spTransform(myKer,CRS(sinputssrs))  
    polygonareas <- sapply(myKer.proj@polygons,function(x) x@area)  
    myKer$area <- polygonareas/10000 # Convert meters to hectares
    myKer$hval <- allh
    return(myKer)  
  }
}

## This code converts SPDF to kml
oztrack_kernelud <- function(srs, h, gridSize, extent, percent, kmlFile, is180=FALSE) {
  if(is180==FALSE)
    myKer <- fmykernel(sinputfile=positionFix, 
                       sinputssrs=paste('+init=', srs, sep=''), 
                       imypercent=percent,smyh=h,
                       imygrid=gridSize,imyextent=extent)
  if(is180==TRUE)
    myKer <- fmykernelAMindegrees(sinputfile=positionFix, 
                                    sinputssrs=paste('+init=', srs, sep=''), 
                                    imypercent=percent,smyh=h,
                                    imygrid=gridSize,imyextent=extent)
  
  fOZkmlPolygons(OzSPDF=myKer, kmlFileName=kmlFile, folderName='KUD')
}
