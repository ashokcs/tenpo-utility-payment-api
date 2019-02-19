
-- migrate:up
CREATE TABLE public.utility_payment_eft (
  id                    bigserial       not null,
  status                varchar(100)    not null,
  transaction_id        bigint          not null,
  public_id             varchar(32)     not null,
  notify_id             varchar(32)     not null,
  order_id              varchar(100)    not null,
  url                   varchar(300)    not null,
  created               timestamp       not null DEFAULT now(),
  updated               timestamp       not null DEFAULT now(),
  CONSTRAINT utility_payment_eft_pk PRIMARY KEY (id),
  CONSTRAINT utility_payment_eft_f1 FOREIGN KEY(transaction_id) REFERENCES public.utility_payment_transactions(id),
  CONSTRAINT utility_payment_eft_u1 UNIQUE (transaction_id),
  CONSTRAINT utility_payment_eft_u2 UNIQUE (public_id),
  CONSTRAINT utility_payment_eft_u3 UNIQUE (notify_id)
);

CREATE INDEX utility_payment_eft_i1 ON public.utility_payment_eft (status);
CREATE INDEX utility_payment_eft_i2 ON public.utility_payment_eft (order_id);
CREATE INDEX utility_payment_eft_i3 ON public.utility_payment_eft (created);

-- migrate:down
DROP TABLE public.utility_payment_eft;