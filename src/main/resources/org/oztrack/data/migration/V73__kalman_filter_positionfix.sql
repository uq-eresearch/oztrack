-- position fixes created from kalman filter do not come from a data file
-- add project_id column as direct reference to project table

alter table positionfix alter column datafile_id drop not null;
alter table positionfix add column project_id bigint references project (id);
update positionfix set project_id = datafile.project_id from datafile where positionfix.datafile_id = datafile.id;
alter table positionfix alter column project_id set not null;
create index positionfix_project_id_idx on positionfix (project_id);

-- add positionfix.probable column

alter table positionfix add column probable boolean not null default false;

alter table positionfixlayer add column probable boolean;

update positionfixlayer set probable = positionfix.probable from positionfix where positionfixlayer.id = positionfix.id;

-- link sequence to positionfix (also on rawpositionfix)

alter table positionfix alter column id set default nextval('positionfixid_seq');