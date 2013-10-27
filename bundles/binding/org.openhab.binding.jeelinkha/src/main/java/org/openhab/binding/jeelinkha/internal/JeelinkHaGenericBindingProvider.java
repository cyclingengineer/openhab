/**
 * Copyright (c) 2010-2013, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.jeelinkha.internal;

import java.io.InvalidClassException;

import org.openhab.binding.jeelinkha.JeelinkHaBindingProvider;
import org.openhab.binding.jeelinkha.JeelinkHaValueSelector;
import org.openhab.binding.jeelinkha.internal.messages.JeelinkHaBaseMessage.PacketType;
import org.openhab.binding.jeelinkha.internal.messages.JeelinkHaMessageUtils;
import org.openhab.core.binding.BindingConfig;
import org.openhab.core.items.Item;
import org.openhab.model.item.binding.AbstractGenericBindingProvider;
import org.openhab.model.item.binding.BindingConfigParseException;

/**
 * <p>
 * This class can parse information from the generic binding format and provides
 * JeelinkHa device binding information from it.
 * </p>
 * 
 * <p>
 * The syntax of the binding configuration strings accepted is the following:
 * <p>
 * <p>
 * <code>
 * in:  jeelinkha:"&lt;DeviceId:ValueSelector"
 * <p>
 * out: jeelinkha:"&gt;DeviceId:PacketType.SubType:ValueSelector"
 * </code>
 * <p>
 * <p>
 * Examples for valid binding configuration strings:
 * 
 * <ul>
 * <li><code>jeelinkha="<2264:Temperature"</code></li>
 * <li><code>jeelinkha="<2264:Humidity"</code></li>
 * <li><code>jeelinkha="<635602.1:Command"</code></li>
 * <li><code>jeelinkha="<635602.2:Command"</code></li>
 * <li><code>jeelinkha">635602.1:LIGHTING2.AC:Command"</code></li>
 * </ul>
 * 
 * 
 * @author Pauli Anttila
 * @since 1.2.0
 */
public class JeelinkHaGenericBindingProvider extends
		AbstractGenericBindingProvider implements JeelinkHaBindingProvider {
	
	//private static final Logger logger = LoggerFactory
	//		.getLogger(JeelinkHaConnection.class);

	/**
	 * {@inheritDoc}
	 */
	public String getBindingType() {
		return "jeelinkha";
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public void validateItemType(Item item, String bindingConfig)
			throws BindingConfigParseException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processBindingConfiguration(String context, Item item,
			String bindingConfig) throws BindingConfigParseException {
		super.processBindingConfiguration(context, item, bindingConfig);
		JeelinkHaBindingConfig config = new JeelinkHaBindingConfig();

		String valueSelectorString = null;
		
		if (bindingConfig.startsWith("<")) {

			String[] configParts = bindingConfig.trim().split(":");

			if (configParts.length != 2) {
				throw new BindingConfigParseException(
						"JeelinkHa binding must contain two parts separated by ':'");
			}

			config.id = configParts[0].trim().replace("<", "");
			config.inBinding = true;
			
			//logger.debug("inbinding (<) id = " + config.id);

			valueSelectorString = configParts[1].trim();
			
			//logger.debug("inbinding (<) value = " + valueSelectorString);

		} else if (bindingConfig.startsWith(">")) {
			String[] configParts = bindingConfig.trim().split(":");

			config.id = configParts[0].trim().replace(">", "");
			config.inBinding = false;
			
			//logger.debug("outbinding (>) id = " + config.id);

			String[] types = configParts[1].trim().split("\\.");

			if (types.length != 2) {
				throw new BindingConfigParseException(
						"JeelinkHa out binding second field should contain 2 parts separated by '.'");
			}

			try {
				config.packetType = JeelinkHaMessageUtils.convertPacketType(types[0]
						.trim());
				//logger.debug("outbinding (>) packetType = " + config.packetType);

			} catch (Exception e) {
				throw new BindingConfigParseException("Invalid packet type '"
						+ types[0] + "'!");
			}

			try {
				//config.subType = JeelinkHaMessageUtils.convertSubType(config.packetType,
				//		types[1].trim());
				//logger.debug("outbinding (>) subType = " + config.subType);

			} catch (Exception e) {
				throw new BindingConfigParseException("Invalid sub type '"
						+ types[1] + "'!");
			}

			valueSelectorString = configParts[2].trim();
			//logger.debug("outbinding (>) value = " + valueSelectorString);

		} else {
			throw new BindingConfigParseException(
					"JeelinkHa binding should start < or > character!");
		}
		
		try {

			JeelinkHaValueSelector.validateBinding(valueSelectorString,
					item.getClass());

			config.valueSelector = JeelinkHaValueSelector
					.getValueSelector(valueSelectorString);

		} catch (IllegalArgumentException e1) {
			throw new BindingConfigParseException(
					"Invalid value selector '" + valueSelectorString + "'!");

		} catch (InvalidClassException e1) {
			throw new BindingConfigParseException(
					"Invalid item type for value selector '"
							+ valueSelectorString + "'!");

		}

		addBindingConfig(item, config);
	}

	class JeelinkHaBindingConfig implements BindingConfig {
		String id;
		JeelinkHaValueSelector valueSelector;
		boolean inBinding;
		PacketType packetType;
		Object subType;

	}

	@Override
	public String getId(String itemName) {
		JeelinkHaBindingConfig config = (JeelinkHaBindingConfig) bindingConfigs
				.get(itemName);
		return config != null ? config.id : null;
	}

	@Override
	public JeelinkHaValueSelector getValueSelector(String itemName) {
		JeelinkHaBindingConfig config = (JeelinkHaBindingConfig) bindingConfigs
				.get(itemName);
		return config != null ? config.valueSelector : null;
	}

	@Override
	public boolean isInBinding(String itemName) {
		JeelinkHaBindingConfig config = (JeelinkHaBindingConfig) bindingConfigs
				.get(itemName);
		return config != null ? config.inBinding : null;
	}

	@Override
	public PacketType getPacketType(String itemName) {
		JeelinkHaBindingConfig config = (JeelinkHaBindingConfig) bindingConfigs
				.get(itemName);
		return config != null ? config.packetType : null;
	}

	@Override
	public Object getSubType(String itemName) {
		JeelinkHaBindingConfig config = (JeelinkHaBindingConfig) bindingConfigs
				.get(itemName);
		return config != null ? config.subType : null;
	}

}
