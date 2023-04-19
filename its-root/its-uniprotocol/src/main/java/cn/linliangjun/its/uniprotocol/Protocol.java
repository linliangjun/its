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

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 协议类
 *
 * @author linliangjun
 */
@RequiredArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Protocol {

    /**
     * 协议名称
     */
    @NonNull
    @Getter
    @ToString.Include
    @EqualsAndHashCode.Include
    private final String name;

    /**
     * 协议版本
     */
    @NonNull
    @Getter
    @ToString.Include
    @EqualsAndHashCode.Include
    private final String version;

    @Setter
    private Map<String, String> mappings;

    private final Map<String, Codec<?, ?>> codecMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <P extends Protocol, M> Codec<P, M> getCodec(Class<M> messageClass) {
        String messageClassName = ClassUtil.getClassName(messageClass, false);
        String codecClassName = mappings.get(messageClassName);
        if (codecClassName == null) {
            return null;
        }
        Codec<P, M> codec = (Codec<P, M>) codecMap.get(codecClassName);
        if (codec != null) {
            return codec;
        }
        synchronized (codecMap) {
            codec = (Codec<P, M>) codecMap.get(codecClassName);
            if (codec != null) {
                return codec;
            }
            codec = ReflectUtil.newInstance(codecClassName);
            codecMap.put(codecClassName, codec);
            return codec;
        }
    }
}
