/**
 * Copyright (c) 2010-2013, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.jeelinkha.internal.connector;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RFXCOM connector for TCP/IP communication.
 * 
 * @author Pauli Anttila, Evert van Es
 * @since 1.2.0
 */
public class JeelinkHaTcpConnector implements JeelinkHaConnectorInterface {

	private static final Logger logger = LoggerFactory
			.getLogger(JeelinkHaTcpConnector.class);

	JeelinkHaTcpConnector() {
		
	}

	@Override
	public void connect(String device) throws Exception {

		logger.error("connect not implemented");
	}

	@Override
	public void disconnect() {
		logger.error("disconnect not implemented");
	}
	
	
	@Override
	public void sendMessage(byte[] data) throws IOException {

		logger.error("sendPacket not implemented");
	}

	@Override
	public void addEventListener(JeelinkHaEventListener listener) {

		logger.error("addEventListener not implemented");
	}

	@Override
	public void removeEventListener(JeelinkHaEventListener listener) {

		logger.error("removeEventListener not implemented");
	}

}