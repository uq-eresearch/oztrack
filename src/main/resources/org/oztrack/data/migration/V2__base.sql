SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET search_path = public, pg_catalog;
SET default_tablespace = '';
SET default_with_oids = false;

--
-- Name: acousticdetection; Type: TABLE; Schema: public; Owner: oztrack; Tablespace:
--

CREATE TABLE acousticdetection (
    id bigint NOT NULL,
    detectiontime timestamp without time zone NOT NULL,
    sensor1units character varying(255),
    sensor1value double precision,
    sensor2units character varying(255),
    sensor2value double precision,
    animal_id bigint,
    datafile_id bigint,
    receiverdeployment_id bigint
);


ALTER TABLE public.acousticdetection OWNER TO oztrack;

--
-- Name: acousticdetectionid_seq; Type: SEQUENCE; Schema: public; Owner: oztrack
--

CREATE SEQUENCE acousticdetectionid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.acousticdetectionid_seq OWNER TO oztrack;

--
-- Name: animal; Type: TABLE; Schema: public; Owner: oztrack; Tablespace:
--

CREATE TABLE animal (
    id bigint NOT NULL,
    createdate timestamp without time zone,
    updatedate timestamp without time zone,
    animaldescription character varying(255),
    animalname character varying(255),
    pingintervalseconds bigint,
    projectanimalid character varying(255),
    sensortransmitterid character varying(255),
    speciesname character varying(255),
    transmitterdeploydate timestamp without time zone,
    transmitterid character varying(255),
    transmittertypecode character varying(255),
    verifiedspeciesname character varying(255),
    createuser_id bigint,
    updateuser_id bigint,
    project_id bigint
);


ALTER TABLE public.animal OWNER TO oztrack;

--
-- Name: animalid_seq; Type: SEQUENCE; Schema: public; Owner: oztrack
--

CREATE SEQUENCE animalid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.animalid_seq OWNER TO oztrack;

--
-- Name: appuser; Type: TABLE; Schema: public; Owner: oztrack; Tablespace:
--

CREATE TABLE appuser (
    id bigint NOT NULL,
    dataspaceagentdescription character varying(255),
    dataspaceagenturi character varying(255),
    dataspaceagentupdatedate timestamp without time zone,
    email character varying(255),
    firstname character varying(255),
    lastname character varying(255),
    organisation character varying(255),
    password character varying(255),
    title character varying(255),
    username character varying(255) NOT NULL
);


ALTER TABLE public.appuser OWNER TO oztrack;

--
-- Name: datafile; Type: TABLE; Schema: public; Owner: oztrack; Tablespace:
--

CREATE TABLE datafile (
    id bigint NOT NULL,
    createdate timestamp without time zone,
    updatedate timestamp without time zone,
    contenttype character varying(255),
    detectioncount integer,
    filedescription text,
    firstdetectiondate timestamp without time zone,
    lastdetectiondate timestamp without time zone,
    localtimeconversionhours bigint,
    localtimeconversionrequired boolean,
    oztrackfilename character varying(255),
    singleanimalinfile boolean,
    datafilestatus character varying(255),
    statusmessage text,
    uploaddate timestamp without time zone,
    usergivenfilename character varying(255),
    createuser_id bigint,
    updateuser_id bigint,
    project_id bigint
);


ALTER TABLE public.datafile OWNER TO oztrack;

--
-- Name: datafileid_seq; Type: SEQUENCE; Schema: public; Owner: oztrack
--

CREATE SEQUENCE datafileid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.datafileid_seq OWNER TO oztrack;


--
-- Name: positionfix; Type: TABLE; Schema: public; Owner: oztrack; Tablespace:
--

CREATE TABLE positionfix (
    id bigint NOT NULL,
    hdop double precision,
    detectiontime timestamp without time zone NOT NULL,
    latitude character varying(255),
    locationgeometry geometry,
    longitude character varying(255),
    sensor1units character varying(255),
    sensor1value double precision,
    sensor2units character varying(255),
    sensor2value double precision,
    animal_id bigint,
    datafile_id bigint
);


