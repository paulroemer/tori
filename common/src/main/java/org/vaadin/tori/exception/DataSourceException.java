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

package org.vaadin.tori.exception;

@SuppressWarnings("serial")
public class DataSourceException extends Exception {

    public static final String GENERIC_ERROR_MESSAGE = "Something unexpected happened. Please try reloading the page.";

    public DataSourceException() {
        super();
    }

    /**
     * @deprecated If you know enough about the exception to write a string
     *             description, consider subclassing {@link DataSourceException}
     *             instead.
     */
    @Deprecated
    public DataSourceException(final String arg0) {
        super(arg0);
    }

    public DataSourceException(final Throwable arg0) {
        super(arg0);
    }
}
