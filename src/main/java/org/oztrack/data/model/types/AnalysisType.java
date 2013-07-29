package org.oztrack.data.model.types;

import java.util.Arrays;
import java.util.List;

public enum AnalysisType {
    MCP(
        "Minimum Convex Polygon",
        "<p>Otherwise known as a convex hull, this approach uses the smallest area " +
        "convex set that contains the location data (Worton 1998). This calculation " +
        "is undertaken within R using the adehabitatHR package (Calenge 2008). " +
        "OzTrack will return the MCP calculation in the Analysis Results window. " +
        "An image of the MCP will be produced for visualisation over the map image, " +
        "as well as a KML file for viewing in Google Earth.</p>\n" +
        "\n" +
        "<p style=\"font-weight: bold;\">References</p>\n" +
        "\n" +
        "<p>Calenge, C. (2006) The package adehabitat for the R software: a tool for " +
        "the analysis of space and habitat use by animals. Ecological Modelling, " +
        "197, 516-519</p>\n" +
        "\n" +
        "<p>Worton, B.J. (1995) A convex hull-based estimator of home-range size. " +
        "Biometrics, 51, 1206-1215.</p>",
        AnalysisResultType.HOME_RANGE,
        Arrays.asList(
            new AnalysisParameterType(
                "percent",
                "Percent",
                "<p>OzTrack offers the feature to strip the Minimum Convex Polygon (MCP) " +
                "to different levels based on percentage. At 100% the MCP will be the " +
                "equivalent to the area covered by all locations within the dataset. " +
                "Inserting a lower % value into the box will result in only this " +
                "percentage of locations being contained in the final MCP.</p>",
                "double",
                "%",
                "100",
                false,
                null
            )
        ),
        Arrays.<AnalysisResultAttributeType>asList(),
        Arrays.asList(
            new AnalysisResultAttributeType(
                "area",
                "Area",
                "double",
                "km<sup>2</sup>",
                3
            )
        )
    ),
    KUD(
        "Kernel Utilization Distribution",
        "<p>The fixed kernel density estimator is a non-parametric method of home-range " +
        "analysis, which uses the utilization distribution to estimate the probability " +
        "that an animal will be found at a specific geographical location. This fixed " +
        "method of kernel smoothing ignores the temporal sequence whereby locations " +
        "were obtained, and assumes that all locations from that individual are " +
        "spatially autocorrelated. This means that the location of an individual at a " +
        "particular point implies an increased probability that it frequents " +
        "neighbouring locations as well. The kernel UD accurately estimates areas of " +
        "high use by the tagged animal, providing that the level of smoothing is " +
        "appropriate.</p>\n" +
        "\n" +
        "<p>These calculations are undertaken within R using the adehabitatHR library " +
        "of functions (Calenge 2008).\n" +
        "\n" +
        "<p style=\"font-weight: bold;\">References</p>\n" +
        "\n" +
        "<p>Calenge, C. (2006) The package adehabitat for the R software: a tool for " +
        "the analysis of space and habitat use by animals. Ecological Modelling, " +
        "197: 516-519</p>\n" +
        "\n" +
        "<p>Seaman, D.E., Powell, R.A. (1996) An evaluation of the accuracy of kernel " +
        "density estimators for home range analysis. Ecology, 77: 2075-2085.</p>\n" +
        "\n" +
        "<p>Silverman, B.W. (1986) Density estimation for statistics and data analysis. " +
        "Chapman and Hall, London, UK</p>\n" +
        "\n" +
        "<p>Worton, B.J. (1989) Kernel methods for estimating the utilization " +
        "distribution in home-range studies. Ecology 70: 164-168</p>",
        AnalysisResultType.HOME_RANGE,
        Arrays.asList(
            new AnalysisParameterType(
                "percent",
                "Percent",
                "<p>The user can specify what volume contour they wish to extract from the " +
                "utilisation distribution. This contour represents the boundary of that " +
                "area which contains x% of the volume of the utilisation distribution. " +
                "Inserting a lower value into the box will extract areas of a higher " +
                "probability of usage. The 95% and 50% volume contour are those most " +
                "commonly adopted as the home-range and core-area UD, respectively.</p>",
                "double",
                "%",
                "95",
                false,
                null
            ),
            new AnalysisParameterType(
                "hEstimator",
                "h estimator",
                "<p>There are a number of different smoothing parameters that have been " +
                "adopted in kernel estimates, and no single parameter will perform " +
                "well in all conditions. OzTrack offers three options for selecting the " +
                "kernel smoothing parameter. Two of these are automatically generated " +
                "using either the ad hoc method for a bivariate normal kernel (reference " +
                "bandwidth = ‘href’), or the least-squares cross-validation (‘LSCV’) " +
                "algorithm. Note, the LSCV approach can take some time to compute the " +
                "parameter h and, in some cases, will fail to compute this h value. See " +
                "Seaman and Powell (1998) for more information.</p>\n" +
                "\n" +
                "<p><a href=\"http://cran.r-project.org/web/packages/adehabitatHR/adehabitatHR.pdf\">" +
                "http://cran.r-project.org/web/packages/adehabitatHR/adehabitatHR.pdf</a></p>",
                "string",
                null,
                "href",
                false,
                Arrays.asList(
                    new AnalysisParameterOption("href", "Ad hoc method (href)"),
                    new AnalysisParameterOption("LSCV", "Least-square cross validation (LSCV)"),
                    new AnalysisParameterOption(null, "None (enter h value) [advanced]")
                )
            ),
            new AnalysisParameterType(
                "hValue",
                "h value",
                "<p>OzTrack alternatively " +
                "allows users to a set numerical value for h (in meters). This value " +
                "depends on the scale and rate of the animal’s movement, and the " +
                "frequency of location sampling. Users can gain a feel for what value " +
                "of h to select by running the algorithm using the ‘href’ or ‘LSCV’ " +
                "smoothing algorithm and viewing the returned h value in the Analysis " +
                "Results. As a rule, as h decreases, the kernel will become less " +
                "continuous and more fragmented revealing increasing detail within " +
                "the home range. See the adehabitatHR package notes for more " +
                "information.</p>\n" +
                "\n" +
                "<p><a href=\"http://cran.r-project.org/web/packages/adehabitatHR/adehabitatHR.pdf\">" +
                "http://cran.r-project.org/web/packages/adehabitatHR/adehabitatHR.pdf</a></p>",
                "double",
                null,
                null,
                true,
                null
            ),
            new AnalysisParameterType(
                "gridSize",
                "Grid size",
                "<p>The utilisation distribution is estimated in each pixel of this " +
                "grid superimposed on the location fixes (detections) for each animal. " +
                "The Extent (see below) is split into a specified number of intervals " +
                "(= grid size). For example, setting this value to 100 will return a " +
                "100 x 100 grid. Default for this value = 60. Increasing this parameter " +
                "can help create a smoother kernel surface and identify relatively " +
                "compact areas of usage, however it will also increase processing time. " +
                "See the adehabitatHR package notes for more information.</p>\n" +
                "\n" +
                "<p><a href=\"http://cran.r-project.org/web/packages/adehabitatHR/adehabitatHR.pdf\">" +
                "http://cran.r-project.org/web/packages/adehabitatHR/adehabitatHR.pdf</a></p>",
                "double",
                null,
                "50",
                true,
                null
            ),
            new AnalysisParameterType(
                "extent",
                "Extent",
                "<p>This parameter controls the coverage of the grid (i.e. the polygon’s " +
                "bounding box), where the minimum X coordinate of the bounding box is Xmin " +
                "– Extent * difference between the minimum and maximum X coordinates of the " +
                "relocations. Conversely the maximum Y coordinate of the grid is Ymax + " +
                "Extent * difference between the minimum and maximum Y coordinates of the " +
                "relocations. The default for this value = 1. If your kernel polygon has a " +
                "flat edge, try increasing this parameter value. See the adehabitatHR " +
                "package notes for more information.</p>\n" +
                "\n" +
                "<p><a href=\"http://cran.r-project.org/web/packages/adehabitatHR/adehabitatHR.pdf\">" +
                "http://cran.r-project.org/web/packages/adehabitatHR/adehabitatHR.pdf</a></p>",
                "double",
                null,
                "1",
                true,
                null
            )
        ),
        Arrays.<AnalysisResultAttributeType>asList(),
        Arrays.asList(
            new AnalysisResultAttributeType(
                "area",
                "Area",
                "double",
                "km<sup>2</sup>",
                3
            ),
            new AnalysisResultAttributeType(
                "hval",
                "h value",
                "double",
                null,
                3
            )
        )
    ),
    KBB(
        "Kernel Brownian Bridge",
        "<p>The Kernel Brownian Bridge approach calculates the utilization distribution " +
        "to estimate the probability that an animal will be found at a specific geographical " +
        "location. Unlike the classical Fixed Kernel approach, the Kernel Brownian Bridge " +
        "incorporates serial autocorrelation between fixes (i.e. the time the animal took to " +
        "move between locations) into the calculation (Bullard 1992). Brownian Bridges, " +
        "therefore, only contribute to utilisation distributions when sequential locations " +
        "(i.e. XYn at timen and XYn+1 at timen+1) occur close to one another in time (Kie " +
        "et al. 2010).</p>\n" +
        "\n" +
        "<p>This function uses information on the animal’s trajectory, how long it took to " +
        "move between locations, how fast the animal moves on average (Sig1) and the " +
        "uncertainty around each location fix (Sig2) to control the degree of kernel " +
        "smoothing (Bullard 1992).</p>\n" +
        "\n" +
        "<p>These calculations are undertaken within R using the adehabitatHR library of " +
        "functions (Calenge 2008).</p>\n" +
        "\n" +
        "<p style=\"font-weight: bold;\">References</p>\n" +
        "\n" +
        "\n" +
        "<p>Bullard, F. (1991) Estimating the home range of an animal: a Brownian bridge " +
        "approach. Master of Science, University of North Carolina, Chapel Hill</p>\n" +
        "\n" +
        "<p>Calenge, C. (2006) The package adehabitat for the R software: a tool for the " +
        "analysis of space and habitat use by animals. Ecological Modelling, 197, 516-519</p>\n" +
        "\n" +
        "<p>Horne, J.S., Garton, E.O., Krone, S.M. and Lewis, J.S. (2007) Analyzing animal " +
        "movements using brownian bridge. Ecology, in press</p>\n" +
        "\n" +
        "<p>Kie J.G., Matthiopoulos J., Fieberg J., Powell R.A., Cagnacci F., Mitchell M.S., " +
        "Gaillard J.M., Moorcroft P.R. 2010. The home-range concept: are traditional " +
        "estimators still relevant with modern telemetry technology? Phil. Trans. R. Soc. " +
        "B 365, 2221–2231</p>",
        AnalysisResultType.HOME_RANGE,
        Arrays.asList(
            new AnalysisParameterType(
                "percent",
                "Percent",
                "<p>The user can specify what volume contour they wish to extract from the " +
                "utilisation distribution. This contour represents the boundary of that area " +
                "which contains x% of the volume of the utilisation distribution. Inserting a " +
                "lower value into the box will extract areas of a higher probability of usage. " +
                "The 95% and 50% volume contour are those most commonly adopted as the " +
                "home-range and core-area UD, respectively.</p>",
                "double",
                "%",
                "95",
                false,
                null
            ),
            new AnalysisParameterType(
                "sig1",
                "sig1",
                "<p>OzTrack offers two smoothing parameters for the Brownian Bridge method. " +
                "Sig1 is related to the speed of the animal, describing how far from the line " +
                "joining two successive locations the animal can go in one time step. This " +
                "can be entered manually or can be estimated within R using the liker function " +
                "in adehabitatHR library of functions (Calenge 2008). See the adehabitatHR " +
                "package notes for more information.</p>\n" +
                "\n" +
                "<p><a href=\"http://cran.r-project.org/web/packages/adehabitatHR/adehabitatHR.pdf\">" +
                "http://cran.r-project.org/web/packages/adehabitatHR/adehabitatHR.pdf</a></p>",
                "double",
                null,
                null,
                false,
            null),
            new AnalysisParameterType(
                "sig2",
                "sig2",
                "<p>The second smoothing parameter for the Brownian Bridge method is Sig2. " +
                "This is related to the imprecision of the animal locations, which is assumed " +
                "to be known from static trials, and is the equivalent of the h parameter in the " +
                "classical fixed kernel approach (Calenge 2008). See the adehabitatHR package " +
                "notes for more information.</p>\n" +
                "\n" +
                "<p><a href=\"http://cran.r-project.org/web/packages/adehabitatHR/adehabitatHR.pdf\">" +
                "http://cran.r-project.org/web/packages/adehabitatHR/adehabitatHR.pdf</a></p>",
                "double",
                null,
                null,
                false,
                null
            ),
            new AnalysisParameterType(
                "gridSize",
                "Grid size",
                "<p>The utilisation distribution is estimated in e ach pixel of this grid " +
                "superimposed on the location fixes (detections) for each animal. This parameter " +
                "controls the coverage of the grid, where the minimum X coordinate of the grid is " +
                "Xmin – extent * difference between the minimum and maximum X coordinates of the " +
                "relocations.  Conversely the maximum Y coordinate of the grid is Ymax + extent * " +
                "difference between the minimum and maximum Y coordinates of the relocations. The " +
                "default of extent = 1. If your kernel polygon has a flat edge, try increasing " +
                "this parameter value. See the adehabitatHR package notes for more information.</p>\n" +
                "\n" +
                "<p><a href=\"http://cran.r-project.org/web/packages/adehabitatHR/adehabitatHR.pdf\">" +
                "http://cran.r-project.org/web/packages/adehabitatHR/adehabitatHR.pdf</a></p>",
                "double",
                null,
                "50",
                true,
                null
            ),
            new AnalysisParameterType(
                "extent",
                "Extent",
                "<p>The utilisation distribution is estimated in each pixel of this grid " +
                "superimposed on the location fixes (detections) for each animal. The extents (see " +
                "above) are split into a specified number of intervals (= grid size). Increasing " +
                "this parameter can help create a smoother kernel surface and identify relatively " +
                "compact areas of usage, however it will also increase processing time. See the " +
                "adehabitatHR package notes for more information.</p>\n" +
                "\n" +
                "<p><a href=\"http://cran.r-project.org/web/packages/adehabitatHR/adehabitatHR.pdf\">" +
                "http://cran.r-project.org/web/packages/adehabitatHR/adehabitatHR.pdf</a></p>",
                "double",
                null,
                "1",
                true,
                null
            )
        ),
        Arrays.<AnalysisResultAttributeType>asList(),
        Arrays.asList(
            new AnalysisResultAttributeType(
                "area",
                "Area",
                "double",
                "km<sup>2</sup>",
                3
            )
        )
    ),
    AHULL(
        "Alpha Hull",
        "<p>The alpha hull home range estimation is a generalisation of the convex hull but " +
        "objectively crops low use areas from the polygon surface. Alpha hulls are generated " +
        "by connecting all locations as a Delauney triangulation, then systematically removing " +
        "vertices until only those vertices that are shorter in length than the chosen " +
        "parameter value alpha are retained.  The smaller the value of alpha, the finer the " +
        "resolution of the hull and the greater the exposure of non-use areas. As alpha " +
        "increases, the polygon surface will increase until it is equivalent to a 100% minimum " +
        "convex polygon.</p>\n" +
        "\n" +
        "<p>This calculation is undertaken within R using the alphahull package (Pateiro-Lopez " +
        "&amp; Rodriguez-Casal 2011).</p>\n" +
        "\n" +
        "<p style=\"font-weight: bold;\">References</p>\n" +
        "\n" +
        "<p>Burgman, M.A. &amp; Fox, J.C. (2003) Bias in species range estimates from minimum " +
        "convex polygons: implications for conservation and options for improved planning. " +
        "Animal Conservation, 6, 19-28.</p>",
        AnalysisResultType.HOME_RANGE,
        Arrays.asList(
            new AnalysisParameterType(
                "alpha",
                "Alpha",
                "<p>The smaller the value of alpha, the finer the resolution of the hull and " +
                "the greater the exposure of non-use areas. As alpha increases, the polygon " +
                "surface will increase until it is equivalent to a 100% minimum convex polygon.</p>",
                "double",
                null,
                "100",
                false,
                null
            )
        ),
        Arrays.<AnalysisResultAttributeType>asList(),
        Arrays.asList(
            new AnalysisResultAttributeType(
                "area",
                "Area",
                "double",
                "km<sup>2</sup>",
                3
            )
        )
    ),
    LOCOH(
        "Local Convex Hull",
        "<p>The Local Convex Hull (LoCoH) method estimates individual utilisation distributions " +
        "based on the local nearest-neighbour convex hulls. These are formed by constructing " +
        "convex hulls around each location in the animal’s trajectory then jointing these hulls " +
        "together, iteratively, to form isopleths (Getz 2007). This is a useful home-range " +
        "estimator when the movements of the animal have been constrained along hard edges such " +
        "as roads, fences and rivers.</p>\n" +
        "\n" +
        "<p>This calculation is undertaken within R using the LoCoH series of functions within " +
        "the adehabitatHR library of functions (Calenge 2008). Users may either fix the number " +
        "of nearest neighbours (k-1) to the root point (i.e. the fixed k-LoCoH), or fix the " +
        "maximum radius from root points when generating local hulls (i.e. the fixed r-LoCoH).</p>\n" +
        "\n" +
        "<p style=\"font-weight: bold;\">References</p>" +
        "\n" +
        "<p>Calenge, C. (2006) The package adehabitat for the R software: a tool for the analysis of space and habitat use by animals. Ecological Modelling, 197, 516-519</p>\n" +
        "\n" +
        "<p>Getz, W.M. & Wilmers, C.C. (2004). A local nearest-neighbor convex-hull construction of home ranges and utilization distributions. Ecography, 27, 489–505.</p>\n" +
        "\n" +
        "<p>Getz, W.M., Fortmann-Roe, S.B, Lyons, A., Ryan, S., Cross, P. (2007). LoCoH methods for the construction of home ranges and utilization distributions. PLoS ONE, 2: 1–11.</p>",
        AnalysisResultType.HOME_RANGE,
        Arrays.asList(
            new AnalysisParameterType(
                "percent",
                "Percent",
                "<p>For the Local Convex Hull, the lower the percent value for the home range " +
                "contour/isopleth, the greater the probability of finding the individual in " +
                "that region. The 100% isopleth contains all the locations, while the 50% " +
                "isopleth contains 50% of the locations.</p>",
                "double",
                "%",
                "95",
                false,
                null
            ),
            new AnalysisParameterType(
                "k",
                "Neighbours",
                "<p>By entering a value in this field, OzTrack runs the fixed k-LoCoH (function " +
                "LoCoH.k) contained within the adehabitatHR package in R. Here neighbours = the " +
                "number of neighbours to include (k) – 1 with which to construct the convex hulls. " +
                "See the adehabitatHR package notes for more information.<p>\n" +
                "\n" +
                "<p><a href=\"http://cran.r-project.org/web/packages/adehabitatHR/adehabitatHR.pdf\">" +
                "http://cran.r-project.org/web/packages/adehabitatHR/adehabitatHR.pdf</a></p>",
                "double",
                null,
                "30",
                false,
                null
            ),
            new AnalysisParameterType(
                "r",
                "Radius",
                "<p>By entering a value in this field, OzTrack runs the fixed r-LoCoH (function " +
                "LoCoH.r) contained within the adehabitatHR package in R. Here radius = the distance " +
                "(in meters) from the root point with which to include locations in convex hulls. " +
                "See the adehabitatHR package notes for more information.</p>\n" +
                "\n" +
                "<p><a href=\"http://cran.r-project.org/web/packages/adehabitatHR/adehabitatHR.pdf\">" +
                "http://cran.r-project.org/web/packages/adehabitatHR/adehabitatHR.pdf</a></p>",
                "double",
                "m",
                null,
                false,
                null
            )
        ),
        Arrays.<AnalysisResultAttributeType>asList(),
        Arrays.<AnalysisResultAttributeType>asList()
    ),
    HEATMAP_POINT(
        "Heat Map (Point Intensity)",
        "<p>This generates a grid over the study area and uses a coloured gradient to visually identify " +
        "areas of high usage by the tagged animal. These can be applied to either points or connectivity " +
        "lines between points. The size of the grid cells (in meters) can be specified. This OzTrack tool " +
        "utilises the spatstat package in R (Baddeley & Turner, 2005)</p>\n" +
        "\n" +
        "<p style=\"font-weight: bold;\">References</p>" +
        "\n" +
        "<p>Baddeley, A. & Turner, R. (2005) spatstat: An R package for analyzing spatial point patterns. " +
        "Journal of Statistical Software, 12,6</p>",
        AnalysisResultType.HEAT_MAP,
        buildHeatmapParameterTypes(),
        Arrays.<AnalysisResultAttributeType>asList(),
        Arrays.<AnalysisResultAttributeType>asList()
    ),
    HEATMAP_LINE(
        "Heat Map (Line Intensity)",
        "<p>This generates a grid over the study area and uses a coloured gradient to visually identify " +
        "areas of high usage by the tagged animal. These can be applied to either points or connectivity " +
        "lines between points. The size of the grid cells (in meters) can be specified. This OzTrack tool " +
        "utilises the spatstat package in R (Baddeley & Turner, 2005)</p>\n" +
        "\n" +
        "<p style=\"font-weight: bold;\">References</p>" +
        "\n" +
        "<p>Baddeley, A. & Turner, R. (2005) spatstat: An R package for analyzing spatial point patterns. " +
        "Journal of Statistical Software, 12,6</p>",
        AnalysisResultType.HEAT_MAP,
        buildHeatmapParameterTypes(),
        Arrays.<AnalysisResultAttributeType>asList(),
        Arrays.<AnalysisResultAttributeType>asList()
    ),
    KALMAN(
        "Kalman Filter",
        null,
        AnalysisResultType.FILTER,
        Arrays.asList(
            new AnalysisParameterType(
                "startDate",
                "Start date",
                null,
                "date",
                null,
                null,
                false,
                null
            ),
            new AnalysisParameterType(
                "startX",
                "Start longitude",
                null,
                "double",
                "°",
                null,
                false,
                null
            ),
            new AnalysisParameterType(
                "startY",
                "Start latitude",
                null,
                "double",
                "°",
                null,
                false,
                null
            ),
            new AnalysisParameterType(
                "endDate",
                "End date",
                null,
                "date",
                null,
                null,
                false,
                null
            ),
            new AnalysisParameterType(
                "endX",
                "End longitude",
                null,
                "double",
                "°",
                null,
                false,
                null
            ),
            new AnalysisParameterType(
                "endY",
                "End latitude",
                null,
                "double",
                "°",
                null,
                false,
                null
            ),
            new AnalysisParameterType(
                "uActive",
                "u.active",
                "<p>Whether <i>u</i> should be optimized; uncheck to fix <i>u</i> at <i>u.init</i> value below.</p>",
                "boolean",
                null,
                "true",
                true,
                null
            ),
            new AnalysisParameterType(
                "vActive",
                "v.active",
                "<p>Whether <i>v</i> should be optimized; uncheck to fix <i>v</i> at <i>v.init</i> value below.</p>",
                "boolean",
                null,
                "true",
                true,
                null
            ),
            new AnalysisParameterType(
                "DActive",
                "D.active",
                "<p>Whether <i>D</i> should be optimized; uncheck to fix <i>D</i> at <i>D.init</i> value below.</p>",
                "boolean",
                null,
                "true",
                true,
                null
            ),
            new AnalysisParameterType(
                "bxActive",
                "bx.active",
                "<p>Whether <i>b[x]</i> should be optimized; uncheck to fix <i>b[x]</i> at <i>bx.init</i> value below.</p>",
                "boolean",
                null,
                "true",
                true,
                null
            ),
            new AnalysisParameterType(
                "byActive",
                "by.active",
                "<p>Whether <i>b[y]</i> should be optimized; uncheck to fix <i>b[y]</i> at <i>by.init</i> value below.</p>",
                "boolean",
                null,
                "true",
                true,
                null
            ),
            new AnalysisParameterType(
                "sxActive",
                "sx.active",
                "<p>Whether <i>sigma[x]</i> should be optimized; uncheck to fix <i>sigma[x]</i> at <i>sx.init</i> value below.</p>",
                "boolean",
                null,
                "true",
                true,
                null
            ),
            new AnalysisParameterType(
                "syActive",
                "sy.active",
                "<p>Whether <i>sigma[y]</i> should be optimized; uncheck to fix <i>sigma[y]</i> at <i>sy.init</i> value below.</p>",
                "boolean",
                null,
                "true",
                true,
                null
            ),
            new AnalysisParameterType(
                "a0Active",
                "a0.active",
                "<p>\n" +
                "   If <i>var.struct</i>=\"solstice\",\n" +
                "   whether <i>a[0]</i> should be optimized; uncheck to fix <i>a[0]</i> at <i>a0.init</i> value below.\n" +
                "</p>",
                "boolean",
                null,
                "true",
                true,
                null
            ),
            new AnalysisParameterType(
                "b0Active",
                "b0.active",
                "<p>\n" +
                "   If <i>var.struct</i>=\"solstice\",\n" +
                "   Whether <i>b[0]</i> should be optimized; uncheck to fix <i>b[0]</i> at <i>b0.init</i> value below.\n" +
                "</p>",
                "boolean",
                null,
                "true",
                true,
                null
            ),
            new AnalysisParameterType(
                "vscaleActive",
                "vscale.active",
                "<p>Whether <i>vscale</i> should be optimized; uncheck to fix <i>vscale</i> at <i>vscale.init</i> value below.</p>",
                "boolean",
                null,
                "true",
                true,
                null
            ),
            new AnalysisParameterType(
                "uInit",
                "u.init",
                "<p>\"Advection\" component of movement; northward component of directed movement.</p>",
                "double",
                "nm/day",
                "0",
                true,
                null
            ),
            new AnalysisParameterType(
                "vInit",
                "v.init",
                "<p>\"Advection\" component of movement; eastward component of directed movement.</p>",
                "double",
                "nm/day",
                "0",
                true,
                null
            ),
            new AnalysisParameterType(
                "DInit",
                "D.init",
                "<p>\"Diffusion\" component of movement; a measure of the variability in movement.</p>",
                "double",
                "nm^2/day",
                "100",
                true,
                null
            ),
            new AnalysisParameterType(
                "bxInit",
                "bx.init",
                "<p>Systematic error (or bias) in the estimation of position (longitude).</p>",
                "double",
                "°",
                "0",
                true,
                null
            ),
            new AnalysisParameterType(
                "byInit",
                "by.init",
                "<p>Systematic error (or bias) in the estimation of position (latitude).</p>",
                "double",
                "°",
                "0",
                true,
                null
            ),
            new AnalysisParameterType(
                "sxInit",
                "sx.init",
                "<p>Random error in the estimation of position (longitude).</p>",
                "double",
                "°",
                "0.5",
                true,
                null
            ),
            new AnalysisParameterType(
                "syInit",
                "sy.init",
                "<p>Random error in the estimation of position (latitude).</p>",
                "double",
                "°",
                "1.5",
                true,
                null
            ),
            new AnalysisParameterType(
                "a0Init",
                "a0.init",
                "<p>\n" +
                "    Upper bound for latitude variance. Used in \"solstice\" variance structure;\n" +
                "    related to how the latitude estimation error varies around the equinox.\n" +
                "</p>",
                "double",
                "°",
                "0.001",
                true,
                null
            ),
            new AnalysisParameterType(
                "b0Init",
                "b0.init",
                "<p>\n" +
                "    The number of days prior to the equinox where the latitude error is maximal.\n" +
                "    Used in \"solstice\" variance structure; related to how the latitude estimation\n" +
                "    error varies around the equinox.\n" +
                "</p>",
                "double",
                "days",
                "0",
                true,
                null
            ),
            new AnalysisParameterType(
                "vscaleInit",
                "vscale.init",
                "<p>Initial value for the common scaling parameter for the specified covariance matrices.</p>",
                "double",
                null,
                "1",
                true,
                null
            ),
            new AnalysisParameterType(
                "varStruct",
                "var.struct",
                "<p>Sets the model for the latitude error: \"uniform\", \"specified\", \"solstice\", or \"daily\".</p>" +
                "<p>\n" +
                "    If \"uniform\", the same variance is assumed for all observations.\n" +
                "</p>\n" +
                "<p>\n" +
                "    If \"specified\", and <i>vscale.active</i> is checked,\n" +
                "    a common scaling parameter is estimated for the specified covariance matrices." +
                "</p>" +
                "<p>\n" +
                "    If \"solstice\", the variance is assumed to follow the model\n" +
                "    sigma[y[i]]^2 = sigma[y[0]]^2 / (cos^2(2pi(J[i]+(-1)^(s[i])b[0])/365.25)+a[0]),\n" +
                "    where J[i] is the number of days since last solstice prior to all observations,\n" +
                "    s[i] is the season number since the beginning of the track \n" +
                "    (one for the first 182.625 days, then two for the next 182.625, then three and so on).\n" +
                "    a[0], b[0] and sigma[y[0]]^2 are model parameters.\n" +
                "</p>\n" +
                "<p>\n" +
                "    If \"daily\" the variance is assumed to have a different value at\n" +
                "    each time step, and psi[i] are normally distributed random\n" +
                "    variables with mean zero and variance sigma[psi]^2 representing\n" +
                "    transient deviations in the latitude error.\n" +
                "</p>",
                "string",
                null,
                "solstice",
                true,
                Arrays.asList(
                    new AnalysisParameterOption("uniform", "uniform"),
                    new AnalysisParameterOption("specified", "specified"),
                    new AnalysisParameterOption("solstice", "solstice"),
                    new AnalysisParameterOption("daily", "daily")
                )
            )
        ),
        Arrays.asList(
            new AnalysisResultAttributeType(
                "Negativeloglik",
                "Negativeloglik",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "MaxGradComp",
                "MaxGradComp",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "uValue",
                "u value",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "uStdDev",
                "u std dev",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "vValue",
                "v value",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "vStdDev",
                "v std dev",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "DValue",
                "D value",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "DStdDev",
                "D std dev",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "bxValue",
                "bx value",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "bxStdDev",
                "bx std dev",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "byValue",
                "by value",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "byStdDev",
                "by std dev",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "bsstValue",
                "bsst value",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "bsstStdDev",
                "bsst std dev",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "sxValue",
                "sx value",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "sxStdDev",
                "sx std dev",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "syValue",
                "sy value",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "syStdDev",
                "sy std dev",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "ssstValue",
                "ssst value",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "ssstStdDev",
                "ssst std dev",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "rValue",
                "r value",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "rStdDev",
                "r std dev",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "a0Value",
                "a0 value",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "a0StdDev",
                "a0 std dev",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "b0Value",
                "b0 value",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "b0StdDev",
                "b0 std dev",
                "double",
                null,
                6
            )
        ),
        Arrays.<AnalysisResultAttributeType>asList(
            new AnalysisResultAttributeType(
                "varLon",
                "var lon",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "varLat",
                "var lat",
                "double",
                null,
                6
            )
        )
    ),
    KALMAN_SST(
        "Kalman Filter (SST)",
        null,
        AnalysisResultType.FILTER,
        Arrays.asList(
            new AnalysisParameterType(
                "startDate",
                "Start date",
                null,
                "date",
                null,
                null,
                false,
                null
            ),
            new AnalysisParameterType(
                "startX",
                "Start longitude",
                null,
                "double",
                "°",
                null,
                false,
                null
            ),
            new AnalysisParameterType(
                "startY",
                "Start latitude",
                null,
                "double",
                "°",
                null,
                false,
                null
            ),
            new AnalysisParameterType(
                "endDate",
                "End date",
                null,
                "date",
                null,
                null,
                false,
                null
            ),
            new AnalysisParameterType(
                "endX",
                "End longitude",
                null,
                "double",
                "°",
                null,
                false,
                null
            ),
            new AnalysisParameterType(
                "endY",
                "End latitude",
                null,
                "double",
                "°",
                null,
                false,
                null
            ),
            new AnalysisParameterType(
                "uActive",
                "u.active",
                "<p>Whether <i>u</i> should be optimized; uncheck to fix <i>u</i> at <i>u.init</i> value below.</p>",
                "boolean",
                null,
                "true",
                true,
                null
            ),
            new AnalysisParameterType(
                "vActive",
                "v.active",
                "<p>Whether <i>v</i> should be optimized; uncheck to fix <i>v</i> at <i>v.init</i> value below.</p>",
                "boolean",
                null,
                "true",
                true,
                null
            ),
            new AnalysisParameterType(
                "DActive",
                "D.active",
                "<p>Whether <i>D</i> should be optimized; uncheck to fix <i>D</i> at <i>D.init</i> value below.</p>",
                "boolean",
                null,
                "true",
                true,
                null
            ),
            new AnalysisParameterType(
                "bxActive",
                "bx.active",
                "<p>Whether <i>b[x]</i> should be optimized; uncheck to fix <i>b[x]</i> at <i>bx.init</i> value below.</p>",
                "boolean",
                null,
                "true",
                true,
                null
            ),
            new AnalysisParameterType(
                "byActive",
                "by.active",
                "<p>Whether <i>b[y]</i> should be optimized; uncheck to fix <i>b[y]</i> at <i>by.init</i> value below.</p>",
                "boolean",
                null,
                "true",
                true,
                null
            ),
            new AnalysisParameterType(
                "sxActive",
                "sx.active",
                "<p>Whether <i>sigma[x]</i> should be optimized; uncheck to fix <i>sigma[x]</i> at <i>sx.init</i> value below.</p>",
                "boolean",
                null,
                "true",
                true,
                null
            ),
            new AnalysisParameterType(
                "syActive",
                "sy.active",
                "<p>Whether <i>sigma[y]</i> should be optimized; uncheck to fix <i>sigma[y]</i> at <i>sy.init</i> value below.</p>",
                "boolean",
                null,
                "true",
                true,
                null
            ),
            new AnalysisParameterType(
                "a0Active",
                "a0.active",
                "<p>\n" +
                "   If <i>var.struct</i>=\"solstice\",\n" +
                "   whether <i>a[0]</i> should be optimized; uncheck to fix <i>a[0]</i> at <i>a0.init</i> value below.\n" +
                "</p>",
                "boolean",
                null,
                "true",
                true,
                null
            ),
            new AnalysisParameterType(
                "b0Active",
                "b0.active",
                "<p>\n" +
                "   If <i>var.struct</i>=\"solstice\",\n" +
                "   Whether <i>b[0]</i> should be optimized; uncheck to fix <i>b[0]</i> at <i>b0.init</i> value below.\n" +
                "</p>",
                "boolean",
                null,
                "true",
                true,
                null
            ),
            new AnalysisParameterType(
                "bsstActive",
                "bsst.active",
                "<p>Whether <i>b[sst]</i> should be optimized; uncheck to fix <i>b[sst]</i> at <i>bsst.init</i> value below.</p>",
                "boolean",
                null,
                "true",
                true,
                null
            ),
            new AnalysisParameterType(
                "ssstActive",
                "ssst.active",
                "<p>Whether <i>sigma[sst]</i> should be optimized; uncheck to fix <i>sigma[sst]</i> at <i>ssst.init</i> value below.</p>",
                "boolean",
                null,
                "true",
                true,
                null
            ),
            new AnalysisParameterType(
                "rActive",
                "r.active",
                "<p>Whether <i>r</i> should be optimized; uncheck to fix <i>r</i> at <i>r.init</i> value below.</p>",
                "boolean",
                null,
                "false",
                true,
                null
            ),
            new AnalysisParameterType(
                "uInit",
                "u.init",
                "<p>\"Advection\" component of movement; northward component of directed movement.</p>",
                "double",
                "nm/day",
                "0",
                true,
                null
            ),
            new AnalysisParameterType(
                "vInit",
                "v.init",
                "<p>\"Advection\" component of movement; eastward component of directed movement.</p>",
                "double",
                "nm/day",
                "0",
                true,
                null
            ),
            new AnalysisParameterType(
                "DInit",
                "D.init",
                "<p>\"Diffusion\" component of movement; a measure of the variability in movement.</p>",
                "double",
                "nm^2/day",
                "100",
                true,
                null
            ),
            new AnalysisParameterType(
                "bxInit",
                "bx.init",
                "<p>Systematic error (or bias) in the estimation of position (longitude).</p>",
                "double",
                "°",
                "0",
                true,
                null
            ),
            new AnalysisParameterType(
                "byInit",
                "by.init",
                "<p>Systematic error (or bias) in the estimation of position (latitude).</p>",
                "double",
                "°",
                "0",
                true,
                null
            ),
            new AnalysisParameterType(
                "sxInit",
                "sx.init",
                "<p>Random error in the estimation of position (longitude).</p>",
                "double",
                "°",
                "0.1",
                true,
                null
            ),
            new AnalysisParameterType(
                "syInit",
                "sy.init",
                "<p>Random error in the estimation of position (latitude).</p>",
                "double",
                "°",
                "1.0",
                true,
                null
            ),
            new AnalysisParameterType(
                "a0Init",
                "a0.init",
                "<p>\n" +
                "    Upper bound for latitude variance. Used in \"solstice\" variance structure;\n" +
                "    related to how the latitude estimation error varies around the equinox.\n" +
                "</p>",
                "double",
                "°",
                "0.001",
                true,
                null
            ),
            new AnalysisParameterType(
                "b0Init",
                "b0.init",
                "<p>\n" +
                "    The number of days prior to the equinox where the latitude error is maximal.\n" +
                "    Used in \"solstice\" variance structure; related to how the latitude estimation\n" +
                "    error varies around the equinox.\n" +
                "</p>",
                "double",
                "days",
                "0",
                true,
                null
            ),
            new AnalysisParameterType(
                "bsstInit",
                "bsst.init",
                "<p>Initial value of b[sst].</p>",
                "double",
                null,
                "0",
                true,
                null
            ),
            new AnalysisParameterType(
                "ssstInit",
                "ssst.init",
                "<p>Initial value of sigma[sst].</p>",
                "double",
                null,
                "0.1",
                true,
                null
            ),
            new AnalysisParameterType(
                "rInit",
                "r.init",
                "<p>\n" +
                "    The initial value for the radius (in nautical miles) around\n" +
                "    each track point where the SST is to be used.\n" +
                "</p>",
                "double",
                null,
                "200",
                true,
                null
            )
        ),
        Arrays.asList(
            new AnalysisResultAttributeType(
                "Negativeloglik",
                "Negativeloglik",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "MaxGradComp",
                "MaxGradComp",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "uValue",
                "u value",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "uStdDev",
                "u std dev",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "vValue",
                "v value",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "vStdDev",
                "v std dev",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "DValue",
                "D value",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "DStdDev",
                "D std dev",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "bxValue",
                "bx value",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "bxStdDev",
                "bx std dev",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "byValue",
                "by value",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "byStdDev",
                "by std dev",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "sxValue",
                "sx value",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "sxStdDev",
                "sx std dev",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "syValue",
                "sy value",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "syStdDev",
                "sy std dev",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "a0Value",
                "a0 value",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "a0StdDev",
                "a0 std dev",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "b0Value",
                "b0 value",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "b0StdDev",
                "b0 std dev",
                "double",
                null,
                6
            )
        ),
        Arrays.<AnalysisResultAttributeType>asList(
            new AnalysisResultAttributeType(
                "varLon",
                "var lon",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "varLat",
                "var lat",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "sst_o",
                "sst o",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "sst_p",
                "sst p",
                "double",
                null,
                6
            ),
            new AnalysisResultAttributeType(
                "sst_smooth",
                "sst smooth",
                "double",
                null,
                6
            )
        )
    );

