-- migrate:up
CREATE SEQUENCE public.utility_payments_seq MAXVALUE 999999 CYCLE;
CREATE TABLE public.utility_payments (
  id                    bigserial       not null,
  uuid                  varchar(36)     not null,
  oc                    varchar(15)     not null DEFAULT 'P'||to_char(current_timestamp, 'YYYYMMDD')||lpad(nextval('public.utility_payments_seq')::VARCHAR, 6, '0'),
  state                 integer         not null,
  created_at            timestamp       not null DEFAULT now(),
  updated_at            timestamp       not null DEFAULT now(),
  CONSTRAINT utility_payments_pk PRIMARY KEY (id),
  CONSTRAINT utility_payments_u1 UNIQUE (uuid),
  CONSTRAINT utility_payments_u2 UNIQUE (oc)
);

-- migrate:down
DROP TABLE public.utility_payments;
DROP SEQUENCE public.utility_payments_seq;