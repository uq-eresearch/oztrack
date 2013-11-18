create table institution (
    id bigserial primary key,
    createdate timestamp without time zone,
    updatedate timestamp without time zone,
    createuser_id bigint references appuser (id) on delete no action,
    updateuser_id bigint references appuser (id) on delete no action,
    title character varying(255) unique not null,
    domainname character varying(255) unique
);

alter table person add column institution_id bigint references institution (id) on delete no action;

insert into institution (createdate, title)
select distinct now(), organisation
from person
where organisation is not null;

update person set institution_id = institution.id
from institution
where person.organisation is not null and institution.title = person.organisation;

alter table person drop column organisation;