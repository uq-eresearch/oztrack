alter table positionfix alter column animal_id set not null;
alter table positionfix alter column datafile_id set not null;
alter table positionfix alter column locationgeometry set not null;
alter table positionfix alter column longitude set not null;
alter table positionfix alter column latitude set not null;

create index positionfix_deleted on positionfix(deleted);
create index positionfix_detectiontime on positionfix(detectiontime);

alter table datafile alter column project_id set not null;

alter table animal alter column project_id set not null;
alter table animal alter column animalname set not null;
alter table animal alter column projectanimalid set not null;

create index animal_projectanimalid on animal(projectanimalid);

alter table project alter column title set not null;

create index project_data_licence_id on project(data_licence_id);