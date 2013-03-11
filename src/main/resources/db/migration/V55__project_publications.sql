create table publication (
    id bigserial primary key,
    project_id bigint not null references project (id),
    ordinal int not null,
    title text not null,
    url text not null
);

insert into publication (project_id, ordinal, title, url)
select id, 0, publicationtitle, publicationurl
from project
where
    publicationtitle is not null and publicationtitle <> '' and
    publicationurl is not null and publicationurl <> '';

alter table project drop publicationtitle, drop publicationurl;