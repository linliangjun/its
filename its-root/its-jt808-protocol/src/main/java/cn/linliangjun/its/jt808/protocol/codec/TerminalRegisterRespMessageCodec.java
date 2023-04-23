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

package cn.linliangjun.its.jt808.protocol.codec;

import cn.linliangjun.its.jt808.protocol.Version;
import cn.linliangjun.its.jt808.protocol.message.TerminalRegisterRespMessage;
import io.netty.buffer.ByteBuf;

public class TerminalRegisterRespMessageCodec extends AbstractCodec<TerminalRegisterRespMessage>{

    @Override
    protected void encodeBody(TerminalRegisterRespMessage message, ByteBuf buf) {
        buf.writeShort(message.getRespSerialNum());
        buf.writeByte(message.getResult().getValue());
        buf.writeCharSequence(message.getAuthKey(), GBK);
    }

    @Override
    protected TerminalRegisterRespMessage getMessageTemplate(Version version) {
        return new TerminalRegisterRespMessage(version);
    }

    @Override
    protected void decodeBody(TerminalRegisterRespMessage message, ByteBuf buf) {
        message.setSerialNum(buf.readUnsignedShort());
        message.setResult(TerminalRegisterRespMessage.Result.getInstance(buf.readByte()));
        message.setAuthKey(buf.readCharSequence(message.getBodyLength() - 3, GBK).toString());
    }
}
