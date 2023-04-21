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

package cn.linliangjun.its.jt808.protocol.message;

import cn.linliangjun.its.jt808.protocol.Version;
import cn.linliangjun.its.jt808.protocol.codec.TerminalRegisterMessageCodec;
import cn.linliangjun.its.uniprotocol.Message;
import cn.linliangjun.its.uniprotocol.Protocol;
import lombok.Getter;
import lombok.Setter;

import java.util.StringJoiner;

/**
 * JT/T 808 消息——终端注册
 *
 * @author linliangjun
 */
public abstract class TerminalRegisterMessage extends AbstractMessage {

    public TerminalRegisterMessage(Version version) {
        super(Type.TERMINAL_REGISTER, version);
    }

    @Getter
    @Setter
    @Protocol(name = "JT/T808", version = "V2011")
    @Message(id = 0x0100, name = "终端注册", codecClass = TerminalRegisterMessageCodec.class)
    public static class TerminalRegisterMessage_2011 extends TerminalRegisterMessage {

        /**
         * 省域 ID
         */
        private Integer provinceId;

        /**
         * 市/县 ID
         */
        private Integer cityId;

        /**
         * 制造商 ID
         */
        private String manufacturerId;

        /**
         * 终端型号
         */
        private String terminalModel;

        /**
         * 终端 ID
         */
        private String terminalId;

        /**
         * 车牌颜色
         */
        private LicensePlateColor licensePlateColor;

        /**
         * 车牌号码
         */
        private String licensePlateNum;

        public TerminalRegisterMessage_2011() {
            super(Version.V2011);
        }

        @Override
        protected String getDescription() {
            return new StringJoiner(", ", TerminalRegisterMessage_2011.class.getSimpleName() + "{", "}")
                    .add("provinceId=" + provinceId)
                    .add("cityId=" + cityId)
                    .add("manufacturerId=" + manufacturerId)
                    .add("terminalModel=" + terminalModel)
                    .add("terminalId=" + terminalId)
                    .add("licensePlateColor=" + licensePlateColor)
                    .add("licensePlateNum=" + licensePlateNum)
                    .toString();
        }
    }

    @Getter
    @Setter
    @Protocol(name = "JT/T808", version = {"V2013", "V2019"})
    @Message(id = 0x0100, name = "终端注册", codecClass = TerminalRegisterMessageCodec.class)
    public static class TerminalRegisterMessage_2013_2019 extends TerminalRegisterMessage {

        /**
         * 省域 ID
         */
        private Integer provinceId;

        /**
         * 市/县 ID
         */
        private Integer cityId;

        /**
         * 制造商 ID
         */
        private String manufacturerId;

        /**
         * 终端型号
         */
        private String terminalModel;

        /**
         * 终端 ID
         */
        private String terminalId;

        /**
         * 车牌颜色
         */
        private LicensePlateColor licensePlateColor;

        /**
         * 车牌号码
         */
        private String licensePlateNum;

        /**
         * 车辆识别码
         */
        private String vin;

        public TerminalRegisterMessage_2013_2019(Version version) {
            super(version);
        }

        @Override
        protected String getDescription() {
            return new StringJoiner(", ", "{", "}")
                    .add("provinceId=" + provinceId)
                    .add("cityId=" + cityId)
                    .add("manufacturerId=" + manufacturerId)
                    .add("terminalModel=" + terminalModel)
                    .add("terminalId=" + terminalId)
                    .add("licensePlateColor=" + licensePlateColor)
                    .add("licensePlateNum=" + licensePlateNum + (licensePlateNum == null ? ", vin=" + vin : ""))
                    .toString();
        }
    }
}
