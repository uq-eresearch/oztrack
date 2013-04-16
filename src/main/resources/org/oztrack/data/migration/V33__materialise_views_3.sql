create index positionfixlayer_index_1 on positionfixlayer (project_id, animal_id, deleted);
create index positionfixnumbered_index_1 on positionfixnumbered (project_id, animal_id, row_number);
create index trajectorylayer_index_1 on trajectorylayer (project_id, animal_id);