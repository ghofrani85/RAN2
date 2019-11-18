--
-- PostgreSQL database dump
--

-- Dumped from database version 10.5
-- Dumped by pg_dump version 10.4

-- Started on 2019-11-18 20:21:22

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 1 (class 3079 OID 12278)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2306 (class 0 OID 0)
-- Dependencies: 1
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 206 (class 1259 OID 16476)
-- Name: artefacts; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.artefacts (
    id integer NOT NULL,
    title character varying(255) NOT NULL,
    start character varying(255),
    endmark character varying(255),
    picturewidth character varying(255),
    pictureheight character varying(255),
    featureid integer NOT NULL,
    assetid integer NOT NULL
);


ALTER TABLE public.artefacts OWNER TO postgres;

--
-- TOC entry 205 (class 1259 OID 16474)
-- Name: artefacts_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.artefacts_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.artefacts_id_seq OWNER TO postgres;

--
-- TOC entry 2307 (class 0 OID 0)
-- Dependencies: 205
-- Name: artefacts_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.artefacts_id_seq OWNED BY public.artefacts.id;


--
-- TOC entry 202 (class 1259 OID 16437)
-- Name: assets; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.assets (
    id integer NOT NULL,
    title character varying(255) NOT NULL,
    description text,
    url character varying(2083),
    metadata character varying(255),
    astype smallint NOT NULL,
    last_change timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    last_change_userid integer
);


ALTER TABLE public.assets OWNER TO postgres;

--
-- TOC entry 201 (class 1259 OID 16435)
-- Name: assets_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.assets_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.assets_id_seq OWNER TO postgres;

--
-- TOC entry 2308 (class 0 OID 0)
-- Dependencies: 201
-- Name: assets_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.assets_id_seq OWNED BY public.assets.id;


--
-- TOC entry 198 (class 1259 OID 16400)
-- Name: authorities; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.authorities (
    userid integer NOT NULL,
    authority character varying(50) NOT NULL
);


ALTER TABLE public.authorities OWNER TO postgres;

--
-- TOC entry 204 (class 1259 OID 16454)
-- Name: features; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.features (
    id integer NOT NULL,
    title character varying(255),
    description text,
    parentid integer,
    updatedparent boolean NOT NULL,
    last_change timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    last_change_userid integer
);


ALTER TABLE public.features OWNER TO postgres;

--
-- TOC entry 203 (class 1259 OID 16452)
-- Name: features_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.features_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.features_id_seq OWNER TO postgres;

--
-- TOC entry 2309 (class 0 OID 0)
-- Dependencies: 203
-- Name: features_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.features_id_seq OWNED BY public.features.id;


--
-- TOC entry 213 (class 1259 OID 16564)
-- Name: featuresxassets; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.featuresxassets (
    featureid integer NOT NULL,
    assetid integer NOT NULL
);


ALTER TABLE public.featuresxassets OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 16853)
-- Name: featuresxfeatureartefacts; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.featuresxfeatureartefacts (
    featureid integer NOT NULL,
    featureartefactid integer NOT NULL
);


ALTER TABLE public.featuresxfeatureartefacts OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 16876)
-- Name: folders; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.folders (
    description character varying[],
    id integer NOT NULL,
    parentid integer,
    title character varying[] NOT NULL,
    updatedparent boolean NOT NULL
);


ALTER TABLE public.folders OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 16594)
-- Name: passwordresettokens; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.passwordresettokens (
    id integer NOT NULL,
    token character varying(50) NOT NULL,
    userid integer NOT NULL,
    expirationdate date
);


ALTER TABLE public.passwordresettokens OWNER TO postgres;

--
-- TOC entry 216 (class 1259 OID 16592)
-- Name: passwordresettokens_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.passwordresettokens_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.passwordresettokens_id_seq OWNER TO postgres;

--
-- TOC entry 2310 (class 0 OID 0)
-- Dependencies: 216
-- Name: passwordresettokens_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.passwordresettokens_id_seq OWNED BY public.passwordresettokens.id;


--
-- TOC entry 208 (class 1259 OID 16497)
-- Name: products; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.products (
    id integer NOT NULL,
    title character varying(255),
    description text,
    last_change timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    last_change_userid integer
);


ALTER TABLE public.products OWNER TO postgres;

--
-- TOC entry 207 (class 1259 OID 16495)
-- Name: products_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.products_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.products_id_seq OWNER TO postgres;

--
-- TOC entry 2311 (class 0 OID 0)
-- Dependencies: 207
-- Name: products_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.products_id_seq OWNED BY public.products.id;


--
-- TOC entry 212 (class 1259 OID 16549)
-- Name: productsxfeatures; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.productsxfeatures (
    productid integer NOT NULL,
    featureid integer NOT NULL
);


ALTER TABLE public.productsxfeatures OWNER TO postgres;

