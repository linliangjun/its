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

import cn.linliangjun.its.jt808.protocol.message.Type;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCounted;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ByteBufWrapper implements ReferenceCounted {

    @Getter
    private final ByteBuf buf;

    private final Map<String, Object> attr = new HashMap<>();

    public void discard(String message) {
        attr.put("discardCause", message);
    }

    public boolean isDiscard() {
        return attr.containsKey("discardCause");
    }

    public String getDiscardCause() {
        return attr.get("discardCause").toString();
    }

    public Type getType() {
        return ((Type) attr.get("type"));
    }

    public void setType(Type type) {
        attr.put("type", type);
    }

    @Override
    public int refCnt() {
        return buf.refCnt();
    }

    @Override
    public ReferenceCounted retain() {
        return buf.retain();
    }

    @Override
    public ReferenceCounted retain(int increment) {
        return buf.retain(increment);
    }

    @Override
    public ReferenceCounted touch() {
        buf.touch();
        return this;
    }

    @Override
    public ReferenceCounted touch(Object hint) {
        buf.touch(hint);
        return this;
    }

    @Override
    public boolean release() {
        return buf.release();
    }

    @Override
    public boolean release(int decrement) {
        return buf.release(decrement);
    }
}
