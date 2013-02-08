### This function is an edited version of the kmlPolygon() function in package maptools
### Functionality has been extended so that details from the @data slot in the SPDF are returned in the kml

fOZkmlHeader <- function(folderName, OzSPDF) {
  headers <- c("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
  headers <- append(headers, "<kml xmlns=\"http://www.opengis.net/kml/2.2\">")
  headers <- append(headers, "<Document>")
  headers <- append(headers, paste('<Schema name="', 'OzTrack', '" id="OzTrackPolygons">', sep=''))
  for (fieldName in names(as(OzSPDF, "data.frame"))) {
    rClassName <- class(as(OzSPDF, "data.frame")[[fieldName]])
    if ((rClassName == 'numeric') || (rClassName == 'double')) {
      kmlFieldType <- 'double'
    }
    else if (rClassName == 'float') {
      kmlFieldType <- 'float'
    }
    else if (rClassName == 'integer') {
      kmlFieldType <- 'int'
    }
    else if (rClassName == 'logical') {
      kmlFieldType <- 'bool'
    }
    else {
      kmlFieldType <- 'string'
    }
    headers <- append(headers, paste('<SimpleField name="', fieldName, '" type="', kmlFieldType, '"/>', sep=''))
  }
  headers <- append(headers, "</Schema>")
  headers <- append(headers, "<Folder>")
  headers <- append(headers, paste('<name>',folderName,'</name>',sep = ""))
}

fOZkmlFooter <- function() {
  c("</Folder>",
    "</Document>",
    "</kml>")
}

fOZkmlPlacemark <- function (obj, name, visibility=TRUE, id, fieldNames, fieldValues) {
  if (class(obj) != "Polygons" && class(obj) != "SpatialPolygonsDataFrame") {
    stop("obj must be of class 'Polygons' or 'SpatialPolygonsDataFrame' [package 'sp']")
  } 
  if (class(obj) == "SpatialPolygonsDataFrame") {
    if (length(obj@polygons) > 1L) {
      warning(paste("Only the first Polygons object with the ID '", 
          obj@polygons[[1]]@ID, "' is taken from 'obj'", 
          sep = ""))
    }
    var3 <- names(obj)[3]
    obj <- obj@polygons[[1]]
  }
  kml <- ""
  kml <- append(kml, "<Placemark>")
  kml <- append(kml, paste("<name>", name, "</name>", sep = ""))
  kml <- append(kml, paste("<visibility>", as.integer(visibility),"</visibility>", sep=""))
  kml <- append(kml, paste('<ExtendedData>'))
  kml <- append(kml, '<SchemaData schemaUrl="#OzTrackPolygons">')
  for (i in 1:length(fieldNames)) {
    if (class(fieldValues[i]) == 'logical') {
      kmlFieldValue <- ifelse(fieldValues[i], 1, 0)
    }
    else {
      kmlFieldValue <- fieldValues[i]
    }
    kml <- append(kml, paste('<SimpleData name="', fieldNames[i], '">', as.character(kmlFieldValue), '</SimpleData>',sep=""))
  }
  kml <- append(kml, "</SchemaData>")
  kml <- append(kml, "</ExtendedData>")
  kml <- append(kml, "<MultiGeometry>")
  holeFlag <- FALSE
  for (i in 1:length(obj@Polygons)) {
    if (!holeFlag) 
      kml <- append(kml, "<Polygon>")
    kml <- append(kml, ifelse(obj@Polygons[[i]]@hole, "<innerBoundaryIs>", "<outerBoundaryIs>"))
    kml <- append(kml, c("<LinearRing>", "<coordinates>"))
    kml <- append(kml, paste(coordinates(obj@Polygons[[i]])[,1], coordinates(obj@Polygons[[i]])[, 2], sep = ","))
    kml <- append(kml, c("</coordinates>", "</LinearRing>"))
    kml <- append(kml, ifelse(obj@Polygons[[i]]@hole, "</innerBoundaryIs>","</outerBoundaryIs>"))
    holeFlag <- ifelse((i + 1L) <= length(obj@Polygons), obj@Polygons[[i + 1L]]@hole, FALSE)
    if (!holeFlag) 
      kml <- append(kml, "</Polygon>")
  }
  kml <- append(kml, "</MultiGeometry>")
  kml <- append(kml, "</Placemark>")
  kml
}

#  This function extends the fOZkmlPlacemark function to accept a SpatialPolygonsDataFrame object, 
#  Labels are based on attributes in the dataframe of the SpatialPolygonsDataFrame object
fOZkmlPolygons <- function(OzSPDF, kmlFileName, folderName) {
  kmlFile <- file(kmlFileName, "w")
  kmlPlacemarks <- sapply(1:nrow(OzSPDF), function(x) {
      fOZkmlPlacemark(OzSPDF[x,],
        name=as.character(as(OzSPDF, "data.frame")[x,'id']), 
        visibility=TRUE,
        fieldNames=names(as(OzSPDF, "data.frame")),
        fieldValues=as(OzSPDF, "data.frame")[x,]
      )
    })
  
  cat(fOZkmlHeader(folderName=folderName, OzSPDF=OzSPDF), file=kmlFile, sep="\n")
  cat(unlist(kmlPlacemarks), file=kmlFile, sep="\n")
  cat(fOZkmlFooter(), file=kmlFile, sep="\n")
  close(kmlFile)
}
