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

package cn.linliangjun.its.uniprotocol;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.lang.ClassScanner;
import lombok.var;

import java.util.ArrayList;

public class AnnotationDefinitionLoader implements DefinitionLoader {

    private final String basePackage;

    public AnnotationDefinitionLoader(String basePackage) {
        this.basePackage = basePackage;
    }

    @Override
    public void load() {
        for (var clazz : ClassScanner.scanPackageByAnnotation(basePackage, Message.class)) {
            var protocolDefinitions = new ArrayList<ProtocolDefinition>();
            Protocol protocol = AnnotationUtil.getAnnotation(clazz, Protocol.class);
            if (protocol == null) {
                protocolDefinitions.add(ProtocolDefinition.getDEFAULT());
            }else {
                for (String version : protocol.version()) {
                    protocolDefinitions.add(new ProtocolDefinition(protocol.name(), version));
                }
            }
            Message message = AnnotationUtil.getAnnotation(clazz, Message.class);
            for (var protocolDefinition : protocolDefinitions) {

                var p1 = DefinitionManager.getProtocolDefinition(protocolDefinition);
                if (p1 == null) {
                    DefinitionManager.registerProtocolDefinition(protocolDefinition);
                }else {
                    protocolDefinition = p1;
                }
                var messageDefinition = new MessageDefinition(message.id(), message.name(), clazz, message.codecClass());
                DefinitionManager.registerMessageDefinition(protocolDefinition, messageDefinition);
            }
        }
    }
}
