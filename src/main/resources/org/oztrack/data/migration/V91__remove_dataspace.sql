alter table project
drop column if exists dataspaceuri,
drop column if exists dataspaceupdatedate,
drop column if exists dataspaceagent_id;

alter table person
drop column if exists dataspaceagenturi,
drop column if exists dataspaceagentupdatedate;