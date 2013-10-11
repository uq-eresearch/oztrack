alter table institution drop constraint if exists institution_domainname_key;
drop index if exists institution_domainname_key;
create unique index institution_domainname_key on institution (lower(domainname));