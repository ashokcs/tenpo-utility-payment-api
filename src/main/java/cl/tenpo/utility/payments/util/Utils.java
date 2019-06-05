package cl.tenpo.utility.payments.util;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public class Utils
{
	public static final DateTimeFormatter orderFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	public static String uuid()
	{
		return UUID.randomUUID().toString();
	}

	public static String format(final String pattern, final TemporalAccessor date)
	{
		return DateTimeFormatter.ofPattern(pattern).format(date);
	}

	public static String paymentTypeFriendlyName(final String paymentTypeCode)
	{
		if (paymentTypeCode != null) {
			if (paymentTypeCode.equals("VD")) {
				return "Débito";
			} else if (paymentTypeCode.equals("VP")) {
				return "Prepago";
			}else {
				return "Crédito";
			}
		}
		return "";
	}

	public static String sharesTypeFriendlyName(final String paymentTypeCode)
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
			}else if (paymentTypeCode.equals("VP")) {
				return "Venta Prepago";
			}
		}
		return "";
	}

	public static String currency(final long amount)
	{
		return "$" + NumberFormat.getNumberInstance(new Locale("es", "CL")).format(amount);
	}

	public static String utilityFriendlyName(final String utility)
	{
		return utility.replaceAll("\\_", " ");
	}

	public static Optional<String> getValidParam(final String param, final String regex)
	{
		if ((param != null) && param.matches(regex)) {
			return Optional.of(param);
		}
		return Optional.empty();
	}
}