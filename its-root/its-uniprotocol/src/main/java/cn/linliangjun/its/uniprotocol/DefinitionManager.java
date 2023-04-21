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

import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import lombok.var;

import java.util.HashSet;
import java.util.Set;

/**
 * 协议管理器
 *
 * @author linliangjun
 */
@Slf4j
public final class DefinitionManager {

    private static final Set<ProtocolDefinition> PROTOCOLS = new HashSet<>();

    /**
     * 注册协议定义
     *
     * @param definition 协议定义
     */
    public static void registerProtocolDefinition(ProtocolDefinition definition) {
        Assert.notNull(definition, "协议定义不能为 null");
        if (PROTOCOLS.contains(definition)) {
            throw new DuplicateDefinitionException(String.format("协议定义 %s 已注册，无法再次注册", definition));
        }
        // 注册协议一般是串行的，因此并未使用同步代码
        PROTOCOLS.add(definition);
        log.info("注册协议定义 {}", definition);
    }

    /**
     * 根据名称和版本获取已注册的协议定义（可能为 {@code null}）
     *
     * @param definition 协议定义
     * @return 已注册的协议定义（可能为 {@code null}）
     */
    public static ProtocolDefinition getProtocolDefinition(ProtocolDefinition definition) {
        for (var item : PROTOCOLS) {
            if (item.equals(definition)) {
                return item;
            }
        }
        return null;
    }

    public static void registerMessageDefinition(ProtocolDefinition protocolDefinition, MessageDefinition messageDefinition) {
        ProtocolDefinition p1 = getProtocolDefinition(protocolDefinition);
        if (p1 == null) {
            throw new DefinitionNotFoundException(String.format("协议定义 %s 尚未注册，无法为其注册消息定义", protocolDefinition));
        }
        p1.addMessageDefinition(messageDefinition);
    }

    public static MessageDefinition getMessageDefinition(ProtocolDefinition protocolDefinition, int messageId) {
        protocolDefinition = getProtocolDefinition(protocolDefinition);
        if (protocolDefinition == null) {
            return null;
        }
        return protocolDefinition.getMessageDefinition(messageId);
    }
}
