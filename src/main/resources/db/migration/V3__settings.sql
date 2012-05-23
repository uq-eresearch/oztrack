CREATE TABLE settings (
    id bigint NOT NULL,
    abouttext text
);

ALTER TABLE public.settings OWNER TO oztrack;

CREATE SEQUENCE settings_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.settings_id_seq OWNER TO oztrack;

ALTER SEQUENCE settings_id_seq OWNED BY settings.id;

ALTER TABLE ONLY settings ALTER COLUMN id SET DEFAULT nextval('settings_id_seq'::regclass);

ALTER TABLE ONLY settings ADD CONSTRAINT settings_pkey PRIMARY KEY (id);

insert into settings (abouttext) values ('');
