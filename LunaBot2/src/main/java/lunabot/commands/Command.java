package lunabot.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author SirMangler
 *
 * @date 2 Mar 2019
 */
public interface Command {

	public boolean execute(String line, MessageReceivedEvent e);
	
	public boolean execute(String line, String name);
	
}
