package lunabot.commands;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import lunabot.discord.LunaDiscordClient;
import lunabot.gateway.Configuration;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author SirMangler
 *
 * @date 13 May 2019
 */
public class TemperatureConverter implements Command {

	String syntax = Configuration.getVariable("prefix")+"[number] [C|F]\n - 1 C = 33.8 F";
	
	@Override
	public boolean execute(String line, MessageReceivedEvent e) {
		if (line.startsWith(Configuration.getVariable("prefix"))) {
			String[] sects = line.trim().replace(Configuration.getVariable("prefix"), "").split(" ");

			if (sects.length == 2) {
				BigDecimal i;
				NumberFormat f = new DecimalFormat("#.##");
				
				try {
					i = new BigDecimal(sects[0]);
				} catch (NumberFormatException nfe) {
					return true;
				}
				
				if (sects[1].equalsIgnoreCase("C")) {
					i = i.multiply(new BigDecimal("1.8")).add(new BigDecimal(32));
					
					String output = f.format(i);
					e.getTextChannel().sendMessage(":thermometer: "+sects[0]+ "°C = "+output+"°F").queue();;
				} else if (sects[1].equalsIgnoreCase("F")){
					i = i.subtract(new BigDecimal("32")).divide(new BigDecimal(1.8), BigDecimal.ROUND_HALF_UP);
					
					String output = f.format(i);
					e.getTextChannel().sendMessage(":thermometer: "+sects[0]+ "°F = "+output+"°C").queue();
				} else {
					LunaDiscordClient.sendTempMessage(syntax, e.getTextChannel());
					return true;
				}
				
				return false;
			} else {
				return true;
			}
		}	
		return false;
	}

	@Override
	public boolean execute(String line, String name) {
		// TODO Auto-generated method stub
		return false;
	}
	

}
