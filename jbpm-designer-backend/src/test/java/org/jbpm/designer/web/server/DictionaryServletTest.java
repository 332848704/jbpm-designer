/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.designer.web.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.designer.helper.TestHttpServletRequest;
import org.jbpm.designer.helper.TestHttpServletResponse;
import org.jbpm.designer.helper.TestServletConfig;
import org.jbpm.designer.helper.TestServletContext;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.RepositoryBaseTest;
import org.jbpm.designer.repository.filters.FilterByExtension;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.repository.vfs.VFSRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DictionaryServletTest extends RepositoryBaseTest {

    @Before
    public void setup() {
        super.setup();
    }

    @After
    public void teardown() {
        super.teardown();
    }

    @Test
    public void testStoreDictionary() throws Exception {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("action",
                   "save");
        params.put("profile",
                   "jbpm");
        params.put("dvalue",
                   "this is dictionary");

        DictionaryServlet dictionaryServlet = new DictionaryServlet();
        dictionaryServlet.setProfile(profile);

        dictionaryServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new TestHttpServletResponse();
        dictionaryServlet.doPost(new TestHttpServletRequest(params),
                                 response);

        String responseText = new String(response.getContent());
        assertNotNull(responseText);
        assertEquals("saved",
                     responseText);

        Collection<Asset> dictionary = repository.listAssets("/global",
                                                             new FilterByExtension("json"));
        assertNotNull(dictionary);
        assertEquals(1,
                     dictionary.size());

        Asset<String> dictionaryAsset = repository.loadAsset(dictionary.iterator().next().getUniqueId());
        assertNotNull(dictionaryAsset);
        assertEquals("this is dictionary",
                     dictionaryAsset.getAssetContent());
    }

    @Test
    public void testLoadDictionary() throws Exception {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("test dictionary content")
                .type("json")
                .name("processdictionary")
                .location("/global");
        repository.createAsset(builder.getAsset());
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("action",
                   "load");
        params.put("profile",
                   "jbpm");

        DictionaryServlet dictionaryServlet = new DictionaryServlet();
        dictionaryServlet.setProfile(profile);

        dictionaryServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new TestHttpServletResponse();
        dictionaryServlet.doPost(new TestHttpServletRequest(params),
                                 response);

        String dictionaryContent = new String(response.getContent());
        assertNotNull(dictionaryContent);
        assertEquals("test dictionary content",
                     dictionaryContent);
    }
}
