package smartx.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

public class Convertor {

	public static float convertCoordinates(int rawCoordinates)
	{
		float convertedCoordinates = -1;
		if(rawCoordinates != -1)
		{
			// Convert the received coordinate
			convertedCoordinates = (float) (rawCoordinates * 0.000001);
		}

		return convertedCoordinates;
	}

	public static LocalDateTime convertTimestamp(long unixTimestamp)
	{
		// initialize LocalDateTime
		// LocalDateTime.now() uses the system default (RO) zone ID, so we must specify to use GMT zone ID
		LocalDateTime convertedTimestamp = LocalDateTime.now(TimeZone.getTimeZone("GMT").toZoneId());
		// convert the received unix timesamp to LocalDateTime
		convertedTimestamp = LocalDateTime.ofInstant(Instant.ofEpochSecond(unixTimestamp),
				TimeZone.getTimeZone("GMT").toZoneId());
		
		return convertedTimestamp;
	}

}
