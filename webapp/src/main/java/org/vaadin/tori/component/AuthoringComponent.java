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

package org.vaadin.tori.component;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.ocpsoft.prettytime.PrettyTime;
import org.vaadin.tori.ToriUI;
import org.vaadin.tori.util.ToriScheduler;
import org.vaadin.tori.util.ToriScheduler.ScheduledCommand;
import org.vaadin.tori.view.thread.AuthoringData;
import org.vaadin.tori.view.thread.PostComponent;
import org.vaadin.tori.widgetset.client.ui.post.PostComponentClientRpc;
import org.vaadin.tori.widgetset.client.ui.post.PostData.PostAdditionalData;
import org.vaadin.tori.widgetset.client.ui.post.PostData.PostPrimaryData;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.server.Page;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class AuthoringComponent extends PostComponent {

    private final Map<String, byte[]> attachments = new HashMap<String, byte[]>();
    private final AuthoringListener listener;
    private CssLayout attachmentsLayout;
    private String attachmentFileName;
    private ByteArrayOutputStream attachmentData;
    private int maxFileSize = 307200;
    private VerticalLayout editorLayout;
    private AbstractField<String> editor;
    private Upload attach;
    private CheckBox followCheckbox;
    private boolean ignoreInputChanges;
    private Button postButton;

    public AuthoringComponent(final AuthoringListener listener) {
        super(null, null, new PrettyTime());
        this.listener = listener;
        addStyleName("authoringcomponent");
        addStyleName("authoring");
        addStyleName("editing");

        addComponent(buildEditorLayout());
    }

    private Component buildEditorLayout() {
        editorLayout = new VerticalLayout();
        editorLayout.addStyleName("posteditor");
        editor = buildEditor();
        ToriScheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                editorLayout.addComponent(editor, 0);
            }
        });

        attachmentsLayout = new CssLayout();
        attachmentsLayout.setVisible(false);
        attachmentsLayout.setStyleName("attachments");
        editorLayout.addComponent(attachmentsLayout);
        editorLayout.addComponent(buildButtons());
        return editorLayout;
    }

    private AbstractField<String> buildEditor() {
        AbstractField<String> result = null;
        if (Page.getCurrent().getWebBrowser().isAndroid()) {
            result = new TextArea();
        } else {
            result = new BBCodeWysiwygEditor(true, true);

            result.addValueChangeListener(new ValueChangeListener() {
                @Override
                public void valueChange(final ValueChangeEvent event) {
                    if (!ignoreInputChanges) {
                        listener.inputValueChanged(editor.getValue());
                    }
                }
            });

            ((BBCodeWysiwygEditor) result).addBlurListener(new BlurListener() {
                @Override
                public void blur(final BlurEvent event) {
                    UI.getCurrent().setPollInterval(
                            ToriUI.DEFAULT_POLL_INTERVAL);
                }
            });

            ((BBCodeWysiwygEditor) result)
                    .addFocusListener(new FocusListener() {
                        @Override
                        public void focus(final FocusEvent event) {
                            UI.getCurrent().setPollInterval(3000);
                        }
                    });
        }
        result.setSizeFull();
        return result;
    }

    public void setPostButtonCaption(final String caption) {
        postButton.setCaption(caption);
    }

    private Component buildButtons() {
        HorizontalLayout result = new HorizontalLayout();
        result.addStyleName("buttonslayout");
        result.setWidth(100.0f, Unit.PERCENTAGE);
        result.setSpacing(true);
        result.setMargin(true);
        postButton = new Button("Post Reply", new Button.ClickListener() {

            @Override
            public void buttonClick(final ClickEvent event) {
                if (editor.getValue().trim().isEmpty()) {
                    postButton.setEnabled(true);
                } else {
                    listener.submit(editor.getValue(), attachments,
                            followCheckbox.getValue());
                }
            }
        });
        postButton.setDisableOnClick(true);
        result.addComponent(postButton);

        attach = buildAttachUpload();
        result.addComponent(attach);
        followCheckbox = new CheckBox("Follow topic after posting", true);
        result.addComponent(followCheckbox);
        result.setComponentAlignment(followCheckbox, Alignment.MIDDLE_RIGHT);

        result.setExpandRatio(attach, 1.0f);
        result.setExpandRatio(followCheckbox, 1.0f);

        return result;
    }

    private Upload buildAttachUpload() {
        final Receiver receiver = new Receiver() {

            @Override
            public OutputStream receiveUpload(final String filename,
                    final String mimeType) {
                attachmentData = new ByteArrayOutputStream();
                attachmentFileName = filename;
                return attachmentData;
            }
        };

        final Upload attach = new Upload(null, receiver);
        attach.setButtonCaption("Add Attachment...");
        attach.setImmediate(true);
        attach.addSucceededListener(new Upload.SucceededListener() {

            @Override
            public void uploadSucceeded(final SucceededEvent event) {
                attachments.put(attachmentFileName,
                        attachmentData.toByteArray());
                attachmentFileName = null;
                attachmentData = null;
                updateAttachmentList();
            }
        });

        attach.addStartedListener(new Upload.StartedListener() {
            @Override
            public void uploadStarted(final StartedEvent event) {
                if (maxFileSize > 0 && event.getContentLength() > maxFileSize) {
                    attach.interruptUpload();
                    Notification.show("File size too large");
                    return;
                }
            }
        });
        return attach;
    }

    @Override
    protected void initData() {
    }

    public void insertIntoMessage(final String unformattedText) {
        ignoreInputChanges = true;
        if (unformattedText != null) {
            final String text = editor.getValue();
            editor.setValue(text + unformattedText);
        }
        editor.focus();
        ignoreInputChanges = false;
    }

    private void updateAttachmentList() {
        attachmentsLayout.removeAllComponents();
        attachmentsLayout.setVisible(!attachments.isEmpty());
        for (final Entry<String, byte[]> entry : attachments.entrySet()) {
            final String fileName = entry.getKey();
            final int fileSize = entry.getValue().length;
            final String caption = String.format("%s (%s KB)", fileName,
                    fileSize / 1024);

            final Label nameComponent = new Label();
            nameComponent.addStyleName("namelabel");
            nameComponent.setValue(caption);
            nameComponent.setWidth(300.0f, Unit.PIXELS);
            try {
                nameComponent.addStyleName(fileName.substring(fileName
                        .lastIndexOf(".") + 1));
            } catch (final IndexOutOfBoundsException e) {
                // NOP
            }

            final HorizontalLayout wrapperLayout = new HorizontalLayout();
            wrapperLayout.addStyleName("filerow");
            wrapperLayout.addComponent(nameComponent);

            final Label deleteLabel = new Label();
            deleteLabel.addStyleName("deleteattachment");

            wrapperLayout.addComponent(deleteLabel);
            wrapperLayout.addLayoutClickListener(new LayoutClickListener() {
                @Override
                public void layoutClick(final LayoutClickEvent event) {
                    if (event.getChildComponent() == deleteLabel) {
                        attachments.remove(entry.getKey());
                        updateAttachmentList();
                    }
                }
            });

            attachmentsLayout.addComponent(wrapperLayout);

        }
    }

    @Override
    public int getComponentCount() {
        return 1;
    }

    @Override
    public Iterator<Component> iterator() {
        return Arrays.asList((Component) editorLayout).iterator();
    }

    public void setAuthoringData(final AuthoringData authoringData) {
        if (authoringData != null) {
            attach.setVisible(authoringData.mayAddFiles());
            followCheckbox.setVisible(authoringData.mayFollow());
            maxFileSize = authoringData.getMaxFileSize();

            PostPrimaryData data = new PostPrimaryData();
            data.authorName = authoringData.getCurrentUserName();
            data.authorAvatarUrl = authoringData.getCurrentUserAvatarUrl();
            data.authorLink = null; //authoringData.getCurrentUserLink();
            getRpcProxy(PostComponentClientRpc.class).setPostPrimaryData(data);

            PostAdditionalData additionalData = new PostAdditionalData();
            additionalData.badgeHTML = authoringData.getCurrentUserBadgeHTML();
            getRpcProxy(PostComponentClientRpc.class).setPostAdditionalData(
                    additionalData);

            getRpcProxy(PostComponentClientRpc.class).editPost(editorLayout);
        }
    }

    public interface AuthoringListener {
        void submit(String rawBody, Map<String, byte[]> attachments,
                boolean follow);

        void inputValueChanged(String value);
    }

    public void reEnablePosting() {
        postButton.setEnabled(true);
    }
}
