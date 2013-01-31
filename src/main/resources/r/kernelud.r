oztrack_kernelud <- function(h, gridSize, extent, percent, kmlFile) {
  KerHRp <- try({kernelUD(xy=positionFix.proj, h=h, grid=gridSize, extent=extent)}, silent=TRUE)
  if (class(KerHRp) == 'try-error') {
    if (h == 'href') {
      stop('Kernel unable to generate under these parameters. Try increasing the extent.')
    }
    if (h == 'LSCV') {
      stop('Kernel unable to generate under these parameters. Try increasing the extent and the grid size.')
    }
    if (class(h) == 'numeric') {
      stop('Kernel unable to generate under these parameters. Try increasing the h smoothing parameter value.')
    }
    stop('Kernel unable to generate due to error: ' + conditionMessage(KerHRp))
  }
  allh <- sapply(1:length(KerHRp), function(x) {KerHRp[[x]]@h$h})
  myKerP <- try({getverticeshr(KerHRp, percent=percent, unin=c('m'), unout=c('km2'))}, silent=TRUE)
  if (class(myKerP) == 'try-error') {
    stop('Kernel polygon unable to generate under these parameters. Try increasing the grid size or change the percentile.')
  }
  myKer <- spTransform(myKerP, CRS('+proj=longlat +datum=WGS84'))
  myKer$area <- myKerP$area
  myKer$hval <- allh
  writeOGR(myKer, dsn=kmlFile, layer='KUD', driver='KML', dataset_options=c('NameField=id'))
}