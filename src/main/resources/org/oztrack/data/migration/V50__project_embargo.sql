alter table project add column access text;
update project set access = (case when isglobal then 'OPEN' else 'CLOSED' end);
alter table project alter column access set not null;

alter table project drop column isglobal;

alter table project add column embargodate timestamp without time zone;

create index project_access_idx on project(access);