package lunabot.discord;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.security.auth.login.LoginException;

import lunabot.gateway.Configuration;
import lunabot.gateway.Gateway;
import lunabot.gateway.Log;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;


public class LunaDiscordClient implements Runnable {

	 /*
	 * @author SirMangler
	 * 22nd Feburary 2019
	 */
	
	final private static Log log = new Log("Discord");
	private static JDA jda;
	static TextChannel welcomeChannel;
	static TextChannel alertsChannel;
	
	//private static Guild guild = jda.getGuildById("325273265985683457"); star server
	private static Guild guild;
	
	private static Role cometsrole;
	private static Role moonrole;
	private static Role starrole;
	private static Role aurorarole;
	
	
	@Override
	public void run() {
		log.info("Starting JDA");

		try {
			jda = new JDABuilder(AccountType.BOT)
					.setToken(Gateway.debugMode ? System.getenv("debug-token") : System.getenv("token")).build();
			jda.awaitReady();
		} catch (LoginException | InterruptedException e) {
			log.error("Cannot Start Discord-Bot!!!", e);
			return;
		}
		
		guild = jda.getGuildById("270234493124608000");
		
		welcomeChannel = jda.getTextChannelById("325276999947911168");
		//alertsChannel = jda.getTextChannelById("442774379223449600");
		
		jda.addEventListener(new DiscordEvents());
	}
	
	public static Role getCometsRole() {
		if (cometsrole == null)
			cometsrole = guild.getRolesByName("The Comets", true).get(0);
		
		return cometsrole;
	}
	
	public static Role getMoonRole() {
		if (moonrole == null)
			moonrole = guild.getRolesByName("The Moon", true).get(0);
		
		return moonrole;
	}
	
	public static Role getAuroraRole() {
		if (aurorarole == null)
			aurorarole = guild.getRolesByName("The Aurora", true).get(0);
		
		return aurorarole;
	}
	
	public static Role getStarRole() {
		if (starrole == null)
			starrole = guild.getRolesByName("The Star", true).get(0);
		
		return starrole;
	}
	
	public static boolean isStaff(Member member) {
		if (member.getRoles().contains(getCometsRole()) || member.getRoles().contains(getMoonRole()) || member.getRoles().contains(getStarRole())) {
			return true;
		}
		
		return false;
	}
	
	public static JDA getJDA() {
		return jda;
	}
	
	public static Guild getGuild() {
		return guild;
	}
	
	static ExecutorService exc = Executors.newCachedThreadPool();
	public static void sendTempMessage(String message, TextChannel c) {
		if (c.canTalk()) {
			Message msg = c.sendMessage(message).complete();

			exc.submit(() -> {
				try {
					Thread.sleep(4000);
					
					msg.delete().complete();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}	
			});
			
		} else {
			log.error("Cannot send message to channel: "+c.getName());
		}
	}

	public static void twitchAlert(String message) {
		TextChannel c = jda.getTextChannelById(Configuration.getVariable("announcementChannel"));
		if (c != null && c.canTalk()) {
			c.sendMessage(message).queue();
		} else {
			log.error("Can't access Alerts channel. Can't talk or doesn't exist.");
		}
	}
	
	public static void spacebar(String message) {
		if (welcomeChannel != null && welcomeChannel.canTalk()) {
			welcomeChannel.sendMessage(message).queue();
		} else {
			log.error("Can't access the welcome channel. Can't talk or doesn't exist.");
		}
	}
}
