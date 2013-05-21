alter table positionfix
alter column argosclass type integer
using case
    when argosclass = 'CLASS_Z' then 0
    when argosclass = 'CLASS_B' then 1
    when argosclass = 'CLASS_A' then 2
    when argosclass = 'CLASS_0' then 3
    when argosclass = 'CLASS_1' then 4
    when argosclass = 'CLASS_2' then 5
    when argosclass = 'CLASS_3' then 6
    else null
end;

alter table rawpositionfix
alter column argosclass type integer
using case
    when argosclass = 'CLASS_Z' then 0
    when argosclass = 'CLASS_B' then 1
    when argosclass = 'CLASS_A' then 2
    when argosclass = 'CLASS_0' then 3
    when argosclass = 'CLASS_1' then 4
    when argosclass = 'CLASS_2' then 5
    when argosclass = 'CLASS_3' then 6
    else null
end;