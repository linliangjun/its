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

import cn.linliangjun.its.jt808.protocol.ProtocolDefinition;
import cn.linliangjun.its.jt808.protocol.message.Jt808Message;
import cn.linliangjun.its.jt808.protocol.Version;
import cn.linliangjun.its.uniprotocol.Codec;
import cn.linliangjun.its.uniprotocol.CodecException;
import cn.linliangjun.its.uniprotocol.util.CodecUtils;
import cn.linliangjun.its.uniprotocol.util.StrUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.var;

import java.nio.charset.Charset;

/**
 * 抽象JT 808 协议编解码器
 *
 * @param <M> JT 808 消息类型
 * @author linliangjun
 * @apiNote 编码时，不会进行转义；解码时，不会解析消息头和尾的魔数，不会进行转义，不会进行 BCC 校验
 */
public abstract class AbstractCodec<M extends Jt808Message> implements Codec<ProtocolDefinition, M> {

    protected static final Charset GBK = Charset.forName("GBK");

    protected static final ByteBufAllocator ALLOCATOR = ByteBufAllocator.DEFAULT;

    private static final Encoder ENCODER = new Encoder();
    private static final Decoder DECODER = new Decoder();

    /**
     * 编码消息体
     *
     * @param message 消息对象
     * @param buf     字节缓冲区
     */
    protected abstract void encodeBody(M message, ByteBuf buf);

    /**
     * 获取消息模版
     *
     * @param version 协议版本
     */
    protected abstract M getMessageTemplate(Version version);

    /**
     * 解码消息体
     *
     * @param message 消息对象
     * @param buf     字节缓冲区
     */
    protected abstract void decodeBody(M message, ByteBuf buf);

    @Override
    public final ByteBuf encode(M message) {
        var buf = ALLOCATOR.buffer(32);
        buf.writeZero(1);       // 预留一个字节的头
        try {
            ENCODER.encodeHeader(message, buf);
            int startIndex = buf.writerIndex();
            encodeBody(message, buf);
            ENCODER.encodeBodyProperties(message, buf, startIndex);
            byte code = CodecUtils.getBccCode(buf, buf.readerIndex() + 1, buf.writerIndex() - buf.readerIndex() - 1);
            buf.writeByte(code);        // BCC 校验码
            // 转义
            var indexes = CodecUtils.getIndexes(buf, (byte) 0x7e);
            indexes.addAll(CodecUtils.getIndexes(buf, (byte) 0x7d));
            if (!indexes.isEmpty()) {
                var bufCopy = ALLOCATOR.buffer(buf.writerIndex() + indexes.size());
                int start = 0;
                for (var index : indexes) {
                    bufCopy.writeBytes(buf, start, index - start);
                    byte b = buf.getByte(index);
                    bufCopy.writeByte(0x7d);
                    bufCopy.writeByte(b == 0x7d ? 1 : 2);
                    start = index + 1;
                }
                bufCopy.writeBytes(buf, indexes.get(indexes.size() - 1));
                buf.release();
                buf = bufCopy;
            }
            buf.setByte(0, 0x7e);
            buf.writeByte(0x7e);
            return buf;
        } catch (Exception e) {
            buf.release();
            throw new CodecException(e);
        }
    }

    @Override
    public final M decode(ProtocolDefinition protocolDefinition, ByteBuf buf) {
        try {
            M message = getMessageTemplate(Version.valueOf(protocolDefinition.getVersion()));
            DECODER.decodeHeader(message, buf);
            decodeBody(message, buf);
            return message;
        } catch (Exception e) {
            throw new CodecException(e);
        }
    }

    private static final class Encoder {

        private void encodeHeader(Jt808Message message, ByteBuf buf) {
            buf.writeShort(message.getType().getValue());
            buf.writeZero(2);           // 留空，等消息体编码后再进行编码
            encodeVersion(message, buf);
            encodeTerminalPhoneNum(message, buf);
            buf.writeShort(message.getSerialNum());
            if (message.isPartial()) {
                buf.writeShort(message.getPackageTotal());
                buf.writeShort(message.getPackageNum());
            }
        }