--
-- TOC entry 200 (class 1259 OID 16410)
-- Name: projects; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.projects (
    id integer NOT NULL,
    title character varying(255),
    description text,
    userid integer NOT NULL,
    parentid integer,
    updatedparent boolean NOT NULL,
    last_change timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    last_change_userid integer,
    up_vote integer,
    down_vote integer
);


ALTER TABLE public.projects OWNER TO postgres;

--
-- TOC entry 199 (class 1259 OID 16408)
-- Name: projects_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.projects_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.projects_id_seq OWNER TO postgres;

--
-- TOC entry 2312 (class 0 OID 0)
-- Dependencies: 199
-- Name: projects_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.projects_id_seq OWNED BY public.projects.id;


--
-- TOC entry 211 (class 1259 OID 16534)
-- Name: projectsxfeatures; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.projectsxfeatures (
    projectid integer NOT NULL,
    featureid integer NOT NULL
);


ALTER TABLE public.projectsxfeatures OWNER TO postgres;

--
-- TOC entry 210 (class 1259 OID 16519)
-- Name: projectsxproducts; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.projectsxproducts (
    projectid integer NOT NULL,
    productid integer NOT NULL
);


ALTER TABLE public.projectsxproducts OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 16838)
-- Name: projectsxratedusers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.projectsxratedusers (
    ratedprojectid integer NOT NULL,
    rateduserid integer NOT NULL
);


ALTER TABLE public.projectsxratedusers OWNER TO postgres;

--
-- TOC entry 209 (class 1259 OID 16512)
-- Name: tracking; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tracking (
    itemid integer NOT NULL,
    trtype smallint NOT NULL,
    changemade timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    text text
);


ALTER TABLE public.tracking OWNER TO postgres;

--
-- TOC entry 197 (class 1259 OID 16388)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id integer NOT NULL,
    username character varying(60) NOT NULL,
    password character varying(60) NOT NULL,
    enabled boolean NOT NULL,
    locked boolean NOT NULL,
    email character varying(255),
    registrationdate date NOT NULL,
    totaldatavolume bigint,
    dailyuploadvolume bigint,
    last_change timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    last_change_userid integer
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 196 (class 1259 OID 16386)
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_id_seq OWNER TO postgres;

--
-- TOC entry 2313 (class 0 OID 0)
-- Dependencies: 196
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- TOC entry 215 (class 1259 OID 16581)
-- Name: verificationtokens; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.verificationtokens (
    id integer NOT NULL,
    token character varying(50) NOT NULL,
    userid integer NOT NULL,
    expirationdate date
);


ALTER TABLE public.verificationtokens OWNER TO postgres;

--
-- TOC entry 214 (class 1259 OID 16579)
-- Name: verificationtokens_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.verificationtokens_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.verificationtokens_id_seq OWNER TO postgres;

--
-- TOC entry 2314 (class 0 OID 0)
-- Dependencies: 214
-- Name: verificationtokens_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.verificationtokens_id_seq OWNED BY public.verificationtokens.id;


--
-- TOC entry 2117 (class 2604 OID 16479)
-- Name: artefacts id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.artefacts ALTER COLUMN id SET DEFAULT nextval('public.artefacts_id_seq'::regclass);


--
-- TOC entry 2113 (class 2604 OID 16440)
-- Name: assets id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.assets ALTER COLUMN id SET DEFAULT nextval('public.assets_id_seq'::regclass);


--
-- TOC entry 2115 (class 2604 OID 16457)
-- Name: features id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.features ALTER COLUMN id SET DEFAULT nextval('public.features_id_seq'::regclass);


--
-- TOC entry 2122 (class 2604 OID 16597)
-- Name: passwordresettokens id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.passwordresettokens ALTER COLUMN id SET DEFAULT nextval('public.passwordresettokens_id_seq'::regclass);


--
-- TOC entry 2118 (class 2604 OID 16500)
-- Name: products id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products ALTER COLUMN id SET DEFAULT nextval('public.products_id_seq'::regclass);


--
-- TOC entry 2111 (class 2604 OID 16413)
-- Name: projects id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.projects ALTER COLUMN id SET DEFAULT nextval('public.projects_id_seq'::regclass);


--
-- TOC entry 2109 (class 2604 OID 16391)
-- Name: users id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- TOC entry 2121 (class 2604 OID 16584)
-- Name: verificationtokens id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.verificationtokens ALTER COLUMN id SET DEFAULT nextval('public.verificationtokens_id_seq'::regclass);


--
-- TOC entry 2132 (class 2606 OID 16484)
-- Name: artefacts artefacts_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.artefacts
    ADD CONSTRAINT artefacts_pkey PRIMARY KEY (id);


--
-- TOC entry 2128 (class 2606 OID 16446)
-- Name: assets assets_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.assets
    ADD CONSTRAINT assets_pkey PRIMARY KEY (id);