    private final String displayName;
    private final String explanation;
    private final AnalysisResultType resultType;
    private final List<AnalysisParameterType> parameterTypes;
    private final List<AnalysisResultAttributeType> overallResultAttributeTypes;
    private final List<AnalysisResultAttributeType> featureResultAttributeTypes;

    private AnalysisType(
        String display,
        String explanation,
        AnalysisResultType resultType,
        List<AnalysisParameterType> parameterTypes,
        List<AnalysisResultAttributeType> overallResultAttributeTypes,
        List<AnalysisResultAttributeType> featureResultAttributeTypes
    ) {
        this.displayName = display;
        this.explanation = explanation;
        this.resultType = resultType;
        this.parameterTypes = parameterTypes;
        this.overallResultAttributeTypes = overallResultAttributeTypes;
        this.featureResultAttributeTypes = featureResultAttributeTypes;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getExplanation() {
        return explanation;
    }

    public AnalysisResultType getResultType() {
        return resultType;
    }

    public List<AnalysisParameterType> getParameterTypes() {
        return parameterTypes;
    }

    public AnalysisParameterType getParameterType(String identifier) {
        for (AnalysisParameterType parameterType : parameterTypes) {
            if (parameterType.getIdentifier().equals(identifier)) {
                return parameterType;
            }
        }
        return null;
    }

    public List<AnalysisResultAttributeType> getOverallResultAttributeTypes() {
        return overallResultAttributeTypes;
    }

    public AnalysisResultAttributeType getOverallResultAttributeType(String identifier) {
        for (AnalysisResultAttributeType resultAttributeType : overallResultAttributeTypes) {
            if (resultAttributeType.getIdentifier().equals(identifier)) {
                return resultAttributeType;
            }
        }
        return null;
    }

    public List<AnalysisResultAttributeType> getFeatureResultAttributeTypes() {
        return featureResultAttributeTypes;
    }

    public AnalysisResultAttributeType getFeatureResultAttributeType(String identifier) {
        for (AnalysisResultAttributeType resultAttributeType : featureResultAttributeTypes) {
            if (resultAttributeType.getIdentifier().equals(identifier)) {
                return resultAttributeType;
            }
        }
        return null;
    }

    private static List<AnalysisParameterType> buildHeatmapParameterTypes() {
        return Arrays.asList(
            new AnalysisParameterType("showAbsence", "Show absence", null, "boolean", null, "false", false, null),
            new AnalysisParameterType("gridSize", "Grid size", null, "double", "m", "100", false, null),
            new AnalysisParameterType("colours", "Colours", null, "string", null, "YlOrRed", false, Arrays.asList(
                new AnalysisParameterOption("YlOrRd", "Yellow-Orange-Red"),
                new AnalysisParameterOption("YlOrBr", "Yellow-Orange-Brown"),
                new AnalysisParameterOption("YlGnBu", "Yellow-Green-Blue"),
                new AnalysisParameterOption("YlGn", "Yellow-Green"),
                new AnalysisParameterOption("Reds", "Red"),
                new AnalysisParameterOption("RdPu", "Red-Purple"),
                new AnalysisParameterOption("Purples", "Purple"),
                new AnalysisParameterOption("PuRd", "Purple-Red"),
                new AnalysisParameterOption("PuBuGn", "Purple-Blue-Green"),
                new AnalysisParameterOption("PuBu", "Purple-Blue"),
                new AnalysisParameterOption("OrRd", "Orange-Red"),
                new AnalysisParameterOption("Oranges", "Orange"),
                new AnalysisParameterOption("Greys", "Grey"),
                new AnalysisParameterOption("Greens", "Green"),
                new AnalysisParameterOption("GnBu", "Green-Blue"),
                new AnalysisParameterOption("BuPu", "Blue-Purple"),
                new AnalysisParameterOption("BuGn", "Blue-Green"),
                new AnalysisParameterOption("Blues", "Blue")
            ))
        );
    }
}