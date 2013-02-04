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
        Arrays.asList(
            new AnalysisParameterType(
                "percent",
                "Percent",
                "OzTrack offers the feature to strip the Minimum Convex Polygon (MCP) " +
                "to different levels based on percentage. At 100% the MCP will be the " +
                "equivalent to the area covered by all locations within the dataset. " +
                "Inserting a lower % value into the box will result in only this " +
                "percentage of locations being contained in the final MCP.",
                "double",
                "%",
                "100",
                false,
                null
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
        )
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
        buildHeatmapParameterTypes()
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
        buildHeatmapParameterTypes()
    );

    private final String displayName;
    private final String explanation;
    private final List<AnalysisParameterType> parameterTypes;

    private AnalysisType(String display, String explanation, List<AnalysisParameterType> parameterTypes) {
        this.displayName = display;
        this.explanation = explanation;
        this.parameterTypes = parameterTypes;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getExplanation() {
        return explanation;
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