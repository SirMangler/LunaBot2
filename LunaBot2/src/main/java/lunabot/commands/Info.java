package lunabot.commands;

import java.awt.Color;

import lunabot.discord.LunaDiscordClient;
import lunabot.gateway.Configuration;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author SirMangler
 *
 * @date 9 May 2019
 */
public class Info implements Command {

	@Override
	public boolean execute(String line, MessageReceivedEvent e) {
		if (line.startsWith(Configuration.getVariable("prefix")+"info") || line.startsWith(Configuration.getVariable("prefix")+"commands")) {
			if (LunaDiscordClient.isStaff(e.getMember())) {
				EmbedBuilder embed = new EmbedBuilder();
				embed.setColor(Color.cyan);
				embed.setTitle("**Commands <Admins>**");
				embed.setDescription("\n```css\n"+Configuration.getVariable("prefix")
						+"\n\n afk <reason> \n   /*Set's you as AFK*/\n    (default: everyone)"
						+"\n\n brb <reason> \n   /*Alias of AFK*/"+""
						+"\n\n reload \n   /*Reloads the bot's data file*/\n    (default: star)"
						//+"\n\n purge <variables..> \n  /*Purge messages in a channel*/\n    (default: Manage_Messages Permission)"
						+"\n\n todo \n  /*Star's to-do list*/\n    (default: The Comets)"
						//+"\n\n remindMe \n  /*Star's reminders*/\n    (default: The Comets)"
						+"\n\n [number] C \n  /*Converts the number to Celcius*/\n    (default: everyone)"
						+"\n\n [number] F \n  /*Converts the number to Fahrenheit*/\n    (default: everyone)"
						//+"\n\n [number] [currency] \n  /*Converts to specified currency*/\n    (default: everyone)"
						//+"\n\n sexbang \n  /*The best command*/\n    (default: #anons-hubcap-stash)"
						+"\n\n commands \n  /*Displays this.*/\n    (default: everyone)"
						//+"\n\ngoogle [search] \n  /*Googles something for you*/\n    (default: everyone)"
						//+"\n\nban [user] <reason>"
						//+"\n\nme \n  /*Displays the commands for the Queuer*/\n    (default: everyone)
						+"```");
				e.getChannel().sendMessage(embed.build()).queue();
			} else {
				EmbedBuilder embed = new EmbedBuilder();
				embed.setColor(Color.cyan);
				embed.setTitle("**Commands**");
				embed.setDescription("\n```css\n"+Configuration.getVariable("prefix")
						+"\n afk <reason> \n   /*Set's you as AFK*/"
						+"\n\n brb <reason> \n   /*Alias of AFK*/"+""
						+"\n\n [number] C \n  /*Converts the number to Celcius*/"
						+"\n\n [number] F \n  /*Converts the number to Fahrenheit*/"
						+"\n\n [number] [currency] \n  /*Converts to specified currency*/"
						+"\n\n commands \n  /*Displays this.*/"
						//+"\n\ngoogle [search] \n  /*Googles something for you*/\n    (default: everyone)"
						//+"\n\nme \n  /*Displays the commands for the Queuer*/\n    (default: everyone)"
						+ "```");
				e.getChannel().sendMessage(embed.build()).queue();
			}
		}

		return false;
	}

	@Override
	public boolean execute(String line, String name) {
		return false; //Not twitch available.
	}

}
