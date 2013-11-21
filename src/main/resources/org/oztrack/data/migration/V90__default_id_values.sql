alter table animal alter column id set default nextval('animalid_seq'::regclass);
alter table appuser alter column id set default nextval('userid_seq'::regclass);
alter table datafile alter column id set default nextval('datafileid_seq'::regclass);
alter table project alter column id set default nextval('projectid_seq'::regclass);
alter table rawpositionfix alter column id set default nextval('positionfixid_seq'::regclass);