alter table project_user drop role;
alter table project_user add column role text;
update project_user set role = 'ADMIN';
alter table project_user alter column role set not null;