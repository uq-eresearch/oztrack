-- add analysis_result_feature.resulttype column

alter table analysis_result_feature
add column resulttype text;

update analysis_result_feature
set resulttype = 'HOME_RANGE'
where analysis_id in (
    select id
    from analysis
    where analysistype in (
        'MCP',
        'KBB',
        'LOCOH',
        'AHULL',
        'KUD'
    )
);

update analysis_result_feature
set resulttype = 'HEAT_MAP'
where analysis_id in (
    select id
    from analysis
    where analysistype in (
        'HEATMAP_LINE',
        'HEATMAP_POINT'
    )
);

alter table analysis_result_feature
alter column resulttype set not null;

-- add datetime to analysis_result_feature

alter table analysis_result_feature add column datetime timestamp without time zone;

-- no long have unique constraint on analysis_result_feature (analysis_id, animal_id)

alter table analysis_result_feature
-- drop constraint analysis_result_feature_analysis_id_animal_id_key; -- postgresql 9.1
drop constraint analysis_result_feature_analysis_id_key; -- postgresql 8.4

-- add support for overall result attributes on analyses

alter table analysis_result_attribute
alter column feature_id drop not null,
add column analysis_id bigint references analysis (id) on delete cascade,
add constraint analysis_result_attribute_analysis_id_name_key unique (analysis_id, name),
add constraint analysis_result_attribute_analysis_id_feature_id_exclusive check (analysis_id is null or feature_id is null);
