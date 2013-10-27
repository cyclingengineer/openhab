/**
 * Copyright (c) 2010-2013, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.jeelinkha.internal;

import javax.xml.bind.DatatypeConverter;

import org.openhab.binding.jeelinkha.JeelinkHaValueSelector;
import org.openhab.binding.jeelinkha.internal.messages.*;
import org.openhab.binding.jeelinkha.internal.messages.JeelinkHaBaseMessage.PacketType;
import org.openhab.core.library.items.ContactItem;
import org.openhab.core.library.items.DimmerItem;
import org.openhab.core.library.items.NumberItem;
import org.openhab.core.library.items.RollershutterItem;
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.OpenClosedType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.types.State;
import org.openhab.core.types.Type;
import org.openhab.core.types.UnDefType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides utilities to convert OpenHAB data types to RFXCOM data
 * types and vice verse.
 * 
 * @author Paul Hampson
 * @since 1.4.0
 */
public class JeelinkHaDataConverter {

	
	private static final Logger logger = LoggerFactory
			.getLogger(JeelinkHaDataConverter.class);

	/**
	 * Generate device id from JeelinkHa object data.
	 * 
	 * @param obj
	 *            JeelinkHa data object.
	 * 
	 * @return device id.
	 */
	public static String generateDeviceId(Object obj) {
		String id = null;

		if (obj instanceof JeelinkHaBaseMessage) {
			id = ((JeelinkHaBaseMessage) obj).generateDeviceId();
			
		} else {
			logger.warn("Error generate device id.");
		}

		return id;
	}

	/**
	 * Convert JeelinkHa objects to OpenHAB state.
	 * 
	 * @param obj
	 *            RFXCOM data object.
	 * @param valueSelector
	 *            value selector.
	 * 
	 * @return openHAB state.
	 */
	public static State convertJeelinkHaValueToOpenHABValue(Object obj,
			JeelinkHaValueSelector valueSelector) throws NumberFormatException {

		if (obj instanceof JeelinkHaSalusRt500RfMessage)
			return convertSalusRt500RfToState((JeelinkHaSalusRt500RfMessage) obj,
					valueSelector);
	
		throw new NumberFormatException("Can't convert " + obj.getClass()
				+ " to " + valueSelector.getItemClass());
	}

	private static State convertSalusRt500RfToState(JeelinkHaSalusRt500RfMessage obj,
			JeelinkHaValueSelector valueSelector) {

		org.openhab.core.types.State state = UnDefType.UNDEF;

		if (valueSelector.getItemClass() == NumberItem.class) {

			throw new NumberFormatException("Can't convert "
						+ valueSelector + " to NumberItem");			

		} else if (valueSelector.getItemClass() == DimmerItem.class
				|| valueSelector.getItemClass() == RollershutterItem.class) {

			throw new NumberFormatException("Can't convert "
						+ valueSelector + " to DimmerItem/RollershutterItem");

		} else if (valueSelector.getItemClass() == SwitchItem.class) {

			if (valueSelector == JeelinkHaValueSelector.COMMAND) {

				switch (obj.command) {
				case OFF:
					state = OnOffType.OFF;
					break;

				case ON:				
					state = OnOffType.ON;
					break;
				
				default:
					throw new NumberFormatException("Can't convert "
							+ obj.command + " to SwitchItem");
				
				}

			} else {
				throw new NumberFormatException("Can't convert "
						+ valueSelector + " to SwitchItem");
			}

		} else if (valueSelector.getItemClass() == ContactItem.class) {

			if (valueSelector == JeelinkHaValueSelector.COMMAND) {

				switch (obj.command) {
				case OFF:
					state = OpenClosedType.OPEN;
					break;

				case ON:				
					state = OpenClosedType.CLOSED;
					break;
				
				default:
					throw new NumberFormatException("Can't convert "
							+ obj.command + " to ContactItem");
				}

			} else {
				throw new NumberFormatException("Can't convert "
						+ valueSelector + " to ContactItem");
			}

		} else if (valueSelector.getItemClass() == StringItem.class) {

			if (valueSelector == JeelinkHaValueSelector.RAW_DATA) {

				state = new StringType(
						DatatypeConverter.printHexBinary(obj.rawMessage));

			} else {
				throw new NumberFormatException("Can't convert "
						+ valueSelector + " to StringItem");
			}

		} else {

			throw new NumberFormatException("Can't convert " + valueSelector
					+ " to " + valueSelector.getItemClass());

		}

		return state;
	}
	

	/**
	 * Convert OpenHAB value to JeelinkHa data object.
	 * 
	 * @param id
	 *            JeelinkHa device ID.
	 * @param packetType
	 *            JeelinkHa target packet type.
	 * @param valueSelector
	 *            value selector.
	 * @param type
	 *            OpenHAB data type.
	 * 
	 * @return RFXCOM object.
	 */
	public static Object convertOpenHABValueToJeelinkHaValue(String id,
			PacketType packetType,
			JeelinkHaValueSelector valueSelector, Type type) {

		Object obj = null;

		switch (packetType) {
			case SALUSRT500RF:
				JeelinkHaSalusRt500RfMessage d5 = new JeelinkHaSalusRt500RfMessage();		
				String[] ids5 = id.split("\\.");
				d5.sensorId = Integer.parseInt(ids5[0]);
				d5.unitcode = Byte.parseByte(ids5[1]);

				logger.debug(
						"convertOpenHABValueToJeelinkHaValue SalusRt500Rf (command='{}', type='{}')",
						new Object[] { valueSelector.toString(), type.toString()});

				
				switch (valueSelector) {
				case COMMAND:
					if (type instanceof OnOffType) {
						d5.command = (type == OnOffType.ON ? JeelinkHaSalusRt500RfMessage.Commands.ON
								: JeelinkHaSalusRt500RfMessage.Commands.OFF);
						obj = d5;
					} else {
						throw new NumberFormatException("Can't convert " + type
								+ " to Command");
					}
					break;

				case DIMMING_LEVEL:
					throw new NumberFormatException("Can't convert " + type
								+ " to Command");
				default:
					break;
				}
				break;				

		case UNKNOWN:
		default:
			break;
		}

		return obj;
	}

}
