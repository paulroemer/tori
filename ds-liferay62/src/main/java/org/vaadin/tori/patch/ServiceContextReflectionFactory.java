package org.vaadin.tori.patch;

import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.messageboards.model.MBMessage;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by wolfgang on 27/05/16.
 */
public class ServiceContextReflectionFactory {
	public static ServiceContext getInstance(String className, final HttpServletRequest request) {
		Class patchedClass = null;
		try {
			patchedClass = Class.forName("org.vaadin.tori.patch.ServiceContextFactoryPatch");
			Method m = patchedClass.getMethod("getInstance", new Class[]{String.class, HttpServletRequest.class});
			return (ServiceContext) m.invoke(null, new Object[]{className, request});
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
