-- migrate:up
CREATE TABLE public.transferencia_payments (
  id                    bigserial       not null,
  bill_id               bigint          not null,
  status                varchar(100)    not null,
  public_id             varchar(32)     not null,
  notify_id             varchar(32)     not null,
  mc_order_id           varchar(100)    not null,
  url                   varchar(300)    not null,
  created               timestamp       not null DEFAULT now(),
  updated               timestamp       not null DEFAULT now(),
  CONSTRAINT transferencia_payments_pk PRIMARY KEY (id),
  CONSTRAINT transferencia_payments_f1 FOREIGN KEY(bill_id) REFERENCES public.bills(id),
  CONSTRAINT transferencia_payments_u1 UNIQUE (bill_id),
  CONSTRAINT transferencia_payments_u2 UNIQUE (public_id),
  CONSTRAINT transferencia_payments_u3 UNIQUE (notify_id)
);

CREATE INDEX transferencia_payments_i1 ON public.transferencia_payments (status);
CREATE INDEX transferencia_payments_i2 ON public.transferencia_payments (mc_order_id);
CREATE INDEX transferencia_payments_i3 ON public.transferencia_payments (created);

-- migrate:down
DROP TABLE public.transferencia_payments;