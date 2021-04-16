package kr.co.eventroad.admin.common;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.LoggerFactory;

public class RootService {
	protected static final org.slf4j.Logger log = LoggerFactory.getLogger("ahsol.d");

	protected static void emptyRemoveMap(Map<?, ?> map) {
		if (map != null) {
			Iterator<Object> iter = new HashSet<Object>(map.keySet()).iterator();
			String key = null;
			while (iter.hasNext()) {
				key = (String) iter.next();
				if (map.get(key) == null || org.apache.commons.lang3.StringUtils.isEmpty(map.get(key).toString())) {
					map.remove(key);
				}
			}
		}
	}
}