ALTER TABLE public.positionfix OWNER TO oztrack;

--
-- Name: positionfixid_seq; Type: SEQUENCE; Schema: public; Owner: oztrack
--

CREATE SEQUENCE positionfixid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.positionfixid_seq OWNER TO oztrack;

--
-- Name: project; Type: TABLE; Schema: public; Owner: oztrack; Tablespace:
--

CREATE TABLE project (
    id bigint NOT NULL,
    createdate timestamp without time zone,
    updatedate timestamp without time zone,
    boundingbox geometry,
    datadirectorypath text,
    dataspaceuri text,
    dataspaceupdatedate timestamp without time zone,
    description text,
    detectioncount integer,
    firstdetectiondate timestamp without time zone,
    imagefilelocation character varying(255),
    isglobal boolean NOT NULL,
    lastdetectiondate timestamp without time zone,
    projecttype character varying(255),
    publicationtitle text,
    publicationurl text,
    rightsstatement character varying(255),
    spatialcoveragedescr character varying(255),
    speciescommonname character varying(255),
    speciesscientificname character varying(255),
    title character varying(255),
    createuser_id bigint,
    updateuser_id bigint,
    dataspaceagent_id bigint
);


ALTER TABLE public.project OWNER TO oztrack;

--
-- Name: project_user; Type: TABLE; Schema: public; Owner: oztrack; Tablespace:
--

CREATE TABLE project_user (
    role integer,
    user_id bigint NOT NULL,
    project_id bigint NOT NULL
);


ALTER TABLE public.project_user OWNER TO oztrack;

--
-- Name: projectid_seq; Type: SEQUENCE; Schema: public; Owner: oztrack
--

CREATE SEQUENCE projectid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.projectid_seq OWNER TO oztrack;

--
-- Name: rawacousticdetection; Type: TABLE; Schema: public; Owner: oztrack; Tablespace:
--

CREATE TABLE rawacousticdetection (
    id bigint NOT NULL,
    animalid character varying(255),
    codespace character varying(255),
    datetime timestamp without time zone,
    receivername character varying(255),
    receiversn character varying(255),
    sensor1 double precision,
    sensor2 double precision,
    stationlatitude character varying(255),
    stationlongitude character varying(255),
    stationname character varying(255),
    transmittername character varying(255),
    transmittersn character varying(255),
    units1 character varying(255),
    units2 character varying(255)
);


ALTER TABLE public.rawacousticdetection OWNER TO oztrack;

--
-- Name: rawpositionfix; Type: TABLE; Schema: public; Owner: oztrack; Tablespace:
--

CREATE TABLE rawpositionfix (
    id bigint NOT NULL,
    hdop double precision,
    animalid character varying(255),
    detectiontime timestamp without time zone,
    latitude character varying(255),
    locationgeometry geometry,
    longitude character varying(255),
    sensor1units character varying(255),
    sensor1value double precision,
    sensor2units character varying(255),
    sensor2value double precision
);


ALTER TABLE public.rawpositionfix OWNER TO oztrack;

--
-- Name: receiverdeployid_seq; Type: SEQUENCE; Schema: public; Owner: oztrack
--

CREATE SEQUENCE receiverdeployid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.receiverdeployid_seq OWNER TO oztrack;

--
-- Name: receiverdeployment; Type: TABLE; Schema: public; Owner: oztrack; Tablespace:
--

CREATE TABLE receiverdeployment (
    id bigint NOT NULL,
    createdate timestamp without time zone,
    updatedate timestamp without time zone,
    deploymentdate timestamp without time zone,
    originalid character varying(255),
    receiverdescription character varying(255),
    receivername character varying(255),
    retrievaldate timestamp without time zone,
    createuser_id bigint,
    updateuser_id bigint,
    project_id bigint,
    receiverlocation_id bigint
);


ALTER TABLE public.receiverdeployment OWNER TO oztrack;

--
-- Name: receiverlocatid_seq; Type: SEQUENCE; Schema: public; Owner: oztrack
--