--
-- TOC entry 2130 (class 2606 OID 16463)
-- Name: features features_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.features
    ADD CONSTRAINT features_pkey PRIMARY KEY (id);


--
-- TOC entry 2142 (class 2606 OID 16568)
-- Name: featuresxassets featuresxassets_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.featuresxassets
    ADD CONSTRAINT featuresxassets_pkey PRIMARY KEY (featureid, assetid);


--
-- TOC entry 2150 (class 2606 OID 16857)
-- Name: featuresxfeatureartefacts featuresxfeatureartefacts_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.featuresxfeatureartefacts
    ADD CONSTRAINT featuresxfeatureartefacts_pkey PRIMARY KEY (featureid, featureartefactid);


--
-- TOC entry 2152 (class 2606 OID 16883)
-- Name: folders folders_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.folders
    ADD CONSTRAINT folders_pkey PRIMARY KEY (id);


--
-- TOC entry 2146 (class 2606 OID 16599)
-- Name: passwordresettokens passwordresettokens_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.passwordresettokens
    ADD CONSTRAINT passwordresettokens_pkey PRIMARY KEY (id);


--
-- TOC entry 2134 (class 2606 OID 16506)
-- Name: products products_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_pkey PRIMARY KEY (id);


--
-- TOC entry 2140 (class 2606 OID 16553)
-- Name: productsxfeatures productsxfeatures_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.productsxfeatures
    ADD CONSTRAINT productsxfeatures_pkey PRIMARY KEY (productid, featureid);


--
-- TOC entry 2126 (class 2606 OID 16419)
-- Name: projects projects_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.projects
    ADD CONSTRAINT projects_pkey PRIMARY KEY (id);


--
-- TOC entry 2138 (class 2606 OID 16538)
-- Name: projectsxfeatures projectsxfeatures_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.projectsxfeatures
    ADD CONSTRAINT projectsxfeatures_pkey PRIMARY KEY (projectid, featureid);


--
-- TOC entry 2136 (class 2606 OID 16523)
-- Name: projectsxproducts projectsxproducts_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.projectsxproducts
    ADD CONSTRAINT projectsxproducts_pkey PRIMARY KEY (projectid, productid);


--
-- TOC entry 2148 (class 2606 OID 16842)
-- Name: projectsxratedusers projectsxratedusers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.projectsxratedusers
    ADD CONSTRAINT projectsxratedusers_pkey PRIMARY KEY (ratedprojectid, rateduserid);


--
-- TOC entry 2124 (class 2606 OID 16394)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 2144 (class 2606 OID 16586)
-- Name: verificationtokens verificationtokens_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.verificationtokens
    ADD CONSTRAINT verificationtokens_pkey PRIMARY KEY (id);


--
-- TOC entry 2162 (class 2606 OID 16490)
-- Name: artefacts artefacts_assetid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.artefacts
    ADD CONSTRAINT artefacts_assetid_fkey FOREIGN KEY (assetid) REFERENCES public.assets(id);


--
-- TOC entry 2161 (class 2606 OID 16485)
-- Name: artefacts artefacts_featureid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.artefacts
    ADD CONSTRAINT artefacts_featureid_fkey FOREIGN KEY (featureid) REFERENCES public.features(id);


--
-- TOC entry 2158 (class 2606 OID 16447)
-- Name: assets assets_last_change_userid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.assets
    ADD CONSTRAINT assets_last_change_userid_fkey FOREIGN KEY (last_change_userid) REFERENCES public.users(id);


--
-- TOC entry 2154 (class 2606 OID 16403)
-- Name: authorities authorities_userid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.authorities
    ADD CONSTRAINT authorities_userid_fkey FOREIGN KEY (userid) REFERENCES public.users(id);


--
-- TOC entry 2160 (class 2606 OID 16469)
-- Name: features features_last_change_userid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.features
    ADD CONSTRAINT features_last_change_userid_fkey FOREIGN KEY (last_change_userid) REFERENCES public.users(id);


--
-- TOC entry 2159 (class 2606 OID 16464)
-- Name: features features_parentid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.features
    ADD CONSTRAINT features_parentid_fkey FOREIGN KEY (parentid) REFERENCES public.features(id);


--
-- TOC entry 2171 (class 2606 OID 16574)
-- Name: featuresxassets featuresxassets_assetid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.featuresxassets
    ADD CONSTRAINT featuresxassets_assetid_fkey FOREIGN KEY (assetid) REFERENCES public.assets(id);


--
-- TOC entry 2170 (class 2606 OID 16569)
-- Name: featuresxassets featuresxassets_featureid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.featuresxassets
    ADD CONSTRAINT featuresxassets_featureid_fkey FOREIGN KEY (featureid) REFERENCES public.features(id);


