fmyLoCoH<- function(sinputfile,sinputssrs,ik=NULL,ir=NULL,imypercent) {
  require(adehabitatHR)
  require(rgdal)
  require(sp)
  require(maptools)
  require(gpclib)
  
  myids <- data.frame(ID=as.character(sinputfile$ID))
  mycoords <- SpatialPoints(sinputfile[,c("X","Y")])
  proj4string(mycoords) <- CRS("+proj=longlat +datum=WGS84")
  positionFix.xy <- SpatialPointsDataFrame(mycoords,myids)
  positionFix.proj <- spTransform(positionFix.xy,CRS(sinputssrs))  
    
  if(is.null(ir) & !is.null(ik)){
    locoh.obj <- LoCoH.k(positionFix.proj, k=ik)
  }else{
    if(!is.null(ir) & is.null(ik)){ 
      locoh.obj <- LoCoH.r(positionFix.proj, r=ir)
    }else{
      stop('Either Neighbours or Radius must be entered.')}}
  
  # Generate the polygon at the chosen percentile
  hr.proj <- try({
    getverticeshr(locoh.obj, percent=imypercent, unin=c('m'), unout=c('km2'))
  },silent=TRUE)
  
  if (class(hr.proj) == 'try-error') 
  {
    hr.xy <- print('Kernel polygon unable to generate under these parameters.')
  }else{  
    hr.xy <- spTransform(hr.proj, CRS('+proj=longlat +datum=WGS84'))
  }
  return(hr.xy)
}

fmyLoCoHAM<- function(sinputfile,sinputssrs,ik=NULL,ir=NULL,imypercent) {
  require(adehabitatHR)
  require(rgdal)
  require(sp)
  require(maptools)
  require(gpclib)
  
 # sinputfile=positionFix;sinputssrs=paste('+init=', srs, sep='');ik=10;imypercent=percent
  
  myids <- data.frame(ID=as.character(sinputfile$ID))

  # Create polygon for Easterlies
  # Add 360 to negatives to make all xys on same plane
  sinputfile$newX <- ifelse(sinputfile$X < 0,sinputfile$X+360,sinputfile$X)
  mycoords<- SpatialPoints(sinputfile[,c("newX","Y")]) 
  proj4string(mycoords) <- CRS("+proj=longlat +datum=WGS84 +over")
  positionFix.xy <- SpatialPointsDataFrame(mycoords,myids)
  positionFix.proj <- spTransform(positionFix.xy,CRS(sinputssrs))  
  
  if(is.null(ir) & !is.null(ik)){
    locoh.obj <- LoCoH.k(positionFix.proj, k=ik)
  }else{
    if(!is.null(ir) & is.null(ik)){ 
      locoh.obj <- LoCoH.r(positionFix.proj, r=ir)
    }else{
      stop('Either Neighbours or Radius must be entered.')}}
  
  # Generate the polygon at the chosen percentile
  hr.proj <- try({
    getverticeshr(locoh.obj, percent=imypercent, unin=c('m'), unout=c('km2'))
  },silent=TRUE)
  
  if (class(hr.proj) == 'try-error') 
  {
    hr.xy <- print('LoCoH polygon unable to generate under these parameters.')
  }else{  
    row.names(hr.proj@data) <- row.names(hr.proj)
    hr.xy <- spTransform(hr.proj, CRS('+proj=longlat +datum=WGS84 +over'))
    # This ensures all coordinates 0 - 360   
    hr.xy <- elide.polygons2(hr.xy, shift = c(360.0, 0.0))
    hr.xy <- SpatialPolygonsDataFrame(hr.xy,hr.proj@data)
  }
  return(hr.xy)
}

## This code converts SPDF to kml
oztrack_locoh <- function(srs, r=NULL, k=NULL, percent, is180=FALSE) {
  if(is180==FALSE)
    myLoCoH <- fmyLoCoH(sinputfile=positionFix, 
                        sinputssrs=paste('+init=', srs, sep=''), 
                        imypercent=percent,ik=k,ir=r)
  if(is180==TRUE)
    myLoCoH <- fmyLoCoHAM(sinputfile=positionFix, 
                        sinputssrs=paste('+init=', srs, sep=''), 
                        imypercent=percent,ik=k,ir=r)

  if(class(myLoCoH)=='SpatialPolygonsDataFrame'){
    kmlFile <- tempfile('locoh', fileext='.kml')
    fOZkmlPolygons(OzSPDF=myLoCoH, kmlFileName=kmlFile)
    return(kmlFile)
  }else{ 
    if(is.null(r) & !is.null(k)){
      print('LoCoH unable to generate under these parameters. Try a different r value.') 
      }else{
        if(!is.null(r) & is.null(k)){ 
          print('LoCoH unable to generate under these parameters. Try a different k value.') 
        }else{
          stop('Either Neighbours or Radius must be entered.')}}
  }
}

