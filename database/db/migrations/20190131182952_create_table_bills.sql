
-- migrate:up
CREATE SEQUENCE public.bills_buy_order_seq MAXVALUE 9999 CYCLE;
CREATE TABLE public.bills (
  id                    bigserial       not null,
  public_id             varchar(32)     not null,
  buy_order             bigint          not null default ('1'||to_char(current_timestamp, 'YYYYMMDDHH24MISS')||lpad(nextval('public.bills_buy_order_seq')::VARCHAR, 4, '0'))::bigint,
  status                varchar(100)    not null,
  utility               varchar(300)    not null,
  collector             varchar(100)    not null,
  identifier            varchar(300)    not null,
  amount                numeric(12,2)   not null,
  due_date              varchar(100)    not null,
  transaction_id        varchar(100)    not null,
  payment               varchar(100)    null,
  email                 varchar(300)    null,
  created               timestamp       not null default now(),
  updated               timestamp       not null default now(),
  CONSTRAINT bills_pk PRIMARY KEY (id),
  CONSTRAINT bills_u1 UNIQUE (public_id),
  CONSTRAINT bills_u2 UNIQUE (buy_order)
);

CREATE INDEX bills_i1 ON public.bills (status);
CREATE INDEX bills_i2 ON public.bills (payment);
CREATE INDEX bills_i3 ON public.bills (utility);
CREATE INDEX bills_i4 ON public.bills (collector);
CREATE INDEX bills_i5 ON public.bills (email);
CREATE INDEX bills_i6 ON public.bills (identifier);
CREATE INDEX bills_i7 ON public.bills (transaction_id);
CREATE INDEX bills_i8 ON public.bills (created);

-- migrate:down
DROP TABLE public.bills;
DROP SEQUENCE public.bills_buy_order_seq;