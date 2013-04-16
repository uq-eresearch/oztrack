create table analysis (
    id bigserial primary key,
    createdate timestamp without time zone not null,
    updatedate timestamp without time zone not null,
    createuser_id bigint not null references appuser (id) on delete no action,
    updateuser_id bigint not null references appuser (id) on delete no action,
    project_id bigint not null references project (id) on delete cascade,
    analysistype text not null,
    fromdate timestamp without time zone,
    todate timestamp without time zone
);

create table analysis_animal (
    analysis_id bigint not null references analysis (id) on delete cascade,
    animal_id bigint not null references animal (id) on delete cascade
);

create table analysis_parameter (
    id bigserial primary key,
    analysis_id bigint not null references analysis (id) on delete cascade,
    name text not null,
    value text not null,
    unique(analysis_id, name)
);