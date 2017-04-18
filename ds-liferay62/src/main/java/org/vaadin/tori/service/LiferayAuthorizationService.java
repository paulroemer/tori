/*
 * Copyright 2012 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.vaadin.tori.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.messageboards.model.MBMessage;
import com.liferay.portlet.messageboards.service.MBBanLocalServiceUtil;
import com.liferay.portlet.messageboards.service.MBMessageServiceUtil;
import org.apache.log4j.Logger;
import org.vaadin.tori.HttpServletRequestAware;
import org.vaadin.tori.ThreadUser;

import javax.servlet.http.HttpServletRequest;

public class LiferayAuthorizationService implements AuthorizationService,
        HttpServletRequestAware {

    private static final Logger LOG = Logger
            .getLogger(LiferayAuthorizationService.class);
    private long scopeGroupId = -1;

    private User currentUser;
    private boolean banned;

    @Override
    public boolean mayEditCategories() {
		return isForumAdmin();
    }

    @Override
    public boolean mayReportPosts() {
        return isLoggedIn() && !isBanned();
    }

    @Override
    public boolean mayFollowCategory(final Long categoryId) {
    	// this feature is not available
        return false;
    }

    @Override
    public boolean mayDeleteCategory(final Long categoryId) {
		return isForumAdmin();
    }

    private boolean isForumAdmin() {
    	if(currentUser != null) {
			try {
				for (Role role : currentUser.getRoles()) {
					final String name = role.getName();

					if (name.equals("Adminstrator") ||
							name.equals("Forum Administrator")) {
						return true;
					}
				}
			} catch (SystemException e) {
				e.printStackTrace();
			}
		}

		return false;
	}

    @Override
    public boolean mayEditCategory(final Long categoryId) {
		return isForumAdmin();
    }

    @Override
    public boolean mayEditPost(final long postId) {
    	if(isForumAdmin()) { return true; }
		return isPostOwner(postId);
    }

    private boolean isPostOwner(final long postId) {
    	if(currentUser != null) {
			try {
				MBMessage message = MBMessageServiceUtil.getMessage(postId);
				return message.getUserId() == currentUser.getUserId();

			} catch (PortalException e) {
				e.printStackTrace();
			} catch (SystemException e) {
				e.printStackTrace();
			}
		}

		return false;
	}
    @Override
    public boolean mayReplyInThread(final long threadid) {
        return isLoggedIn() && !isBanned();
    }

    @Override
    public boolean mayAddFilesInCategory(final Long categoryId) {
		return isLoggedIn() && !isBanned();
    }

    @Override
    public boolean mayBan() {
        return isForumAdmin();
    }

    @Override
    public boolean mayFollowThread(final long threadId) {
        return isLoggedIn() && !isBanned();
    }

    @Override
    public boolean mayDeletePost(final long postId) {
		if(isForumAdmin()) { return true; }
		return isPostOwner(postId);
    }

    @Override
    public boolean mayVote() {
        return isLoggedIn() && !isBanned();
    }

    @Override
    public boolean mayMoveThreadInCategory(final Long categoryId) {
        return isForumAdmin();
    }

    @Override
    public boolean mayStickyThreadInCategory(final Long categoryId) {
		return isForumAdmin();
    }

    @Override
    public boolean mayLockThreadInCategory(final Long categoryId) {
		return isForumAdmin();
    }

    @Override
    public boolean mayDeleteThread(final long threadId) {
		return isForumAdmin();
    }

    @Override
    public boolean mayCreateThreadInCategory(final Long categoryId) {
		return isLoggedIn() && !isBanned();
    }

    private boolean isBanned() {
        return banned;
    }

    private boolean isLoggedIn() {
		return currentUser != null && !currentUser.isDefaultUser();
	}

    private void setCurrentUser(final User user) {
    	if(user != null) {
    		if(currentUser == null || !currentUser.equals(user)) {
				// user has changed
				currentUser = user;
				LOG.debug(String.format("Current user is now %s.", currentUser));
			}
		}
    }

    private void setBannedStatus() {
        // check the ban status and store in the banned field
        if (currentUser != null) {
            try {
                banned = MBBanLocalServiceUtil.hasBan(scopeGroupId,
                        Long.valueOf(currentUser.getUserId()));
            } catch (final SystemException e) {
                LOG.error("Cannot check ban status for user " + currentUser, e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setRequest(final HttpServletRequest request) {
        if (scopeGroupId < 0) {
            // scope not defined yet -> get if from the request
            final ThemeDisplay themeDisplay = (ThemeDisplay) request
                    .getAttribute(WebKeys.THEME_DISPLAY);

            if (themeDisplay != null) {
                scopeGroupId = themeDisplay.getScopeGroupId();
                LOG.debug("Using groupId " + scopeGroupId + " as the scope.");
            }
        }

        User user = null;
		try {
			user = UserLocalServiceUtil.getUser(Long.parseLong(ThreadUser.get()));
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}

		setCurrentUser(user);
        setBannedStatus();
    }

    @Override
    public boolean mayViewCategory(final Long categoryId) {
        return true;
    }

    @Override
    public boolean mayViewThread(final long threadId) {
        return true;
    }

    @Override
    public boolean mayViewPost(final long postId) {
        return true;
    }

}
