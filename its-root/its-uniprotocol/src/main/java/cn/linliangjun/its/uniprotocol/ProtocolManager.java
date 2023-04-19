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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 协议管理器
 *
 * @author linliangjun
 */
@Slf4j
public final class ProtocolManager {

    private static final Set<Protocol> PROTOCOLS = new HashSet<>();

    /**
     * 注册协议
     *
     * @param protocol 协议对象
     */
    public static void registerProtocol(Protocol protocol) {
        Assert.notNull(protocol, "协议不能为 null");
        if (PROTOCOLS.contains(protocol)) {
            throw new DuplicateProtocolException(protocol);
        }
        // 注册协议一般是串行的，因此并未使用同步代码
        PROTOCOLS.add(protocol);
        log.info("注册协议 {}", protocol);
    }

    /**
     * 根据名称和版本获取已注册的协议（可能为 {@code null}）
     *
     * @param protocol 协议对象
     * @return 已注册的协议（可能为 {@code null}）
     */
    public static Protocol getProtocol(Protocol protocol) {
        for (var item : PROTOCOLS) {
            if (item.equals(protocol)) {
                return item;
            }
        }
        return null;
    }

    public static Set<Protocol> getAllProtocols() {
        return Collections.unmodifiableSet(PROTOCOLS);
    }
}
