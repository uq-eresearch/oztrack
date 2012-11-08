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
set speciesname = null
where speciesname = 'Unknown';

update animal
set animaldescription = null
where animaldescription = 'Unknown';

alter table animal add column createdescription varchar;
update animal
    set (animaldescription, createdescription) = (null, regexp_replace(animaldescription, '^created in datafile upload:', 'Created from data file'))
    where animaldescription ~ '^created in datafile upload:';