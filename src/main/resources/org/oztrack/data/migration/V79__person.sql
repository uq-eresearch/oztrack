create table person (
    id bigserial primary key,
    createdate timestamp without time zone,
    updatedate timestamp without time zone,
    createuser_id bigint references appuser (id) on delete no action,
    updateuser_id bigint references appuser (id) on delete no action,
    title character varying(255),
    firstname character varying(255),
    lastname character varying(255),
    email character varying(255) not null,
    organisation character varying(255),
    description character varying(255),
    dataspaceagenturi character varying(255),
    dataspaceagentupdatedate timestamp without time zone
);

insert into person (
    createdate,
    createuser_id,
    title,
    firstname,
    lastname,
    email,
    organisation,
    description,
    dataspaceagenturi,
    dataspaceagentupdatedate
)
select
    createdate,
    id,
    title,
    firstname,
    lastname,
    email,
    organisation,
    dataspaceagentdescription,
    dataspaceagenturi,
    dataspaceagentupdatedate
from appuser;

alter table appuser
    add column updatedate timestamp without time zone,
    add column person_id bigint references person (id) on delete no action,
    drop column email,
    drop column title,
    drop column firstname,
    drop column lastname,
    drop column organisation,
    drop column dataSpaceAgentDescription,
    drop column dataSpaceAgentURI,
    drop column dataSpaceAgentUpdateDate;

update appuser set person_id = person.id from person where appuser.id = person.createuser_id;
alter table appuser alter column person_id set not null;