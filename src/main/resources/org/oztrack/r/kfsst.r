## Code written by Ross Dwyer on the 02.07.2013
## Code is a collection of functions to read and write results from Kalman Filter as a kml object

#setwd("C:/Users/rdwye/SkyDrive/Australia/OzTrack")

# Function to reorganise data into coprrect format and run the kftrack kalman filter 
fozkalmankfsst <- function(sinputfile,is.AM=TRUE,
                        startdate=NULL,startX=NULL,startY=NULL,
                        enddate=NULL,endX=NULL,endY=NULL,
                        u.active = TRUE, v.active = TRUE, D.active = TRUE, bx.active = TRUE, by.active = TRUE, sx.active = TRUE, sy.active = TRUE, a0.active = TRUE, b0.active = TRUE, bsst.active = TRUE, ssst.active = TRUE, r.active = TRUE,
                        u.init = 0, v1.init = 0, D.init = 100, bx.init = 0, by.init = 0, sx.init = 0.5, sy.init = 1.5, a0.init = 0.001, b0.init = 0, bsst.init = 0, ssst.init = 0.1, r.init = 200)
{
  
  #sinputfile=positionFix;
  #startdate='2010-12-18';startX=174.436;startY=-36.89;
  #enddate='2010-12-29';endX=174.436;endY=-36.89;
  #u1.active = TRUE; v1.active = TRUE;D1.active = TRUE; bx1.active = FALSE; by1.active = FALSE; sx1.active = TRUE;sy1.active = TRUE; a01.active = TRUE; b01.active = TRUE; vscale1.active = TRUE;
  #u1.init = 0; v1.init = 0; D1.init = 100; bx1.init = 0; by1.init = 0;sx1.init = 0.5; sy1.init = 1.5; a01.init = 0.001; b01.init = 0
  
  
  # if the user has specified that there is a start date and an end date
  if(is.null(startdate)==FALSE){
    sinputfile <- subset(sinputfile,sinputfile$Date > as.POSIXlt(startdate) & sinputfile$Date < as.POSIXlt(enddate))
    trackdata <- sinputfile
    
    tagattach <- data.frame(ID=trackdata[1,1],Date=as.POSIXlt(startdate),X=startX,Y=startY)
    tagremove <- data.frame(ID=trackdata[1,1],Date=as.POSIXlt(enddate),X=endX,Y=endY)
    trackdata <- rbind(tagattach,trackdata,tagremove)
  }else{
    trackdata <- sinputfile
  }
  
  # Remove duplicates and ensure data in the correct order
  trackdata <- trackdata[!duplicated(order(trackdata$Date)),]
  
  Lat <- trackdata$Y
  Long <- trackdata$X  
  # If data crosses 180th meridian change longitude so 0 - 360
  if(is.AM==TRUE)  Long <- ifelse(Long<0,Long+360,Long)

  # Ensure dates in correct decimal
  Datetime <- trackdata$Date
  
  day <- as.numeric(strftime(Datetime, format="%d")) 
  dhour <- sapply(strsplit(substr(Datetime,12,19),":"),
                  function(x) {
                    x <- as.numeric(x)
                    x[1]/24+x[2]/(24*60)+x[3]/(24*60*60)})
  day <- day+dhour
  
  # to prevent step size being too small, remove anything greater than 1 dp
  day <- floor(day*10)/10 
  month <- as.numeric(strftime(Datetime,"%m"))
  year <- as.numeric(strftime(Datetime,"%Y"))  
  sst <-  trackdata$sst
  
  track <- data.frame(day,month,year,Long,Lat,sst)
  
  dups <- duplicated(track$year,track$month,track$day)
  
  track <- track[!dups,]
  
  #plot(track$long,track$lati,type='b')
  
  # For small datasets
  setwd("C:/Users/rdwye/SkyDrive/Australia/OzTrack")
  
  kfm <- try({
    kfsst(data = track, fix.first = TRUE, fix.last = TRUE, 
               u.active, v.active = v.active, D.active = D.active, bx.active = bx.active, by.active = by.active, sx.active = sx.active, sy.active = sy.active, a0.active = a0.active, b0.active = b0.active, bsst.active = bsst.active, ssst.active = ssst.active, r.active = r.active,
               u.init = u.init, v.init = v.init, D.init = D.init, bx.init = bx.init, by.init = by.init, sx.init = sx.init, sy.init = sy.init, a0.init = a0.init , b0.init = b0.init, bsst.init = bsst.init, ssst.init = ssst.init, r.init = r.init)
    },silent=TRUE)
  
  # Error handling
  if (class(kfm) == 'try-error') 
  {
    stop('Kalman filter did not work using these parameters.')
  }else{
    
    # Combine Datetime data to Object
    kfm$Datetime <- Datetime[!dups]}
  
  return(kfm)
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
    kml <- append(kml,"<when>" %+% substr(datetime[i],1,10) %+% "T" %+% substr(datetime[i],12,19) %+% "Z" %+% "</when>")
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


