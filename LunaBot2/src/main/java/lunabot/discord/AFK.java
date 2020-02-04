package lunabot.discord;

import java.util.HashMap;

import lunabot.gateway.Configuration;
import lunabot.gateway.Log;
import lunabot.gateway.Tuple;
import net.dv8tion.jda.core.entities.Member;

/**
 * @author SirMangler
 *
 * @date 2 Mar 2019
 */
public class AFK {

	public static HashMap<Member, String> afkmembers = new HashMap<Member, String>();
	final private static Log log = new Log("Discord-AFK");
	
	public static boolean isAFK(Member m) {
		if (afkmembers.containsKey(m)) {
			return true;
		}
		
		return false;
	}
	
	public static void loadAFKMembers() {
		afkmembers = new HashMap<Member, String>();
		for (Tuple t : Configuration.getTuples("afkmembers")) {
			Member m = LunaDiscordClient.getGuild().getMemberById(t.a);
			
			if (m == null) {
				log.warn(t.a+" is not a member!");
			} else {
				afkmembers.put(m, t.b);
			}
		}
	}
	
	public static void addAFKMember(Member m, String reason) {
		afkmembers.put(m, reason);
		Configuration.addTuple("afkmembers", m.getUser().getId(), reason);
		
		String nickname = m.getEffectiveName();
		if (!nickname.startsWith("[AFK] "))
			nickname = "[AFK] "+nickname;
		
		if (LunaDiscordClient.getGuild().getSelfMember().canInteract(m)) {
			m.getGuild().getController().setNickname(m, nickname).queue();
		} else {
			log.info("Can't modify nickname of "+m.getEffectiveName());
		}
	}
	
	public static void removeAFKMember(Member m) {
		afkmembers.remove(m);
		Configuration.removeTuple("afkmembers", m.getUser().getId());
		
		String nickname = m.getEffectiveName();
		if (nickname.startsWith("[AFK] "))
			nickname = nickname.replaceFirst("[AFK] ", "");
		
		if (LunaDiscordClient.getGuild().getSelfMember().canInteract(m)) {
			m.getGuild().getController().setNickname(m, nickname).queue();
		} else {
			log.info("Can't modify nickname of "+m.getEffectiveName());
		}
	}
}
