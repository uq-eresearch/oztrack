## Code written by Ross Dwyer on the 02.07.2013
## Code is a collection of functions to read and write results from Kalman Filter as a kml object

# Function to reorganise data into coprrect format and run the kftrack kalman filter 
fozkalmankfsst <- function(
  sinputfile, is.AM=TRUE,
  startdate=NULL, startX=NULL, startY=NULL,
  enddate=NULL, endX=NULL, endY=NULL,
  u.active=TRUE,
  v.active=TRUE,
  D.active=TRUE,
  bx.active=TRUE,
  by.active=TRUE,
  sx.active=TRUE,
  sy.active=TRUE,
  a0.active=TRUE,
  b0.active=TRUE,
  bsst.active=TRUE,
  ssst.active=TRUE,
  r.active=FALSE,
  u.init=0,
  v.init=0,
  D.init=100,
  bx.init=0,
  by.init=0,
  sx.init=0.1,
  sy.init=1,
  a0.init=0.001,
  b0.init=0,
  bsst.init=0,
  ssst.init=0.1,
  r.init=200
) {
  #sinputfile=sstlocs; is.AM=TRUE;
  #startdate=NULL; startX=NULL; startY=NULL;
  #enddate=NULL; endX=NULL; endY=NULL;
  #u.active=TRUE;
  #v.active=TRUE;
  #D.active=TRUE;
  #bx.active=FALSE;
  #by.active=TRUE;
  #sx.active=TRUE;
  #sy.active=TRUE;
  #a0.active=TRUE;
  #b0.active=TRUE;
  #bsst.active=FALSE;
  #ssst.active=TRUE;
  #r.active=FALSE;
  #u.init=0;
  #v.init=0;
  #D.init=100;
  #bx.init=0;
  #by.init=0;
  #sx.init=0.1;
  #sy.init=1;
  #a0.init=0.001;
  #b0.init=0;
  #bsst.init=0;
  #ssst.init=0.1;
  #r.init=200

  if(length(unique(c(is.null(startdate), is.null(startX), is.null(startY)))) != 1){
    stop('All or none of start date, longitude, and latitude must be entered.')
  }
  if(length(unique(c(is.null(enddate), is.null(endX), is.null(endY)))) != 1){
    stop('All or none of end date, longitude, and latitude must be entered.')
  }
  
  trackdata <- sinputfile
  if(!is.null(startdate) && !is.null(startX) && !is.null(startY)){
    trackdata <- subset(trackdata,trackdata$Date > as.POSIXlt(startdate))
    tagattach <- data.frame(ID=trackdata[1,1],Date=as.POSIXlt(startdate),X=startX,Y=startY,sst=NA)
    trackdata <- rbind(tagattach,trackdata)
  }
  if(!is.null(enddate) && !is.null(endX) && !is.null(endY)){
    trackdata <- subset(trackdata,trackdata$Date < as.POSIXlt(enddate))
    tagremove <- data.frame(ID=trackdata[1,1],Date=as.POSIXlt(enddate),X=endX,Y=endY,sst=NA)
    trackdata <- rbind(trackdata,tagremove)
  }
  
  # Remove duplicates and ensure data in the correct order
  trackdata <- trackdata[!duplicated(order(trackdata$Date)),]
  
  lati <- trackdata$Y
  long <- trackdata$X  
  # If data crosses 180th meridian change longitude so 0 - 360
  if(is.AM==TRUE)  long <- ifelse(long<0,long+360,long)
  
  # Ensure dates in correct decimal
  Datetime <- trackdata$Date
  
  day <- as.numeric(strftime(Datetime, format="%d")) 
  dhour <- sapply(strsplit(substr(Datetime,12,19),":"),
                  function(x) {
                    x <- as.numeric(x)
                    x[1]/24+x[2]/(24*60)+x[3]/(24*60*60)})
  #day <- day+dhour
  
  # to prevent step size being too small, remove anything greater than 1 dp
  day <- floor(day*10)/10
  month <- as.numeric(strftime(Datetime,"%m"))
  year <- as.numeric(strftime(Datetime,"%Y"))
  sst <-  trackdata$sst
  
  track <- data.frame(day,month,year,long,lati,sst)
  dups <- duplicated(data.frame(track$year,track$month,track$day))
  track <- track[!dups,]
  
  #Obtain a corresponding SST-field
  sst.path <- try({get.sst.from.server(track)})
  
  # Error handling
  if (class(sst.path) == 'try-error')
    stop('Failed to get sst from server. Try removing any extreme outliers and re-running the kalman filter.')
  
  # Run the  Unscented Kalman filter (+sst)
  kfm <- try({
    kfsst(
      data=track,
      fix.first=TRUE,
      fix.last=TRUE,
      u.active=u.active,
      v.active=v.active,
      D.active=D.active,
      bx.active=bx.active,
      by.active=by.active,
      sx.active=sx.active,
      sy.active=sy.active,
      a0.active=a0.active,
      b0.active=b0.active,
      bsst.active=bsst.active,
      ssst.active=ssst.active,
      r.active=r.active,
      u.init=u.init,
      v.init=v.init,
      D.init=D.init,
      bx.init=bx.init,
      by.init=by.init,
      sx.init=sx.init,
      sy.init=sy.init,
      a0.init=a0.init ,
      b0.init=b0.init,
      bsst.init=bsst.init,
      ssst.init=ssst.init,
      r.init=r.init
    )
  },silent=TRUE)
  
  # Error handling
  if (class(kfm) == 'try-error') 
  {
    if (is.null(startdate) || is.null(enddate))
      stop('Kalman filter failed to work using these parameters. Try adding a true start and end date and location.')
    else
      stop('Kalman filter failed to work using these parameters. In "Advanced parameters", try simplifying the model (e.g. bx.active=FALSE, by.active=FALSE, bsst.active=FALSE) or provide better initial values (e.g. D.i=500).')
  }else{
    # Combine Datetime data to Object
    kfm$Datetime <- Datetime[!dups]
    return(kfm)
  }
  
  
}