CREATE SEQUENCE receiverlocatid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.receiverlocatid_seq OWNER TO oztrack;

--
-- Name: receiverlocation; Type: TABLE; Schema: public; Owner: oztrack; Tablespace:
--

CREATE TABLE receiverlocation (
    id bigint NOT NULL,
    createdate timestamp without time zone,
    updatedate timestamp without time zone,
    latitude character varying(255),
    locationdescription character varying(255),
    locationname character varying(255),
    longitude character varying(255),
    receiverarrayname character varying(255),
    createuser_id bigint,
    updateuser_id bigint
);


ALTER TABLE public.receiverlocation OWNER TO oztrack;

--
-- Name: sighting; Type: TABLE; Schema: public; Owner: oztrack; Tablespace:
--

CREATE TABLE sighting (
    id bigint NOT NULL,
    animaldescription text,
    comments text,
    contactemail character varying(255),
    contactname character varying(255),
    createddate timestamp without time zone,
    imagelocation character varying(255),
    latitude double precision NOT NULL,
    localitydescription text,
    longitude double precision NOT NULL,
    sightingdate timestamp without time zone,
    sightingtime character varying(255),
    speciescommonname character varying(255),
    speciesscientificname character varying(255)
);


ALTER TABLE public.sighting OWNER TO oztrack;

--
-- Name: sightingid_seq; Type: SEQUENCE; Schema: public; Owner: oztrack
--

CREATE SEQUENCE sightingid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.sightingid_seq OWNER TO oztrack;

--
-- Name: userid_seq; Type: SEQUENCE; Schema: public; Owner: oztrack
--

CREATE SEQUENCE userid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.userid_seq OWNER TO oztrack;


--
-- Name: acousticdetection_pkey; Type: CONSTRAINT; Schema: public; Owner: oztrack; Tablespace:
--

ALTER TABLE ONLY acousticdetection
    ADD CONSTRAINT acousticdetection_pkey PRIMARY KEY (id);


--
-- Name: animal_pkey; Type: CONSTRAINT; Schema: public; Owner: oztrack; Tablespace:
--

ALTER TABLE ONLY animal
    ADD CONSTRAINT animal_pkey PRIMARY KEY (id);


--
-- Name: appuser_pkey; Type: CONSTRAINT; Schema: public; Owner: oztrack; Tablespace:
--

ALTER TABLE ONLY appuser
    ADD CONSTRAINT appuser_pkey PRIMARY KEY (id);


--
-- Name: appuser_username_key; Type: CONSTRAINT; Schema: public; Owner: oztrack; Tablespace:
--

ALTER TABLE ONLY appuser
    ADD CONSTRAINT appuser_username_key UNIQUE (username);


--
-- Name: datafile_pkey; Type: CONSTRAINT; Schema: public; Owner: oztrack; Tablespace:
--

ALTER TABLE ONLY datafile
    ADD CONSTRAINT datafile_pkey PRIMARY KEY (id);

--
-- Name: positionfix_pkey; Type: CONSTRAINT; Schema: public; Owner: oztrack; Tablespace:
--

ALTER TABLE ONLY positionfix
    ADD CONSTRAINT positionfix_pkey PRIMARY KEY (id);


--
-- Name: project_pkey; Type: CONSTRAINT; Schema: public; Owner: oztrack; Tablespace:
--

ALTER TABLE ONLY project
    ADD CONSTRAINT project_pkey PRIMARY KEY (id);


--
-- Name: project_user_pkey; Type: CONSTRAINT; Schema: public; Owner: oztrack; Tablespace:
--

ALTER TABLE ONLY project_user
    ADD CONSTRAINT project_user_pkey PRIMARY KEY (project_id, user_id);


--
-- Name: rawacousticdetection_pkey; Type: CONSTRAINT; Schema: public; Owner: oztrack; Tablespace:
--

ALTER TABLE ONLY rawacousticdetection
    ADD CONSTRAINT rawacousticdetection_pkey PRIMARY KEY (id);


