package lunabot.gateway;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import lunabot.discord.LunaDiscordClient;
import lunabot.twitch.LunaTwitchClient;
import lunabot.webinterface.ControlPanel;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;

public class Gateway {

	/**
	 * @author SirMangler
	 * @date 20th Feburary 2019
	 * 
	 * Handles threading, inter-communication and maintains status.
	 */
	

	
	final private static ThreadFactory tf = new ThreadFactoryBuilder().setDaemon(false).setNameFormat("Luna-Thread-%s").build();
	final private static ExecutorService exc = Executors.newFixedThreadPool(5, tf);
	private static Future<?> twitch;
	private static Future<?> discord;
	private static Future<?> autoRestart;
	private static Future<?> webinterface;

	final private static Log log = new Log("Gateway");
	
	private static boolean autoStartDiscord = false;
	public static boolean debugMode = false;
	
	public static void main(String[] args) {
		if (args.length > 0) {
			debugMode = true;
			log.info("Debug mode enabled");
		}
		
		log.info("Initialising Luna-WebInterface", initialiseWebInterface());
		open();
	}
	
	public static void open() {
		if (discord != null && !discord.isDone()) {
			log.info("Discord Bot already running!");
		} else {
			log.info("Initialising Luna-Discord", initialiseDiscordBot());
			autoStartDiscord = true;
		}
		
		if (twitch != null && !twitch.isDone()) {
			log.info("Twitch Bot already running!");
		} else {
			log.info("Initialising Luna-Twitch", initialiseTwitchBot());
		}

		log.info("Luna has been inialised.");
	}
	
	public static void restart() {
		log.info("Restarting TwitchBot and DiscordBot.");
		close();
		open();
	}
	
	public static void close() {
		log.info("Shutting down TwitchBot and DiscordBot.");
		
		if (LunaDiscordClient.getJDA() == null) {
			log.info("Discord-Bot is already shutdown!");
		} else {
			LunaDiscordClient.getJDA().shutdown();
			autoStartDiscord = false;
		}
		if (LunaTwitchClient.cl == null) {
			log.info("Twitch-Bot is already shutdown!");
		} else {
			LunaTwitchClient.cl = null;
		}
	}
	
	public static JDA getJDA() {
		if (!discord.isDone()) {
			JDA jda;
			if ((jda = LunaDiscordClient.getJDA()) != null) return jda;
			
			log.error("Cannot get JDA. Is the bot dead?");
			return null;
		} else {
			log.error("Cannot get JDA. Luna Discord is down.");
			return null;
		}
	}
	
	public static Guild getGuild() {
		if (!discord.isDone()) {
			Guild guild;
			if ((guild = LunaDiscordClient.getGuild()) != null) return guild;
			
			log.error("Cannot get Guild. Is the bot dead or is the bot not in the server?");
			return null;
		} else {
			log.error("Cannot get Guild. Luna Discord is down.");
			return null;
		}
	}
	
	
	public static boolean sendDAlert(String line) {
		if (discord != null) {
			LunaDiscordClient.twitchAlert(line);
			return true;
		} else {
			log.warn("Cannot send Discord Alert: Discord Thread is not alive.");
			return false;
		}
	}
	
	private static boolean initialiseTwitchBot() {
		twitch = exc.submit(new LunaTwitchClient());
		return true;
	}
	
	private static boolean initialiseDiscordBot() {
		discord = exc.submit(new LunaDiscordClient());
		return true;
	}

	public static boolean initialiseWebInterface() {
		webinterface = exc.submit(new ControlPanel());
		return true;
	}
	
	public static boolean initialiseRestart() {
		autoRestart = exc.submit(() -> {
			if (autoStartDiscord == true) {
				while (true) {
					if (discord != null && !discord.isDone()) {
						log.warn("\n=\n= Automatically Restarted Luna\n=");
						open();
					}
					
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		return true;
	}
	
	public void loadConfiguration() {
		
	}
}
