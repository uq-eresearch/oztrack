alter table person drop constraint person_createuser_id_fkey;
alter table person add  constraint person_createuser_id_fkey
foreign key (createuser_id) REFERENCES appuser(id) on delete set null;

alter table person drop constraint person_updateuser_id_fkey;
alter table person add  constraint person_updateuser_id_fkey
foreign key (updateuser_id) REFERENCES appuser(id) on delete set null;