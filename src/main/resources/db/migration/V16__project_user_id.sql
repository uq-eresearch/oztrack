alter table project_user drop constraint project_user_pkey;
create unique index on project_user (project_id, user_id);
alter table project_user add column id bigserial primary key;