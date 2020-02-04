package lunabot.commands;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

import lunabot.gateway.Configuration;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author SirMangler
 *
 * @date 30 May 2019
 */
public class TimeConverter implements Command {

	SimpleDateFormat format24 = new SimpleDateFormat("HH:mm z");	
	SimpleDateFormat format12 = new SimpleDateFormat("hh:mma z");
	
	DateTimeFormatter formatdisplay = DateTimeFormatter.ofPattern("HH:mm");
	
	@Override
	public boolean execute(String line, MessageReceivedEvent e) {
		if (line.startsWith(Configuration.getVariable("prefix"))) {
			String[] sects = line.trim().replace(Configuration.getVariable("prefix"), "").split(" ");
			
			if (sects.length == 3) {
				Instant i;
				
				try {
					 i = format24.parse(sects[0]).toInstant();
				} catch (Exception e1) {
					try {
						i = format12.parse(sects[0]).toInstant();
					} catch (Exception e2) {
						e.getChannel().sendMessage("Couldn't parse input time. Format: 1pm or 13:00").queue();
						return true;
					}
				}

				ZoneId zone1 = TimeZone.getTimeZone(sects[1]).toZoneId();
				ZoneId zone2 = TimeZone.getTimeZone(sects[2]).toZoneId();
				
				ZonedDateTime dtd = ZonedDateTime.ofInstant(i, ZoneId.systemDefault());
				
				ZonedDateTime dt1 = dtd.withZoneSameInstant(zone1);
				ZonedDateTime dt2 = dtd.withZoneSameInstant(zone2);
				
				String output = dt1.format(formatdisplay)+" "+sects[1]+" = "+dt2.format(formatdisplay)+" "+sects[2];
				
				e.getChannel().sendMessage(":clock: "+output).queue();
			}
		}
			
		return false;
	}

	@Override
	public boolean execute(String line, String name) {
		
		return false;
	}

}
