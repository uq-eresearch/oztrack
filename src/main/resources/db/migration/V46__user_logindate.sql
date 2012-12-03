create table appuser_logindate (
    user_id bigint not null references appuser (id) on delete cascade,
    logindate timestamp without time zone not null
);