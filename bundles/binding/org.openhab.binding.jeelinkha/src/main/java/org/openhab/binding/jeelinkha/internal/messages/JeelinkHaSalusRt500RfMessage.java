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
 * JeelinkHa data class for Salus RT500RF message.
 * 
 * @author Paul Hampson
 * @since 1.4.0
 */
public class JeelinkHaSalusRt500RfMessage extends JeelinkHaBaseMessage {
	
	public enum Commands {
		OFF(0),
		ON(1);

		private final int command;

		Commands(int command) {
			this.command = command;
		}

		Commands(byte command) {
			this.command = command;
		}

		public byte toByte() {
			return (byte) command;
		}
	}

	public Commands command = Commands.OFF;
	public byte unitcode = 0;
	public int sensorId = 0;

	public JeelinkHaSalusRt500RfMessage() {
		packetType = PacketType.SALUSRT500RF;

	}

	public JeelinkHaSalusRt500RfMessage(byte[] data) {

		encodeMessage(data);
	}

	@Override
	public String toString() {
		String str = "";

		str += super.toString();		
		str += "\n - Command = " + command;		

		return str;
	}

	@Override
	public void encodeMessage(byte[] data) {

		super.encodeMessage(data);
		command = Commands.values()[data[2]];		
	}

	@Override
	public byte[] decodeMessage() {

		byte[] data = new byte[7];

		data[0] = 0x07;
		data[1] = JeelinkHaBaseMessage.PacketType.SALUSRT500RF.toByte();
		sensorId = (data[2] & 0xFF) << 16 | (data[3] & 0xFF) << 8
				| (data[4] & 0xFF) << 0;
		unitcode = data[5];
		data[6] = command.toByte();		
		return data;
	}
	
	@Override
	public String generateDeviceId() {
		 return "";
	}
}
