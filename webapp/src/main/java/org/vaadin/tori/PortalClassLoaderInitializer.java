package org.vaadin.tori;

import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by wolfgang on 15/05/16.
 */
public class PortalClassLoaderInitializer implements InitializingBean {
	@Override
	public void afterPropertiesSet() throws Exception {
		PortalClassLoaderUtil.setClassLoader(Thread.currentThread().getContextClassLoader());
	}
}
