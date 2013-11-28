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

<pre>sudo service postgresql reload</pre>

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

RServe can be installed on the same machine as the Web application and/or on
one or more other hosts. See the `org.oztrack.conf.rserveHosts` configuration
property described below for how to identify these other hosts to the main server.
The following instructions should only be applied to hosts that will run `Rserve`.

Install R, including `r-base-dev` for compiling/installing auxiliary R packages.
OzTrack has been tested with R versions 2.15.2, 3.0.1, and 3.0.2.

    sudo tee /etc/apt/sources.list.d/cran.list > /dev/null << EOF
    deb http://cran.csiro.au/bin/linux/ubuntu $(lsb_release -s -c)/
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

If `Rserve` is running on the same host as the Web application, it will be started automatically.
To run Rserve on other hosts, place the following script in `/etc/init/rserve.conf`.
If necessary, replace "ubuntu" on the `setuid` with the name of another non-root user.

    description "Rserve"

    start on runlevel [2345]
    stop on runlevel [!2345]

    setuid ubuntu

    respawn

    script
    R --no-save --slave > /dev/null 2>&1 <<EOF
    library(Rserve)
    run.Rserve(interactive='no', remote='enable')
    EOF
    end script

The service will start automatically at boot but can be started at other times
using `sudo service rserve start`.

### Installing Tomcat

Install Tomcat.

    sudo apt-get install -y tomcat7

Configure ports for HTTP and HTTPS. The following assumes there's a reverse proxy
that listens on ports 80/443 and terminates SSL. See Apache installation/config below.

    --- /etc/tomcat7/server.xml.1   2013-11-06 16:17:00.813980000 +1000
    +++ /etc/tomcat7/server.xml     2013-11-13 16:55:40.444228660 +1000
    @@ -72,7 +72,8 @@
         <Connector port="8080" protocol="HTTP/1.1"
                    connectionTimeout="20000"
                    URIEncoding="UTF-8"
    -               redirectPort="8443" />
    +               redirectPort="8443"
    +               proxyPort="80" />
         <!-- A "Connector" using the shared thread pool-->
         <!--
         <Connector executor="tomcatThreadPool"
    @@ -90,6 +92,16 @@
                    clientAuth="false" sslProtocol="TLS" />
         -->
     
    +    <!-- Assmes there is a reverse proxy out front that handles SSL and
    +         sends us traffic on ports 8080/8443 for HTTP/HTTPS respectively. -->
    +    <Connector port="8443" protocol="HTTP/1.1"
    +               connectionTimeout="20000"
    +               URIEncoding="UTF-8"
    +               SSLEnabled="false"
    +               scheme="https"
    +               secure="true"
    +               proxyPort="443" />
    +
         <!-- Define an AJP 1.3 Connector on port 8009 -->
         <!--
         <Connector port="8009" protocol="AJP/1.3" redirectPort="8443" />

### Installing GeoServer

