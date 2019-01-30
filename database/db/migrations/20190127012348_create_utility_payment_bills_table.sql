-- migrate:up
CREATE TABLE public.utility_payment_bills (
  id                    bigserial       not null,
  intent_id             bigint          not null,
  state                 integer         not null,
  utility               varchar(300)    not null,
  identifier            varchar(300)    not null,
  amount                numeric(12,2)   null,
  created               timestamp       not null DEFAULT now(),
  updated               timestamp       not null DEFAULT now(),
  CONSTRAINT utility_payment_bills_pk PRIMARY KEY (id),
  CONSTRAINT utility_payment_bills_f1 FOREIGN KEY(intent_id) REFERENCES public.utility_payment_intents(id)
);

CREATE INDEX utility_payment_bills_i1 ON public.utility_payment_bills (intent_id);
CREATE INDEX utility_payment_bills_i2 ON public.utility_payment_bills (state);
CREATE INDEX utility_payment_bills_i3 ON public.utility_payment_bills (identifier);

-- migrate:down
DROP TABLE public.utility_payment_bills;