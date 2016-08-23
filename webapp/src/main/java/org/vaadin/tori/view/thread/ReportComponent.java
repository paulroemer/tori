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

package org.vaadin.tori.view.thread;

import java.net.URI;

import org.vaadin.hene.popupbutton.PopupButton;
import org.vaadin.hene.popupbutton.PopupButton.PopupVisibilityEvent;
import org.vaadin.hene.popupbutton.PopupButton.PopupVisibilityListener;
import org.vaadin.tori.service.post.PostReport.Reason;
import org.vaadin.tori.util.ComponentUtil;
import org.vaadin.tori.view.thread.ThreadView.PostData;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ReportComponent extends CustomComponent {
    private final PostData post;
    private final ThreadPresenter presenter;
    private final PopupButton reportPopup;

    private Layout explanationLayout;
    private Button reportButton;
    private final String postPermalink;

    /**
     * 
     * @param post
     * @param presenter
     * @param postFragmentPermalink
     *            The fragment part to the post component to be reported.
     */
    public ReportComponent(final PostData post,
            final ThreadPresenter presenter, final String postFragmentPermalink) {

        this.post = post;
        this.presenter = presenter;
        this.postPermalink = postFragmentPermalink;
        addStyleName("flagpost");
        setSizeUndefined();

        reportPopup = new PopupButton("Flag post...");
        reportPopup.addPopupVisibilityListener(new PopupVisibilityListener() {
            @Override
            public void popupVisibilityChange(final PopupVisibilityEvent event) {
                if (reportPopup.getContent() == null) {
                    reportPopup.setContent(newReportLayout());
                }
            }
        });
        setCompositionRoot(reportPopup);
    }

    private Component newReportLayout() {
        final VerticalLayout layout = new VerticalLayout();
        //layout.setWidth("260px");
        layout.setSpacing(true);
        layout.setMargin(true);

        layout.addComponent(new Label("What's wrong with this post?"));

        final OptionGroup reason = new OptionGroup();
        reason.addItem(Reason.SPAM);
        reason.setItemCaption(Reason.SPAM, "Spam");
        reason.addItem(Reason.OFFENSIVE);
        reason.setItemCaption(Reason.OFFENSIVE,
                "Offensive, abusive or hateful.");
        reason.addItem(Reason.WRONG_CATEGORY);
        reason.setItemCaption(Reason.WRONG_CATEGORY,
                "Doesn't belong in the category.");
        reason.addItem(Reason.MODERATOR_ALERT);
        reason.setItemCaption(Reason.MODERATOR_ALERT,
                "A moderator should take a look at it.");

        reason.setImmediate(true);
        reason.addValueChangeListener(new OptionGroup.ValueChangeListener() {
            @Override
            public void valueChange(final ValueChangeEvent event) {
                explanationLayout.setVisible(reason.getValue() == Reason.MODERATOR_ALERT);
                reportButton.setEnabled(reason.getValue() != null);
            }
        });

        layout.addComponent(reason);

        explanationLayout = new CssLayout();
        explanationLayout.setStyleName("explanationlayout");
        explanationLayout.addComponent(new Label("Here's why:"));
        final TextArea reasonText = createReasonTextArea();
        explanationLayout.addComponent(reasonText);
        explanationLayout.setVisible(false);
        explanationLayout.setWidth("100%");
        layout.addComponent(explanationLayout);

        final HorizontalLayout footer = new HorizontalLayout();
        footer.setSpacing(true);
        layout.addComponent(footer);
        layout.setComponentAlignment(footer, Alignment.BOTTOM_RIGHT);

        reportButton = new Button("Report Post");
        reportButton.addClickListener(new NativeButton.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                final URI l = Page.getCurrent().getLocation();
                final String link = l.getScheme() + "://" + l.getAuthority()
                        + l.getPath() + postPermalink;
                presenter.handlePostReport(post, (Reason) reason.getValue(),
                        reasonText.getValue(), link);
                reportPopup.setPopupVisible(false);
            }
        });
        reportButton.setEnabled(false);
        footer.addComponent(reportButton);

        final Button cancel = ComponentUtil.getSecondaryButton("Cancel",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
                        reportPopup.setPopupVisible(false);
                    }
                });
        footer.addComponent(cancel);

        return layout;
    }

    private static TextArea createReasonTextArea() {
        final TextArea area = new TextArea();
        area.setWidth("100%");
        area.setRows(4);
        return area;
    }

}