--
-- Name: rawpositionfix_pkey; Type: CONSTRAINT; Schema: public; Owner: oztrack; Tablespace:
--

ALTER TABLE ONLY rawpositionfix
    ADD CONSTRAINT rawpositionfix_pkey PRIMARY KEY (id);


--
-- Name: receiverdeployment_pkey; Type: CONSTRAINT; Schema: public; Owner: oztrack; Tablespace:
--

ALTER TABLE ONLY receiverdeployment
    ADD CONSTRAINT receiverdeployment_pkey PRIMARY KEY (id);


--
-- Name: receiverlocation_pkey; Type: CONSTRAINT; Schema: public; Owner: oztrack; Tablespace:
--

ALTER TABLE ONLY receiverlocation
    ADD CONSTRAINT receiverlocation_pkey PRIMARY KEY (id);


--
-- Name: sighting_pkey; Type: CONSTRAINT; Schema: public; Owner: oztrack; Tablespace:
--

ALTER TABLE ONLY sighting
    ADD CONSTRAINT sighting_pkey PRIMARY KEY (id);

--
-- Name: fk1af338e2a7fceb6b; Type: FK CONSTRAINT; Schema: public; Owner: oztrack
--

ALTER TABLE ONLY acousticdetection
    ADD CONSTRAINT fk1af338e2a7fceb6b FOREIGN KEY (datafile_id) REFERENCES datafile(id);


--
-- Name: fk1af338e2bf2a76b; Type: FK CONSTRAINT; Schema: public; Owner: oztrack
--

ALTER TABLE ONLY acousticdetection
    ADD CONSTRAINT fk1af338e2bf2a76b FOREIGN KEY (receiverdeployment_id) REFERENCES receiverdeployment(id);


--
-- Name: fk1af338e2d5f3fcb; Type: FK CONSTRAINT; Schema: public; Owner: oztrack
--

ALTER TABLE ONLY acousticdetection
    ADD CONSTRAINT fk1af338e2d5f3fcb FOREIGN KEY (animal_id) REFERENCES animal(id);


--
-- Name: fk38016131377e81a9; Type: FK CONSTRAINT; Schema: public; Owner: oztrack
--

ALTER TABLE ONLY project_user
    ADD CONSTRAINT fk38016131377e81a9 FOREIGN KEY (project_id) REFERENCES project(id);


--
-- Name: fk3801613157e8e9ab; Type: FK CONSTRAINT; Schema: public; Owner: oztrack
--

ALTER TABLE ONLY project_user
    ADD CONSTRAINT fk3801613157e8e9ab FOREIGN KEY (user_id) REFERENCES appuser(id);


--
-- Name: fk4e6fde34377e81a9; Type: FK CONSTRAINT; Schema: public; Owner: oztrack
--

ALTER TABLE ONLY receiverdeployment
    ADD CONSTRAINT fk4e6fde34377e81a9 FOREIGN KEY (project_id) REFERENCES project(id);


--
-- Name: fk4e6fde347a97752f; Type: FK CONSTRAINT; Schema: public; Owner: oztrack
--

ALTER TABLE ONLY receiverdeployment
    ADD CONSTRAINT fk4e6fde347a97752f FOREIGN KEY (createuser_id) REFERENCES appuser(id);


--
-- Name: fk4e6fde349b85ecc2; Type: FK CONSTRAINT; Schema: public; Owner: oztrack
--

ALTER TABLE ONLY receiverdeployment
    ADD CONSTRAINT fk4e6fde349b85ecc2 FOREIGN KEY (updateuser_id) REFERENCES appuser(id);


--
-- Name: fk4e6fde34bdcf01ab; Type: FK CONSTRAINT; Schema: public; Owner: oztrack
--

ALTER TABLE ONLY receiverdeployment
    ADD CONSTRAINT fk4e6fde34bdcf01ab FOREIGN KEY (receiverlocation_id) REFERENCES receiverlocation(id);


