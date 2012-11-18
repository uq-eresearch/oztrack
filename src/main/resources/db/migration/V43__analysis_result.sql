alter table analysis add column status text;
update analysis set status = 'NEW';
alter table analysis alter column status set not null;

alter table analysis add column message text;
alter table analysis add column resultfilepath text;