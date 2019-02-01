
-- migrate:up
CREATE TABLE public.bills (
  id                    bigserial       not null,
  public_id             varchar(32)     not null,
  status                integer         not null,
  utility_id            bigint          not null,
  identifier            varchar(300)    not null,
  amount                numeric(12,2)   not null,
  email                 varchar(300)    null,
  created               timestamp       not null DEFAULT now(),
  updated               timestamp       not null DEFAULT now(),
  CONSTRAINT bills_pk PRIMARY KEY (id),
  CONSTRAINT bills_u1 UNIQUE (public_id)
);

CREATE INDEX bills_i1 ON public.bills (status);
CREATE INDEX bills_i2 ON public.bills (utility_id);
CREATE INDEX bills_i3 ON public.bills (identifier);
CREATE INDEX bills_i4 ON public.bills (email);
CREATE INDEX bills_i5 ON public.bills (created);

-- migrate:down
DROP TABLE public.bills;