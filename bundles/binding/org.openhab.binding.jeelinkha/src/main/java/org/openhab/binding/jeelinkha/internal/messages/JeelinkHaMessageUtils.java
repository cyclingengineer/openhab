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
