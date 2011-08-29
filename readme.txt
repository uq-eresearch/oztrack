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
