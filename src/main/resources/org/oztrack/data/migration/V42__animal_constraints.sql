alter table animal alter column animalname drop not null;
alter table animal drop constraint animalname_not_empty;

alter table animal alter column projectanimalid drop not null;
alter table animal drop constraint projectanimalid_not_empty;