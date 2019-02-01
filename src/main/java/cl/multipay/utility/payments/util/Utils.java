package cl.multipay.utility.payments.util;

import java.util.UUID;

public class Utils
{
	private Utils()
	{

	}

	public static String uuid()
	{
		return UUID.randomUUID().toString().replaceAll("\\-", "");
	}
}