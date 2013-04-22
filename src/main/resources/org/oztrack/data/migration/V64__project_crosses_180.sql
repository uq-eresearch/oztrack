alter table project add column crosses180 boolean not null default false;

delete from analysis_parameter where name = 'is180';