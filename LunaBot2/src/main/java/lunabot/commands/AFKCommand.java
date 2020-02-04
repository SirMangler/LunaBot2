package lunabot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lunabot.discord.AFK;
import lunabot.gateway.Configuration;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author SirMangler
 *
 * @date 2 Mar 2019
 */
public class AFKCommand implements Command {

	Pattern afkregex = Pattern.compile("(?:afk|brb)(?:\\s|,\\s|\\.\\s|)(.+)*");
	
	@Override
	public boolean execute(String line, MessageReceivedEvent e) {		
		if (line.startsWith(Configuration.getVariable("prefix")+"afk") || line.startsWith(Configuration.getVariable("prefix")+"brb")) {
			String reason = line.substring(3+Configuration.getVariable("prefix").length());
			
			if (reason.isEmpty()) {
				AFK.addAFKMember(e.getMember(), "null");
				e.getTextChannel().sendMessage(e.getMember().getEffectiveName()+" is now AFK").queue();
			} else {
				AFK.addAFKMember(e.getMember(), reason);
				e.getTextChannel().sendMessage(e.getMember().getEffectiveName()+" is now AFK: "+reason).queue();
			}

			
			return true;
		} else {
			Matcher m = afkregex.matcher(line);
			if (m.matches()) {
				if (m.group(1) != null) {
					AFK.addAFKMember(e.getMember(), m.group(1));
					e.getTextChannel().sendMessage(e.getMember().getEffectiveName()+" is now AFK: "+m.group(1)).queue();
				} else {
					AFK.addAFKMember(e.getMember(), null);
					e.getTextChannel().sendMessage(e.getMember().getEffectiveName()+" is now AFK").queue();
				}
				
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean execute(String line, String name) {
		return false; // Not supported for twitch.
	}
}