[GeoServer](http://geoserver.org/) is an open-source GIS server used to render map layers in OzTrack.
OzTrack has been tested with [GeoServer version 2.3.1](http://geoserver.org/display/GEOS/GeoServer+2.3.1).
For complete installation instructions, see the [GeoServer user manual](http://docs.geoserver.org/stable/en/user/).

The following commands install the GeoServer WAR distribution to Tomcat:

    wget 'http://downloads.sourceforge.net/geoserver/geoserver-2.3.1-war.zip' -P /tmp/
    sudo apt-get install -y unzip
    unzip -d /tmp/geoserver /tmp/geoserver-2.3.1-war.zip
    sudo service tomcat7 stop
    sudo unzip -d /var/lib/tomcat7/webapps/geoserver/ /tmp/geoserver/geoserver.war
    sudo chown -R tomcat7: /var/lib/tomcat7/webapps/geoserver/
    sudo service tomcat7 start

Log in to the GeoServer web administation interface at <http://localhost:8080/geoserver/web/>
using the default username/password, "admin"/"geoserver", and follow the instructions for configuring security.

### Installing Apache

The following commands install the Apache HTTP server and configure reverse proxying from
port 80 to port 8080.
Configuring HTTPS is optional and requires that you create and install an SSL certificate.
Note that you should modify the `ServerName` directive and port numbers to match your deployment.
Depending on your installation of Apache, you may need to place site configuration in either
`/etc/apache2/sites-available/oztrack.conf` or `/etc/apache2/sites-available/oztrack`.

    sudo apt-get install -y apache2
    sudo a2enmod proxy_http
    sudo service apache2 restart
    sudo tee /etc/apache2/sites-available/oztrack.conf > /dev/null << EOF
    <VirtualHost _default_:80>
      ServerName oztrack.org
      ProxyPreserveHost on
      ProxyPass /geoserver http://localhost:8080/geoserver nocanon retry=0
      ProxyPassReverse /geoserver http://localhost:8080/geoserver
      ProxyPass / http://localhost:8080/ nocanon retry=0
      ProxyPassReverse / http://localhost:8080/
    </VirtualHost>
    
    <VirtualHost _default_:443>
      ServerName https://oztrack.org
      SSLEngine on
      SSLCertificateFile /etc/apache2/ssl/oztrack.crt
      SSLCertificateKeyFile /etc/apache2/ssl/oztrack.key
      ProxyPreserveHost on
      ProxyPass /geoserver http://localhost:8443/geoserver nocanon retry=0
      ProxyPassReverse /geoserver http://localhost:8443/geoserver
      ProxyPass / http://localhost:8443/ nocanon retry=0
      ProxyPassReverse / http://localhost:8443/
    </VirtualHost>
    EOF
    sudo a2ensite oztrack
    sudo service apache2 reload

### Installing Shibboleth

Install Apache with Shibboleth modules.

    apt-get install apache2 libapache2-mod-shib2

Enable SSL and Shibboleth modules.

    sudo a2enmod ssl
    sudo a2enmod shib2 
    sudo service apache2 restart

Download AAF metadata signing certificate (either AAF Test Federation or AAF Production Federation).

    sudo wget https://ds.test.aaf.edu.au/distribution/metadata/aaf-metadata-cert.pem -O /etc/shibboleth/aaf-metadata-cert.pem
    sudo wget https://ds.aaf.edu.au/distribution/metadata/aaf-metadata-cert.pem -O /etc/shibboleth/aaf-metadata-cert.pem

Create service provider certificate

    sudo /etc/shibboleth/keygen.sh -f -o /etc/shibboleth -h oztrack.org -e https://oztrack.org/shibboleth

Configure Shibboleth with Entity ID, Discovery Service, and Metadata Provider values matching your
application and the AAF by editing `/etc/shibboleth/shibboleth2.xml`.

    --- /etc/shibboleth/shibboleth2.xml.1   2013-11-07 11:13:28.293980000 +1000
    +++ /etc/shibboleth/shibboleth2.xml     2013-11-07 11:58:01.557980000 +1000
    @@ -20,7 +20,7 @@
         -->
     
         <!-- The ApplicationDefaults element is where most of Shibboleth's SAML bits are defined. -->
    -    <ApplicationDefaults entityID="https://sp.example.org/shibboleth"
    +    <ApplicationDefaults entityID="https://oztrack.org/shibboleth"
                              REMOTE_USER="eppn persistent-id targeted-id">
     
             <!--
    @@ -40,8 +40,7 @@
                 (Set discoveryProtocol to "WAYF" for legacy Shibboleth WAYF support.)
                 You can also override entityID on /Login query string, or in RequestMap/htaccess.
                 -->
    -            <SSO entityID="https://idp.example.org/shibboleth"
    -                 discoveryProtocol="SAMLDS" discoveryURL="https://ds.example.org/DS/WAYF">
    +            <SSO discoveryProtocol="SAMLDS" discoveryURL="https://ds.test.aaf.edu.au/discovery/DS">
                   SAML2 SAML1
                 </SSO>
     
    @@ -83,6 +82,12 @@
             <MetadataProvider type="XML" file="partner-metadata.xml"/>
             -->
     
    +        <MetadataProvider type="XML" uri="https://ds.test.aaf.edu.au/distribution/metadata/metadata.aaf.signed.complete.xml"
    +             backingFilePath="metadata.aaf.xml" reloadInterval="7200">
    +           <MetadataFilter type="RequireValidUntil" maxValidityInterval="2419200"/>
    +           <MetadataFilter type="Signature" certificate="aaf-metadata-cert.pem"/>
    +        </MetadataProvider>
    +
             <!-- Map to extract attributes from SAML assertions. -->
             <AttributeExtractor type="XML" validate="true" path="attribute-map.xml"/>

Uncomment attribute map elements by editing `/etc/shibboleth/attribute-map.xml`.

    --- /etc/shibboleth/attribute-map.xml.1 2013-11-07 11:12:10.317980000 +1000
    +++ /etc/shibboleth/attribute-map.xml   2013-11-07 11:12:40.877980000 +1000
    @@ -52,7 +52,6 @@
         </Attribute>
     
         <!-- Some more eduPerson attributes, uncomment these to use them... -->
    -    <!--
         <Attribute name="urn:mace:dir:attribute-def:eduPersonPrimaryAffiliation" id="primary-affiliation">
             <AttributeDecoder xsi:type="StringAttributeDecoder" caseSensitive="false"/>
         </Attribute>
    @@ -75,10 +74,8 @@
     
         <Attribute name="urn:oid:1.3.6.1.4.1.5923.1.6.1.1" id="eduCourseOffering"/>
         <Attribute name="urn:oid:1.3.6.1.4.1.5923.1.6.1.2" id="eduCourseMember"/>
    -    -->
     
         <!--Examples of LDAP-based attributes, uncomment to use these... -->
    -    <!--
         <Attribute name="urn:mace:dir:attribute-def:cn" id="cn"/>
         <Attribute name="urn:mace:dir:attribute-def:sn" id="sn"/>
         <Attribute name="urn:mace:dir:attribute-def:givenName" id="givenName"/>
    @@ -132,6 +129,5 @@
         <Attribute name="urn:oid:2.5.4.11" id="ou"/>
         <Attribute name="urn:oid:2.5.4.15" id="businessCategory"/>
         <Attribute name="urn:oid:2.5.4.19" id="physicalDeliveryOfficeName"/>
    -    -->
     
     </Attributes>

Configure Apache by adding these lines to `/etc/apache2/sites-available/oztrack.conf`.

    <Location />
      ShibRequestSetting authType shibboleth
      ShibRequestSetting requireSession false
      require shibboleth
      AuthType shibboleth
      ShibUseHeaders on
    </Location>
     
    <Location /login/shibboleth>
      ShibRequestSetting authType shibboleth
      ShibRequestSetting requireSession true
      require shibboleth
      AuthType shibboleth
      ShibUseHeaders on
    </Location>

Restart all the services.

    service shibd restart
    service tomcat7 restart
    service apache2 restart

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
    # Rserve hosts to connect to for running R analyses
    org.oztrack.conf.rserveHosts=localhost,127.0.0.1
    # Number of Rserve processes to run on each host
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
