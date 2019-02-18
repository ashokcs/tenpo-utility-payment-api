-- migrate:up
CREATE TABLE public.totaliser_daily (
  id        bigserial not null,
  year      integer   not null,
  month     integer   not null,
  day       integer   not null,
  quantity  bigint    not null,
  amount    bigint    not null,
  CONSTRAINT totaliser_daily_pk PRIMARY KEY (id),
  CONSTRAINT totaliser_daily_u1 UNIQUE (year, month, day)
);

CREATE INDEX totaliser_daily_i1 ON public.totaliser_daily (quantity);
CREATE INDEX totaliser_daily_i2 ON public.totaliser_daily (amount);

-- migrate:down
DROP TABLE public.totaliser_daily;