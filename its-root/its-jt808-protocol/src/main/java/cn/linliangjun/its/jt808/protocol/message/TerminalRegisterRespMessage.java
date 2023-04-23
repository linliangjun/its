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
import cn.linliangjun.its.jt808.protocol.codec.TerminalRegisterRespMessageCodec;
import cn.linliangjun.its.uniprotocol.Message;
import cn.linliangjun.its.uniprotocol.Protocol;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.StringJoiner;

/**
 * JT/T 808 消息——终端注册应答
 *
 * @author linliangjun
 */
@Getter
@Setter
@Protocol(name = "JT/T808", version = {"V2011", "V2013", "V2019"})
@Message(id = 0x8100, name = "终端注册应答", codecClass = TerminalRegisterRespMessageCodec.class)
public class TerminalRegisterRespMessage extends Jt808Message {

    /**
     *（终端）消息流水号
     */
    private Integer respSerialNum;

    /**
     * 结果
     */
    private Result result;

    /**
     * 鉴权码
     */
    private String authKey;

    public TerminalRegisterRespMessage(Version version) {
        super(Type.TERMINAL_REGISTER_RESP, version);
    }

    @Override
    protected String getDescription() {
        return new StringJoiner(", ", "{", "}")
                .add("respSerialNum=" + respSerialNum)
                .add("result=" + result)
                .add("authKey='" + authKey + "'")
                .toString();
    }

    @Getter
    @RequiredArgsConstructor
    public enum Result {

        SUCCESS(0),
        CAR_ALREADY_REGISTERED(1),
        CAR_NOT_FOUND(2),
        TERMINAL_ALREADY_REGISTERED(3),
        TERMINAL_NOT_FOUND(4);

        private final int value;

        public static Result getInstance(int value) {
            for (Result result : Result.values()) {
                if (result.value == value) {
                    return result;
                }
            }
            return null;
        }
    }
}
