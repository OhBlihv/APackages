package me.ohblihv.APackages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static me.ohblihv.APackages.util.Messages.*;

/**
 * Created by Chris on 4/20/2016.
 */
@RequiredArgsConstructor
public class MonthlyPackage
{

	@Getter
	private final String displayname;

	@Getter
	private final String internalName;

	@Getter
	private final TimeUnit expiryUnit;

	@Getter
	private final long expiryMillis;

	public String getTimeleft(long expirySeconds)
	{
		LocalDateTime   currentTime = LocalDateTime.now(),
						expiryTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(expirySeconds * 1000L), ZoneId.systemDefault()),
						tempTime = LocalDateTime.from(currentTime);

		String format = CMD_TIMELEFT_FORMAT;
		long    days, hours, minutes, seconds,
				lastValidUnit = 0; //Used to leave spaces between units

		//Calculate the unit, then add it on to total time
		days = tempTime.until(expiryTime, ChronoUnit.DAYS);
		if(days > 0)
		{
			String daysString = CMD_TIMELEFT_FORMAT_DAYS;
			daysString = daysString.replace("{days}", String.valueOf(days));
			if(days > 1)
			{
				daysString += "s";
			}
			format = format.replace("{days}", daysString);
			lastValidUnit = days;
		}
		else
		{
			format = format.replace("{days}", "");
		}
		tempTime = tempTime.plusDays(days);

		hours = tempTime.until(expiryTime, ChronoUnit.HOURS);
		if(hours > 0)
		{
			String hoursString = CMD_TIMELEFT_FORMAT_HOURS;
			hoursString = hoursString.replace("{hours}", String.valueOf(hours));
			if(hours > 1)
			{
				hoursString += "s";
			}
			if(lastValidUnit > 0)
			{
				hoursString = " ".concat(hoursString);
			}
			format = format.replace("{hours}", hoursString);
			lastValidUnit = hours;
		}
		else
		{
			format = format.replace("{hours}", "");
		}
		tempTime = tempTime.plusHours(hours);

		minutes = tempTime.until(expiryTime, ChronoUnit.MINUTES);
		if(minutes > 0)
		{
			String minutesString = CMD_TIMELEFT_FORMAT_MINUTES;
			minutesString = minutesString.replace("{minutes}", String.valueOf(minutes));
			if(minutes > 1)
			{
				minutesString += "s";
			}
			if(lastValidUnit > 0)
			{
				minutesString = " ".concat(minutesString);
			}
			format = format.replace("{minutes}", minutesString);
			lastValidUnit = minutes;
		}
		else
		{
			format = format.replace("{minutes}", "");
		}
		tempTime = tempTime.plusMinutes(minutes);

		seconds = tempTime.until(expiryTime, ChronoUnit.SECONDS);
		if(minutes > 0)
		{
			String secondsString = CMD_TIMELEFT_FORMAT_SECONDS;
			//                                                      Don't allow the timer to go below 0.
			secondsString = secondsString.replace("{seconds}", String.valueOf(seconds > 0 ? seconds : 0));
			if(seconds != 1)
			{
				secondsString += "s";
			}
			if(lastValidUnit > 0)
			{
				secondsString = " ".concat(secondsString);
			}
			format = format.replace("{seconds}", secondsString);
			lastValidUnit = seconds;
		}
		else
		{
			format = format.replace("{seconds}", "");
		}

		if(lastValidUnit <= 0)
		{
			format = CMD_TIMELEFT_FORMAT_EXPIRING;
		}

		return format;
	}

}
