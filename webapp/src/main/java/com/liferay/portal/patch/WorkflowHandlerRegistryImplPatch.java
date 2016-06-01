package com.liferay.portal.patch;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.model.WorkflowDefinitionLink;
import com.liferay.portal.workflow.WorkflowHandlerRegistryImpl;
import com.liferay.portlet.asset.model.AssetRenderer;
import com.liferay.portlet.asset.model.AssetRendererFactory;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

/**
 * Created by wolfgang on 31/05/16.
 */
public class WorkflowHandlerRegistryImplPatch extends WorkflowHandlerRegistryImpl {
	@Override
	public WorkflowHandler getWorkflowHandler(final String className) {
		System.out.println("# # # # Faking WorkflowHandler for " + className);
		WorkflowHandler result = new WorkflowHandler() {
			@Override
			public AssetRenderer getAssetRenderer(long classPK) throws PortalException, SystemException {
				return null;
			}

			@Override
			public AssetRendererFactory getAssetRendererFactory() {
				return null;
			}

			@Override
			public String getClassName() {
				return className;
			}

			@Override
			public String getIconPath(LiferayPortletRequest liferayPortletRequest) {
				return null;
			}

			@Override
			public String getSummary(long classPK, Locale locale) {
				return null;
			}

			@Override
			public String getTitle(long classPK, Locale locale) {
				return null;
			}

			@Override
			public String getType(Locale locale) {
				return null;
			}

			@Override
			public PortletURL getURLEdit(long classPK, LiferayPortletRequest liferayPortletRequest, LiferayPortletResponse liferayPortletResponse) {
				return null;
			}

			@Override
			public String getURLViewInContext(long classPK, LiferayPortletRequest liferayPortletRequest, LiferayPortletResponse liferayPortletResponse, String noSuchEntryRedirect) {
				return null;
			}

			@Override
			public WorkflowDefinitionLink getWorkflowDefinitionLink(long companyId, long groupId, long classPK) throws PortalException, SystemException {
				return null;
			}

			@Override
			public boolean isAssetTypeSearchable() {
				return false;
			}

			@Override
			public boolean isScopeable() {
				return false;
			}

			@Override
			public boolean isVisible() {
				return false;
			}

			@Override
			public String render(long classPK, RenderRequest renderRequest, RenderResponse renderResponse, String template) {
				return null;
			}

			@Override
			public void startWorkflowInstance(long companyId, long groupId, long userId, long classPK, Object model, Map<String, Serializable> workflowContext) throws PortalException, SystemException {

			}

			@Override
			public Object updateStatus(int status, Map<String, Serializable> workflowContext) throws PortalException, SystemException {
				return null;
			}
		};
		return result;
	}
}
