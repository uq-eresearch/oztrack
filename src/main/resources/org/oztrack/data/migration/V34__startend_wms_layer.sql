create view startendlayer as
select
    animal.project_id as project_id,
    animal.id as animal_id,
    startpositionfix.detectiontime as startdetectiontime,
    startpositionfix.locationgeometry as startlocationgeometry,
    endpositionfix.detectiontime as enddetectiontime,
    endpositionfix.locationgeometry as endlocationgeometry
from
    animal
    left outer join positionfix startpositionfix on
        startpositionfix.id = (
            select positionfix.id
            from positionfix
            where positionfix.animal_id = animal.id
            order by detectiontime asc
            limit 1
        )
    left outer join positionfix endpositionfix on
        endpositionfix.id = (
            select positionfix.id
            from positionfix
            where positionfix.animal_id = animal.id
            order by detectiontime desc
            limit 1
        );