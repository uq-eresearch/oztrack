alter table animal add column colour varchar;

update animal set colour =
    case
        when id % 12 =  0 then '#8DD3C7'
        when id % 12 =  1 then '#FFFFB3'
        when id % 12 =  2 then '#BEBADA'
        when id % 12 =  3 then '#FB8072'
        when id % 12 =  4 then '#80B1D3'
        when id % 12 =  5 then '#FDB462'
        when id % 12 =  6 then '#B3DE69'
        when id % 12 =  7 then '#FCCDE5'
        when id % 12 =  8 then '#D9D9D9'
        when id % 12 =  9 then '#BC80BD'
        when id % 12 = 10 then '#CCEBC5'
        when id % 12 = 11 then '#FFED6F'
    end;

alter table animal alter column colour set not null;

drop view positionfixlayer;
create view positionfixlayer as
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

drop view trajectorylayer;
drop view positionfixnumbered;

create view positionfixnumbered as
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

create view trajectorylayer as
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