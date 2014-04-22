create table projectvisit (
    id bigserial primary key,
    project_id bigint not null references project (id) on delete cascade,
    visittype text not null,
    visitdate timestamp without time zone not null
);