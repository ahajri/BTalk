package com.ahajri.btalk.data.domain.xml;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Model Class to all XML Document
 * 
 * @author ahajri
 *
 */
@XmlRootElement(name = "XmlData")
public class XmlMap extends HashMap<String, Object> {

	/**
	 * Serialization UID
	 */
	private static final long serialVersionUID = 6806031461093444980L;



}
