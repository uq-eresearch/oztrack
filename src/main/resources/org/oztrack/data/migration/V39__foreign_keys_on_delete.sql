alter table animal drop constraint "fk752a7a1c377e81a9";
alter table animal add constraint "fk752a7a1c377e81a9" foreign key (project_id) references project(id) on delete cascade;

alter table animal drop constraint "fk752a7a1c7a97752f";
alter table animal add constraint "fk752a7a1c7a97752f" foreign key (createuser_id) references appuser(id) on delete no action;

alter table animal drop constraint "fk752a7a1c9b85ecc2";
alter table animal add constraint "fk752a7a1c9b85ecc2" foreign key (updateuser_id) references appuser(id) on delete no action;

alter table datafile drop constraint "fk6aab0026377e81a9";
alter table datafile add constraint "fk6aab0026377e81a9" foreign key (project_id) references project(id) on delete cascade;

alter table datafile drop constraint "fk6aab00267a97752f";
alter table datafile add constraint "fk6aab00267a97752f" foreign key (createuser_id) references appuser(id) on delete no action;

alter table datafile drop constraint "fk6aab00269b85ecc2";
alter table datafile add constraint "fk6aab00269b85ecc2" foreign key (updateuser_id) references appuser(id) on delete no action;

alter table positionfix drop constraint "fk5b38260ca7fceb6b";
alter table positionfix add constraint "fk5b38260ca7fceb6b" foreign key (datafile_id) references datafile(id) on delete cascade;

alter table positionfix drop constraint "fk5b38260cd5f3fcb";
alter table positionfix add constraint "fk5b38260cd5f3fcb" foreign key (animal_id) references animal(id) on delete cascade;

alter table project drop constraint "fk50c8e2f97a97752f";
alter table project add constraint "fk50c8e2f97a97752f" foreign key (createuser_id) references appuser(id) on delete no action;

alter table project drop constraint "fk50c8e2f99b85ecc2";
alter table project add constraint "fk50c8e2f99b85ecc2" foreign key (updateuser_id) references appuser(id) on delete no action;

alter table project drop constraint "fk50c8e2f9e3f76fcd";
alter table project add constraint "fk50c8e2f9e3f76fcd" foreign key (dataspaceagent_id) references appuser(id) on delete no action;

alter table project drop constraint "project_data_licence_id_fkey";
alter table project add constraint "project_data_licence_id_fkey" foreign key (data_licence_id) references data_licence(id) on delete no action;

alter table project_user drop constraint "fk38016131377e81a9";
alter table project_user add constraint "fk38016131377e81a9" foreign key (project_id) references project(id) on delete cascade;

alter table project_user drop constraint "fk3801613157e8e9ab";
alter table project_user add constraint "fk3801613157e8e9ab" foreign key (user_id) references appuser(id) on delete no action;