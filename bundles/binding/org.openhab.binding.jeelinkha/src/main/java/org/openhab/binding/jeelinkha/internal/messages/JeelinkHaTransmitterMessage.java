/**
 * Copyright (c) 2010-2013, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.jeelinkha.internal.messages;

/**
 * JeelinkHa data class for transmitter message.
 * 
 * @author Paul Hampson
 * @since 1.4.0
 */
public class JeelinkHaTransmitterMessage extends JeelinkHaBaseMessage {

	public enum SubType {
		TRANSMITTER_MESSAGE(1),

		UNKNOWN(255);

		private final int subType;

		SubType(int subType) {
			this.subType = subType;
		}

		SubType(byte subType) {
			this.subType = subType;
		}

		public byte toByte() {
			return (byte) subType;
		}
	}

	public enum Response {
		OK(0), // transmit OK
		FAIL(1), // transmit Fail

		UNKNOWN(255);

		private final int response;

		Response(int response) {
			this.response = response;
		}

		Response(byte response) {
			this.response = response;
		}

		public byte toByte() {
			return (byte) response;
		}
	}

	public Response response = Response.UNKNOWN;

	public JeelinkHaTransmitterMessage() {
		packetType = PacketType.TRANSMITTER_MESSAGE;

	}

	public JeelinkHaTransmitterMessage(byte[] data) {

		encodeMessage(data);
	}

	@Override
	public String toString() {
		String str = "";

		str += super.toString();
		str += "\n - Response = " + response;

		return str;
	}

	@Override
	public void encodeMessage(byte[] data) {

		super.encodeMessage(data);

		response = Response.values()[data[2]];

	}

	@Override
	public byte[] decodeMessage() {

		byte[] data = new byte[3];

		data[0] = 0x03;
		data[1] = JeelinkHaBaseMessage.PacketType.TRANSMITTER_MESSAGE.toByte();		
		data[2] = response.toByte();

		return data;
	}

}
