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

package cn.linliangjun.its.uniprotocol.util;

import cn.hutool.core.util.StrUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StrUtils {

    public static String zerofill(String str, int len) {
        int gap = len - str.length();
        if (gap > 0) {
            str = StrUtil.repeat('0', gap) + str;
        }
        return str;
    }

    public static String[] splitWithFixedLength(String str, int len) {
        String regex = String.format("(.{%d})", len);
        return str.replaceAll(regex, "$1,").split(",");
    }
}
