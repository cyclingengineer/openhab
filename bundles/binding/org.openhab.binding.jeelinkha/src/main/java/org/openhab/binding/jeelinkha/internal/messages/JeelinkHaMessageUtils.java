/**
 * Copyright (c) 2010-2013, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.jeelinkha.internal.messages;

import org.openhab.binding.jeelinkha.internal.messages.JeelinkHaBaseMessage.PacketType;

/**
 * This class provides utilities to encode and decode JeelinkHa data.
 * 
 * @author Paul Hampson
 * @since 1.4.0
 */
public class JeelinkHaMessageUtils {

	/**
	 * Command to reset JeelinkHa controller.
	 * 
	 */
	public final static byte[] CMD_RESET = new byte[] { 0x0D, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

	/**
	 * Command to get JeelinkHa controller status.
	 * 
	 */
	public final static byte[] CMD_STATUS = new byte[] { 0x0D, 0x00, 0x00,
			0x01, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

	/**
	 * Command to save JeelinkHa controller configuration.
	 * 
	 */
	public final static byte[] CMD_SAVE = new byte[] { 0x0D, 0x00, 0x00, 0x00,
			0x06, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

	public static Object decodePacket(byte[] data) throws IllegalArgumentException {

		Object obj = null;

		byte packetType = data[1];

		switch (packetType) {
		case (byte) 0x00:
			obj = new JeelinkHaTransmitterMessage(data);
			break;
		case (byte) 0x01:
			obj = new JeelinkHaSalusRt500RfMessage(data);
			break;

		default:
			throw new IllegalArgumentException("Packet type " + (int) packetType
					+ " not implemented!");
		}

		return obj;
	}

	public static byte[] encodePacket(Object obj)  throws IllegalArgumentException {

		byte[] data = null;

		if (obj instanceof JeelinkHaBaseMessage)
			data = ((JeelinkHaBaseMessage) obj).decodeMessage();

		if( data == null ) {
			throw new IllegalArgumentException("No valid encoder implemented!");
		}
	
		return data;
	}

	public static PacketType convertPacketType(String packetType)
			throws IllegalArgumentException {

		for (PacketType p : PacketType.values()) {
			if (p.toString().equals(packetType)) {
				return p;
			}
		}

		throw new IllegalArgumentException("Unknown packet type " + packetType);
	}
	
	
}
