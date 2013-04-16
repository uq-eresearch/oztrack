alter table project add column srsidentifier text;
update project set srsidentifier = 'EPSG:3577';