        private void encodeVersion(Jt808Message message, ByteBuf buf) {
            var version = message.getVersion();
            switch (version) {
                case V2011:
                case V2013:
                    return;
                case V2019:
                    buf.writeByte(1);
                    return;
                default:
                    throw new IllegalArgumentException("未适配的版本：" + version);
            }
        }

        private void encodeTerminalPhoneNum(Jt808Message message, ByteBuf buf) {
            var version = message.getVersion();
            int limit;
            switch (version) {
                case V2011:
                case V2013:
                    limit = 6;
                    break;
                case V2019:
                    limit = 10;
                    break;
                default:
                    throw new IllegalArgumentException("未适配的版本：" + version);
            }
            byte[] bytes = CodecUtils.decimalToBcd8421(message.getTerminalPhoneNum());
            CodecUtils.writeMaxLenBytesFillHead(buf, bytes, limit, 0, "终端手机号");
        }

        private void encodeBodyProperties(Jt808Message message, ByteBuf buf, int startIndex) {
            var version = message.getVersion();
            var sb = new StringBuilder(16)
                    .append("0")
                    .append(version == Version.V2011 || version == Version.V2013 ? "0" : "1")
                    .append(message.isPartial() ? "1" : "0");
            switch (message.getEncryption()) {
                case NONE:
                    sb.append("000");
                    break;
                case RSA:
                    sb.append("001");
                    break;
                case RESERVE:
                    sb.append("010");
            }
            int bodyLen = buf.writerIndex() - startIndex;
            if (bodyLen > 1024) {
                throw new IllegalArgumentException("消息体长度溢出：" + bodyLen + " 字节");
            }
            String bin = Integer.toBinaryString(bodyLen);
            sb.append(StrUtils.zerofill(bin, 10));
            int i = Integer.parseInt(sb.toString(), 2);
            buf.setShort(3, i);
        }
    }

    private static final class Decoder {

        private void decodeHeader(Jt808Message message, ByteBuf buf) {
            buf.skipBytes(2);
            decodeBodyProperties(message, buf);
            decodeVersion(message, buf);
            decodeTerminalPhoneNum(message, buf);
            message.setSerialNum(buf.readUnsignedShort());
            if (message.isPartial()) {
                message.setPackageTotal(buf.readUnsignedShort());
                message.setPackageNum(buf.readUnsignedShort());
            }
        }

        private void decodeBodyProperties(Jt808Message message, ByteBuf buf) {
            String bin = Integer.toBinaryString(buf.readUnsignedShort());
            bin = StrUtils.zerofill(bin, 16);
            message.setBodyLength(Integer.parseInt(bin.substring(6), 2));
            String subStr = bin.substring(3, 6);
            if (subStr.equals("000")) {
                message.setEncryption(Jt808Message.Encryption.NONE);
            } else if (subStr.equals("001")) {
                message.setEncryption(Jt808Message.Encryption.RSA);
            } else {
                message.setEncryption(Jt808Message.Encryption.RESERVE);
            }
            subStr = bin.substring(2, 3);
            message.setPartial(subStr.equals("1"));
        }

        private void decodeVersion(Jt808Message message, ByteBuf buf) {
            var version = message.getVersion();
            int ordinal = version.ordinal();
            if (ordinal < Version.V2019.ordinal()) {
                return;
            }
            int n = buf.readUnsignedByte() + 1;
            if (ordinal != n) {
                throw new CodecException(String.format("协议版本号不匹配，期望：%s，实际：%s", version, Version.valueOf(ordinal)));
            }
        }

        private void decodeTerminalPhoneNum(Jt808Message message, ByteBuf buf) {
            var version = message.getVersion();
            int len;
            switch (version) {
                case V2011:
                case V2013:
                    len = 6;
                    break;
                case V2019:
                    len = 10;
                    break;
                default:
                    throw new IllegalArgumentException("未知的版本：" + version);
            }
            byte[] bytes = new byte[len];
            buf.readBytes(bytes);
            String terminalPhoneNum = CodecUtils.bcd8421ToDecimal(bytes).trim().replaceFirst("^0*", "");
            message.setTerminalPhoneNum(terminalPhoneNum);
        }
    }
}
