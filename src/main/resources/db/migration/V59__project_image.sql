create table projectimage (
    id bigserial primary key,
    createuser_id bigint references appuser (id),
    createdate timestamp without time zone,
    updateuser_id bigint references appuser (id),
    updatedate timestamp without time zone,
    project_id bigint not null references project (id) on delete cascade,
    originalfilename text,
    datadirectorypath text,
    thumbnailpath text,
    thumbnailmimetype text,
    filepath text,
    filemimetype text
);