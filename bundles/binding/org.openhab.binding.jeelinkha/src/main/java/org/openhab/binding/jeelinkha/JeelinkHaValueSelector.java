/**
 * Copyright (c) 2010-2013, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.jeelinkha;

import java.io.InvalidClassException;

import org.openhab.core.items.Item;
import org.openhab.core.library.items.ContactItem;
import org.openhab.core.library.items.DimmerItem;
import org.openhab.core.library.items.NumberItem;
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.core.library.items.RollershutterItem;

/**
 * Represents all valid value selectors which could be processed by this
 * binding.
 * 
 * @author Pauli Anttila, Evert van Es
 * @since 1.2.0
 */
public enum JeelinkHaValueSelector {

	RAW_DATA ("RawData", StringItem.class),
	SHUTTER ("Shutter", RollershutterItem.class),
	COMMAND ("Command", SwitchItem.class),
	SIGNAL_LEVEL ("SignalLevel", NumberItem.class),
	DIMMING_LEVEL ("DimmingLevel", DimmerItem.class),
	TEMPERATURE ("Temperature", NumberItem.class),
	HUMIDITY ("Humidity", NumberItem.class),
	HUMIDITY_STATUS ("HumidityStatus", StringItem.class),
	BATTERY_LEVEL ("BatteryLevel", NumberItem.class),
	PRESSURE("Pressure", NumberItem.class),
	FORECAST("Forecast", NumberItem.class),
	RAIN_RATE("RainRate", NumberItem.class),
	RAIN_TOTAL("RainTotal", NumberItem.class),
	WIND_DIRECTION("WindDirection", NumberItem.class),
	WIND_SPEED("WindSpeed", NumberItem.class),
	GUST("Gust", NumberItem.class),
	CHILL_FACTOR("ChillFactor", NumberItem.class),
	INSTANT_POWER("InstantPower", NumberItem.class),
	TOTAL_USAGE("TotalUsage", NumberItem.class),
	INSTANT_AMPS("InstantAmps", NumberItem.class),
	TOTAL_AMP_HOURS("TotalAmpHours", NumberItem.class),
	STATUS("Status", StringItem.class),  	// Security1
	MOTION("Motion", SwitchItem.class),		// Security1
	CONTACT("Contact", ContactItem.class),	// Security1
	VOLTAGE("Voltage", NumberItem.class);

	private final String text;
	private Class<? extends Item> itemClass;

	private JeelinkHaValueSelector(final String text, Class<? extends Item> itemClass) {
		this.text = text;
		this.itemClass = itemClass;
	}

	@Override
	public String toString() {
		return text;
	}

	public Class<? extends Item> getItemClass() {
		return itemClass;
	}

	/**
	 * Procedure to validate selector string.
	 * 
	 * @param valueSelector
	 *            selector string e.g. RawData, Command, Temperature
	 * @return true if item is valid.
	 * @throws IllegalArgumentException
	 *             Not valid value selector.
	 * @throws InvalidClassException
	 *             Not valid class for value selector.
	 */
	public static boolean validateBinding(String valueSelector,
			Class<? extends Item> itemClass) throws IllegalArgumentException,
			InvalidClassException {

		for (JeelinkHaValueSelector c : JeelinkHaValueSelector.values()) {
			if (c.text.equals(valueSelector)) {

				if (c.getItemClass().equals(itemClass))
					return true;
				else
					throw new InvalidClassException(
							"Not valid class for value selector");
			}
		}

		throw new IllegalArgumentException("Not valid value selector");

	}

	/**
	 * Procedure to convert selector string to value selector class.
	 * 
	 * @param valueSelectorText
	 *            selector string e.g. RawData, Command, Temperature
	 * @return corresponding selector value.
	 * @throws InvalidClassException
	 *             Not valid class for value selector.
	 */
	public static JeelinkHaValueSelector getValueSelector(String valueSelectorText)
			throws IllegalArgumentException {

		for (JeelinkHaValueSelector c : JeelinkHaValueSelector.values()) {
			if (c.text.equals(valueSelectorText)) {
				return c;
			}
		}

		throw new IllegalArgumentException("Not valid value selector");
	}

}