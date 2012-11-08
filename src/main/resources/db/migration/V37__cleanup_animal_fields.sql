update animal
    set animalname = 'Unknown'
    where animalname is null or animalname = '';
alter table animal alter column animalname set not null;
alter table animal add constraint animalname_not_empty check (animalname != '');
    
update animal
    set projectanimalid = 'Unknown'
    where projectanimalid is null or projectanimalid = '';
alter table animal alter column projectanimalid set not null;
alter table animal add constraint projectanimalid_not_empty check (projectanimalid != '');

update animal
    set colour =
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
        end
    where colour is null or colour = '';
alter table animal alter column colour set not null;
alter table animal add constraint colour_not_empty check (colour != '');

update animal
set speciesname = null
where speciesname = 'Unknown';

update animal
set animaldescription = null
where animaldescription = 'Unknown';

alter table animal add column createdescription varchar;
update animal
    set (animaldescription, createdescription) = (null, regexp_replace(animaldescription, '^created in datafile upload:', 'Created from data file'))
    where animaldescription ~ '^created in datafile upload:';