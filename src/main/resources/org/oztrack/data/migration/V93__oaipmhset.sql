create table project_oaipmhset (
    project_id bigint not null references project (id) on delete cascade,
    oaipmhset varchar not null,
    unique(project_id, oaipmhset)
);
create table person_oaipmhset (
    person_id bigint not null references person (id) on delete cascade,
    oaipmhset varchar not null,
    unique(person_id, oaipmhset)
);
create table institution_oaipmhset (
    institution_id bigint not null references institution (id) on delete cascade,
    oaipmhset varchar not null,
    unique(institution_id, oaipmhset)
);
