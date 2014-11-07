/*******************************************************************************
 * Copyright (c) 2014 Institute for Pervasive Computing, ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * 
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 * 
 * Contributors:
 *    Matthias Kovatsch - creator and main architect
 ******************************************************************************/
package ch.bfh.iot.smoje.raspi.resources;

import java.io.File;
import java.io.FileInputStream;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.ResourceAttributes;


/**
 * Get a Image from the RaspiCam
 */
public class RaspiCamResource extends CoapResource {
	
	private final String destDir = "/home/pi/smoje/cam/";
	private final String imgName = "temp";
	private final String imgExtension = ".jpg";
	private final String imgCaptureInstr = "/usr/bin/raspistill -o " + destDir+ imgName + imgExtension + " -t 1 -q 15";

	public RaspiCamResource(String resourceIdentifier) {
		super(resourceIdentifier);
		
		ResourceAttributes attributes = getAttributes();
		attributes.setTitle("GET an image with different content-types");
		attributes.addResourceType("Image");
		attributes.addContentType(MediaTypeRegistry.IMAGE_JPEG);
		attributes.setMaximumSizeEstimate(18029);
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		captureImage();
		Integer ct = MediaTypeRegistry.IMAGE_JPEG;
		if (exchange.getRequestOptions().hasAccept()) {
			ct = exchange.getRequestOptions().getAccept();
		}
		
		String filename = destDir + imgName + "." + MediaTypeRegistry.toFileExtension(ct);

		File file = new File(filename);
		
		if (!file.exists()) {
			exchange.respond(ResponseCode.INTERNAL_SERVER_ERROR, "Image file not found");
			return;
		}
		
		// get length of file for buffer
        int fileLength = (int)file.length();
        byte[] fileData = new byte[fileLength];
        
        try
        {
			// open input stream from file
        	FileInputStream fileIn = new FileInputStream(file);
			// read file into byte array
			fileIn.read(fileData);
			fileIn.close();
			
			// create response
			Response response = new Response(ResponseCode.CONTENT);
			response.setPayload(fileData);
			response.getOptions().setContentFormat(ct);

			exchange.respond(response);
			
        } catch (Exception e) {
			exchange.respond(ResponseCode.INTERNAL_SERVER_ERROR, "I/O error");
        }
	}
	
    private void captureImage(){
    	executeCommand(this.imgCaptureInstr);
    }
 
	private void executeCommand(String cmd) {
		Runtime r = Runtime.getRuntime();
		try {
			Process p = r.exec(cmd);
			p.waitFor();
		} catch (Exception e) {
			
		}
	}	
}