--
-- Name: fk50c8e2f97a97752f; Type: FK CONSTRAINT; Schema: public; Owner: oztrack
--

ALTER TABLE ONLY project
    ADD CONSTRAINT fk50c8e2f97a97752f FOREIGN KEY (createuser_id) REFERENCES appuser(id);


--
-- Name: fk50c8e2f99b85ecc2; Type: FK CONSTRAINT; Schema: public; Owner: oztrack
--

ALTER TABLE ONLY project
    ADD CONSTRAINT fk50c8e2f99b85ecc2 FOREIGN KEY (updateuser_id) REFERENCES appuser(id);


--
-- Name: fk50c8e2f9e3f76fcd; Type: FK CONSTRAINT; Schema: public; Owner: oztrack
--

ALTER TABLE ONLY project
    ADD CONSTRAINT fk50c8e2f9e3f76fcd FOREIGN KEY (dataspaceagent_id) REFERENCES appuser(id);


--
-- Name: fk5b38260ca7fceb6b; Type: FK CONSTRAINT; Schema: public; Owner: oztrack
--

ALTER TABLE ONLY positionfix
    ADD CONSTRAINT fk5b38260ca7fceb6b FOREIGN KEY (datafile_id) REFERENCES datafile(id);


--
-- Name: fk5b38260cd5f3fcb; Type: FK CONSTRAINT; Schema: public; Owner: oztrack
--

ALTER TABLE ONLY positionfix
    ADD CONSTRAINT fk5b38260cd5f3fcb FOREIGN KEY (animal_id) REFERENCES animal(id);


--
-- Name: fk6aab0026377e81a9; Type: FK CONSTRAINT; Schema: public; Owner: oztrack
--

ALTER TABLE ONLY datafile
    ADD CONSTRAINT fk6aab0026377e81a9 FOREIGN KEY (project_id) REFERENCES project(id);


--
-- Name: fk6aab00267a97752f; Type: FK CONSTRAINT; Schema: public; Owner: oztrack
--

ALTER TABLE ONLY datafile
    ADD CONSTRAINT fk6aab00267a97752f FOREIGN KEY (createuser_id) REFERENCES appuser(id);


--
-- Name: fk6aab00269b85ecc2; Type: FK CONSTRAINT; Schema: public; Owner: oztrack
--

ALTER TABLE ONLY datafile
    ADD CONSTRAINT fk6aab00269b85ecc2 FOREIGN KEY (updateuser_id) REFERENCES appuser(id);


--
-- Name: fk752a7a1c377e81a9; Type: FK CONSTRAINT; Schema: public; Owner: oztrack
--

ALTER TABLE ONLY animal
    ADD CONSTRAINT fk752a7a1c377e81a9 FOREIGN KEY (project_id) REFERENCES project(id);


--
-- Name: fk752a7a1c7a97752f; Type: FK CONSTRAINT; Schema: public; Owner: oztrack
--

ALTER TABLE ONLY animal
    ADD CONSTRAINT fk752a7a1c7a97752f FOREIGN KEY (createuser_id) REFERENCES appuser(id);


--
-- Name: fk752a7a1c9b85ecc2; Type: FK CONSTRAINT; Schema: public; Owner: oztrack
--

ALTER TABLE ONLY animal
    ADD CONSTRAINT fk752a7a1c9b85ecc2 FOREIGN KEY (updateuser_id) REFERENCES appuser(id);


--
-- Name: fkcca4d2e47a97752f; Type: FK CONSTRAINT; Schema: public; Owner: oztrack
--

ALTER TABLE ONLY receiverlocation
    ADD CONSTRAINT fkcca4d2e47a97752f FOREIGN KEY (createuser_id) REFERENCES appuser(id);


--
-- Name: fkcca4d2e49b85ecc2; Type: FK CONSTRAINT; Schema: public; Owner: oztrack
--

ALTER TABLE ONLY receiverlocation
    ADD CONSTRAINT fkcca4d2e49b85ecc2 FOREIGN KEY (updateuser_id) REFERENCES appuser(id);
