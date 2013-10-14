alter table person add column uuid uuid unique;

update person set uuid = uuid_generate_v4();

alter table person alter column uuid set not null;