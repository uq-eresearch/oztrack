drop view positionfixlayer;
create table positionfixlayer as
select
    positionfix.id as id,
    project.id as project_id,
    positionfix.animal_id as animal_id,
    positionfix.detectiontime as detectiontime,
    positionfix.locationgeometry as locationgeometry,
    positionfix.deleted as deleted,
    animal.colour as colour
from
    positionfix,
    animal,
    project
where
    positionfix.animal_id = animal.id and
    animal.project_id = project.id;