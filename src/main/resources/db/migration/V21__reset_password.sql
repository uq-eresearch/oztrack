alter table appuser
    add column passwordresettoken text unique,
    add column passwordresetexpiresat timestamp without time zone;