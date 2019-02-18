-- migrate:up
CREATE TABLE public.totaliser_monthly (
  id        bigserial not null,
  year      integer   not null,
  month     integer   not null,
  quantity  bigint    not null,
  amount    bigint    not null,
  CONSTRAINT totaliser_monthly_pk PRIMARY KEY (id),
  CONSTRAINT totaliser_monthly_u1 UNIQUE (year, month)
);

CREATE INDEX totaliser_monthly_i1 ON public.totaliser_monthly (quantity);
CREATE INDEX totaliser_monthly_i2 ON public.totaliser_monthly (amount);

-- migrate:down
DROP TABLE public.totaliser_monthly;