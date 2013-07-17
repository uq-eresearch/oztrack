#require(kftrack)

kftrack_RD <- function (data, fix.first = TRUE, fix.last = TRUE, 
                      theta.active = c(u.active,v.active, D.active, bx.active, by.active, sx.active, sy.active,a0.active, b0.active, vscale.active), 
                      theta.init = c(u.init,v.init, D.init, bx.init, by.init, sx.init, sy.init, a0.init,b0.init, vscale.init), 
                      u.active = TRUE, v.active = TRUE,D.active = TRUE, bx.active = TRUE, by.active = TRUE, sx.active = TRUE,sy.active = TRUE, a0.active = TRUE, b0.active = TRUE, vscale.active = TRUE,
                      u.init = 0, v.init = 0, D.init = 100, bx.init = 0, by.init = 0,sx.init = 0.5, sy.init = 1.5, a0.init = 0.001, b0.init = 0,
                      vscale.init = 1, var.struct = "solstice", dev.pen = 0, save.dir = NULL,admb.string = "")
{ 
  
  olddir <- getwd()
  dirname <- ifelse(is.null(save.dir), "_kftrack_temp_", save.dir)
  dir.create(dirname)
  setwd(dirname)
  if (!is.null(save.dir)) {
    mptfilename <- paste("mpt_", paste(as.numeric(c(theta.active[1:7],
                                                    var.struct == "solstice", var.struct == "daily")),
                                       collapse = ""), ".dat", sep = "")
    unlink(c(mptfilename, "kftrack.par", "kftrack.rep", "kftrack.std"))
  }
  header <- .generate.dat.file(data, "kftrack.dat", fix.first,
                               fix.last, theta.active, theta.init, var.struct = var.struct,
                               dev.pen = dev.pen)
  filename <- dir(paste(.path.package("kftrack"), "/admb",
                        sep = ""), pattern = "kf")
  if (.Platform$OS.type == "windows") {
    if (Sys.info()["release"] == "XP") {
      error.code <- system(paste(.path.package("kftrack"),
                                 "/admb/", filename, " ", admb.string, sep = ""))
    }else{# RD changed this section
      file.copy(paste(.path.package("kftrack"), "/admb/",
                      filename, sep = ""), getwd())
      error.code <- system(paste(filename, admb.string,
                                 sep = ""))
    }
  }else{# RD changed this else
    system(paste("cp ", .path.package("kftrack"), "/admb/",
                 filename, " ", filename, sep = ""))
    .sys("chmod +x kftrack")
    error.code <- system(paste("./", filename, " ", admb.string,
                               sep = ""))
  }
  kf.o <- .read.output(data, fix.first, fix.last, theta.active,
                       theta.init, var.struct = var.struct, dev.pen = dev.pen)
  kf.o$nobs <- nrow(data)
  kf.o$header <- header
  kf.o$fix.first <- fix.first
  kf.o$fix.last <- fix.last
  kf.o$theta.active <- theta.active
  kf.o$theta.init <- theta.init
  kf.o$var.struct <- var.struct
  kf.o$dev.pen <- dev.pen
  kf.o$call <- match.call()
  kf.o$data.name <- deparse(substitute(data))
  kf.o$error.code <- error.code
  setwd(olddir)
  if (is.null(save.dir)) {
    unlink(dirname, recursive = TRUE)
  }
  class(kf.o) <- "kftrack"
  return(kf.o)
}

