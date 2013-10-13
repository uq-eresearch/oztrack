alter table person_institution add column ordinal int;

update person_institution
set ordinal = v.ordinal
from (
    select person_id, institution_id, (row_number() over w) - 1 as ordinal
    from person_institution
    window w as (partition by person_id order by institution_id)
) as v
where person_institution.person_id = v.person_id and person_institution.institution_id = v.institution_id;

alter table person_institution alter column ordinal set not null;