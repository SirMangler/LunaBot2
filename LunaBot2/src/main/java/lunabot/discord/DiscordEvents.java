package lunabot.discord;

import lunabot.commands.AFKCommand;
import lunabot.commands.Command;
import lunabot.commands.Info;
import lunabot.commands.TemperatureConverter;
import lunabot.commands.TimeConverter;
import lunabot.commands.ToDoList;
import lunabot.commands.reloadData;
import lunabot.gateway.Log;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberNickChangeEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * @author SirMangler
 *
 * 22nd Feb 2019
 */
public class DiscordEvents extends ListenerAdapter {

	final private static Log log = new Log("Discord-Events");
	
	Command[] commands = { 
			new AFKCommand(), 
			new ToDoList(), 
			new TemperatureConverter(), 
			new Info(), 
			new TimeConverter(), 
			new reloadData() 
		};
	
	@Override
	public void onGuildMemberJoin(final GuildMemberJoinEvent e) {
		LunaDiscordClient.spacebar("Welcome to Asteroid Belt, "+e.getMember().getAsMention()+"!");
	}
	
	@Override
	public void onGuildMemberLeave(final GuildMemberLeaveEvent e) {
		Member star = LunaDiscordClient.getGuild().getMemberById("176128994176008192");
		if (star != null) {
			star.getUser().openPrivateChannel().queue(success -> {
				
			}, failure -> {
				log.error("Cannot send user leave message: "+failure.getMessage());
			});
		}
	}
	
	@Override
	public void onGuildMemberNickChange(final GuildMemberNickChangeEvent e) {
		if (AFK.isAFK(e.getMember())) {
			if (!e.getNewNick().startsWith("[AFK] ")) {
				e.getGuild().getController().setNickname(e.getMember(), "[AFK] "+e.getNewNick()).complete();
			}
		}
	}
	
	@Override
	public void onMessageReceived(final MessageReceivedEvent e) {
		if (!e.getChannel().getId().equalsIgnoreCase("325285012096417802")) {
			if (AFK.isAFK(e.getMember())) {
				AFK.removeAFKMember(e.getMember());
				
				LunaDiscordClient.sendTempMessage(e.getMember().getEffectiveName()+" is no longer AFK!", e.getTextChannel());
			}
			
			for (Member afk : AFK.afkmembers.keySet()) {
				if (e.getMessage().getMentionedMembers().contains(afk)) {
					String reason = AFK.afkmembers.get(afk);
					e.getTextChannel().sendMessage(afk.getEffectiveName()+" is currently afk"+(reason.equalsIgnoreCase("null") ? "." : ": "+reason)).queue();
				}
			}			
		}
		
		if (!e.getAuthor().isBot()) {
			for (Command command : commands) {
				command.execute(e.getMessage().getContentRaw(), e);
			}
		}
	}
}