.generate.dat.file<-function(data, file="kftrack.dat", fix.first=TRUE,
                             fix.last=TRUE,
                             theta.active=c(u.active, v.active, D.active, bx.active, by.active,
                                            sx.active, sy.active, a0.active, b0.active, vscale.active),
                             theta.init=c(u.init, v.init, D.init, bx.init, by.init, sx.init, sy.init, a0.init,
                                          b0.init, vscale.init), u.active=TRUE, v.active=TRUE, D.active=TRUE, bx.active=TRUE,
                             by.active=TRUE, sx.active=TRUE, sy.active=TRUE, a0.active=TRUE, b0.active=TRUE,
                             vscale.active=TRUE, u.init=0, v.init=0, D.init=100, bx.init=0, by.init=0, sx.init=.5,
                             sy.init=1.5, a0.init=0.001, b0.init=0, vscale.init=1, var.struct="solstice", dev.pen=0.0){
  
  "%+%"<-function(s1, s2)paste(s1, s2, sep="")
  tostr<-function(x)paste(as.numeric(x), collapse="\t")
  var.flags<-switch(var.struct, "uniform"=c(0,0,0,0), "solstice"=c(1,0,0,0),
                    "daily"=c(0,1,dev.pen,0), "specified"=c(0,0,0,1),
                    warning("No matching variance structure found"))
  
  header<-"#Auto generated data file from R-KFtrack \n#"%+%date()%+%"\n#\n"%+%
    "# Number of data points\n  "%+%
    tostr(nrow(data))%+%"\n#\n"%+%
    "# 1 if first point is true release position; 0 otherwise\n  "%+%
    tostr(fix.first)%+%"\n#\n"%+%
    "# 1 if last point is true recapture position; 0 otherwise\n  "%+%
    tostr(fix.last)%+%"\n#\n"%+%
    "# active parameters \n# u\tv\tD\tbx\tby\tsx\tsy\ta0\tb0\tvscale\n  "%+%
    tostr(theta.active)%+%"\n#\n"%+%
    "# initial values \n# u\tv\tD\tbx\tby\tsx\tsy\ta0\tb0\tvscale\n  "%+%
    tostr(theta.init)%+%"\n#\n"%+%
    "# latitude errors \n# cos\tdev\tdeviation penalty weight\tspecified flag\n  "%+%
    tostr(var.flags)%+%"\n#\n"%+%
    "# Positions \n# day\tmonth\tyear\tlong\tlati\tvlon\tvlat\tvlonlat (last three optional)\n  "
  cat(header, file=file)
  write.table(data, file=file, row.names=FALSE, col.names=FALSE,
              sep="\t", eol="\n  ", append=TRUE)
  cat("\n", file=file, append=TRUE)
  class(header)<-"kfhead"
  return(header)
}

