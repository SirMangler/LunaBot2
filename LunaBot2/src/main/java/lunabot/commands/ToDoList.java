package lunabot.commands;

import java.awt.Color;

import lunabot.discord.LunaDiscordClient;
import lunabot.gateway.Configuration;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author SirMangler
 *
 * @date 2 Mar 2019
 */
public class ToDoList implements Command {

	@Override
	public boolean execute(String line, MessageReceivedEvent e) {
		if (LunaDiscordClient.isStaff(e.getMember())) {
			if (line.startsWith(Configuration.getVariable("prefix")+"todo")) {
				String[] sectors = line.split(" ");
				if (sectors.length==1) {
					String[] todoList = Configuration.getStrings("todo");
					StringBuilder list = new StringBuilder();
					
					for (int i = 0; i < todoList.length; i++) {
						list.append((i+1)+". "+todoList[i]);	
					}
					
					EmbedBuilder b = new EmbedBuilder();
					b.setTitle("ToDo List");
					b.setColor(Color.GREEN);
					b.setDescription(list.toString());
					
				}
			}
		}
		return false;
	}

	@Override
	public boolean execute(String line, String name) {
		
		return false;
	}
	

}
