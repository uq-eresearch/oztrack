drop view trajectorylayer;
drop view positionfixnumbered;

create table positionfixnumbered as
select
    positionfix.id as id,
    project.id as project_id,
    positionfix.animal_id as animal_id,
    positionfix.detectiontime as detectiontime,
    positionfix.locationgeometry as locationgeometry,
    animal.colour as colour,
    row_number() over (partition by project.id, positionfix.animal_id order by positionfix.detectiontime) as row_number
from
    positionfix positionfix
    inner join animal on positionfix.animal_id = animal.id
    inner join project on animal.project_id = project.id
where
    not(positionfix.deleted);

create table trajectorylayer as
select
    positionfix1.id as id,
    positionfix1.project_id as project_id,
    positionfix1.animal_id as animal_id,
    positionfix1.detectiontime as startdetectiontime,
    positionfix2.detectiontime as enddetectiontime,
    positionfix1.colour as colour,
    ST_MakeLine(positionfix1.locationgeometry, positionfix2.locationgeometry) as trajectorygeometry
from
    positionfixnumbered positionfix1
    inner join positionfixnumbered positionfix2 on
        positionfix1.project_id = positionfix2.project_id and
        positionfix1.animal_id = positionfix2.animal_id and
        positionfix1.row_number + 1 = positionfix2.row_number;