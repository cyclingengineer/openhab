/**
 * Copyright (c) 2010-2013, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.jeelinkha;

import org.openhab.binding.jeelinkha.internal.messages.JeelinkHaBaseMessage.PacketType;
import org.openhab.core.binding.BindingProvider;

/**
 * This interface is implemented by classes that can provide mapping information
 * between openHAB items and JeelinkHa items.
 * 
 * Implementing classes should register themselves as a service in order to be
 * taken into account.
 * 
 * @author Paul Hampson
 * @since 1.4.0
 */
public interface JeelinkHaBindingProvider extends BindingProvider {

	/**
	 * Returns the id to the given <code>itemName</code>.
	 * 
	 * @param itemName
	 *            the item for which to find a id.
	 * 
	 * @return the corresponding id to the given <code>itemName</code>.
	 */
	public String getId(String itemName);

	/**
	 * Returns the value selector to the given <code>itemName</code>.
	 * 
	 * @param itemName
	 *            the item for which to find a unit code.
	 * 
	 * @return the corresponding value selector to the given
	 *         <code>itemName</code>.
	 */
	public JeelinkHaValueSelector getValueSelector(String itemName);

	/**
	 * Returns item direction to the given <code>itemName</code>.
	 * 
	 * @param itemName
	 *            the item for which to find a binding mode.
	 * 
	 * @return true if item is in binding.
	 */
	public boolean isInBinding(String itemName);

	/**
	 * Returns item packet type to the given <code>itemName</code>.
	 * 
	 * @param itemName
	 *            the item for which to find a packet type.
	 * 
	 * @return the corresponding packet type to the given <code>itemName</code>.
	 */
	public PacketType getPacketType(String itemName);

}