.read.output<-function(data, fix.first=TRUE, fix.last=TRUE,
                       theta.active=c(u.active, v.active, D.active, bx.active, by.active,
                                      sx.active, sy.active, a0.active, b0.active, vscale.active),
                       theta.init=c(u.init, v.init, D.init, bx.init, by.init, sx.init, sy.init, a0.init,
                                    b0.init, vscale.init), u.active=TRUE, v.active=TRUE, D.active=TRUE, bx.active=TRUE,
                       by.active=TRUE, sx.active=TRUE, sy.active=TRUE, a0.active=TRUE, b0.active=TRUE,
                       vscale.active=TRUE, u.init=0, v.init=0, D.init=100, bx.init=0, by.init=0, sx.init=.5,
                       sy.init=1.5, a0.init=0.001, b0.init=0, vscale.init=1, var.struct="solstice", dev.pen=0.0){
  
  getpar<-function(what, file){
    txt<-readLines(file)
    return(as.numeric(strsplit(txt[grep(what, txt)+1], split=" ")[[1]]))
  }
  getrep<-function(what, file){
    txt<-readLines(file)
    return(as.numeric(strsplit(txt[grep(what, txt)], split=" ")[[1]][3]))
  }
  
  
  theta.names<-c("u","v","D","bx","by","sx","sy")
  if(var.struct=="solstice"){theta.names<-c(theta.names, "a0", "b0")}
  if(var.struct=="specified"){theta.names<-c(theta.names, "vscale")}
  
  if(file.access("kftrack.par")!=0){
    warning("File \"kftrack.par\" not found (possibly because there was no solution to minimization problem)", call.=FALSE)
    npar<-NA; nlogL<-NA; max.grad.comp<-NA; estimates<-NA
  }else{
    tmp<-as.numeric(scan("kftrack.par", what="character", 16, quiet=TRUE)[c(6,11,16)])
    npar<-tmp[1]; nlogL<-tmp[2]; max.grad.comp<-tmp[3]
    estimates<-sapply(c("uu","vv","D","bx","by","vx","vy"), getpar, file="kftrack.par")
    if(var.struct=="solstice"){estimates<-c(estimates,sapply(c("a0", "b0"), getpar, file="kftrack.par"))}
    if(var.struct=="specified"){estimates<-c(estimates,sapply(c("vscale"), getpar, file="kftrack.par"))}
    names(estimates)<-theta.names
  }
  
  if(file.access("kftrack.rep")!=0){
    warning("File \"kftrack.rep\" not found", call.=FALSE)
    spd<-NA; hdg<-NA
  }else{
    spd<-getrep("spd", "kftrack.rep")
    hdg<-getrep("hdg", "kftrack.rep")
  }
  
  if(file.access("kftrack.std")!=0){
    warning("File \"kftrack.std\" not found (possibly the hessian was not estimated)", call.=FALSE)
    std.dev<-NA
  }else{
    dat<-read.table("kftrack.std", skip=1)
    tmp<-dat[dat[,2]%in%c("sduu","sdvv","sdD","sdbx","sdby","sdvx","sdvy"), 3:4]
    if(var.struct=="solstice"){
      if(theta.active[8]){tmp<-rbind(tmp,dat[dat[,2]=="a0",3:4])}else{tmp<-rbind(tmp,c(0, 0))}
      if(theta.active[9]){tmp<-rbind(tmp,dat[dat[,2]=="b0",3:4])}else{tmp<-rbind(tmp,c(0, 0))}
    }
    if(var.struct=="specified"){
      if(theta.active[10]){tmp<-rbind(tmp,dat[dat[,2]=="vscale",3:4])}else{tmp<-rbind(tmp,c(1, 0))}
    }
    std.dev<-tmp[,2]
    names(std.dev)<-paste("sd.",theta.names, sep="")
  }
  ta<-theta.active[1:7]
  if(var.struct=="specified"){
    ta[6:7]<-0
    estimates<-estimates[!names(estimates)%in%c('sx','sy')]
    std.dev<-std.dev[!names(std.dev)%in%c('sd.sx','sd.sy')]
  }
  mptfilename<-"mpt.dat"
  #mptfilename<-paste("mpt_", paste(as.numeric(
  #  c(ta,var.struct=="solstice",var.struct=="daily")), collapse=""),".dat",sep="")
  if(file.access(mptfilename)!=0){warning("File not found")}else{
    tmp<-read.table(mptfilename,skip=3, header=FALSE)
    name<-strsplit(readLines(mptfilename, 3)[3], split=" ")[[1]]
    colnames(tmp)<-name[!name%in%c("","#")]
    
    nominal.track<-cbind(x=tmp$ox, y=tmp$oy)
    pred.track<-cbind(x=tmp$px,y=tmp$py)
    most.prob.track<-cbind(x=tmp$smoothX, y=tmp$smoothY)
    days.at.liberty<-cumsum(tmp$dt)
    date<-matrix(as.numeric(unlist(strsplit(as.character(tmp$date), "/"))), ncol=3, byrow=TRUE)
    colnames(date)<-c("year", "month", "day")
    var.most.prob.track<-cbind(tmp$Psmooth11,tmp$Psmooth12,tmp$Psmooth21,tmp$Psmooth22)
    
  }
  
  return(list(npar=npar, nlogL=nlogL, max.grad.comp=max.grad.comp, estimates=estimates,
              std.dev=std.dev, nominal.track=nominal.track, pred.track=pred.track,
              most.prob.track=most.prob.track, var.most.prob.track=var.most.prob.track,
              days.at.liberty=days.at.liberty, date=date, spd=spd, hdg=hdg))
}

#######################################################################################

.getCI<-function (x, level = 0.95, npoints = 100)
{
  t.quan <- sqrt(qchisq(level, 2))
  centre <- x[5:6]
  x <- matrix(x[1:4], 2, 2)
  r <- x[1, 2]
  scale <- sqrt(diag(x))
  if (scale[1] > 0) {
    r <- r/scale[1]
  }
  if (scale[2] > 0) {
    r <- r/scale[2]
  }
  r <- min(max(r, -1), 1)
  d <- acos(r)
  a <- seq(0, 2 * pi, len = npoints)
  matrix(c(t.quan * scale[1] * cos(a + d/2) + centre[1],
           t.quan * scale[2] * cos(a - d/2) + centre[2]), npoints,
         2)
}
