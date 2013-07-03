# OzTrack

OzTrack is a free-to-use web-based platform for the analysis and visualisation of animal tracking data.
It was developed for the Australian animal tracking community but can be used to determine, measure and
plot home-ranges for animals anywhere in the world.

This software is copyright The University of Queensland.

This software is distributed under the GNU GENERAL PUBLIC LICENSE Version 2. See the COPYING file for detail.

## Installing on Ubuntu Linux

### Setting up the database

Install PostgreSQL and PostGIS:

    sudo apt-get install postgresql-9.1 postgresql-9.1-postgis

Setup the OzTrack database, including PostGIS:

    sudo -u postgres psql -c "create user oztrack with password 'changeme';"
    sudo -u postgres psql -c "create database oztrack with owner oztrack;"
    sudo -u postgres psql -d oztrack -f /usr/share/postgresql/9.1/contrib/postgis-1.5/postgis.sql
    sudo -u postgres psql -d oztrack -f /usr/share/postgresql/9.1/contrib/postgis-1.5/spatial_ref_sys.sql
    sudo -u postgres psql -d oztrack -c "alter table geometry_columns owner to oztrack;"
    sudo -u postgres psql -d oztrack -c "alter table spatial_ref_sys owner to oztrack;"
    sudo -u postgres psql -d oztrack -c "alter view geography_columns owner to oztrack;"

### Installing GIS Packages

Install the `gdal-config` and `geos-config` utilities, required by the `rgdal` and `rgeos` R packages,
and `libproj-dev`, also required by `rgdal`:

    sudo apt-get install libgeos-dev libgdal1-dev libproj-dev

### Installing R (including Rserve and other packages)

Install R:

    sudo apt-get install r-base

Install the R packages used by OzTrack, including Rserve:

    sudo R

    install.packages(
        c(
            "Rserve",
            "sp",
            "ade4",
            "adehabitatHR",
            "adehabitatMA",
            "maptools",
            "shapefiles",
            "rgdal",
            "alphahull",
            "raster",
            "plyr",
            "spatstat",
            "Grid2Polygons",
            "RColorBrewer",
            "googleVis",
            "spacetime",
            "plotKML"
        ),
        dependencies=TRUE
    )

### Installing GeoServer

