--
-- PostgreSQL database dump
--

-- Dumped from database version 17.5
-- Dumped by pg_dump version 17.5

-- Started on 2025-08-03 03:47:18

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 4940 (class 1262 OID 24939)
-- Name: app_aug; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE app_aug WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'English_United States.1252';


ALTER DATABASE app_aug OWNER TO postgres;

\connect app_aug

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 4941 (class 0 OID 0)
-- Dependencies: 4940
-- Name: DATABASE app_aug; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON DATABASE app_aug IS 'For 1st project our main app';


--
-- TOC entry 4 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: pg_database_owner
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO pg_database_owner;

--
-- TOC entry 4942 (class 0 OID 0)
-- Dependencies: 4
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: pg_database_owner
--

COMMENT ON SCHEMA public IS 'standard public schema';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 226 (class 1259 OID 24980)
-- Name: accessibility; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.accessibility (
    accessibility_id integer NOT NULL,
    accessibility_type text[] NOT NULL
);


ALTER TABLE public.accessibility OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 24979)
-- Name: accessibility_accessibility_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.accessibility_accessibility_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.accessibility_accessibility_id_seq OWNER TO postgres;

--
-- TOC entry 4943 (class 0 OID 0)
-- Dependencies: 225
-- Name: accessibility_accessibility_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.accessibility_accessibility_id_seq OWNED BY public.accessibility.accessibility_id;


--
-- TOC entry 224 (class 1259 OID 24971)
-- Name: place; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.place (
    place_id integer NOT NULL,
    place_name character varying NOT NULL,
    longitude character varying NOT NULL,
    latitude character varying[] NOT NULL,
    place_catagory character varying NOT NULL
);


ALTER TABLE public.place OWNER TO postgres;

--
-- TOC entry 228 (class 1259 OID 24996)
-- Name: place_feauture; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.place_feauture (
    id integer NOT NULL,
    place_id integer NOT NULL,
    accessibility_id integer NOT NULL,
    is_avaliable boolean
);


ALTER TABLE public.place_feauture OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 24995)
-- Name: place_feauture_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.place_feauture_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.place_feauture_id_seq OWNER TO postgres;

--
-- TOC entry 4944 (class 0 OID 0)
-- Dependencies: 227
-- Name: place_feauture_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.place_feauture_id_seq OWNED BY public.place_feauture.id;


--
-- TOC entry 223 (class 1259 OID 24970)
-- Name: place_place_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.place_place_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.place_place_id_seq OWNER TO postgres;

--
-- TOC entry 4945 (class 0 OID 0)
-- Dependencies: 223
-- Name: place_place_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.place_place_id_seq OWNED BY public.place.place_id;


--
-- TOC entry 222 (class 1259 OID 24962)
-- Name: review; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.review (
    review_id bigint NOT NULL,
    description character varying NOT NULL,
    rating integer NOT NULL,
    user_id bigint NOT NULL,
    place_id bigint NOT NULL
);


ALTER TABLE public.review OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 24961)
-- Name: review_review_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.review_review_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.review_review_id_seq OWNER TO postgres;

--
-- TOC entry 4946 (class 0 OID 0)
-- Dependencies: 221
-- Name: review_review_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.review_review_id_seq OWNED BY public.review.review_id;


--
-- TOC entry 220 (class 1259 OID 24950)
-- Name: token; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.token (
    token_id integer NOT NULL,
    user_id bigint NOT NULL,
    jwt_token character varying(255) NOT NULL,
    issued_at timestamp with time zone,
    expires_at timestamp with time zone NOT NULL,
    is_revoked boolean
);


ALTER TABLE public.token OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 24949)
-- Name: token_token_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.token_token_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.token_token_id_seq OWNER TO postgres;

--
-- TOC entry 4947 (class 0 OID 0)
-- Dependencies: 219
-- Name: token_token_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.token_token_id_seq OWNED BY public.token.token_id;


--
-- TOC entry 218 (class 1259 OID 24941)
-- Name: user; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."user" (
    user_id integer NOT NULL,
    user_name character varying,
    user_email character varying,
    password_hash character varying,
    user_role character varying
);


