oztrack_mcp <- function(percent, kmlFile) {
  mcp.obj <- try({mcp(positionFix.xy, percent=percent)}, silent=TRUE)
  if (class(mcp.obj) == 'try-error') {
    stop('At least 5 relocations are required to fit a home range. Please ensure all animals have >5 locations.')
  }
  mcp.obj$area <- mcp(positionFix.proj, percent=percent, unin=c('m'), unout=c('km2'))$area
  writeOGR(mcp.obj, dsn=kmlFile, layer='MCP', driver='KML', dataset_options=c('NameField=id'))
}