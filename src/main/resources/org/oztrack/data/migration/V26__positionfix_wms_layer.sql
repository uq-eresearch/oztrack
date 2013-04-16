create view positionfixlayer as
select
    positionfix.id as id,
    project.id as project_id,
    animal_id,
    detectiontime,
    locationgeometry,
    deleted
from
    positionfix,
    datafile,
    project
where
    positionfix.datafile_id = datafile.id and
    datafile.project_id = project.id;