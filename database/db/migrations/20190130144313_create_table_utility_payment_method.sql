-- migrate:up
CREATE TABLE public.utility_payment_methods (
  id                    bigserial       not null,
  intent_id             bigint          not null,
  uuid                  varchar(36)     not null,
  state                 integer         not null,
  order_id              bigint          not null,
  redirect_url          varchar(300)    not null,
  created               timestamp       not null DEFAULT now(),
  updated               timestamp       not null DEFAULT now(),
  CONSTRAINT utility_payment_methods_pk PRIMARY KEY (id),
  CONSTRAINT utility_payment_methods_f1 FOREIGN KEY(intent_id) REFERENCES public.utility_payment_intents(id),
  CONSTRAINT utility_payment_methods_u1 UNIQUE (intent_id),
  CONSTRAINT utility_payment_methods_u2 UNIQUE (uuid)
);

CREATE INDEX utility_payment_methods_i1 ON public.utility_payment_methods (order_id);
CREATE INDEX utility_payment_methods_i2 ON public.utility_payment_methods (created);

-- migrate:down
DROP TABLE public.utility_payment_methods;