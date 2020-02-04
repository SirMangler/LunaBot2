package lunabot.commands;

import lunabot.discord.LunaDiscordClient;
import lunabot.gateway.Configuration;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author SirMangler
 *
 * @date 6 May 2019
 */
public class reloadData implements Command {


	@Override
	public boolean execute(String line, MessageReceivedEvent e) {
		if (line.startsWith(Configuration.getVariable("prefix")+"reloaddata")) {
			if (LunaDiscordClient.isStaff(e.getMember())) {
				Configuration.reloadData();
				e.getTextChannel().sendMessage("Reloaded Data.").queue();
			}
		}
		
		return true;
	}

	@Override
	public boolean execute(String line, String name) {
		return false; //Not needed for Twitch.
	}

}
