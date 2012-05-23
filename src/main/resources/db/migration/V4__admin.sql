alter table appuser add column admin boolean;
update appuser set admin = false;