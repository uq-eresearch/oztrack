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
    sudo -u postgres psql -d oztrack -f /usr/share/postgresql/8.4/contrib/postgis-1.5/postgis.sql
    sudo -u postgres psql -d oztrack -f /usr/share/postgresql/8.4/contrib/postgis_comments.sql
    sudo -u postgres psql -d oztrack -c "alter table geometry_columns owner to oztrack;"
    sudo -u postgres psql -d oztrack -c "alter table spatial_ref_sys owner to oztrack;"
    sudo -u postgres psql -d oztrack -c "alter view geography_columns owner to oztrack;"
    psql -U oztrack -d oztrack -f /usr/share/postgresql/8.4/contrib/postgis-1.5/spatial_ref_sys.sql

    -- Out own tables should be created on first run by Hibernate

See http://postgis.refractions.net/documentation/manual-1.5/ch02.html#id2565921

Installing R (including Rserve)
--------------------------------------------------------------------------------
You can just install R from the EPEL package repository on CentOS:

    yum install R

There are further libraries used in this project that are installed by running
intall.packages in the R interpreter (run the "R" command from Linux console).

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
