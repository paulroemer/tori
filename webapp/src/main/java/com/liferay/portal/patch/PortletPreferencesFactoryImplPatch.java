package com.liferay.portal.patch;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Layout;
import com.liferay.portlet.PortletPreferencesFactoryImpl;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

/**
 * Created by wolfgang on 27/05/16.
 */
public class PortletPreferencesFactoryImplPatch extends PortletPreferencesFactoryImpl {
	@Override
	protected PortletPreferences getPortletSetup(long scopeGroupId, Layout layout, String portletId, String defaultPreferences, boolean strictMode) throws SystemException {
		return new PortletPreferences() {
			@Override
			public boolean isReadOnly(String key) {
				System.out.println("# # # # isReadOnly");
				return false;
			}

			@Override
			public String getValue(String key, String def) {
				System.out.println("# # # # getValue for key: " + key + " and def: " + def + " (returning default)");
				return def;
			}

			@Override
			public String[] getValues(String key, String[] def) {
				System.out.println("# # # # getValues for key: " + key + " and def: " + def);
				return new String[0];
			}

			@Override
			public void setValue(String key, String value) throws ReadOnlyException {
				System.out.println("# # # # setValue for key: " + key + " : " + value);
			}

			@Override
			public void setValues(String key, String[] values) throws ReadOnlyException {
				System.out.println("# # # # setValues for key: " + key + " : " + values);
			}

			@Override
			public Enumeration<String> getNames() {
				System.out.println("# # # # getNames");
				return null;
			}

			@Override
			public Map<String, String[]> getMap() {
				System.out.println("# # # # getMap");
				return null;
			}

			@Override
			public void reset(String key) throws ReadOnlyException {
				System.out.println("# # # # reset key: " + key);
			}

			@Override
			public void store() throws IOException, ValidatorException {
				System.out.println("# # # # store");
			}
		};
	}
}
