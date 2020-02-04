package lunabot.webinterface;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import lunabot.gateway.Configuration;
import lunabot.gateway.Log;

/**
 * @author SirMangler
 *
 * @date 25 Apr 2019
 */
public class ControlPanel extends NanoHTTPD implements Runnable {
	final private static Log log = new Log("Luna-WebInterface");
	String INDEX;

	ControlWS ws = new ControlWS();
	
	public ControlPanel() {
		super("0.0.0.0", 80);
	}
	
	public void startWebServer() throws IOException {
		log.info("Loading Index");
		StringBuilder b = new StringBuilder();
		
		try {
			Path path = Paths.get(Configuration.getResource("\\web\\index.html").toURI());
			Files.readAllLines(path).forEach(b::append);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		INDEX = b.toString();
		
		log.info("Starting Web Interface");
		
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);

        ControlWS cws = new ControlWS();
        cws.start();
        
        log.info("Luna Web-Interface Started!");
	}
	//"<body><p>404 Not found BECAUSE IT DONT EXIST IN-IT\n</p></body>".getBytes()
	public byte[] tryLoadResource(String resource) throws IOException {
		log.info("Trying to load: "+resource);
		
		if (resource.equalsIgnoreCase("/")) resource = "index.html";
		if (resource.equalsIgnoreCase("/login")) resource = "login.html";
		
		URL url = Configuration.getResource("\\web\\"+resource);
		
		try {
			Path p = Paths.get(url.toURI());
			
			if (!Files.exists(p)) return null;

			return Files.readAllBytes(p);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String getMime(String uri) {
		if (uri.contains(".")) {
			switch (uri.split(".", 2)[1].toLowerCase()) {
				case "jpg":
					return "image/jpeg";
				case "jpeg":
					return "image/jpeg";
				case "png":
					return "image/png";
				case "js":
					return "text/javascript";
				case "css":
					return "text/css";
				case "ico":
					return "image/vnd.microsoft.icon";
			}
		}
		
		return "text/html";
	}

    @Override
    public Response serve(IHTTPSession session) {
        if (session.getMethod() == Method.GET) {
			try {
				byte[] buf = tryLoadResource(session.getUri());
				String mime = getMime(session.getUri());
				
				if (buf == null) {
					buf = "<body><p>404 Not found BECAUSE IT DONT EXIST IN-IT\n</p></body>".getBytes();
					mime = "text/html";
					
					return newFixedLengthResponse(Status.NOT_FOUND, mime, new ByteArrayInputStream(buf), buf.length);
				}
				
				return newFixedLengthResponse(Status.OK, mime, new ByteArrayInputStream(buf), buf.length);
			} catch (IOException e) {
				log.error("Connection Closed", e);
			}
        }
        
		return newFixedLengthResponse("<body><p>404 Not found BECAUSE IT DONT EXIST IN-IT\n</p></body>");
    }

	@Override
	public void run() {
		try {
			startWebServer();
		} catch (IOException e) {
			log.error("Couldn't start", e);
		}
	}
}
