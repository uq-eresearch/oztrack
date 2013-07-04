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

### Installing Linux packages

Install the following packages:

* `libgdal-dev`: required by `rgdal` R package;
* `libproj-dev`: required by `rgdal` R package.
* `libxml2-devel`: required by `plotKML` R package.

    sudo apt-get install libgdal-dev libproj-dev libxml2-dev

### Installing R (including Rserve and other packages)

Install R.
OzTrack has been tested with R version 2.15.2.

    sudo apt-get install r-base

Install the R packages used by OzTrack, including Rserve.
Note that the following command takes a while (e.g. 10 minutes) because it
downloads, compiles, tests, and installs a large number of dependencies.

    sudo R --interactive << EOF
    install.packages(c(
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
    ))
    EOF

### Installing GeoServer

[GeoServer](http://geoserver.org/) is an open-source GIS server used to render map layers in OzTrack.
OzTrack has been tested with [GeoServer version 2.3.1](http://geoserver.org/display/GEOS/GeoServer+2.3.1).

For complete installation instructions, see the [GeoServer user manual](http://docs.geoserver.org/stable/en/user/).

The following commands install the GeoServer WAR distribution to Tomcat:

    sudo apt-get install tomcat6
    wget 'http://downloads.sourceforge.net/geoserver/geoserver-2.3.1-war.zip' -P /tmp/
    unzip -d /tmp/geoserver /tmp/geoserver-2.3.1-war.zip
    sudo service tomcat6 stop
    sudo unzip -d /var/lib/tomcat6/webapps/geoserver/ /tmp/geoserver/geoserver.war 
    sudo chown -R tomcat6: /var/lib/tomcat6/webapps/geoserver/
    sudo service tomcat6 start

Log in to the GeoServer web administation interface at <http://localhost:8080/geoserver/web/>
using the default username/password, "admin"/"geoserver", and follow the instructions for configuring security.

### Completing installation

See the *Installing and configuring OzTrack* section below.

## Installing on Red Hat Linux

### Setting up the database

To install PostgreSQL, use the following commands:

    sudo yum install postgresql.x86_64
    sudo yum install postgresql-server.x86_64
    sudo yum install postgresql-devel.x86_64

To install PostGIS, you need the EPEL repository. The URL used in the
following command depends on the version of Red Hat that you're running: see
instructions on <http://fedoraproject.org/wiki/EPEL>.

    sudo rpm -Uvh 'http://mirror.iprimus.com.au/epel/6/i386/epel-release-6-7.noarch.rpm'
    sudo yum install postgis

Sort out PostgreSQL authentication:

    sudo $EDITOR /var/lib/pgsql/data/pg_hba.conf

    # TYPE  DATABASE    USER        CIDR-ADDRESS          METHOD
    local   all         postgres                          ident
    local   all         all                               md5
    host    all         all         127.0.0.1/32          md5
    host    all         all         ::1/128               md5

Initialise PostgreSQL and set up the service:

    sudo service postgresql initdb
    sudo chkconfig postgresql on
    sudo service postgresql start

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

### Installing Linux packages

These packages are required for both PostGIS and R spatial functionality.

First, add the Enterprise Linux GIS (ELGIS) yum repository. The URL used in the
following command depends on the version of Red Hat that you're running: see
instructions on <http://elgis.argeo.org/>.

    sudo rpm -Uvh http://elgis.argeo.org/repos/6/elgis-release-6-6_0.noarch.rpm

Install the following packages:

* `gdal-devel`: required by `rgdal` R package;
* `proj-devel`: required by `rgdal` R package;
* `libxml2-devel`: required by `plotKML` R package.

    sudo yum install gdal-devel proj-devel libxml2-devel
 
### Installing R (including Rserve and other packages)

Install R from the EPEL repository.
OzTrack has been tested with R version 2.15.2.

    sudo yum install R

Install the R packages used by OzTrack, including Rserve.
Note that the following command takes a while (e.g. 10 minutes) because it
downloads, compiles, tests, and installs a large number of dependencies.

    sudo R --interactive << EOF
    install.packages(c(
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
    ))
    EOF

### Installing GeoServer

[GeoServer](http://geoserver.org/) is an open-source GIS server used to render map layers in OzTrack.
OzTrack has been tested with [GeoServer version 2.3.1](http://geoserver.org/display/GEOS/GeoServer+2.3.1).

For complete installation instructions, see the [GeoServer user manual](http://docs.geoserver.org/stable/en/user/).

The following commands install the GeoServer WAR distribution to Tomcat:

    sudo apt-get install tomcat6
    wget 'http://downloads.sourceforge.net/geoserver/geoserver-2.3.1-war.zip' -P /tmp/
    unzip -d /tmp/geoserver /tmp/geoserver-2.3.1-war.zip
    sudo service tomcat6 stop
    sudo unzip -d /var/lib/tomcat6/webapps/geoserver/ /tmp/geoserver/geoserver.war 
    sudo chown -R tomcat: /var/lib/tomcat6/webapps/geoserver/
    sudo service tomcat6 start

Log in to the GeoServer web administation interface at <http://localhost:8080/geoserver/web/>
using the default username/password, "admin"/"geoserver", and follow the instructions for configuring security.

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

OzTrack automatically configures layers in GeoServer. Log in to OzTrack as admin, go to `/settings/geoserver`, and click the 'Update GeoServer' button.

## Developer notes

### Upgrading jQuery UI theme

The jQuery UI theme used on this project is produced using the jQuery UI ThemeRoller
(http://jqueryui.com/themeroller/). To modify the theme, use the link included in the
ThemeRoller-generated CSS file to pre-fill settings in the theme creation form.

    grep 'jqueryui\.com\/themeroller\/?' src/main/webapp/css/jquery-ui/*.css
