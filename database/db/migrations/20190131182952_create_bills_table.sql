
-- migrate:up
CREATE TABLE public.bills (
  id                    bigserial       not null,
  public_id             varchar(32)     not null,
  status                integer         not null,
  payment               integer         not null,
  utility               varchar(300)    not null,
  collector             varchar(100)    not null,
  email                 varchar(300)    null,
  identifier            varchar(300)    not null,
  amount                numeric(12,2)   not null,
  due_date              varchar(100)    null,
  transaction_id        varchar(100)    null,
  created               timestamp       not null DEFAULT now(),
  updated               timestamp       not null DEFAULT now(),
  CONSTRAINT bills_pk PRIMARY KEY (id),
  CONSTRAINT bills_u1 UNIQUE (public_id)
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