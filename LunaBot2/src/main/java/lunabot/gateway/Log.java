package lunabot.gateway;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import lunabot.webinterface.ControlWS;

/**
 * @author SirMangler
 *
 * @date 28 Apr 2019
 */
public class Log {

	String name;
	
	public Log(String name) {
		this.name="["+name+"]";
	}
	
	public Log(Class<?> clazz) {
		this.name=clazz.getName();
	}
	
	public void info(String string) {
		print(name+" INFO "+string);
	}
	
	public void info(Object object) {
		print(name+" INFO "+object.toString());
	}
	
	public void info(String string, Object object) {
		print("\n"+name+" INFO "+string+"\n");
	}
	
	public void warn(String string) {
		print(name+" WARN "+string);
	}
	
	public void error(String string) {
		print(name+" ERROR "+string);
	}
	
	public void error(String string, Exception e) {
		print("\n**\n"+name+" ERROR "+string+" "+e.getMessage()+"\n**\n");
	}
	
	static DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());
	public static void print(String line) {
		String output = "["+formatter.format(Instant.now())+"] "+line;
		System.out.println(output);
		
		ControlWS.outputs.forEach(writer -> {
			if (writer == null || writer.isClosed()) {
				ControlWS.outputs.remove(writer);
			} else {
				writer.send(output);
			}
		});
	}
}
