create unique index appuser_email_idx on appuser (lower(email));

alter table appuser drop constraint appuser_aafid_key;
create unique index appuser_aafid_idx on appuser (lower(aafid));

alter table appuser drop constraint appuser_username_key;
create unique index appuser_username_idx on appuser (lower(username));