/*
 * Copyright 2014 Vaadin Ltd.
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

package org.vaadin.tori.view.thread;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.ocpsoft.prettytime.PrettyTime;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.tori.ToriNavigator;
import org.vaadin.tori.ToriUI;
import org.vaadin.tori.exception.DataSourceException;
import org.vaadin.tori.util.ComponentUtil;
import org.vaadin.tori.util.ToriScheduler;
import org.vaadin.tori.util.ToriScheduler.ScheduledCommand;
import org.vaadin.tori.view.thread.PostEditor.PostEditorListener;
import org.vaadin.tori.view.thread.ThreadView.PostData;
import org.vaadin.tori.widgetset.client.ui.post.PostComponentClientRpc;
import org.vaadin.tori.widgetset.client.ui.post.PostData.PostAdditionalData;
import org.vaadin.tori.widgetset.client.ui.post.PostData.PostPrimaryData;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;

@SuppressWarnings("serial")
public class PostComponent extends AbstractComponentContainer implements
        PostEditorListener {

    private static final String DELETE_CAPTION = "Delete Post...";
    private static final String EDIT_CAPTION = "Edit Post";
    private static final String BAN_CAPTION = "Ban Author";
    private static final String UNBAN_CAPTION = "Unban Author";
    private static final String STYLE_BANNED = "banned-author";

    private final PrettyTime prettyTime;
    private PostData post;

    private MenuBar settings;
    private Footer footer;
    private Component editorComponent;

    private final ThreadPresenter presenter;

    public PostComponent(final PostData post, final ThreadPresenter presenter,
            final PrettyTime prettyTime) {
        this.prettyTime = prettyTime;
        this.presenter = presenter;
        this.post = post;

        setVisible(post == null || post.userMayView());

        setStyleName("post");

        initData();
    }

    protected void initData() {
        updatePrimaryData();

        ToriScheduler.get().scheduleManual(new ScheduledCommand() {
            @Override
            public void execute() {
                updateAdditionalData();
            }
        });
    }

    public void update(final PostData post) {
        this.post = post;
        initData();
    }

    private void updatePrimaryData() {
        PostPrimaryData data = new PostPrimaryData();
        data.attachments = post.getAttachments();
        data.authorName = post.getAuthorName();
        data.authorAvatarUrl = post.getAuthorAvatarUrl();
        data.authorLink = null; //post.getAuthorLink();
        data.postBody = post.getFormattedBody(true);
        getRpcProxy(PostComponentClientRpc.class).setPostPrimaryData(data);
    }

    private void updateAdditionalData() {
        PostAdditionalData data = new PostAdditionalData();
        data.prettyTime = prettyTime.format(post.getTime());
        data.permaLink = getPermaLinkUrl(post);
        setUserIsBanned(post.isAuthorBanned());
        data.badgeHTML = post.getBadgeHTML();

        if (footer == null) {
            footer = new Footer();
            addComponent(footer);
        }
        footer.update();
        data.footer = footer;

        if (settings == null) {
            settings = buildSettings();
            addComponent(settings);
        }
        updateSettings();
        data.settings = settings.isVisible() ? settings : null;

        getRpcProxy(PostComponentClientRpc.class).setPostAdditionalData(data);
    }

    private void updateSettings() {
        MenuItem root = settings.getMoreMenuItem();
        root.removeChildren();
        Command command = new Command() {
            @Override
            public void menuSelected(final MenuItem selectedItem) {
                if (EDIT_CAPTION.equals(selectedItem.getText())) {
                    editPost();
                } else if (DELETE_CAPTION.equals(selectedItem.getText())) {
                    confirmDelete();
                } else if (UNBAN_CAPTION.equals(selectedItem.getText())) {
                    presenter.unban(post.getAuthorId());
                } else if (BAN_CAPTION.equals(selectedItem.getText())) {
                    presenter.ban(post.getAuthorId());
                }
            }
        };

        if (post.userMayEdit()) {
            root.addItem(EDIT_CAPTION, command);
        }
        if (post.userMayDelete()) {
            root.addItem(DELETE_CAPTION, command);
        }
        if (post.userMayBanAuthor()) {
            if (root.hasChildren()) {
                root.addSeparator();
            }
            if (post.isAuthorBanned()) {
                root.addItem(UNBAN_CAPTION, command);
            } else {
                root.addItem(BAN_CAPTION, command);
            }
        }

        settings.setVisible(root.hasChildren());
    }

    private MenuBar buildSettings() {
        final MenuBar settingsMenuBar = ComponentUtil.getDropdownMenu();
        settingsMenuBar.addStyleName("settings");
        return settingsMenuBar;
    }

    private void confirmDelete() {
        ConfirmDialog dialog = ConfirmDialog.show(getUI(),
                "Are you sure you want to delete the post?",
                new ConfirmDialog.Listener() {
                    @Override
                    public void onClose(final ConfirmDialog arg0) {
                        if (arg0.isConfirmed()) {
                            ((ComponentContainer) getParent())
                                    .removeComponent(PostComponent.this);
                            presenter.delete(post.getId());
                        }
                    }
                });
        dialog.getOkButton().setCaption("Delete Post");
    }

    private void postVoted(final boolean up) {
        try {
            if (up) {
                presenter.upvote(post.getId());
            } else {
                presenter.downvote(post.getId());
            }
            updateAdditionalData();
        } catch (final DataSourceException e) {
            Notification.show(DataSourceException.GENERIC_ERROR_MESSAGE);
        }
    }

    private void quoteForReply() {
        presenter.quotePost(post.getId());
    }

    private void editPost() {
        final CssLayout editorWrapper = new CssLayout();
        editorWrapper.setSizeFull();
        ToriScheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                PostEditor postEditor = new PostEditor(post.getRawBody(), post
                        .isFormatBBCode(), PostComponent.this);
                editorWrapper.addComponent(postEditor);
                addStyleName("editing");
            }
        });
        getRpcProxy(PostComponentClientRpc.class).editPost(editorWrapper);
        if (editorComponent != null) {
            removeComponent(editorComponent);
        }
        editorComponent = editorWrapper;
        addComponent(editorComponent);
    }

    private void setUserIsBanned(final boolean banned) {
        if (banned) {
            addStyleName(STYLE_BANNED);
            setDescription(post.getAuthorName() + " is banned.");
        } else {
            removeStyleName(STYLE_BANNED);
            setDescription(null);
        }
    }

    private static String getPermaLinkUrl(final PostData post) {
        // @formatter:off
        final String linkUrl = String.format(
                "forum/#%s/%s/%s",
                ToriNavigator.ApplicationView.THREADS.getUrl(),
                post.getThreadId(),
                post.getId()
                );
        // @formatter:on

        return linkUrl;
    }

    @Override
    public void replaceComponent(final Component oldComponent,
            final Component newComponent) {

    }

    @Override
    public int getComponentCount() {
        int count = 0;
        Iterator<Component> i = iterator();
        while (i.hasNext()) {
            i.next();
            count++;
        }
        return count;
    }

    @Override
    public Iterator<Component> iterator() {
        List<Component> components = new ArrayList<Component>(Arrays.asList(
                editorComponent, settings, footer));
        components.removeAll(Collections.singleton(null));
        return components.iterator();
    }

    @Override
    public void submitEdit(final String rawBody) {
        presenter.saveEdited(post.getId(), rawBody);
        ToriUI.getCurrent().trackAction("edit-post");

        closeEditor();
    }

    @Override
    public void cancelEdit() {
        closeEditor();
    }

    private void closeEditor() {
        removeStyleName("editing");
        updatePrimaryData();
    }

    public Long getPostId() {
        return post.getId();
    }

    private class Footer extends CssLayout {
        private final Label upVote = new Label();
        private final Label downVote = new Label();
        private final Label reply = new Label("Reply");
        private final Label score = new Label();
        private final ReportComponent report = new ReportComponent(post,
                presenter, getPermaLinkUrl(post));

        public Footer() {
            setStyleName("footer");

            upVote.setSizeUndefined();
            downVote.setSizeUndefined();
            score.setSizeUndefined();
            reply.setSizeUndefined();
            reply.setStyleName("quoteforreply");

            addComponent(upVote);
            addComponent(downVote);
            addComponent(score);
            addComponent(reply);
            addComponent(report);

            addLayoutClickListener(new LayoutClickListener() {
                @Override
                public void layoutClick(final LayoutClickEvent event) {
                    if (event.getClickedComponent() == upVote) {
                        postVoted(true);
                    } else if (event.getClickedComponent() == downVote) {
                        postVoted(false);
                    } else if (event.getClickedComponent() == reply) {
                        quoteForReply();
                    }
                }
            });
        }

        public void update() {
            upVote.setVisible(post.userMayVote());
            downVote.setVisible(post.userMayVote());

            upVote.setStyleName("vote upvote");
            downVote.setStyleName("vote downvote");
            if (post.userMayVote() && post.getUpVoted() != null) {
                if (post.getUpVoted()) {
                    upVote.addStyleName("done");
                } else {
                    downVote.addStyleName("done");
                }
            }

            long newScore = post.getScore();
            score.setStyleName("score");
            score.setValue((newScore > 0 ? "+" : "") + String.valueOf(newScore));
            String scoreStyle = "zero";
            if (newScore > 0) {
                scoreStyle = "positive";
            } else if (newScore < 0) {
                scoreStyle = "negative";
            }
            score.addStyleName(scoreStyle);

            reply.setVisible(post.userMayQuote());

            report.setVisible(post.userMayReportPosts());
        }
    }
}
