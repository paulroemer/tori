package org.vaadin.tori.util;

import com.thoughtworks.xstream.XStream;
import org.apache.commons.collections.Transformer;

public class XStreamCopyTransformer implements Transformer {
	private static final XStream XSTREAM;

	static {
		XSTREAM = new XStream();
		XSTREAM.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);
	}

	@Override
	public Object transform(Object o) {
		String s = XSTREAM.toXML(o);
		Object result = XSTREAM.fromXML(s);
		return result;
	}
}