ALTER TABLE public."user" OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 24940)
-- Name: user_user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.user_user_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.user_user_id_seq OWNER TO postgres;

--
-- TOC entry 4948 (class 0 OID 0)
-- Dependencies: 217
-- Name: user_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.user_user_id_seq OWNED BY public."user".user_id;


--
-- TOC entry 4771 (class 2604 OID 24983)
-- Name: accessibility accessibility_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.accessibility ALTER COLUMN accessibility_id SET DEFAULT nextval('public.accessibility_accessibility_id_seq'::regclass);


--
-- TOC entry 4770 (class 2604 OID 24974)
-- Name: place place_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.place ALTER COLUMN place_id SET DEFAULT nextval('public.place_place_id_seq'::regclass);


--
-- TOC entry 4772 (class 2604 OID 24999)
-- Name: place_feauture id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.place_feauture ALTER COLUMN id SET DEFAULT nextval('public.place_feauture_id_seq'::regclass);


--
-- TOC entry 4769 (class 2604 OID 24965)
-- Name: review review_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.review ALTER COLUMN review_id SET DEFAULT nextval('public.review_review_id_seq'::regclass);


--
-- TOC entry 4768 (class 2604 OID 24953)
-- Name: token token_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.token ALTER COLUMN token_id SET DEFAULT nextval('public.token_token_id_seq'::regclass);


--
-- TOC entry 4767 (class 2604 OID 24944)
-- Name: user user_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."user" ALTER COLUMN user_id SET DEFAULT nextval('public.user_user_id_seq'::regclass);


--
-- TOC entry 4782 (class 2606 OID 24987)
-- Name: accessibility accessibility_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.accessibility
    ADD CONSTRAINT accessibility_pkey PRIMARY KEY (accessibility_id);


--
-- TOC entry 4784 (class 2606 OID 25001)
-- Name: place_feauture place_feauture_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.place_feauture
    ADD CONSTRAINT place_feauture_pkey PRIMARY KEY (id);


--
-- TOC entry 4780 (class 2606 OID 24978)
-- Name: place place_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.place
    ADD CONSTRAINT place_pkey PRIMARY KEY (place_id);


--
-- TOC entry 4778 (class 2606 OID 24969)
-- Name: review review_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.review
    ADD CONSTRAINT review_pkey PRIMARY KEY (review_id);


--
-- TOC entry 4776 (class 2606 OID 24955)
-- Name: token token_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.token
    ADD CONSTRAINT token_pkey PRIMARY KEY (token_id);


--
-- TOC entry 4774 (class 2606 OID 24948)
-- Name: user user_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."user"
    ADD CONSTRAINT user_pkey PRIMARY KEY (user_id);


--
-- TOC entry 4788 (class 2606 OID 25007)
-- Name: place_feauture accessibility_id ; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.place_feauture
    ADD CONSTRAINT "accessibility_id " FOREIGN KEY (accessibility_id) REFERENCES public.accessibility(accessibility_id);


--
-- TOC entry 4789 (class 2606 OID 25002)
-- Name: place_feauture place_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.place_feauture
    ADD CONSTRAINT place_id FOREIGN KEY (place_id) REFERENCES public.place(place_id);


--
-- TOC entry 4786 (class 2606 OID 25017)
-- Name: review place_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.review
    ADD CONSTRAINT place_id FOREIGN KEY (place_id) REFERENCES public.place(place_id) NOT VALID;


--
-- TOC entry 4785 (class 2606 OID 24956)
-- Name: token user_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.token
    ADD CONSTRAINT user_id FOREIGN KEY (user_id) REFERENCES public."user"(user_id);


--
-- TOC entry 4787 (class 2606 OID 25012)
-- Name: review user_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.review
    ADD CONSTRAINT user_id FOREIGN KEY (user_id) REFERENCES public."user"(user_id) NOT VALID;


-- Completed on 2025-08-03 03:47:18

--
-- PostgreSQL database dump complete
--

