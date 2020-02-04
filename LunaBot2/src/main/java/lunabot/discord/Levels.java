package lunabot.discord;

import java.util.Random;

import lunabot.gateway.Configuration;
import lunabot.gateway.Log;
import lunabot.gateway.Tuple;

/**
 * @author SirMangler
 *
 * @date 13 May 2019
 */
public class Levels {

	final private static Log log = new Log("Levels");
	Tuple[] levels = Configuration.getTuples("levels");
	
	static Random r = new Random();
	
	public static int updateLevel(String id) {
		String[] xplevel = getXPLevel(id).split(":");
		if (xplevel.length != 2) {
			log.error("XPLevel is not correctly formatted!! xplevel = "+String.join(",", xplevel));
		} else {

			try {
				int xp = Integer.parseInt(xplevel[1]);
				
				xp += r.nextInt(5)+5;
				
				setXP(id, xp);
				
				return xpToLevel(xp);
			} catch (NumberFormatException e) {
				log.error("Could not format level. xplevel = "+String.join(",", xplevel));
			}
		}
		
		return 0;
	}
		
	public static void setXP(String id, int xp) {
		Tuple[] tuple = Configuration.getTuples("levels");
		for (int i = 0; i < tuple.length; i++) {
			if (tuple[i].a.equals(id)) {
				int level = xpToLevel(xp);
				tuple[i].b = level+":"+xp;
				break;
			}
		}
		
		Configuration.setTuples("levels", tuple);
	}

	public static int xpToLevel(long xp) {
		return (int) Math.sqrt((double) xp);
	}
	
	public static String getXPLevel(String id) {
		Tuple[] tuple = Configuration.getTuples("levels");
		for (Tuple t : tuple) {
			if (t.a.equals(id)) {
				return t.b;
			}
		}
		
		return null;
	}
}
