
-- migrate:up
CREATE SEQUENCE public.utility_payment_transactions_seq MAXVALUE 999 CYCLE;
CREATE TABLE public.utility_payment_transactions (
  id              bigserial       not null,
  status          varchar(100)    not null,
  public_id       varchar(32)     not null,
  buy_order       bigint          not null default (to_char(current_timestamp, 'YYYYMMDDHH24MISS')||lpad(nextval('public.utility_payment_transactions_seq')::VARCHAR, 3, '0'))::bigint,
  amount          numeric(12,2)   not null,
  payment_method  varchar(100)    null,
  email           varchar(300)    null,
  created         timestamp       not null default now(),
  updated         timestamp       not null default now(),
  CONSTRAINT utility_payment_transactions_pk PRIMARY KEY (id),
  CONSTRAINT utility_payment_transactions_u1 UNIQUE (public_id),
  CONSTRAINT utility_payment_transactions_u2 UNIQUE (buy_order)
);

CREATE INDEX utility_payment_transactions_i1 ON public.utility_payment_transactions (status);
CREATE INDEX utility_payment_transactions_i2 ON public.utility_payment_transactions (payment_method);
CREATE INDEX utility_payment_transactions_i3 ON public.utility_payment_transactions (email);
CREATE INDEX utility_payment_transactions_i4 ON public.utility_payment_transactions (created);

-- migrate:down
DROP TABLE public.utility_payment_transactions;
DROP SEQUENCE public.utility_payment_transactions_seq;