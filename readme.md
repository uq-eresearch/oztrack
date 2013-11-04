# OzTrack

OzTrack is a free-to-use web-based platform for the analysis and visualisation of animal tracking data.
It was developed for the Australian animal tracking community but can be used to determine, measure and
plot home-ranges for animals anywhere in the world.

This software is copyright The University of Queensland.

This software is distributed under the GNU GENERAL PUBLIC LICENSE Version 2. See the COPYING file for detail.

## Installing on Ubuntu

### Setting up the database

Install PostgreSQL and PostGIS:

    sudo apt-get install -y postgresql-9.1 postgresql-client-9.1 postgresql-9.1-postgis postgresql-contrib-9.1

Sort out PostgreSQL authentication:

    --- /etc/postgresql/9.1/main/pg_hba.conf.1	2013-10-12 14:03:37.171531579 +1000
    +++ /etc/postgresql/9.1/main/pg_hba.conf	2013-10-12 14:03:40.923531719 +1000
    @@ -87,7 +87,7 @@
     # TYPE  DATABASE        USER            ADDRESS                 METHOD
     
     # "local" is for Unix domain socket connections only
    -local   all             all                                     peer
    +local   all             all                                     md5
     # IPv4 local connections:
     host    all             all             127.0.0.1/32            md5
     # IPv6 local connections:

Setup the OzTrack database, including PostGIS and UUID module:

    sudo -u postgres psql -c "create user oztrack with password 'changeme';"
    sudo -u postgres psql -c "create database oztrack with owner oztrack;"
    sudo -u postgres psql -d oztrack -f /usr/share/postgresql/9.1/contrib/postgis-1.5/postgis.sql
    sudo -u postgres psql -d oztrack -f /usr/share/postgresql/9.1/contrib/postgis-1.5/spatial_ref_sys.sql
    sudo -u postgres psql -d oztrack -c "alter table geometry_columns owner to oztrack;"
    sudo -u postgres psql -d oztrack -c "alter table spatial_ref_sys owner to oztrack;"
    sudo -u postgres psql -d oztrack -c "alter view geography_columns owner to oztrack;"
    sudo -u postgres psql -d oztrack -c 'create extension "uuid-ossp";'

### Installing Linux packages

Install the following packages:

* `libgdal-dev`: required by `rgdal` R package.
* `libproj-dev`: required by `rgdal` R package.
* `libxml2-dev`: required by `plotKML` R package.
* `libnetcdf-dev`, `netcdf-bin`: required by `ncdf` R package.

<pre>sudo apt-get install -y libgdal-dev libproj-dev libxml2-dev libnetcdf-dev netcdf-bin</pre>

### Installing R (including Rserve and other packages)

Install R, including `r-base-dev` for compiling/installing auxiliary R packages.
OzTrack has been tested with R versions 2.15.2, 3.0.1, and 3.0.2.

    sudo tee -a /etc/apt/sources.list > /dev/null << EOF
    deb http://cran.csiro.au/bin/linux/ubuntu saucy/
    EOF
    sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys E084DAB9
    sudo apt-get update
    sudo apt-get install -y --no-install-recommends r-base-core r-base-dev

Install the R packages used by OzTrack, including Rserve.
Note that the following command takes a while (e.g. 10 minutes) because it
downloads, compiles, tests, and installs a large number of dependencies.

    sudo R --no-save << EOF
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
        repos='http://cran.csiro.au/'
    )
    EOF

The `kftrack` and `ukfsst` packages need to be downloaded and installed from files.

    wget -P /tmp 'https://geolocation.googlecode.com/files/kftrack_0.70-x64.tar.gz'
    wget -P /tmp 'https://geolocation.googlecode.com/files/ukfsst_0.3-x64.tar.gz'
    sudo R --no-save << EOF
    install.packages('/tmp/kftrack_0.70-x64.tar.gz', repos=NULL)
    install.packages(c('date', 'ncdf'), repos='http://cran.csiro.au/')
    install.packages('/tmp/ukfsst_0.3-x64.tar.gz', repos=NULL)
    EOF

### Installing GeoServer

