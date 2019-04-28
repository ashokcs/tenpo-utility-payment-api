package cl.tenpo.utility.payments.util;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import cl.tenpo.utility.payments.jpa.entity.Transaction;

public class Utils
{
	public static String uuid()
	{
		return UUID.randomUUID().toString();
	}

	public static String format(final String pattern, final TemporalAccessor date)
	{
		return DateTimeFormatter.ofPattern(pattern).format(date);
	}

	public static String paymentMethodFriendlyName(final String paymentMethod)
	{
		if (Transaction.WEBPAY.equals(paymentMethod)) {
			return "Webpay";
		} else if (Transaction.TRANSFERENCIA.equals(paymentMethod)) {
			return "Transferencia";
		}
		return "";
	}

	public static String paymentTypeFriendlyName(final String paymentTypeCode)
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

	public static String collectorFiendlyName(final String collectorId)
	{
		if (collectorId != null) {
			switch (collectorId) {
			case "1": return "OTRO";
			case "2": return "EFT";
			case "3": return "SENCILLITO";
			case "4": return "SANTANDER";}
		}
		return "";
	}

	public static String categoryFriendlyName(final String categoryId)
	{
		if (categoryId != null) {
			switch (categoryId) {
			case "100": return "AGUA";
			case "200": return "LUZ";
			case "300": return "TELEF-TV-INTERNET";
			case "400": return "GAS";
			case "500": return "AUTOPISTAS";
			case "600": return "COSMETICA";
			case "700": return "RETAIL";
			case "800": return "CREDITO-FINANCIERA";
			case "900": return "SEGURIDAD";
			case "1000": return "EDUCACION";
			case "1100": return "CEMENTERIO";
			case "1200": return "OTRAS EMPRESAS";
			case "1300": return "EFECTIVO MULTICAJA";}
		}
		return "";
	}

	public static String getPaymentMethodName(final String paymentCodeMethod)
	{
		if (Transaction.WEBPAY.toLowerCase().equals(paymentCodeMethod)) {return Transaction.WEBPAY;}
		else if (Transaction.TRANSFERENCIA.toLowerCase().equals(paymentCodeMethod)) {return Transaction.TRANSFERENCIA;}
		return null;
	}

	public static Optional<String> getValidParam(final String param, final String regex)
	{
		if ((param != null) && param.matches(regex)) {
			return Optional.of(param);
		}
		return Optional.empty();
	}
}