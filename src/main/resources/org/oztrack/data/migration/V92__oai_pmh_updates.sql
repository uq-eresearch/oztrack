alter table project
add column updatedateforoaipmh timestamp without time zone,
add column includeinoaipmh boolean not null default false;

alter table person
add column updatedateforoaipmh timestamp without time zone,
add column includeinoaipmh boolean not null default false;

alter table institution
add column updatedateforoaipmh timestamp without time zone,
add column includeinoaipmh boolean not null default false;

update project set updatedateforoaipmh = updatedate;
update person set updatedateforoaipmh = updatedate;
update institution set updatedateforoaipmh = updatedate;

update project set includeinoaipmh = true
where id in (select project_id from project_contribution);

update person set includeinoaipmh = true
where id in (select contributor_id from project_contribution);

update institution set includeinoaipmh = true
where id in (
	select institution_id
	from person_institution
	where person_id in (select contributor_id from project_contribution)
);