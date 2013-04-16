create table srs (
    id bigserial primary key,
    identifier text unique not null,
    title text unique not null
);
SELECT AddGeometryColumn('srs', 'bounds', 4326, 'POLYGON', 2);