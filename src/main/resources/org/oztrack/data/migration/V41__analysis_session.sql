alter table analysis alter column createuser_id drop not null;
alter table analysis alter column createdate drop not null;
alter table analysis alter column updateuser_id drop not null;
alter table analysis alter column updatedate drop not null;
alter table analysis add column createsession text;