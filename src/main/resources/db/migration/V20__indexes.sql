create index animal_project_id_idx on animal(project_id);
create index datafile_project_id_idx on datafile(project_id);
create index positionfix_datafile_id_idx on positionfix(datafile_id);
create index positionfix_animal_id_idx on positionfix(animal_id);
create index project_isglobal_idx on project(isglobal);
create index project_user_user_id_idx on project_user(user_id);
