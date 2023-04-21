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

import cn.linliangjun.its.jt808.protocol.message.TerminalAuthMessage;
import cn.linliangjun.its.jt808.protocol.Version;
import cn.linliangjun.its.uniprotocol.util.CodecUtils;
import io.netty.buffer.ByteBuf;
import lombok.var;

import static cn.linliangjun.its.jt808.protocol.message.TerminalAuthMessage.TerminalAuthMessage_2011_2013;
import static cn.linliangjun.its.jt808.protocol.message.TerminalAuthMessage.TerminalAuthMessage_2019;
import static java.nio.charset.StandardCharsets.US_ASCII;

public class TerminalAuthMessageCodec extends AbstractCodec<TerminalAuthMessage> {

    @Override
    protected void encodeBody(TerminalAuthMessage message, ByteBuf buf) {
        var version = message.getVersion();
        switch (version) {
            case V2011:
            case V2013:
                encodeBodyV2011_2013((TerminalAuthMessage_2011_2013) message, buf);
                break;
            case V2019:
                encodeBodyV2019((TerminalAuthMessage_2019) message, buf);
                break;
            default:
                throw new IllegalArgumentException("未适配的版本：" + version);
        }
    }

    private void encodeBodyV2011_2013(TerminalAuthMessage_2011_2013 message, ByteBuf buf) {
        buf.writeCharSequence(message.getAuthKey(), GBK);
    }

    private void encodeBodyV2019(TerminalAuthMessage_2019 message, ByteBuf buf) {
        // 鉴权码长度，暂时先留空
        int writerIndex = buf.writerIndex();
        buf.writeZero(1);
        // 鉴权码
        byte[] bytes = message.getAuthKey().getBytes(GBK);
        CodecUtils.writeMaxLenBytes(buf, bytes, 255, "鉴权码");
        // 鉴权码长度
        buf.setByte(writerIndex, bytes.length);
        // IMEI
        CodecUtils.writeFixedLenBytes(buf, message.getImei().getBytes(US_ASCII), 15, "IMEI");
        // 软件版本号
        CodecUtils.writeMaxLenBytesFillTail(buf, message.getSoftwareVersion().getBytes(US_ASCII), 20, 0, "软件版本号");
    }

    @Override
    protected TerminalAuthMessage getMessageTemplate(Version version) {
        switch (version) {
            case V2011:
            case V2013:
                return new TerminalAuthMessage_2011_2013(version);
            case V2019:
                return new TerminalAuthMessage_2019();
            default:
                throw new IllegalArgumentException("未适配的版本：" + version);
        }
    }

    @Override
    protected void decodeBody(TerminalAuthMessage message, ByteBuf buf) {
        var version = message.getVersion();
        switch (version) {
            case V2011:
            case V2013:
                decodeBodyV2011_2013((TerminalAuthMessage_2011_2013) message, buf);
                break;
            case V2019:
                decodeBodyV2019((TerminalAuthMessage_2019) message, buf);
                break;
            default:
                throw new IllegalArgumentException("未适配的版本：" + version);
        }
    }

    private void decodeBodyV2011_2013(TerminalAuthMessage_2011_2013 message, ByteBuf buf) {
        message.setAuthKey(buf.readCharSequence(message.getBodyLength(), GBK).toString());
    }

    private void decodeBodyV2019(TerminalAuthMessage_2019 message, ByteBuf buf) {
        short authKeyLen = buf.readUnsignedByte();
        message.setAuthKey(buf.readCharSequence(authKeyLen, GBK).toString());
        message.setImei(buf.readCharSequence(15, US_ASCII).toString());
        message.setSoftwareVersion(buf.readCharSequence(20, US_ASCII).toString().trim());
    }
}
