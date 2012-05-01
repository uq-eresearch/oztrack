This software is copyright The University of Queensland.

This software is distributed under the GNU GENERAL PUBLIC LICENSE Version 2. See the COPYING file for detail.

Installing GIS Packages
--------------------------------------------------------------------------------

These packages are required for both PostGIS and R spatial functionality.

First, add the Enterprise Linux GIS (ELGIS) yum repository. The URL used in the
following command depends on the version of Red Hat that you're running: see
instructions on http://elgis.argeo.org/.

    rpm -Uvh http://elgis.argeo.org/repos/6/elgis-release-6-6_0.noarch.rpm

Install the following packages (note they should be installed in this order):

    yum install geos-devel # will install geos as a dependency
    yum install proj-devel # will install proj as a dependency
    yum install postgis
    yum install gdal-devel
 
To test successful installation, on the commmand line you should get a response from:

    geos-config
    gdal-config
    proj

Setting up the database
--------------------------------------------------------------------------------

To install PostgreSQL, use the following commands:

    yum install postgresql.x86_64
    yum install postgresql-server.x86_64
    yum install postgresql-devel.x86_64
    service postgresql initdb
    chkconfig postgresql on
    service postgresql start

Remember to sort out authentication:

    $EDITOR /var/lib/pgsql/data/pg_hba.conf

Run something like the following commands:

    # Create database with PL/pgSQL support
    psql -U postgres -c "create user oztrack with password 'ozadmin';"
    psql -U postgres -c "create database oztrack with owner oztrack;"
    psql -U oztrack -d oztrack -c "create language plpgsql;"

    # Run the PostGIS initialisation scripts: need to run postgis.sql as postgres
    # because only superuser can create c functions; afterwards, we change owner
    # on the resulting tables/views and subsequently connect as normal user.
    psql -U postgres -d oztrack -f /usr/share/pgsql/contrib/postgis.sql
    psql -U postgres -d oztrack -f /usr/share/pgsql/contrib/spatial_ref_sys.sql

    psql -U postgres -d oztrack -c "alter table geometry_columns owner to oztrack;"
    psql -U postgres -d oztrack -c "alter table spatial_ref_sys owner to oztrack;"
    psql -U postgres -d oztrack -c "alter view geography_columns owner to oztrack;"

    # Our own tables should be created on first run by Hibernate

See http://postgis.refractions.net/documentation/manual-1.5/ch02.html#id2565921

Installing R (including Rserve)
--------------------------------------------------------------------------------
You can just install R from the EPEL package repository on CentOS:

    yum install R

There are further libraries used in this project that are installed by running
`install.packages` in the R interpreter (run the `R` command from Linux console, as root with `-E` switch).

    install.packages(c("Rserve"), dependencies=TRUE)

Note that the following command takes a while (eg over 10 minutes) because it
downloads, compiles, tests, and installs a large number of dependencies.

    install.packages(c("sp"), dependencies=TRUE)
    install.packages(c("ade4"), dependencies=TRUE)
    install.packages(c("adehabitatHR"), dependencies=TRUE)
    install.packages(c("adehabitatMA"), dependencies=TRUE)
    install.packages(c("maptools"), dependencies=TRUE)
    install.packages(c("shapefiles"), dependencies=TRUE)
    install.packages(c("rgdal"), dependencies=TRUE)
 
Note that it may be necessary to add a repos argument (eg `repos="http://cran.cnr.berkeley.edu/"`)
when executing `install.packages`, but this caused an error when run on CentOS.
You will need the `gdal` Red Hat package installed in order to install the
`rgdal` R package (ie run `yum install gdal`).

Running Rserve
--------------------------------------------------------------------------------
To run Rserve daemon, execute the following from your Linux console:

    R CMD Rserve

The resulting Rserve process will listen on port 6311.

See http://www.rforge.net/Rserve/faq.html#start

Setting up Properties
--------------------------------------------------------------------------------
The `application.properties` file contains some important values that need to be
set for OzTrack to run correctly.

* `dataDir`: If this isn't set, OzTrack will use the user.home environment variable
  (possibly of the server user environment) to store files. Ensure that such a
  directory is available and can be written to.
* `dataSpaceURL`: This is the URL that project collection records will be written
  to. A username and password must be provided in this file for the functionality
  to work.
