create table person_institution (
    person_id bigint not null references person (id) on delete cascade,
    institution_id bigint not null references institution (id) on delete cascade,
    unique(person_id, institution_id)
);

insert into person_institution (person_id, institution_id)
select id, institution_id from person where institution_id is not null;

alter table person drop column institution_id;