-- migrate:up
CREATE INDEX utility_payments_i1 ON public.utility_payments (state);
CREATE INDEX utility_payments_i2 ON public.utility_payments (created_at);

-- migrate:down
DROP INDEX public.utility_payments_i1;
DROP INDEX public.utility_payments_i2;