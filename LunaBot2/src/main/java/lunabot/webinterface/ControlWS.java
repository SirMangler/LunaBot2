package lunabot.webinterface;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import lunabot.gateway.Configuration;
import lunabot.gateway.Gateway;
import lunabot.gateway.Log;

/**
 * @author SirMangler
 *
 * @date 26 Apr 2019
 */
public class ControlWS extends WebSocketServer  {

	public static List<WebSocket> outputs = new ArrayList<WebSocket>();
	final private static Log log = new Log("Luna-Websocket");

	public ControlWS() {
		super(new InetSocketAddress("0.0.0.0", 90));
	}
	
	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		log.info("Recieved "+conn.getLocalSocketAddress().getHostString());
		outputs.add(conn);
		
		try {
			String s = "[s]"+Configuration.getRawFile();
			conn.send(s);
		} catch (IOException e) {
			conn.send("Unable to load file. Is Luna down? Try restart her, then refresh the page.");
			e.printStackTrace();
		} catch (URISyntaxException e) {
			conn.send("Unable to load file. Is Luna down? Try restart her, then refresh the page.");
			e.printStackTrace();
		}
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		log.info(code +" : "+reason);
		outputs.remove(conn);
		authed.remove(conn);
	}
	
	List<WebSocket> authed = new ArrayList<WebSocket>();
	
	StringBuilder settings;
	byte[] garbagecollector = new byte[] { 'l', 'u', 'n', 'a', '1', '9', '8', '4' };
	
	@Override
	public void onMessage(WebSocket conn, String message) {
		//log.info("["+conn.getResourceDescriptor()+"] Received: "+message);
		
		if (message.startsWith("auth:")) {
			String garbage = message.replace("auth:", "");
			byte[] p = garbage.getBytes();
			if (Arrays.equals(p, garbagecollector)) {
				conn.send("[auth-success]");
				log.info("Login Success from: "+conn.getRemoteSocketAddress().getHostString());
				authed.add(conn);
				System.gc();
			} else {
				conn.send("[auth-failure]");
				log.warn("Login failure from: "+conn.getRemoteSocketAddress().getHostString());
			}
		} else if (authed.contains(conn)) {
			switch (message.toUpperCase()) {	
				case "OPEN": {
					log.info("Recieved OPEN signal.");
					Gateway.open();
					return;
				}
				case "RESTART": {
					log.info("Recieved RESTART signal.");
					Gateway.restart();
					return;
				}
				case "CLOSE": {
					log.info("Recieved CLOSE signal.");
					Gateway.close();
					return;
				}
				case "[SETTINGS]": {
					settings = new StringBuilder();
					return;
				}
				case "[END]": {
					Configuration.updateFile(settings.toString());
					settings = null;
					
					return;
				}
		}
			if (settings != null) {
				settings.append(message);
			}
		} else {
			conn.send("[auth-failure]");
			log.warn("Login failure from: "+conn.getRemoteSocketAddress().getHostString());
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		ex.printStackTrace();
	}
	
	@Override
	public void onStart() {
		log.info("Starting WebSocket");	
	}
}
