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

package cn.linliangjun.its.jt808.server.endpoint;

import cn.linliangjun.its.jt808.protocol.Version;
import cn.linliangjun.its.jt808.protocol.message.PlatformGenericRespMessage;
import cn.linliangjun.its.jt808.protocol.message.TerminalAuthMessage;
import cn.linliangjun.its.jt808.protocol.message.TerminalRegisterMessage;
import cn.linliangjun.its.jt808.protocol.message.TerminalRegisterRespMessage;
import cn.linliangjun.its.jt808.server.adapter.CtxUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Header;

import static cn.linliangjun.its.jt808.protocol.message.PlatformGenericRespMessage.*;

/**
 * 终端管理端点
 *
 * @author linliangjun
 */
@Slf4j
@MessageEndpoint
public class TerminalManagerEndpoint {

    @ServiceActivator(inputChannel = "messageRequestChannel_0x0100", outputChannel = "messageResponseChannel")
    public TerminalRegisterRespMessage register(TerminalRegisterMessage message) {
        log.info("接收到终端注册消息：{}", message);

        // 直接发送注册应答
        var resp = new TerminalRegisterRespMessage(message.getVersion());
        resp.setEncryption(message.getEncryption());
        resp.setTerminalPhoneNum(message.getTerminalPhoneNum());
        resp.setPartial(false);
        resp.setSerialNum(1);
        resp.setRespSerialNum(message.getSerialNum());
        resp.setResult(TerminalRegisterRespMessage.Result.SUCCESS);
        resp.setAuthKey("HX");
        return resp;
    }

    @ServiceActivator(inputChannel = "messageRequestChannel_0x0102", outputChannel = "messageResponseChannel")
    public PlatformGenericRespMessage login(@Header Object ctx, TerminalAuthMessage message) {
        log.info("接收到终端鉴权消息：{}", message);

        CtxUtils.setLogin(ctx);

        // 直接发送平台通用应答
        PlatformGenericRespMessage resp;
        Version version = message.getVersion();
        if (version == Version.V2011) {
            var msg = new PlatformGenericRespMessage_2011();
            msg.setResult(PlatformGenericRespMessage_2011.Result.SUCCESS_ACK);
            resp = msg;
        }else {
            var msg = new PlatformGenericRespMessage_2013_2019(version);
            msg.setResult(PlatformGenericRespMessage_2013_2019.Result.SUCCESS_ACK);
            resp = msg;
        }
        resp.setEncryption(message.getEncryption());
        resp.setTerminalPhoneNum(message.getTerminalPhoneNum());
        resp.setPartial(false);
        resp.setSerialNum(1);
        resp.setRespSerialNum(message.getSerialNum());
        resp.setRespType(message.getType());
        return resp;
    }
}
