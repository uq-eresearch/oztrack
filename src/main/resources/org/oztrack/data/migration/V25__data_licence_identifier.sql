alter table data_licence add column identifier text;

update data_licence set identifier = 'CC-BY' where title = 'Attribution';
update data_licence set identifier = 'CC-BY-SA' where title = 'Attribution-ShareAlike';
update data_licence set identifier = 'CC-BY-ND' where title = 'Attribution-NoDerivatives';
update data_licence set identifier = 'CC-BY-NC' where title = 'Attribution-NonCommercial';
update data_licence set identifier = 'CC-BY-NC-SA' where title = 'Attribution-NonCommercial-ShareAlike';
update data_licence set identifier = 'CC-BY-NC-ND' where title = 'Attribution-NonCommercial-NoDerivatives';
update data_licence set identifier = 'CC0' where title = 'CC0 (No Rights Reserved)';
update data_licence set identifier = 'PDM' where title = 'Public Domain Mark';

alter table data_licence alter column identifier set not null;