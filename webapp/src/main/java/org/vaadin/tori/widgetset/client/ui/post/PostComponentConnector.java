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

package org.vaadin.tori.widgetset.client.ui.post;

import org.vaadin.tori.view.thread.PostComponent;
import org.vaadin.tori.widgetset.client.ui.post.PostData.PostAdditionalData;
import org.vaadin.tori.widgetset.client.ui.post.PostData.PostPrimaryData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.AbstractComponentContainerConnector;
import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.Connect;

@SuppressWarnings("serial")
@Connect(PostComponent.class)
public final class PostComponentConnector extends
        AbstractComponentContainerConnector {

    public PostComponentConnector() {
        registerRpc(PostComponentClientRpc.class, new PostComponentClientRpc() {

            @Override
            public void setPostPrimaryData(final PostPrimaryData data) {
                getWidget().updatePostData(data);
            }

            @Override
            public void setPostAdditionalData(final PostAdditionalData data) {
                getWidget().updatePostData(data);
            }

            @Override
            public void editPost(final Connector editor) {
                getWidget().addEditPostComponent(
                        ((AbstractComponentConnector) editor).getWidget());
            }

        });
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(PostWidget.class);
    }

    @Override
    public PostWidget getWidget() {
        return (PostWidget) super.getWidget();
    }

    @Override
    public void onConnectorHierarchyChange(
            final ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
        // Ignore
    }

    @Override
    public void updateCaption(final ComponentConnector connector) {
        // Ignore
    }

}
