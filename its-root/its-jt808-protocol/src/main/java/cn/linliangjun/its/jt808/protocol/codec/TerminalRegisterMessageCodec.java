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
import cn.linliangjun.its.jt808.protocol.message.LicensePlateColor;
import cn.linliangjun.its.jt808.protocol.message.TerminalRegisterMessage;
import cn.linliangjun.its.uniprotocol.util.CodecUtils;
import io.netty.buffer.ByteBuf;
import lombok.var;

import static cn.linliangjun.its.jt808.protocol.message.LicensePlateColor.*;
import static cn.linliangjun.its.jt808.protocol.message.TerminalRegisterMessage.*;
import static java.nio.charset.StandardCharsets.*;

public class TerminalRegisterMessageCodec extends AbstractCodec<TerminalRegisterMessage> {

    @Override
    protected void encodeBody(TerminalRegisterMessage message, ByteBuf buf) {
        Version version = message.getVersion();
        switch (version) {
            case V2011:
                encodeBodyV2011((TerminalRegisterMessage_2011) message, buf);
                break;
            case V2013:
                encodeBodyV2013((TerminalRegisterMessage_2013_2019) message, buf);
                break;
            case V2019:
                encodeBodyV2019((TerminalRegisterMessage_2013_2019) message, buf);
                break;
            default:
                throw new IllegalArgumentException("未适配的版本：" + version);
        }
    }

    private void encodeBodyV2011(TerminalRegisterMessage_2011 message, ByteBuf buf) {
        buf.writeShort(message.getProvinceId());
        buf.writeShort(message.getCityId());
        CodecUtils.writeFixedLenBytes(buf, message.getManufacturerId().getBytes(US_ASCII), 5, "制造商 ID");
        CodecUtils.writeMaxLenBytesFillHead(buf, message.getTerminalModel().getBytes(US_ASCII), 8, ' ', "终端型号");
        CodecUtils.writeFixedLenBytes(buf, message.getTerminalId().getBytes(US_ASCII), 7, "终端 ID");
        LicensePlateColor plateColor = message.getLicensePlateColor();
        if (plateColor == GREEN) {
            throw new IllegalArgumentException("不支持的车牌颜色：" + plateColor);
        }
        buf.writeByte(plateColor.getValue());
        buf.writeBytes(message.getLicensePlateNum().getBytes(GBK));
    }

    private void encodeBodyV2013(TerminalRegisterMessage_2013_2019 message, ByteBuf buf) {
        buf.writeShort(message.getProvinceId());
        buf.writeShort(message.getCityId());
        CodecUtils.writeFixedLenBytes(buf, message.getManufacturerId().getBytes(US_ASCII), 5, "制造商 ID");
        CodecUtils.writeMaxLenBytesFillTail(buf, message.getTerminalModel().getBytes(US_ASCII), 20, 0, "终端型号");
        CodecUtils.writeMaxLenBytesFillTail(buf, message.getTerminalId().getBytes(US_ASCII), 7, 0, "终端 ID");
        encodeLicensePlate(message, buf);
    }

    private void encodeBodyV2019(TerminalRegisterMessage_2013_2019 message, ByteBuf buf) {
        buf.writeShort(message.getProvinceId());
        buf.writeShort(message.getCityId());
        CodecUtils.writeFixedLenBytes(buf, message.getManufacturerId().getBytes(US_ASCII), 11, "制造商 ID");
        CodecUtils.writeMaxLenBytesFillHead(buf, message.getTerminalModel().getBytes(US_ASCII), 30, 0, "终端型号");
        CodecUtils.writeFixedLenBytes(buf, message.getTerminalId().getBytes(US_ASCII), 30, "终端 ID");
        encodeLicensePlate(message, buf);
    }

    private void encodeLicensePlate(TerminalRegisterMessage_2013_2019 message, ByteBuf buf) {
        String plateNumber = message.getLicensePlateNum();
        if (plateNumber != null) {
            buf.writeByte(message.getLicensePlateColor().getValue());
            buf.writeBytes(plateNumber.getBytes(GBK));
        } else {
            buf.writeZero(1);
            CodecUtils.writeFixedLenBytes(buf, message.getVin().getBytes(US_ASCII), 17, "车辆识别码");
        }
    }

    @Override
    protected TerminalRegisterMessage getMessageTemplate(Version version) {
        switch (version) {
            case V2011:
                return new TerminalRegisterMessage_2011();
            case V2013:
            case V2019:
                return new TerminalRegisterMessage_2013_2019(version);
            default:
                throw new IllegalArgumentException("未适配的版本：" + version);
        }
    }

    @Override
    protected void decodeBody(TerminalRegisterMessage message, ByteBuf buf) {
        var version = message.getVersion();
        switch (version) {
            case V2011:
                decodeBodyV2011((TerminalRegisterMessage_2011) message, buf);
                break;
            case V2013:
                decodeBodyV2013((TerminalRegisterMessage_2013_2019) message, buf);
                break;
            case V2019:
                decodeBodyV2019((TerminalRegisterMessage_2013_2019) message, buf);
                break;
            default:
                throw new IllegalArgumentException("未适配的版本：" + version);
        }
    }


    private void decodeBodyV2011(TerminalRegisterMessage_2011 message, ByteBuf buf) {
        message.setProvinceId(buf.readUnsignedShort());
        message.setCityId(buf.readUnsignedShort());
        message.setManufacturerId(buf.readCharSequence(5, US_ASCII).toString());
        message.setTerminalModel(buf.readCharSequence(8, US_ASCII).toString().trim());
        message.setTerminalId(buf.readCharSequence(7, US_ASCII).toString());
        var plateColor = getInstance(buf.readByte());
        if (plateColor == GREEN) {
            throw new IllegalArgumentException("不支持的车牌颜色：" + plateColor);
        }
        message.setLicensePlateColor(plateColor);
        message.setLicensePlateNum(buf.readCharSequence(message.getBodyLength() - 25, GBK).toString());
    }

    private void decodeBodyV2013(TerminalRegisterMessage_2013_2019 message, ByteBuf buf) {
        message.setProvinceId(buf.readUnsignedShort());
        message.setCityId(buf.readUnsignedShort());
        message.setManufacturerId(buf.readCharSequence(5, US_ASCII).toString());
        message.setTerminalModel(buf.readCharSequence(20, US_ASCII).toString().trim());
        message.setTerminalId(buf.readCharSequence(7, US_ASCII).toString().trim());
        byte value = buf.readByte();
        if (value == 0) {
            message.setVin(buf.readCharSequence(17, US_ASCII).toString());
        } else {
            message.setLicensePlateColor(LicensePlateColor.getInstance(value));
            message.setLicensePlateNum(buf.readCharSequence(message.getBodyLength() - 37, GBK).toString());
        }
    }


    private void decodeBodyV2019(TerminalRegisterMessage_2013_2019 message, ByteBuf buf) {
        message.setProvinceId(buf.readUnsignedShort());
        message.setCityId(buf.readUnsignedShort());
        message.setManufacturerId(buf.readCharSequence(11, US_ASCII).toString());
        message.setTerminalModel(buf.readCharSequence(30, US_ASCII).toString().trim());
        message.setTerminalId(buf.readCharSequence(30, US_ASCII).toString());
        byte value = buf.readByte();
        if (value == 0) {
            message.setVin(buf.readCharSequence(17, US_ASCII).toString());
        } else {
            message.setLicensePlateColor(LicensePlateColor.getInstance(value));
            message.setLicensePlateNum(buf.readCharSequence(message.getBodyLength() - 76, GBK).toString());
        }
    }
}
