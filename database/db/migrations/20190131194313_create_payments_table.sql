-- migrate:up
CREATE TABLE public.payments (
  id                    bigserial       not null,
  public_id             varchar(32)     not null,
  bill_id               bigint          not null,
  status                integer         not null,
  order_id              bigint          not null,
  redirect_url          varchar(300)    not null,
  created               timestamp       not null DEFAULT now(),
  updated               timestamp       not null DEFAULT now(),
  CONSTRAINT payments_pk PRIMARY KEY (id),
  CONSTRAINT payments_f1 FOREIGN KEY(bill_id) REFERENCES public.bills(id),
  CONSTRAINT payments_u2 UNIQUE (public_id),
  CONSTRAINT payments_u1 UNIQUE (bill_id)
);

CREATE INDEX payments_i1 ON public.payments (status);
CREATE INDEX payments_i2 ON public.payments (order_id);
CREATE INDEX payments_i3 ON public.payments (created);

-- migrate:down
DROP TABLE public.payments;