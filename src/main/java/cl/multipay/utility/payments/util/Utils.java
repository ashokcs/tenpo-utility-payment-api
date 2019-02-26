package cl.multipay.utility.payments.util;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.UUID;

import org.springframework.stereotype.Service;

import cl.multipay.utility.payments.entity.UtilityPaymentTransaction;

@Service
public class Utils
{
	public String uuid()
	{
		return UUID.randomUUID().toString().replaceAll("\\-", "");
	}

	public String mdc(final String transaction)
	{
		if ((transaction != null) && !transaction.isEmpty()) {
			return "[" + transaction + "] ";
		}
		return null;
	}

	public String format(final String pattern, final TemporalAccessor date)
	{
		return DateTimeFormatter.ofPattern(pattern).format(date);
	}

	public String paymentMethod(final String paymentMethod)
	{
		if (UtilityPaymentTransaction.WEBPAY.equals(paymentMethod)) {
			return "Webpay";
		} else if (UtilityPaymentTransaction.EFT.equals(paymentMethod)) {
			return "Transferencia";
		}
		return "";
	}

	public String paymentType(final String paymentTypeCode)
	{
		if (paymentTypeCode != null) {
			if (paymentTypeCode.equals("VD")) {
				return "Débito";
			} else {
				return "Crédito";
			}
		}
		return "";
	}

	public String sharesType(final String paymentTypeCode)
	{
		if (paymentTypeCode != null) {
			if (paymentTypeCode.equals("VN")){
				return "Sin Cuotas";
			}else if (paymentTypeCode.equals("VC")) {
				return "Cuotas normales";
			}else if (paymentTypeCode.equals("SI")) {
				return "Sin interés";
			}else if (paymentTypeCode.equals("S2")) {
				return "Sin interés";
			}else if (paymentTypeCode.equals("NC")) {
				return "Sin interés";
			}else if (paymentTypeCode.equals("VD")) {
				return "Venta Débito";
			}
		}
		return "";
	}

	public String currency(final long amount)
	{
		return "$" + NumberFormat.getNumberInstance(new Locale("es", "CL")).format(amount);
	}
}