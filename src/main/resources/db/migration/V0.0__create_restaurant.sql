create sequence public.restaurant_sequence;

alter sequence public.restaurant_sequence OWNER To saginijoy;

CREATE TABLE public.restaurant
(
  id bigint NOT NULL,
  name character varying(255) COLLATE pg_catalog."default",
  CONSTRAINT restaurant_pkey PRIMARY KEY (id)
)
  WITH (
  OIDS = FALSE
       )
  TABLESPACE pg_default;

ALTER TABLE public.restaurant
  OWNER to saginijoy;

-- create table review and sequence
CREATE SEQUENCE public.review_sequence;

ALTER SEQUENCE public.review_sequence
OWNER TO saginijoy;

CREATE TABLE public.review
(
  id bigint NOT NULL,
  comment character varying(255) COLLATE pg_catalog."default",
  restaurant_id bigint,
  CONSTRAINT review_pkey PRIMARY KEY (id),
  CONSTRAINT fk70ry7cuti298yxet366rynxch FOREIGN KEY (restaurant_id)
    REFERENCES public.restaurant (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
)
  WITH (
  OIDS = FALSE
       )
  TABLESPACE pg_default;

ALTER TABLE public.review
  OWNER to saginijoy;