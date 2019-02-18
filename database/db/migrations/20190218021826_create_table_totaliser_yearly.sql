-- migrate:up
CREATE TABLE public.totaliser_yearly (
  id        bigserial not null,
  year      integer   not null,
  quantity  bigint    not null,
  amount    bigint    not null,
  CONSTRAINT totaliser_yearly_pk PRIMARY KEY (id),
  CONSTRAINT totaliser_yearly_u1 UNIQUE (year)
);

CREATE INDEX totaliser_yearly_i1 ON public.totaliser_yearly (quantity);
CREATE INDEX totaliser_yearly_i2 ON public.totaliser_yearly (amount);

-- migrate:down
DROP TABLE public.totaliser_yearly;