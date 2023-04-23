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
import cn.linliangjun.its.jt808.protocol.codec.TerminalAuthMessageCodec;
import cn.linliangjun.its.uniprotocol.Message;
import cn.linliangjun.its.uniprotocol.Protocol;
import lombok.Getter;
import lombok.Setter;

import java.util.StringJoiner;

/**
 * JT/T 808 消息——终端鉴权
 *
 * @author linliangjun
 */
public abstract class TerminalAuthMessage extends Jt808Message {

    public TerminalAuthMessage(Version version) {
        super(Type.TERMINAL_AUTH, version);
    }

    /**
     * JT/T 808 消息——终端鉴权，2011、2013 版
     *
     * @author linliangjun
     */
    @Getter
    @Setter
    @Protocol(name = "JT/T808", version = {"V2011", "V2013"})
    @Message(id = 0x0102, name = "终端鉴权", codecClass = TerminalAuthMessageCodec.class)
    public static final class TerminalAuthMessage_2011_2013 extends TerminalAuthMessage {

        /**
         * 鉴权码
         */
        private String authKey;

        public TerminalAuthMessage_2011_2013(Version version) {
            super(version);
        }

        @Override
        protected String getDescription() {
            return new StringJoiner(", ", "{", "}")
                    .add("authKey=\"" + authKey + '"')
                    .toString();
        }
    }

    /**
     * JT/T 808 消息——终端鉴权，2019 版
     *
     * @author linliangjun
     */
    @Getter
    @Setter
    @Protocol(name = "JT/T808", version = "V2019")
    @Message(id = 0x0102, name = "终端鉴权", codecClass = TerminalAuthMessageCodec.class)
    public static final class TerminalAuthMessage_2019 extends TerminalAuthMessage {

        /**
         * 鉴权码
         */
        private String authKey;

        /**
         * 终端 IMEI
         */
        private String imei;

        /**
         * 软件版本号
         */
        private String softwareVersion;

        public TerminalAuthMessage_2019() {
            super(Version.V2019);
        }

        @Override
        protected String getDescription() {
            return new StringJoiner(", ", "{", "}")
                    .add("authKey=\"" + authKey + '"')
                    .add("imei=\"" + imei + '"')
                    .add("softwareVersion=\"" + softwareVersion + '"')
                    .toString();
        }
    }
}
