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

import cn.hutool.core.util.StrUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * XML 定义加载器
 *
 * @author linliangjun
 */
@Slf4j
@RequiredArgsConstructor
public abstract class XmlDefinitionLoader implements DefinitionLoader {

    @NonNull
    private final List<File> xmlFiles;

    @Override
    public void load() throws Exception {
        for (File xmlFile : xmlFiles) {
            try {
                new SAXReader()
                        .read(xmlFile)
                        .getRootElement()
                        .elements("protocol")
                        .forEach(this::registerProtocol);
            } catch (Exception e) {
                log.error("加载 uniprotocol 定义失败，XML 文件路径：{}，原因：{}", xmlFile.getAbsolutePath(), e.getMessage());
                throw e;
            }
        }
    }

    private void registerProtocol(Element element) {
        // 生成协议对象
        String name = element.attributeValue("name");
        String version = element.attributeValue("version");
        var protocol = new Protocol(name, version);

        // 读取 mappings 元素
        var mappingsElement = element.element("mappings");
        String messageClassPrefix = getPrefix(mappingsElement, "message-class-prefix");
        String codecClassPrefix = getPrefix(mappingsElement, "codec-class-prefix");

        // 生成消息类和编解码器类的映射
        var mappings = mappingsElement
                .elements("mapping").stream()
                .collect(Collectors.toMap(
                        e -> messageClassPrefix + e.attributeValue("message-class"),
                        e -> codecClassPrefix + e.attributeValue("codec-class")));
        protocol.setMappings(mappings);

        // 注册协议
        ProtocolManager.registerProtocol(protocol);
    }

    private String getPrefix(Element element, String attributeName) {
        String prefix = element.attributeValue(attributeName);
        return StrUtil.isBlank(prefix) ? "" : prefix + ".";
    }
}
