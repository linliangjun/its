/*
 * Copyright 2023-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.linliangjun.its.uniprotocol.springframework.boot.autoconfigure;

import cn.linliangjun.its.uniprotocol.DefinitionLoaderProvider;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.boot.autoconfigure.AutoConfiguration;

import javax.annotation.PostConstruct;
import java.util.ServiceLoader;

@Slf4j
@AutoConfiguration
public class UniprotocolDefinitionLoaderAutoconfiguration {

    @PostConstruct
    private void init() throws Exception {
        for (var provider : ServiceLoader.load(DefinitionLoaderProvider.class)) {
            var loader = provider.get();
            loader.load();
        }
    }
}
