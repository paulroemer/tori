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

package org.vaadin.tori.data;

import org.vaadin.tori.Configuration;
import org.vaadin.tori.data.spi.ServiceProvider;
import org.vaadin.tori.service.AuthorizationService;
import org.vaadin.tori.service.LiferayAuthorizationService;
import org.vaadin.tori.service.LiferayToriMailService;
import org.vaadin.tori.util.LiferayToriActivityMessaging;
import org.vaadin.tori.util.LiferayPostFormatter;
import org.vaadin.tori.util.PostFormatter;
import org.vaadin.tori.util.ToriActivityMessaging;
import org.vaadin.tori.util.ToriMailService;

public class LiferayServiceProvider implements ServiceProvider {

    @Override
    public DataSource createDataSource() {
        return new LiferayDataSource() {
            private Configuration configuration = new Configuration();
            @Override
            public Configuration getConfiguration() {
                return configuration;
            }
        };
    }

    @Override
    public PostFormatter createPostFormatter() {
        return new LiferayPostFormatter();
    }

    @Override
    public AuthorizationService createAuthorizationService() {
        return new LiferayAuthorizationService();
    }

    @Override
    public ToriActivityMessaging createToriActivityMessaging() {
        return new LiferayToriActivityMessaging();
    }

    @Override
    public ToriMailService createToriMailService() {
        return new LiferayToriMailService();
    }

}
