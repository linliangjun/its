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
import cn.linliangjun.its.jt808.protocol.message.PlatformGenericRespMessage;
import cn.linliangjun.its.jt808.protocol.message.Type;
import io.netty.buffer.ByteBuf;
import lombok.var;

import static cn.linliangjun.its.jt808.protocol.message.PlatformGenericRespMessage.*;

public class PlatformGenericRespMessageCodec extends AbstractCodec<PlatformGenericRespMessage> {

    @Override
    protected void encodeBody(PlatformGenericRespMessage message, ByteBuf buf) {
        var version = message.getVersion();
        switch (version) {
            case V2011:
                encodeBodyV2011((PlatformGenericRespMessage_2011) message, buf);
                break;
            case V2013:
            case V2019:
                encodeBodyV2013_2019((PlatformGenericRespMessage_2013_2019) message, buf);
                break;
            default:
                throw new IllegalArgumentException("未适配的版本：" + version);
        }
    }

    private void encodeBodyV2011(PlatformGenericRespMessage_2011 message, ByteBuf buf) {
        buf.writeShort(message.getRespSerialNum());
        buf.writeShort(message.getRespType().getValue());
        buf.writeByte(message.getResult().getValue());
    }

    private void encodeBodyV2013_2019(PlatformGenericRespMessage_2013_2019 message, ByteBuf buf) {
        buf.writeShort(message.getRespSerialNum());
        buf.writeShort(message.getRespType().getValue());
        buf.writeByte(message.getResult().getValue());
    }

    @Override
    protected PlatformGenericRespMessage getMessageTemplate(Version version) {
        switch (version) {
            case V2011:
                return new PlatformGenericRespMessage_2011();
            case V2013:
            case V2019:
                return new PlatformGenericRespMessage_2013_2019(version);
            default:
                throw new IllegalArgumentException("未适配的版本：" + version);
        }
    }

    @Override
    protected void decodeBody(PlatformGenericRespMessage message, ByteBuf buf) {
        var version = message.getVersion();
        switch (version) {
            case V2011:
                decodeBodyV2011_2013((PlatformGenericRespMessage_2011) message, buf);
                break;
            case V2013:
            case V2019:
                decodeBodyV2019((PlatformGenericRespMessage_2013_2019) message, buf);
                break;
            default:
                throw new IllegalArgumentException("未适配的版本：" + version);
        }
    }

    private void decodeBodyV2011_2013(PlatformGenericRespMessage_2011 message, ByteBuf buf) {
        message.setRespSerialNum(buf.readUnsignedShort());
        message.setRespType(Type.getType(buf.readUnsignedShort()));
        message.setResult(PlatformGenericRespMessage_2011.Result.getInstance(buf.readByte()));
    }

    private void decodeBodyV2019(PlatformGenericRespMessage_2013_2019 message, ByteBuf buf) {
        message.setRespSerialNum(buf.readUnsignedShort());
        message.setRespType(Type.getType(buf.readUnsignedShort()));
        message.setResult(PlatformGenericRespMessage_2013_2019.Result.getInstance(buf.readByte()));
    }
}
