-- migrate:up
CREATE SEQUENCE public.utility_payment_intents_seq MAXVALUE 999999 CYCLE;
CREATE TABLE public.utility_payment_intents (
  id                    bigserial       not null,
  uuid                  varchar(36)     not null,
  oc                    varchar(15)     not null DEFAULT 'P'||to_char(current_timestamp, 'YYYYMMDD')||lpad(nextval('public.utility_payment_intents_seq')::VARCHAR, 6, '0'),
  state                 integer         not null,
  email                 varchar(300)    null,
  created               timestamp       not null DEFAULT now(),
  updated               timestamp       not null DEFAULT now(),
  CONSTRAINT utility_payment_intents_pk PRIMARY KEY (id),
  CONSTRAINT utility_payment_intents_u1 UNIQUE (uuid),
  CONSTRAINT utility_payment_intents_u2 UNIQUE (oc)
);

CREATE INDEX utility_payment_intents_i1 ON public.utility_payment_intents (state);
CREATE INDEX utility_payment_intents_i2 ON public.utility_payment_intents (email);
CREATE INDEX utility_payment_intents_i3 ON public.utility_payment_intents (created);

-- migrate:down
DROP TABLE public.utility_payment_intents;
DROP SEQUENCE public.utility_payment_intents_seq;