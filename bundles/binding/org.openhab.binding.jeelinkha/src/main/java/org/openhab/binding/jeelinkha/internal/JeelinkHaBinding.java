/**
 * Copyright (c) 2010-2013, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.jeelinkha.internal;

import java.io.IOException;
import java.util.EventObject;

import javax.xml.bind.DatatypeConverter;

import org.openhab.binding.jeelinkha.JeelinkHaBindingProvider;
import org.openhab.binding.jeelinkha.JeelinkHaValueSelector;
import org.openhab.binding.jeelinkha.internal.connector.JeelinkHaEventListener;
import org.openhab.binding.jeelinkha.internal.connector.JeelinkHaSerialConnector;
import org.openhab.binding.jeelinkha.internal.messages.JeelinkHaBaseMessage.PacketType;
import org.openhab.binding.jeelinkha.internal.messages.JeelinkHaMessageUtils;
import org.openhab.binding.jeelinkha.internal.messages.JeelinkHaTransmitterMessage;
import org.openhab.core.binding.AbstractBinding;
import org.openhab.core.events.EventPublisher;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.openhab.core.types.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JeelinkHaComBinding listens to JeelinkHaCOM controller notifications and post values to
 * the openHAB event bus when data is received and post item updates
 * from openHAB internal bus to JeelinkHaCOM controller.
 * 
 * @author Pauli Anttila, Evert van Es
 * @since 1.2.0
 */
public class JeelinkHaBinding extends AbstractBinding<JeelinkHaBindingProvider> {

	private static final Logger logger = LoggerFactory
			.getLogger(JeelinkHaBinding.class);

	private EventPublisher eventPublisher;
	
	private static final int timeout = 5000;
	
	private static JeelinkHaTransmitterMessage responseMessage = null;
	private Object notifierObject = new Object();

	private MessageLister eventLister = new MessageLister();

	public JeelinkHaBinding() {
	}

	public void activate() {
		logger.debug("Activate");
		JeelinkHaSerialConnector connector = JeelinkHaConnection.getCommunicator();
		if (connector != null) {
			connector.addEventListener(eventLister);
		}
	}

	public void deactivate() {
		logger.debug("Deactivate");
		JeelinkHaSerialConnector connector = JeelinkHaConnection.getCommunicator();
		if (connector != null) {
			connector.removeEventListener(eventLister);
		}
	}

	public void setEventPublisher(EventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	public void unsetEventPublisher(EventPublisher eventPublisher) {
		this.eventPublisher = null;
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	protected void internalReceiveCommand(String itemName, Command command) {
		executeCommand(itemName, command);
	}

	/**
	 * Find the first matching {@link JeelinkHaComBindingProvider} according to
	 * <code>itemName</code> and <code>command</code>.
	 * 
	 * @param itemName
	 * 
	 * @return the matching binding provider or <code>null</code> if no binding
	 *         provider could be found
	 */
	private JeelinkHaBindingProvider findFirstMatchingBindingProvider(
			String itemName) {

		JeelinkHaBindingProvider firstMatchingProvider = null;

		for (JeelinkHaBindingProvider provider : this.providers) {

			String Id = provider.getId(itemName);

			if (Id != null) {
				firstMatchingProvider = provider;
				break;
			}
		}

		return firstMatchingProvider;
	}

	private void executeCommand(String itemName, Type command) {
		if (itemName != null) {
			JeelinkHaBindingProvider provider = findFirstMatchingBindingProvider(itemName);
			if (provider == null) {
				logger.warn(
						"Cannot execute command because no binding provider was found for itemname '{}'",
						itemName);
				return;
			}

			if (provider.isInBinding(itemName) == false) {
				logger.debug(
						"Received command (item='{}', state='{}', class='{}')",
						new Object[] { itemName, command.toString(),
								command.getClass().toString() });
				JeelinkHaSerialConnector connector = JeelinkHaConnection
						.getCommunicator();

				if (connector == null) {
					logger.warn("JeelinkHaCom controller is not initialized!");
					return;
				}

				String id = provider.getId(itemName);
				PacketType packetType = provider.getPacketType(itemName);				
				JeelinkHaValueSelector valueSelector = provider
						.getValueSelector(itemName);

				Object obj = JeelinkHaDataConverter
						.convertOpenHABValueToJeelinkHaValue(id, packetType,
								valueSelector, command);
				byte[] data = JeelinkHaMessageUtils.encodePacket(obj);
				logger.debug("Transmitting data: {}",
						DatatypeConverter.printHexBinary(data));

				setResponseMessage(null);

				try {
					connector.sendMessage(data);
				} catch (IOException e) {
					logger.error("Message sending to JeelinkHaCOM controller failed.", e);	
				}

				try {

					synchronized (notifierObject) {
						notifierObject.wait(timeout);
					}

					JeelinkHaTransmitterMessage resp = getResponseMessage();

					switch (resp.response) {
					case OK:					
						logger.debug(
								"Command succesfully transmitted, '{}' received",
								resp.response);
						break;

					case FAIL:					
					case UNKNOWN:
						logger.error("Command transmit failed, '{}' received",
								resp.response);
						break;
					}

				} catch (InterruptedException ie) {
					logger.error(
							"No acknowledge received from JeelinkHa controller, timeout {}ms ",
							timeout);
				}
			}
			else
			{
				logger.warn(
						"Provider is not in binding '{}'",
						provider.toString());
			}

		}

	}

	public static synchronized JeelinkHaTransmitterMessage getResponseMessage() {
		return responseMessage;
	}

	public synchronized void setResponseMessage(
			JeelinkHaTransmitterMessage responseMessage) {
		JeelinkHaBinding.responseMessage = responseMessage;
	}

	private class MessageLister implements JeelinkHaEventListener {

		@Override
		public void packetReceived(EventObject event, byte[] packet) {

			try {
				Object obj = JeelinkHaMessageUtils.decodePacket(packet);

				if (obj instanceof JeelinkHaTransmitterMessage) {
					JeelinkHaTransmitterMessage resp = (JeelinkHaTransmitterMessage) obj;

					/*if (resp.seqNbr == getSeqNumber()) {
						logger.debug("Transmitter response received:\n{}",
								obj.toString()); */
						setResponseMessage(resp);
						synchronized (notifierObject) {
							notifierObject.notify();
						}
					//}

				} else {
					String id2 = JeelinkHaDataConverter.generateDeviceId(obj);

					for (JeelinkHaBindingProvider provider : providers) {
						for (String itemName : provider.getItemNames()) {

							String id1 = provider.getId(itemName);
							boolean inBinding = provider.isInBinding(itemName);

							if (id1.equals(id2) && inBinding) {

								JeelinkHaValueSelector parseItem = provider
										.getValueSelector(itemName);

								State value = JeelinkHaDataConverter
										.convertJeelinkHaValueToOpenHABValue(
												obj, parseItem);
								eventPublisher.postUpdate(itemName, value);
							}

						}
					}
				}
			} catch (IllegalArgumentException e) {
				logger.debug("Unknown packet received, data: {}",
						DatatypeConverter.printHexBinary(packet), e);
			}
		}
	}

}