--
-- TOC entry 2177 (class 2606 OID 16863)
-- Name: featuresxfeatureartefacts featuresxfeatureartefacts_featureartefactid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.featuresxfeatureartefacts
    ADD CONSTRAINT featuresxfeatureartefacts_featureartefactid_fkey FOREIGN KEY (featureartefactid) REFERENCES public.features(id);


--
-- TOC entry 2176 (class 2606 OID 16858)
-- Name: featuresxfeatureartefacts featuresxfeatureartefacts_featureid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.featuresxfeatureartefacts
    ADD CONSTRAINT featuresxfeatureartefacts_featureid_fkey FOREIGN KEY (featureid) REFERENCES public.features(id);


--
-- TOC entry 2173 (class 2606 OID 16600)
-- Name: passwordresettokens passwordresettokens_userid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.passwordresettokens
    ADD CONSTRAINT passwordresettokens_userid_fkey FOREIGN KEY (userid) REFERENCES public.users(id);


--
-- TOC entry 2163 (class 2606 OID 16507)
-- Name: products products_last_change_userid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_last_change_userid_fkey FOREIGN KEY (last_change_userid) REFERENCES public.users(id);


--
-- TOC entry 2169 (class 2606 OID 16559)
-- Name: productsxfeatures productsxfeatures_featureid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.productsxfeatures
    ADD CONSTRAINT productsxfeatures_featureid_fkey FOREIGN KEY (featureid) REFERENCES public.features(id);


--
-- TOC entry 2168 (class 2606 OID 16554)
-- Name: productsxfeatures productsxfeatures_productid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.productsxfeatures
    ADD CONSTRAINT productsxfeatures_productid_fkey FOREIGN KEY (productid) REFERENCES public.products(id);


--
-- TOC entry 2157 (class 2606 OID 16430)
-- Name: projects projects_last_change_userid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.projects
    ADD CONSTRAINT projects_last_change_userid_fkey FOREIGN KEY (last_change_userid) REFERENCES public.users(id);


--
-- TOC entry 2156 (class 2606 OID 16425)
-- Name: projects projects_parentid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.projects
    ADD CONSTRAINT projects_parentid_fkey FOREIGN KEY (parentid) REFERENCES public.projects(id);


--
-- TOC entry 2155 (class 2606 OID 16420)
-- Name: projects projects_userid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.projects
    ADD CONSTRAINT projects_userid_fkey FOREIGN KEY (userid) REFERENCES public.users(id);


--
-- TOC entry 2167 (class 2606 OID 16544)
-- Name: projectsxfeatures projectsxfeatures_featureid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.projectsxfeatures
    ADD CONSTRAINT projectsxfeatures_featureid_fkey FOREIGN KEY (featureid) REFERENCES public.features(id);


--
-- TOC entry 2166 (class 2606 OID 16539)
-- Name: projectsxfeatures projectsxfeatures_projectid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.projectsxfeatures
    ADD CONSTRAINT projectsxfeatures_projectid_fkey FOREIGN KEY (projectid) REFERENCES public.projects(id);


--
-- TOC entry 2165 (class 2606 OID 16529)
-- Name: projectsxproducts projectsxproducts_productid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.projectsxproducts
    ADD CONSTRAINT projectsxproducts_productid_fkey FOREIGN KEY (productid) REFERENCES public.products(id);


--
-- TOC entry 2164 (class 2606 OID 16524)
-- Name: projectsxproducts projectsxproducts_projectid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.projectsxproducts
    ADD CONSTRAINT projectsxproducts_projectid_fkey FOREIGN KEY (projectid) REFERENCES public.projects(id);


--
-- TOC entry 2174 (class 2606 OID 16843)
-- Name: projectsxratedusers projectsxratedusers_ratedprojectid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.projectsxratedusers
    ADD CONSTRAINT projectsxratedusers_ratedprojectid_fkey FOREIGN KEY (ratedprojectid) REFERENCES public.projects(id);


--
-- TOC entry 2175 (class 2606 OID 16848)
-- Name: projectsxratedusers projectsxratedusers_rateduserid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.projectsxratedusers
    ADD CONSTRAINT projectsxratedusers_rateduserid_fkey FOREIGN KEY (rateduserid) REFERENCES public.users(id);


--
-- TOC entry 2153 (class 2606 OID 16395)
-- Name: users users_last_change_userid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_last_change_userid_fkey FOREIGN KEY (last_change_userid) REFERENCES public.users(id);


--
-- TOC entry 2172 (class 2606 OID 16587)
-- Name: verificationtokens verificationtokens_userid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.verificationtokens
    ADD CONSTRAINT verificationtokens_userid_fkey FOREIGN KEY (userid) REFERENCES public.users(id);


-- Completed on 2019-11-18 20:21:23

--
-- PostgreSQL database dump complete
--

