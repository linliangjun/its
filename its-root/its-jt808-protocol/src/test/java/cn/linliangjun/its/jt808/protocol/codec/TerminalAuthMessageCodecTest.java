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

import cn.linliangjun.its.jt808.protocol.Protocol;
import cn.linliangjun.its.jt808.protocol.message.Version;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import lombok.var;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static cn.linliangjun.its.jt808.protocol.message.TerminalAuthMessage.Encryption;
import static cn.linliangjun.its.jt808.protocol.message.TerminalAuthMessage.TerminalAuthMessage_2019;

class TerminalAuthMessageCodecTest {

    private static final TerminalAuthMessageCodec codec = new TerminalAuthMessageCodec();

    protected static final ByteBufAllocator ALLOCATOR = ByteBufAllocator.DEFAULT;

    @Test
    void encode() {
        var message = new TerminalAuthMessage_2019();
        message.setTerminalPhoneNum("17355012222");
        message.setEncryption(Encryption.NONE);
        message.setSerialNum(55);
        message.setAuthKey("OBD");
        message.setImei("123456789012345");
        message.setSoftwareVersion("");
        var buf = codec.encode(message);
        System.out.println(ByteBufUtil.hexDump(buf));
        buf.release();
    }

    @ParameterizedTest
    @ValueSource(strings = {"0102402701000000000173550122220037034f42443132333435363738393031323334350000000000000000000000000000000000000000"})
    void decode(String hex) {
        var buf = ALLOCATOR.buffer().writeBytes(ByteBufUtil.decodeHexDump(hex));
        var message = codec.decode(new Protocol(Version.V2019), buf);
        System.out.println(message);
        buf.release();
    }
}
