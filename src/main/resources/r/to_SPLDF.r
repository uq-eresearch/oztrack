#Function converts alphahull object into sp object
ahull_to_SPLDF <- function(x,new.projection)
	{
	if(class(x) != 'ahull')
		stop('this function will only work with an `ahull` object')

	# convert alpha shape edges to a data.frame
	x.ah.df <- as.data.frame(x$arcs)

	# convert each arc to a line segment
	l.list <- list()
	for(i in 1:nrow(x.ah.df))
		{
		# extract row i
		row_i <- x.ah.df[i,]

		# extract elements for arc()
		v <- c(row_i$v.x, row_i$v.y)
		theta <- row_i$theta
		r <- row_i$r
		cc <- c(row_i$c1, row_i$c2)
		# from arc()
		angles <- anglesArc(v, theta)
		seqang <- seq(angles[1], angles[2], length = 100)
		x <- cc[1] + r * cos(seqang)
		y <- cc[2] + r * sin(seqang)

		# convert data frame to a line segment
		l.list[[i]] <- Line(cbind(x,y))
		}

	# promote to Lines class, then to SpatialLines class
 	l <- Lines(l.list, ID="1")

	# Convert to a spatial lines object with the pre-defined projection
	l.spl <- SpatialLines(list(l), proj4string=CRS(new.projection))

	# In order to export to OGR, promote to SpatialLinesDataFrame 
	l.spldf <- SpatialLinesDataFrame(l.spl, data=data.frame(id=1), match.ID=FALSE)

	return(l.spldf)
	}

