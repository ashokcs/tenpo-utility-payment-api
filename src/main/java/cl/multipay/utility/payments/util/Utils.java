package cl.multipay.utility.payments.util;

public class Utils
{
	public static final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

	private Utils()
	{

	}

	public static boolean isUuidValid(final String uuid)
	{
		return (uuid != null) && uuid.matches(UUID_REGEX) ? true : false;
	}
}