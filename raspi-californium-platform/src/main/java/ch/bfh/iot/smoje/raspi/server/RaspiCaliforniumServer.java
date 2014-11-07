package ch.bfh.iot.smoje.raspi.server;

import java.util.concurrent.Executors;

import org.eclipse.californium.core.CoapServer;
import ch.bfh.iot.smoje.raspi.resources.RaspiCamResource;

/**
 * Example Server
 */
public class RaspiCaliforniumServer {
	
	public static void main(String[] args) throws Exception {
		CoapServer server = new CoapServer();
		server.setExecutor(Executors.newScheduledThreadPool(4));
		server.add(new RaspiCamResource("raspicam"));
		
		server.start();
	}
}
