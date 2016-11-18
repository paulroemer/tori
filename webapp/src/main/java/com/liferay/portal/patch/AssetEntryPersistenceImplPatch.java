package com.liferay.portal.patch;

import com.liferay.portal.service.PersistedModelLocalServiceRegistry;
import com.liferay.portal.service.impl.UserLocalServiceImpl;
import com.liferay.portlet.asset.service.persistence.AssetCategoryPersistence;
import com.liferay.portlet.asset.service.persistence.AssetEntryPersistenceImpl;
import com.liferay.portlet.asset.service.persistence.AssetTagPersistence;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by wolfgang on 31/05/16.
 */
public class AssetEntryPersistenceImplPatch extends AssetEntryPersistenceImpl implements InitializingBean {
	public void setAssetCategoryPersistence(AssetCategoryPersistence persistence) {
		this.assetCategoryPersistence = persistence;
	}

	public void setAssetTagPersistence(AssetTagPersistence persistence) {
		this.assetTagPersistence = persistence;
	}
}
