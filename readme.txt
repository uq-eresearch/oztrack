Installing GIS Packages
--------------------------------------------------------------------------------

These packages are required for both PostGIS and R spatial functionality.

The "traditional" yum repository has very old versions of these packages. To get the later versions,
enlist this repository to yum by running:

rpm -Uvh http://elgis.argeo.org/repos/5/elgis-release-5-5_0.noarch.rpm

then yum install in this order, using the appropriate architecture (with current version at time of writing):
 geos   (3.2.2)
 geos-devel(3.2.2)  // will install geos as a dependency
 proj   (4.7.0)
 proj-devel (4.7.0) // will install proj as a dependency
 postgis

The gdal packages are a little too up to date (1.8) and won't build properly yet when R's rgdal(see below) uses them.
So use the rpms of the previous version (may need to wget the files first):

 rpm -Uvh http://elgis.argeo.org/repos/5/elgis/x86_64/gdal-1.7.2-5_0.el5.elgis.x86_64.rpm
 rpm -Uvh http://elgis.argeo.org/repos/5/elgis/x86_64/gdal-devel-1.7.2-5_0.el5.elgis.x86_64.rpm

However, the gdal package has loads of dependencies. One way to deal quickly is to do a yum install on 1.8 first to get the dependencies, then
yum uninstall, and install via rpm. Yum installing dependencies seems to take care of things.

To test successful installation, on the commmand line you should get a response from:
geos-config
gdal-config
proj
R will be looking for them later.

Setting up the database
--------------------------------------------------------------------------------
The following commands are used on a Linux machine; we should also document the
process for setting up the database on Windows for developers on that platform.

Run something like the following commands:

    -- Create database with PL/pgSQL support
    sudo -u postgres psql -c "create user oztrack with password 'ozadmin';"
    sudo -u postgres psql -c "create database oztrack with owner oztrack;"
    psql -U oztrack -d oztrack -c "create language plpgsql;"

    -- Run the PostGIS initialisation scripts: need to run postgis.sql as postgres
    -- because only superuser can create c functions; afterwards, we change owner
    -- on the resulting tables/views and subsequently connect as normal user.
    psql -U postgres -d oztrack -f /usr/share/pgsql/contrib/postgis.sql
    psql -U postgres -d oztrack -f /usr/share/pgsql/contrib/spatial_ref_sys.sql

    sudo -u postgres psql -d oztrack -c "alter table geometry_columns owner to oztrack;"
    sudo -u postgres psql -d oztrack -c "alter table spatial_ref_sys owner to oztrack;"
    sudo -u postgres psql -d oztrack -c "alter view geography_columns owner to oztrack;"

    -- Out own tables should be created on first run by Hibernate
See http://postgis.refractions.net/documentation/manual-1.5/ch02.html#id2565921

Installing R (including Rserve)
--------------------------------------------------------------------------------
You can just install R from the EPEL package repository on CentOS:

    yum install R

There are further libraries used in this project that are installed by running
install.packages in the R interpreter (run the "R" command from Linux console, as root with -E switch).

    install.packages(c("Rserve"), dependencies=TRUE)

Note that the following command takes a while (eg over 10 minutes) because it
downloads, compiles, tests, and installs a large number of dependencies.

    install.packages(c("sp","ade4","adehabitatHR","adehabitatMA","maptools","shapefiles","rgdal"), dependencies=TRUE)

Note: it may be necessary to add a repos argument (eg repos="http://cran.cnr.berkeley.edu/")
when executing install.packages, but this caused an error when run on CentOS.

Note: you will need the 'gdal' Red Hat package installed in order to install the
'rgdal' R package (ie run yum install gdal).

Running Rserve
--------------------------------------------------------------------------------
To run Rserve daemon, execute the following from your Linux console:

    R CMD Rserve

The resulting Rserve process will listen on port 6311.

See http://www.rforge.net/Rserve/faq.html#start
