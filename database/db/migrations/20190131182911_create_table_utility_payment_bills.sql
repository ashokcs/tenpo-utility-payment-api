
-- migrate:up
CREATE TABLE public.utility_payment_bills (
  id                    bigserial       not null,
  status                varchar(100)    not null,
  transaction_id        bigint          not null,
  utility               varchar(300)    not null,
  collector             varchar(100)    not null,
  category              varchar(100)    not null,
  identifier            varchar(300)    not null,
  mc_code               varchar(100)    not null,
  amount                numeric(12,2)   not null,
  due_date              varchar(100)    not null,
  created               timestamp       not null default now(),
  updated               timestamp       not null default now(),
  CONSTRAINT utility_payment_bills_pk PRIMARY KEY (id),
  CONSTRAINT utility_payment_bills_f1 FOREIGN KEY(transaction_id) REFERENCES public.utility_payment_transactions(id),
  CONSTRAINT utility_payment_bills_u1 UNIQUE (transaction_id)
);

CREATE INDEX utility_payment_bills_i1 ON public.utility_payment_bills (status);
CREATE INDEX utility_payment_bills_i2 ON public.utility_payment_bills (utility);
CREATE INDEX utility_payment_bills_i3 ON public.utility_payment_bills (collector);
CREATE INDEX utility_payment_bills_i4 ON public.utility_payment_bills (category);
CREATE INDEX utility_payment_bills_i5 ON public.utility_payment_bills (identifier);
CREATE INDEX utility_payment_bills_i6 ON public.utility_payment_bills (mc_code);
CREATE INDEX utility_payment_bills_i7 ON public.utility_payment_bills (created);

-- migrate:down
DROP TABLE public.utility_payment_bills;