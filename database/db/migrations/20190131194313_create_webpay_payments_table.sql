-- migrate:up
CREATE TABLE public.webpay_payments (
  id                    bigserial       not null,
  bill_id               bigint          not null,
  status                integer         not null,
  token                 varchar(100)    not null,
  url                   varchar(300)    not null,
  response_code         integer         null,
  auth_code             varchar(100)    null,
  card                  varchar(100)    null,
  payment_type          varchar(100)    null,
  shares                integer         null,
  created               timestamp       not null DEFAULT now(),
  updated               timestamp       not null DEFAULT now(),
  CONSTRAINT webpay_payments_pk PRIMARY KEY (id),
  CONSTRAINT webpay_payments_f1 FOREIGN KEY(bill_id) REFERENCES public.bills(id),
  CONSTRAINT webpay_payments_u1 UNIQUE (bill_id),
  CONSTRAINT webpay_payments_u2 UNIQUE (token)
);

CREATE INDEX webpay_payments_i1 ON public.webpay_payments (status);
CREATE INDEX webpay_payments_i3 ON public.webpay_payments (created);

-- migrate:down
DROP TABLE public.webpay_payments;