[GeoServer](http://geoserver.org/) is an open-source GIS server used to render map layers in OzTrack.

OzTrack has been tested with GeoServer version 2.3.1, which can be downloaded from <http://geoserver.org/display/GEOS/GeoServer+2.3.1>.

For complete installation instructions, see the [GeoServer user manual](http://docs.geoserver.org/stable/en/user/).

The following commands install the GeoServer WAR file to Tomcat:

    sudo apt-get install tomcat6
    sudo service tomcat6 stop
    unzip -d /tmp/geoserver /tmp/geoserver-2.3.1-war.zip
    sudo unzip -d /var/lib/tomcat6/webapps/geoserver/ /tmp/geoserver/geoserver.war 
    sudo chown -R tomcat6: /var/lib/tomcat6/webapps/geoserver/
    sudo service tomcat6 start

Log in to the GeoServer web administation interface at <http://localhost:8080/geoserver/web/>
using the default username/password of "admin"/"geoserver" and follow the instructions for configuring security.

### Completing installation

See the *Installing and configuring OzTrack* section below.

## Installing on Red Hat Linux

### Setting up the database

To install PostgreSQL, use the following commands:

    yum install postgresql.x86_64
    yum install postgresql-server.x86_64
    yum install postgresql-devel.x86_64

To install PostGIS, you need the EPEL repository. The URL used in the
following command depends on the version of Red Hat that you're running: see
instructions on <http://fedoraproject.org/wiki/EPEL>.

    rpm -Uvh 'http://mirror.iprimus.com.au/epel/6/i386/epel-release-6-7.noarch.rpm'
    yum install postgis

Sort out PostgreSQL authentication:

    $EDITOR /var/lib/pgsql/data/pg_hba.conf

    # TYPE  DATABASE    USER        CIDR-ADDRESS          METHOD
    local   all         postgres                          ident
    local   all         all                               md5
    host    all         all         127.0.0.1/32          md5
    host    all         all         ::1/128               md5

Initialise PostgreSQL and set up the service:

    service postgresql initdb
    chkconfig postgresql on
    service postgresql start

Run something like the following commands:

    # Create database with PL/pgSQL support
    sudo -u postgres psql -c "create user oztrack with password 'changeme';"
    sudo -u postgres psql -c "create database oztrack with owner oztrack;"
    psql -U oztrack -d oztrack -c "create language plpgsql;"

    # Run the PostGIS initialisation scripts: need to run postgis.sql as postgres
    # because only superuser can create c functions; afterwards, we change owner
    # on the resulting tables/views and subsequently connect as normal user.
    sudo -u postgres psql -d oztrack -f /usr/share/pgsql/contrib/postgis-64.sql
    sudo -u postgres psql -d oztrack -f /usr/share/pgsql/contrib/spatial_ref_sys.sql

    sudo -u postgres psql -d oztrack -c "alter table geometry_columns owner to oztrack;"
    sudo -u postgres psql -d oztrack -c "alter table spatial_ref_sys owner to oztrack;"
    sudo -u postgres psql -d oztrack -c "alter view geography_columns owner to oztrack;"

    # Our own tables should be created on first run by Hibernate

See <http://postgis.refractions.net/documentation/manual-1.5/ch02.html#id2565921>

### Installing GIS Packages

These packages are required for both PostGIS and R spatial functionality.

First, add the Enterprise Linux GIS (ELGIS) yum repository. The URL used in the
following command depends on the version of Red Hat that you're running: see
instructions on <http://elgis.argeo.org/>.

    rpm -Uvh http://elgis.argeo.org/repos/6/elgis-release-6-6_0.noarch.rpm

Install the following packages (note they should be installed in this order):

    yum install geos-devel # will install geos as a dependency
    yum install proj-devel # will install proj as a dependency
    yum install gdal-devel
 
To test successful installation, on the commmand line you should get a response from:

    geos-config
    gdal-config
    proj

### Installing R (including Rserve and other packages)

You can just install R from the EPEL package repository on CentOS:

    yum install R

There are further libraries used in this project that are installed by running
`install.packages` in the R interpreter (run the `R` command from Linux console, as root with `-E` switch).

    install.packages(c("Rserve"), dependencies=TRUE)

Note that the following command takes a while (eg over 10 minutes) because it
downloads, compiles, tests, and installs a large number of dependencies.

    install.packages(
        c(
            "sp",
            "ade4",
            "adehabitatHR",
            "adehabitatMA",
            "maptools",
            "shapefiles",
            "rgdal",
            "alphahull",
            "raster",
            "plyr",
            "spatstat",
            "Grid2Polygons",
            "RColorBrewer",
            "googleVis",
            "spacetime",
            "plotKML"
        ),
        dependencies=TRUE
    )
 
Note that it may be necessary to add a repos argument (eg `repos="http://cran.cnr.berkeley.edu/"`)
when executing `install.packages`, but this caused an error when run on CentOS.

You will need the `gdal` Red Hat package installed in order to install the
`rgdal` R package (ie run `yum install gdal`). You'll also need the `libxml2-devel`
Red Hat package installed before installing the `plotKML` R package.

### Installing GeoServer

[GeoServer](http://geoserver.org/) is an open-source GIS server used to render map layers in OzTrack.

OzTrack has been tested with GeoServer version 2.3.1, which can be downloaded from http://geoserver.org/display/GEOS/GeoServer+2.3.1.

For complete installation instructions, see the [GeoServer user manual](http://docs.geoserver.org/stable/en/user/).

The following commands install the GeoServer WAR file to Tomcat:

    sudo apt-get install tomcat6
    sudo service tomcat6 stop
    unzip -d /tmp/geoserver /tmp/geoserver-2.3.1-war.zip
    sudo unzip -d /var/lib/tomcat6/webapps/geoserver/ /tmp/geoserver/geoserver.war 
    sudo chown -R tomcat: /var/lib/tomcat6/webapps/geoserver/
    sudo service tomcat6 start

Log in to the GeoServer web administation interface at http://localhost:8080/geoserver/web/
using the default username/password of "admin"/"geoserver" and follow the instructions for configuring security.

### Completing installation

See the *Installing and configuring OzTrack* section below.

## Installing and configuring OzTrack

### Installing OzTrack

OzTrack can be compiled from source into a WAR file by running `mvn package`.

The OzTrack WAR file can be run using any Java Servlet container.

Alternatively, it can be run as a normal Java application via the `org.oztrack.app.OzTrackJettyServer` class.

### Configuring OzTrack

OzTrack can be configured via a range of properties. The `application.properties` file included in the
distribution contains the complete list of properties together with their default values.

To override these default values, either edit `application.properties` or supply values via Java system properties.
System properties can be set as arguments to the `java` command (e.g. `-Dapplication.dataDir=/var/local/oztrack`).
When deploying to Tomcat, arguments can be added to the `JAVA_OPTS` variable used in the startup script.

The following are key properties that should be configured for all applications:

* `application.dataDir`: Directory used to store tracking data files - ensure that this directory exists and can be written to by the application (default "/var/local/oztrack").
* `application.databaseUsername`: Username for PostgreSQL database (default "oztrack").
* `application.databasePassword`: Password for PostgreSQL database.
* `application.geoServerUsername`: Username for Geoserver admin user (default "admin").
* `application.geoServerPassword`: Password for GeoServer admin user.
* `application.baseUrl`: Base URL for application, minus trailing slash (default "http://localhost").
* `application.mailServerHostName`: SMTP host name for sending mail notifications.
* `application.mailServerPort`: SMTP host port number for sending mail notifications.
* `application.mailFromName`: Name in From field for mail notifications (default "OzTrack").
* `application.mailFromEmail`: Email address in From field for mail notifications.

OzTrack automatically configures layers in GeoServer. Login to OzTrack as admin, go to `/settings/geoserver`, and click the 'Update GeoServer' button.

## Developer notes

### Upgrading jQuery UI theme

The jQuery UI theme used on this project is produced using the jQuery UI ThemeRoller
(http://jqueryui.com/themeroller/). To modify the theme, use the link included in the
ThemeRoller-generated CSS file to pre-fill settings in the theme creation form.

    grep 'jqueryui\.com\/themeroller\/?' src/main/webapp/css/jquery-ui/*.css
