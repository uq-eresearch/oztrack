create table analysis_result_feature (
    id bigserial primary key,
    analysis_id bigint not null references analysis (id) on delete cascade,
    animal_id bigint not null references animal (id) on delete cascade,
    the_geom geometry,
    unique(analysis_id, animal_id)
);

create table analysis_result_attribute (
    id bigserial primary key,
    feature_id bigint not null references analysis_result_feature (id) on delete cascade,
    name text not null,
    value text not null,
    unique(feature_id, name)
);