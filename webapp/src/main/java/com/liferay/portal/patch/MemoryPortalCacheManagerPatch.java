package com.liferay.portal.patch;

import com.liferay.portal.cache.memory.MemoryPortalCacheManager;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by wolfgang on 06/05/16.
 * The original implementation sets the internal var '_portalCaches' in 'apterPropertiesSet'
 * but forgets to implement the InitializingBean interface. Guess tha class has never been used or tested
 * by the liferay people
 */
public class MemoryPortalCacheManagerPatch extends MemoryPortalCacheManager implements InitializingBean {
}
