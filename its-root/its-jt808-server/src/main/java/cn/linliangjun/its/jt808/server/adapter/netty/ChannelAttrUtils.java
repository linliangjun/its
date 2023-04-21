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

package cn.linliangjun.its.jt808.server.adapter.netty;

import cn.linliangjun.its.jt808.protocol.Version;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public final class ChannelAttrUtils {

    private static final AttributeKey<Version> VERSION = AttributeKey.newInstance("version");

    private static final AttributeKey<Boolean> LOGIN = AttributeKey.newInstance("login");

    public static Version getVersion(ChannelHandlerContext ctx) {
        return ctx.channel().attr(VERSION).get();
    }

    public static void setVersion(ChannelHandlerContext ctx, Version version) {
        ctx.channel().attr(VERSION).set(version);
    }

    public static boolean login(ChannelHandlerContext ctx) {
        Boolean login = ctx.channel().attr(LOGIN).get();
        return login != null && login;
    }

    public static void setLogin(ChannelHandlerContext ctx) {
        ctx.channel().attr(LOGIN).set(true);
    }
}
