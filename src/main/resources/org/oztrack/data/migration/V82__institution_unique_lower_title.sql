alter table institution drop constraint if exists institution_title_key;
drop index if exists institution_title_key;
create unique index institution_title_key on institution (lower(title));