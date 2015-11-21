/**
 * Copyright (c) 2010-2013, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.jeelinkha.internal;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.Dictionary;
import java.util.EventObject;

import javax.xml.bind.DatatypeConverter;

import org.openhab.binding.jeelinkha.internal.connector.JeelinkHaEventListener;
import org.openhab.binding.jeelinkha.internal.connector.JeelinkHaSerialConnector;
import org.openhab.binding.jeelinkha.internal.messages.JeelinkHaMessageUtils;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class establishes the connection to the JeelinkHa controller.
 * 
 * @author Paul Hampson
 * @since 1.4.0
 */
public class JeelinkHaConnection implements ManagedService {

	private static final Logger logger = LoggerFactory
			.getLogger(JeelinkHaConnection.class);

	private static String serialPort = null;
	private static byte[] setMode = null;

	static JeelinkHaSerialConnector connector = new JeelinkHaSerialConnector();
	private MessageLister eventLister = new MessageLister();
	
	public void activate() {
		logger.debug("Activate");
	}

	public void deactivate() {
		logger.debug("Deactivate");
		
		if (connector != null) {
			connector.removeEventListener(eventLister);
			connector.disconnect();
		}
	}

	/**
	 * Returns the RFXCOM client for communicating to the controller. The link
	 * can be null, if it has not (yet) been established successfully.
	 * 
	 * @return instance to current RFXCOM client.
	 */
	public static synchronized JeelinkHaSerialConnector getCommunicator() {
		return connector;
	}

	@Override
	public void updated(Dictionary<String, ?> config)
			throws ConfigurationException {

		logger.debug("Configuration updated, config {}", config != null ? true
				: false);

		if (config != null) {

			serialPort = (String) config.get("serialPort");
			String setModeStr = (String) config.get("setMode");

			if (setModeStr != null && setModeStr.isEmpty() == false) {
				
				try {
					setMode = DatatypeConverter.parseHexBinary(setModeStr);
				} catch (IllegalArgumentException e) {
					throw new ConfigurationException("setMode", e.getMessage());
				}
				
				if (setMode.length != 14) {
					throw new ConfigurationException("setMode", "hexBinary value lenght should be 14 bytes (28 characters)");
				}
			}

			try {
				connect();

			} catch (Exception e) {
				logger.error("Connection to JeelinkHa controller failed.", e);
			}
		}

	}

	private void connect() throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, IOException, InterruptedException, ConfigurationException {

		logger.info("Connecting to JeelinkHa [serialPort='{}' ].",
				new Object[] { serialPort });

		connector.addEventListener(eventLister);
		connector.connect(serialPort);

		/* current stick implementation has no such command */
		//logger.debug("Reset controller");
		//connector.sendMessage(JeelinkHaMessageUtils.CMD_RESET);
		
		// controller does not response immediately after reset,
		// so wait a while
		Thread.sleep(1000);
		
		/* current stick implementation has no such command */
		/*if (setMode != null) {
			try {
				logger.debug("Set mode: {}",
						DatatypeConverter.printHexBinary(setMode));
			} catch (IllegalArgumentException e) {
				throw new ConfigurationException("setMode", e.getMessage());
			}
			
			connector.sendMessage(setMode);
		} else {
			connector.sendMessage(JeelinkHaMessageUtils.CMD_STATUS);
		} */
	}
	
	private class MessageLister implements JeelinkHaEventListener {

		@Override
		public void packetReceived(EventObject event, byte[] data) {

			try {
				Object obj = JeelinkHaMessageUtils.decodePacket(data);
				logger.debug("Data received:\n{}", obj.toString());
			} catch (IllegalArgumentException e) {
				logger.debug("Unknown data received, data: {}",
						DatatypeConverter.printHexBinary(data));
			}
		}
		
	}
}