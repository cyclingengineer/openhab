/**
 * Copyright (c) 2010-2013, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.jeelinkha.internal.messages;

import javax.xml.bind.DatatypeConverter;

/**
 * Base class for JeelinkHa data classes. All other data classes should extend this class.
 * 
 * @author Paul Hampson
 * @since 1.4.0
 */
public abstract class JeelinkHaBaseMessage implements JeelinkHaMessageInterface {

	public enum PacketType {		
		TRANSMITTER_MESSAGE(0x0),		
		SALUSRT500RF(0x01),
		
		UNKNOWN(255);

		private final int packetType;

		PacketType(int packetType) {
			this.packetType = packetType;
		}

		PacketType(byte packetType) {
			this.packetType = packetType;
		}

		public byte toByte() {
			return (byte) packetType;
		}
	}

	public byte[] rawMessage;
	public PacketType packetType = PacketType.UNKNOWN;
	public byte id1 = 0;
	public byte id2 = 0;
	public byte status = 0;	

	public JeelinkHaBaseMessage() {

	}

	public JeelinkHaBaseMessage(byte[] data) {

		encodeMessage(data);
	}

	public void encodeMessage(byte[] data) {

		rawMessage = data;
		
		packetType = PacketType.UNKNOWN;

		for (PacketType pt : PacketType.values()) {
			if (pt.toByte() == data[1]) {
				packetType = pt;
				break;
			}
		}
		id1 = data[2];
		id2 = data[3];
	}

	public abstract byte[] decodeMessage();

	public String toString() {
		String str = "";

		str += "Raw data = " + DatatypeConverter.printHexBinary(rawMessage);
		str += "\n - Packet type = " + packetType;		

		return str;
	}
	
	public String generateDeviceId() {
	 return id1+"."+id2;
	}

}
