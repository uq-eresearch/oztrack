create table project_contribution (
    id bigserial primary key,
    project_id bigint not null references project (id) on delete cascade,
    contributor_id bigint not null references person (id) on delete cascade,
    ordinal int not null
);