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

import io.netty.buffer.ByteBuf;

/**
 * 编解码器
 *
 * @param <P> 协议类型
 * @param <M> 消息类型
 */
public interface Codec<P extends ProtocolDefinition, M> {

    /**
     * 编码
     *
     * @param message 消息对象
     * @return 字节缓冲区
     * @throws CodecException 编码异常
     */
    ByteBuf encode(M message);

    /**
     * 解码
     *
     * @param protocolDefinition 协议
     * @param buf      字节缓冲区
     * @return 消息对象
     * @throws CodecException 解码异常
     */
    M decode(P protocolDefinition, ByteBuf buf);
}
