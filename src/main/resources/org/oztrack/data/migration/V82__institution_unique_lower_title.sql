drop index institution_title_key;
create unique index institution_title_key on institution (lower(title));