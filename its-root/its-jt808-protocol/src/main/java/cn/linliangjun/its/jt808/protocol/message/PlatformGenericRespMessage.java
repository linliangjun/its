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
import cn.linliangjun.its.jt808.protocol.codec.PlatformGenericRespMessageCodec;
import cn.linliangjun.its.uniprotocol.Message;
import cn.linliangjun.its.uniprotocol.Protocol;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.var;

import java.util.StringJoiner;

/**
 * JT/T 808 消息——平台通用应答
 *
 * @author linliangjun
 */
@Getter
@Setter
public abstract class PlatformGenericRespMessage extends Jt808Message {

    /**
     * （终端）消息流水号
     */
    private Integer respSerialNum;

    /**
     * （终端）消息类型
     */
    private Type respType;

    public PlatformGenericRespMessage(Version version) {
        super(Type.PLATFORM_GENERIC_RESP, version);
    }

    @Getter
    @Setter
    @Protocol(name = "JT/T808", version = "V2011")
    @Message(id = 0x8001, name = "平台通用应答", codecClass = PlatformGenericRespMessageCodec.class)
    public static class PlatformGenericRespMessage_2011 extends PlatformGenericRespMessage {

        /**
         * 结果
         */
        private Result result;

        public PlatformGenericRespMessage_2011() {
            super(Version.V2011);
        }

        @Override
        protected String getDescription() {
            return new StringJoiner(", ", "{", "}")
                    .add("respSerialNum=" + getRespSerialNum())
                    .add("respType=" + getRespType())
                    .add("result=" + result)
                    .toString();
        }

        @Getter
        @RequiredArgsConstructor
        public enum Result {

            SUCCESS_ACK(0),
            FAILURE(1),
            MESSAGE_ERROR(2),
            UNSUPPORTED(3);

            private final int value;

            public static Result getInstance(int value) {
                for (var result : Result.values()) {
                    if (result.value == value) {
                        return result;
                    }
                }
                return null;
            }
        }
    }

    @Getter
    @Setter
    @Protocol(name = "JT/T808", version = {"V2013", "V2019"})
    @Message(id = 0x8001, name = "平台通用应答", codecClass = PlatformGenericRespMessageCodec.class)
    public static class PlatformGenericRespMessage_2013_2019 extends PlatformGenericRespMessage {



        /**
         * 结果
         */
        private Result result;

        public PlatformGenericRespMessage_2013_2019(Version version) {
            super(version);
        }

        @Override
        protected String getDescription() {
            return new StringJoiner(", ", "{", "}")
                    .add("respSerialNum=" + getRespSerialNum())
                    .add("respType=" + getRespType())
                    .add("result=" + result)
                    .toString();
        }

        @Getter
        @RequiredArgsConstructor
        public enum Result {

            SUCCESS_ACK(0),
            FAILURE(1),
            MESSAGE_ERROR(2),
            UNSUPPORTED(3),
            ALARM_DEAL_ACK(4);

            private final int value;

            public static Result getInstance(int value) {
                for (var result : Result.values()) {
                    if (result.value == value) {
                        return result;
                    }
                }
                return null;
            }
        }
    }
}
