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

package org.vaadin.tori.edit;

import java.util.Map;

import org.vaadin.tori.mvp.View;

import edu.umd.cs.findbugs.annotations.CheckForNull;

public interface EditView extends View {

    void setReplacements(Map<String, String> postReplacements);

    void setConvertMessageBoardsUrls(boolean convert);

    void showNotification(String notification);

    void setGoogleAnalyticsTrackerId(
            @CheckForNull String googleAnalyticsTrackerId);

    void setPathRoot(String pathRoot);

}
