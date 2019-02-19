
-- migrate:up
CREATE TABLE public.utility_payment_webpay (
  id                    bigserial       not null,
  status                varchar(100)    not null,
  transaction_id        bigint          not null,
  token                 varchar(100)    not null,
  url                   varchar(300)    not null,
  response_code         integer         null,
  auth_code             varchar(100)    null,
  card                  varchar(100)    null,
  payment_type          varchar(100)    null,
  shares                integer         null,
  created               timestamp       not null DEFAULT now(),
  updated               timestamp       not null DEFAULT now(),
  CONSTRAINT utility_payment_webpay_pk PRIMARY KEY (id),
  CONSTRAINT utility_payment_webpay_f1 FOREIGN KEY(transaction_id) REFERENCES public.utility_payment_transactions(id),
  CONSTRAINT utility_payment_webpay_u1 UNIQUE (transaction_id),
  CONSTRAINT utility_payment_webpay_u2 UNIQUE (token)
);

CREATE INDEX utility_payment_webpay_i1 ON public.utility_payment_webpay (status);
CREATE INDEX utility_payment_webpay_i2 ON public.utility_payment_webpay (created);

-- migrate:down
DROP TABLE public.utility_payment_webpay;