#############################################################


### This function generates a kml from kftrack

"%+%" <- function(s1, s2) paste(s1, s2, sep = "")

fkfsstkmlHeader <- function(fit) { 
  headers <- c("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
  headers <- append(headers, "<kml xmlns=\"http://www.opengis.net/kml/2.2\">")
  headers <- append(headers, "<Document>")
  headers <- append(headers, "<Schema name=\"KalmanFilter\" id=\"KalmanFilter\">")
  headers <- append(headers, "<SimpleField name=\"Negativeloglik\" type=\"float\"/>")
  headers <- append(headers, "<SimpleField name=\"MaxGradComp\" type=\"float\"/>")
  headers <- append(headers, "<SimpleField name=\"uValue\" type=\"float\"/>")
  headers <- append(headers, "<SimpleField name=\"uStdDev\" type=\"float\"/>")
  headers <- append(headers, "<SimpleField name=\"vValue\" type=\"float\"/>")
  headers <- append(headers, "<SimpleField name=\"vStdDev\" type=\"float\"/>")
  headers <- append(headers, "<SimpleField name=\"DValue\" type=\"float\"/>")
  headers <- append(headers, "<SimpleField name=\"DStdDev\" type=\"float\"/>")
  headers <- append(headers, "<SimpleField name=\"bxValue\" type=\"float\"/>")
  headers <- append(headers, "<SimpleField name=\"bxStdDev\" type=\"float\"/>")
  headers <- append(headers, "<SimpleField name=\"byValue\" type=\"float\"/>")
  headers <- append(headers, "<SimpleField name=\"byStdDev\" type=\"float\"/>")
  headers <- append(headers, "<SimpleField name=\"bsstValue\" type=\"float\"/>")
  headers <- append(headers, "<SimpleField name=\"bsstStdDev\" type=\"float\"/>")  
  headers <- append(headers, "<SimpleField name=\"sxValue\" type=\"float\"/>")
  headers <- append(headers, "<SimpleField name=\"sxStdDev\" type=\"float\"/>")
  headers <- append(headers, "<SimpleField name=\"syValue\" type=\"float\"/>")
  headers <- append(headers, "<SimpleField name=\"syStdDev\" type=\"float\"/>")
  headers <- append(headers, "<SimpleField name=\"ssstValue\" type=\"float\"/>")
  headers <- append(headers, "<SimpleField name=\"ssstStdDev\" type=\"float\"/>")
  headers <- append(headers, "<SimpleField name=\"radiusValue\" type=\"float\"/>")
  headers <- append(headers, "<SimpleField name=\"radiusStdDev\" type=\"float\"/>")
  headers <- append(headers, "<SimpleField name=\"a0Value\" type=\"float\"/>") 
  headers <- append(headers, "<SimpleField name=\"a0StdDev\" type=\"float\"/>")
  headers <- append(headers, "<SimpleField name=\"b0Value\" type=\"float\"/>")
  headers <- append(headers, "<SimpleField name=\"b0StdDev\" type=\"float\"/>")
  headers <- append(headers, "</Schema>")
  headers <- append(headers, "<ExtendedData>")
  headers <- append(headers, "<SchemaData schemaUrl=\"#KalmanFilter\">")
  headers <- append(headers, "<SimpleData name=\"Negativeloglik\">" %+% na.omit(fit$nlogL) %+% "</SimpleData>")
  headers <- append(headers, "<SimpleData name=\"MaxGradComp\">" %+% na.omit(fit$max.grad.comp) %+% "</SimpleData>")
  headers <- append(headers, "<SimpleData name=\"uValue\">" %+% na.omit(fit$estimates[1]) %+% "</SimpleData>")
  headers <- append(headers, "<SimpleData name=\"uStdDev\">" %+% na.omit(fit$std.dev[1]) %+% "</SimpleData>")
  headers <- append(headers, "<SimpleData name=\"vValue\">" %+% na.omit(fit$estimates[2]) %+% "</SimpleData>")
  headers <- append(headers, "<SimpleData name=\"vStdDev\">" %+% na.omit(fit$std.dev[2]) %+% "</SimpleData>")
  headers <- append(headers, "<SimpleData name=\"DValue\">" %+% na.omit(fit$estimates[3]) %+% "</SimpleData>")
  headers <- append(headers, "<SimpleData name=\"DStdDev\">" %+% na.omit(fit$std.dev[3]) %+% "</SimpleData>")
  headers <- append(headers, "<SimpleData name=\"bxValue\">" %+% na.omit(fit$estimates[4]) %+% "</SimpleData>")
  headers <- append(headers, "<SimpleData name=\"bxStdDev\">" %+% na.omit(fit$std.dev[4]) %+% "</SimpleData>")
  headers <- append(headers, "<SimpleData name=\"byValue\">" %+% na.omit(fit$estimates[5]) %+% "</SimpleData>")
  headers <- append(headers, "<SimpleData name=\"byStdDev\">" %+% na.omit(fit$std.dev[5]) %+% "</SimpleData>")
  headers <- append(headers, "<SimpleData name=\"bsstValue\">" %+% na.omit(fit$estimates[6]) %+% "</SimpleData>")
  headers <- append(headers, "<SimpleData name=\"bsstStdDev\">" %+% na.omit(fit$std.dev[6]) %+% "</SimpleData>")
  headers <- append(headers, "<SimpleData name=\"sxValue\">" %+% na.omit(fit$estimates[7]) %+% "</SimpleData>")
  headers <- append(headers, "<SimpleData name=\"sxStdDev\">" %+% na.omit(fit$std.dev[7]) %+% "</SimpleData>")
  headers <- append(headers, "<SimpleData name=\"syValue\">" %+% na.omit(fit$estimates[8]) %+% "</SimpleData>")
  headers <- append(headers, "<SimpleData name=\"syStdDev\">" %+% na.omit(fit$std.dev[8]) %+% "</SimpleData>")
  headers <- append(headers, "<SimpleData name=\"ssstValue\">" %+% na.omit(fit$estimates[9]) %+% "</SimpleData>")
  headers <- append(headers, "<SimpleData name=\"ssstStdDev\">" %+% na.omit(fit$std.dev[9]) %+% "</SimpleData>")  
  headers <- append(headers, "<SimpleData name=\"radiusValue\">" %+% na.omit(fit$estimates[10]) %+% "</SimpleData>")
  headers <- append(headers, "<SimpleData name=\"radiusStdDev\">" %+% na.omit(fit$std.dev[10]) %+% "</SimpleData>")
  headers <- append(headers, "<SimpleData name=\"a0Value\">" %+% na.omit(fit$estimates[11]) %+% "</SimpleData>")
  headers <- append(headers, "<SimpleData name=\"a0StdDev\">" %+% na.omit(fit$std.dev[11]) %+% "</SimpleData>")
  headers <- append(headers, "<SimpleData name=\"b0Value\">" %+% na.omit(fit$estimates[12]) %+% "</SimpleData>")
  headers <- append(headers, "<SimpleData name=\"b0StdDev\">" %+% na.omit(fit$std.dev[12]) %+% "</SimpleData>")
  headers <- append(headers, "</SchemaData>")
  headers <- append(headers, "</ExtendedData>")
  headers <- append(headers, "<Schema name=\"KalmanFilterPoint\" id=\"KalmanFilterPoint\">")
  headers <- append(headers, "<SimpleField name=\"varLon\" type=\"float\"/>")
  headers <- append(headers, "<SimpleField name=\"varLat\" type=\"float\"/>")
  headers <- append(headers, "<SimpleField name=\"sst_o\" type=\"float\"/>")
  headers <- append(headers, "<SimpleField name=\"sst_p\" type=\"float\"/>")  
  headers <- append(headers, "<SimpleField name=\"sst_smooth\" type=\"float\"/>")  
  headers <- append(headers, "</Schema>")
  return(headers)
}

fkfsstkmlFooter <- c("</Document>","</kml>")

fkfsstkmlPlacemark <- function(fit, datetime) {
  kml <- ""
  for (i in 1:nrow(fit$most.prob.track)) {
    kml <- append(kml, "<Placemark>")
    kml <- append(kml,"<TimeStamp>")
    datestr <- substr(datetime[i],1,10)
    timestr <- substr(datetime[i],12,19)
    if (timestr == '') {
      timestr <- '00:00:00'
    }
    kml <- append(kml,"<when>" %+% datestr %+% "T" %+% timestr %+% "Z" %+% "</when>")
    kml <- append(kml,"</TimeStamp>")
    kml <- append(kml,"<ExtendedData>")
    kml <- append(kml,"<SchemaData schemaUrl=\"#KalmanFilterPoint\">")
    kml <- append(kml,"<SimpleData name=\"varLon\">" %+% fit$var.most.prob.track[i,1] %+% "</SimpleData>")
    kml <- append(kml,"<SimpleData name=\"varLat\">" %+% fit$var.most.prob.track[i,4] %+% "</SimpleData>")
    kml <- append(kml,"<SimpleData name=\"sst_o\">" %+% fit$SST[i,1] %+% "</SimpleData>")
    kml <- append(kml,"<SimpleData name=\"sst_p\">" %+% fit$SST[i,2] %+% "</SimpleData>")
    kml <- append(kml,"<SimpleData name=\"sst_smooth\">" %+% fit$SST[i,3] %+% "</SimpleData>")
    kml <- append(kml,"</SchemaData>")
    kml <- append(kml,"</ExtendedData>")
    kml <- append(kml,"<Point>")
    kml <- append(kml,"<altitudeMode>relativeToGround</altitudeMode>")
    kml <- append(kml,"<coordinates>" %+% fit$most.prob.track[i,1] %+% "," %+% fit$most.prob.track[i,2] %+% ",1"  %+% "</coordinates>")
    kml <- append(kml,"</Point>")
    kml <- append(kml,"</Placemark>")
  }
  return(kml)
}


#  This function extends the fOZkmlPlacemark function to accept a SpatialPolygonsDataFrame object, 
#  Labels are based on attributes in the dataframe of the SpatialPolygonsDataFrame object
fkfsstkml <- function(fit, datetime, kmlFileName) {
  
  #fit=kfm;datetime=Datetime;kmlFileName="new_kftraj_628.kml"
  
  fkfsstkmlPlacemarks <- fkfsstkmlPlacemark(fit, datetime)
  
  kmlFile <- file(kmlFileName, "w") 
  cat(fkfsstkmlHeader(fit), file=kmlFile, sep="\n")
  cat(fkfsstkmlPlacemarks, file=kmlFile, sep="\n")
  cat(fkfsstkmlFooter, file=kmlFile, sep="\n")
  close(kmlFile)
}

oztrack_kfsst <- function(
  sinputfile, is.AM=TRUE,
  startdate=NULL, startX=NULL, startY=NULL,
  enddate=NULL, endX=NULL, endY=NULL,
  u.active=TRUE,
  v.active=TRUE,
  D.active=TRUE,
  bx.active=TRUE,
  by.active=TRUE,
  sx.active=TRUE,
  sy.active=TRUE,
  a0.active=TRUE,
  b0.active=TRUE,
  bsst.active=TRUE,
  ssst.active=TRUE,
  r.active=FALSE,
  u.init=0,
  v.init=0,
  D.init=100,
  bx.init=0,
  by.init=0,
  sx.init=0.1,
  sy.init=1,
  a0.init=0.001,
  b0.init=0,
  bsst.init=0,
  ssst.init=0.1,
  r.init=200,
  kmlFileName
) {
  mykal <- fozkalmankfsst(
    sinputfile=sinputfile, is.AM=is.AM,
    startdate=startdate, startX=startX, startY=startY,
    enddate=enddate, endX=endX, endY=endY,
    u.active=u.active,
    v.active=v.active,
    D.active=D.active,
    bx.active=bx.active,
    by.active=by.active,
    sx.active=sx.active,
    sy.active=sy.active,
    a0.active=a0.active,
    b0.active=b0.active,
    bsst.active=bsst.active,
    ssst.active=ssst.active,
    r.active=r.active,
    u.init=u.init,
    v.init=v.init,
    D.init=D.init,
    bx.init=bx.init,
    by.init=by.init,
    sx.init=sx.init,
    sy.init=sy.init,
    a0.init=a0.init,
    b0.init=b0.init,
    bsst.init=bsst.init,
    ssst.init=ssst.init,
    r.init=r.init
  )
  if (class(mykal)=="kftrack") {
    fkfsstkml(fit=mykal, datetime=mykal$Datetime, kmlFileName=kmlFileName)
  }
  else {
    stop('Kalman filter failed to work using these parameters.')
  }
}
