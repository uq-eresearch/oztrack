oztrack_locoh <- function(k, r, percent, kmlFile) {
  if (!is.null(k)) {
    locoh.obj <- LoCoH.k(positionFix.proj, k=k)
  }
  else if(!is.null(r)) {
    locoh.obj <- LoCoH.r(positionFix.proj, r=r)
  }
  else {
    stop('Either Neighbours or Radius must be entered.')
  }
  hr.proj <- getverticeshr(locoh.obj, percent=percent, unin=c('m'), unout=c('km2'))
  hr.xy <- spTransform(hr.proj, CRS('+proj=longlat +datum=WGS84'))
  fOZkmlPolygons(OzSPDF=hr.xy, kmlFileName=kmlFile, folderName='LoCoH')
}