[GeoServer](http://geoserver.org/) is an open-source GIS server used to render map layers in OzTrack.
OzTrack has been tested with [GeoServer version 2.3.1](http://geoserver.org/display/GEOS/GeoServer+2.3.1).
For complete installation instructions, see the [GeoServer user manual](http://docs.geoserver.org/stable/en/user/).

The following commands install the GeoServer WAR distribution to Tomcat:

    sudo apt-get install -y tomcat7 unzip
    wget 'http://downloads.sourceforge.net/geoserver/geoserver-2.3.1-war.zip' -P /tmp/
    unzip -d /tmp/geoserver /tmp/geoserver-2.3.1-war.zip
    sudo service tomcat7 stop
    sudo unzip -d /var/lib/tomcat7/webapps/geoserver/ /tmp/geoserver/geoserver.war
    sudo chown -R tomcat7: /var/lib/tomcat7/webapps/geoserver/
    sudo service tomcat7 start

Log in to the GeoServer web administation interface at <http://localhost:8080/geoserver/web/>
using the default username/password, "admin"/"geoserver", and follow the instructions for configuring security.

### Installing Apache

This step is optional - it's only needed to avoid blocking of cross-site requests if you intend to run
GeoServer and the OzTrack web application on different ports. The following commands install the Apache
HTTP server and configure reverse proxying from port 80 to GeoServer (port 8080) and OzTrack (port 8181).

    sudo apt-get install -y apache2
    sudo a2enmod proxy_http
    sudo service apache2 restart
    sudo tee /etc/apache2/sites-available/oztrack.conf > /dev/null << EOF
    ProxyPreserveHost on
    ProxyPass /geoserver http://localhost:8080/geoserver nocanon retry=0
    ProxyPassReverse /geoserver http://localhost:8080/geoserver
    ProxyPass / http://localhost:8181/ nocanon retry=0
    ProxyPassReverse / http://localhost:8181/
    EOF
    sudo a2ensite oztrack
    sudo service apache2 reload

### Completing installation

See the [Installing and configuring OzTrack](#installing-and-configuring-oztrack) section below.

## Installing on Red Hat

### Setting up the database

To install PostgreSQL, use the following commands. The RPM used below depends on the
version of Red Hat you're running: see instructions on <http://yum.postgresql.org/>.

    sudo rpm -Uvh 'http://yum.postgresql.org/9.1/redhat/rhel-6-x86_64/pgdg-sl91-9.1-6.noarch.rpm'
    sudo yum install postgresql91 postgresql91-server postgresql91-contrib postgis91

Sort out PostgreSQL authentication:

    --- /var/lib/pgsql/9.1/data/pg_hba.conf.1       2013-08-29 12:59:57.395611680 +1000
    +++ /var/lib/pgsql/9.1/data/pg_hba.conf 2013-10-13 22:40:04.153573926 +1000
    @@ -73,8 +77,9 @@
     # TYPE  DATABASE        USER            ADDRESS                 METHOD
    +local   all             postgres                                peer
     
     # "local" is for Unix domain socket connections only
    -local   all             all                                     peer
    +local   all             all                                     md5
     # IPv4 local connections:
    -host    all             all             127.0.0.1/32            ident
    +host    all             all             127.0.0.1/32            md5
     # IPv6 local connections:
    -host    all             all             ::1/128                 ident
    +host    all             all             ::1/128                 md5

Initialise PostgreSQL and set up the service:

    sudo service postgresql-9.1 initdb
    sudo service postgresql-9.1 start
    sudo chkconfig postgresql-9.1 on

Run something like the following commands:

    # Create database with PL/pgSQL support
    sudo -u postgres /usr/pgsql-9.1/bin/psql -c "create user oztrack with password 'changeme';"
    sudo -u postgres /usr/pgsql-9.1/bin/psql -c "create database oztrack with owner oztrack;"
    /usr/pgsql-9.1/bin/psql -U oztrack -d oztrack -c "create language plpgsql;"

    # Run the PostGIS initialisation scripts: need to run postgis.sql as postgres
    # because only superuser can create c functions; afterwards, we change owner
    # on the resulting tables/views and subsequently connect as normal user.
    sudo -u postgres /usr/pgsql-9.1/bin/psql -d oztrack -f /usr/share/pgsql/contrib/postgis-64.sql
    sudo -u postgres /usr/pgsql-9.1/bin/psql -d oztrack -f /usr/share/pgsql/contrib/spatial_ref_sys.sql

    sudo -u postgres /usr/pgsql-9.1/bin/psql -d oztrack -c "alter table geometry_columns owner to oztrack;"
    sudo -u postgres /usr/pgsql-9.1/bin/psql -d oztrack -c "alter table spatial_ref_sys owner to oztrack;"
    sudo -u postgres /usr/pgsql-9.1/bin/psql -d oztrack -c "alter view geography_columns owner to oztrack;"
    sudo -u postgres /usr/pgsql-9.1/bin/psql -d oztrack -c 'create extension "uuid-ossp";'

See <http://postgis.refractions.net/documentation/manual-1.5/ch02.html#id2565921>

### Installing Linux packages

These packages are required for both PostGIS and R spatial functionality.

First, add the Enterprise Linux GIS (ELGIS) yum repository. The URL used in the
following command depends on the version of Red Hat that you're running: see
instructions on <http://elgis.argeo.org/>.

    sudo rpm -Uvh http://elgis.argeo.org/repos/6/elgis-release-6-6_0.noarch.rpm

Install the following packages:

* `gdal-devel`: required by `rgdal` R package.
* `proj-devel`: required by `rgdal` R package.
* `libxml2-devel`: required by `plotKML` R package.
* `netcdf-devel`: required by `ncdf` R package.

<pre>sudo yum install gdal-devel proj-devel libxml2-devel netcdf-devel</pre>

### Installing R (including Rserve and other packages)

Install R from the EPEL repository.
OzTrack has been tested with R versions 2.15.2 and 3.0.1.

    sudo yum install R

Install the R packages used by OzTrack, including Rserve.
Note that the following command takes a while (e.g. 10 minutes) because it
downloads, compiles, tests, and installs a large number of dependencies.

    sudo R --no-save << EOF
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
        repos='http://cran.csiro.au/'
    )
    EOF

The `kftrack` and `ukfsst` packages need to be downloaded and installed from files.

    wget -P /tmp 'https://geolocation.googlecode.com/files/kftrack_0.70-x64.tar.gz'
    wget -P /tmp 'https://geolocation.googlecode.com/files/ukfsst_0.3-x64.tar.gz'
    sudo R --no-save << EOF
    install.packages('/tmp/kftrack_0.70-x64.tar.gz', repos=NULL)
    install.packages(c('date', 'ncdf'), repos='http://cran.csiro.au/')
    install.packages('/tmp/ukfsst_0.3-x64.tar.gz', repos=NULL)
    EOF

### Installing GeoServer

[GeoServer](http://geoserver.org/) is an open-source GIS server used to render map layers in OzTrack.
OzTrack has been tested with [GeoServer version 2.3.1](http://geoserver.org/display/GEOS/GeoServer+2.3.1).
For complete installation instructions, see the [GeoServer user manual](http://docs.geoserver.org/stable/en/user/).

The following commands install the GeoServer WAR distribution to Tomcat:

    sudo apt-get install tomcat6
    sudo chkconfig tomcat6 on
    wget 'http://downloads.sourceforge.net/geoserver/geoserver-2.3.1-war.zip' -P /tmp/
    unzip -d /tmp/geoserver /tmp/geoserver-2.3.1-war.zip
    sudo unzip -d /var/lib/tomcat6/webapps/geoserver/ /tmp/geoserver/geoserver.war
    sudo chown -R tomcat: /var/lib/tomcat6/webapps/geoserver/
    sudo service tomcat6 start

Log in to the GeoServer web administation interface at <http://localhost:8080/geoserver/web/>
using the default username/password, "admin"/"geoserver", and follow the instructions for configuring security.

### Installing Apache

This step is optional - it's only needed to avoid blocking of cross-site requests if you intend to run
GeoServer and the OzTrack web application on different ports. The following commands install the Apache
HTTP server and configure reverse proxying from port 80 to GeoServer (port 8080) and OzTrack (port 8181).

    sudo yum install httpd
    sudo chkconfig httpd on
    sudo cat > /etc/httpd/conf.d/oztrack.conf << EOF
    ProxyPreserveHost on
    ProxyPass /geoserver http://localhost:8080/geoserver nocanon retry=0
    ProxyPassReverse /geoserver http://localhost:8080/geoserver
    ProxyPass / http://localhost:8181/ nocanon retry=0
    ProxyPassReverse / http://localhost:8181/
    EOF
    sudo service apache2 start

### Completing installation

See the [Installing and configuring OzTrack](#installing-and-configuring-oztrack) section below.

## Installing and configuring OzTrack

### Installing OzTrack

OzTrack can be compiled from source into a WAR file by running `mvn package`.

The OzTrack WAR file can be run using any Java Servlet container.

Alternatively, it can be run as a normal Java application via the `org.oztrack.app.OzTrackJettyServer` class.

### Configuring OzTrack

OzTrack can be configured via a range of properties. The properties files included in the
distribution under `/WEB-INF/classes/org/oztrack/conf/` contain default settings.

To override these default values, you have three options (in increasing order of precedence):

* edit the `/WEB-INF/classes/org/oztrack/conf/custom.properties` file included in the distribution;
* point to an external properties file using the `org.oztrack.conf.customConfigFile` system property;
* override individual properties via Java system properties.

An advantage of pointing to an external configuration file rather than editing the `custom.properties` file
included in the distribution is that you can keep configuration separate to the application, avoiding the
need to apply patches each time a new version of the application is deployed.

System properties can be set as arguments to the `java` command
(e.g. `-Dorg.oztrack.conf.customConfigFile=/var/local/oztrack/custom.properties`).
When deploying to Tomcat, arguments can be added to the `JAVA_OPTS` variable used in the startup script.

The following are key properties that should be configured for all applications:

    # Base URL for application, minus trailing slash.
    org.oztrack.conf.baseUrl=http://localhost
    # Directory used to store tracking data files.
    # Ensure that this directory exists and can be written to by the application.
    org.oztrack.conf.dataDir=/var/local/oztrack
    # Username for PostgreSQL database.
    org.oztrack.conf.databaseUsername=oztrack
    # Password for PostgreSQL database.
    org.oztrack.conf.databasePassword=changeme
    # Username for Geoserver admin user.
    org.oztrack.conf.geoServerUsername=admin
    # Password for GeoServer admin user.
    org.oztrack.conf.geoServerPassword=changeme
    # SMTP host name for sending mail notifications.
    org.oztrack.conf.mailServerHostName=smtp.example.org
    # SMTP host port number for sending mail notifications.
    org.oztrack.conf.mailServerPort=25
    # Name in From field for mail notifications.
    org.oztrack.conf.mailFromName=OzTrack
    # Email address in From field for mail notifications.
    org.oztrack.conf.mailFromEmail=oztrack@example.org
    # Date from which the ability to create closed access projects is disabled.
    org.oztrack.conf.closedAccessDisableDate=2013-01-18T17:00:00
    # Date from which project embargoes must be annually renewed.
    org.oztrack.conf.nonIncrementalEmbargoDisableDate=2013-05-28T09:00:00
    # Number of `Rserve` instances run in the background (reduce if memory is limited).
    org.oztrack.conf.numRConnections=4

OzTrack defines a default admin user with the username/password "admin"/"oztrack".
To log into OzTrack, click the 'Login' button at the top-right of screen. You should change the default
admin password immediately by selecting 'Edit profile' from within the user menu at the top-right of screen.

The admin user, unlike ordinary users in OzTrack, also has a 'Settings' link under the user menu;
the settings page allows various aspects of the OzTrack application to be configured.

OzTrack automatically creates and updates layers in GeoServer.
Log in to OzTrack as admin, go the Settings page, and click the 'Update GeoServer' button.

## Developer notes

### Upgrading jQuery UI theme

The jQuery UI theme used on this project is produced using the jQuery UI ThemeRoller
(http://jqueryui.com/themeroller/). To modify the theme, use the link included in the
ThemeRoller-generated CSS file to pre-fill settings in the theme creation form.

    grep 'jqueryui\.com\/themeroller\/?' src/main/webapp/css/jquery-ui/*.css
