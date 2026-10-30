package org.iottree.driver.zigbee.ember.util;

import org.iottree.driver.zigbee.ember.EmberManager;

public interface ICopyable<T>
{
	public T copyMe(EmberManager emgr) ;
}
