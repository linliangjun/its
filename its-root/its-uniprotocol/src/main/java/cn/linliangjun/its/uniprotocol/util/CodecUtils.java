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

import cn.linliangjun.its.uniprotocol.CodecException;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.var;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CodecUtils {

    public static void writeFixedLenBytes(ByteBuf buf, byte[] bytes, int len, String fieldName) {
        if (len != bytes.length) {
            throw new CodecException(String.format("%s 的字节长度与预期不符合（%d != %d）", fieldName, bytes.length, len));
        }
        buf.writeBytes(bytes);
    }

    public static void writeMaxLenBytes(ByteBuf buf, byte[] bytes, int maxLen, String fieldName) {
        if (maxLen < bytes.length) {
            throw new CodecException(String.format("%s 超出最大长度限制（%d > %d）", fieldName, bytes.length, maxLen));
        }
        buf.writeBytes(bytes);
    }

    public static void writeMaxLenBytesFillTail(ByteBuf buf, byte[] bytes, int maxLen, int pad, String fieldName) {
        if (maxLen < bytes.length) {
            throw new CodecException(String.format("%s 超出最大长度限制（%d > %d）", fieldName, bytes.length, maxLen));
        }
        buf.writeBytes(bytes);
        for (int i = 0; i < maxLen - bytes.length; i++) {
            buf.writeByte(pad);
        }
    }

    public static void writeMaxLenBytesFillHead(ByteBuf buf, byte[] bytes, int maxLen, int pad, String fieldName) {
        if (maxLen < bytes.length) {
            throw new CodecException(String.format("%s 超出最大长度限制（%d > %d）", fieldName, bytes.length, maxLen));
        }
        for (int i = 0; i < maxLen - bytes.length; i++) {
            buf.writeByte(pad);
        }
        buf.writeBytes(bytes);
    }

    /**
     * 十进制字符串转 BCD 8421 码
     */
    public static byte[] decimalToBcd8421(String decimal) {
        int len = decimal.length();
        if (len % 2 != 0) {
            decimal = '0' + decimal;
        }
        for (var c : decimal.toCharArray()) {
            if (c < '0' || c > '9') {
                throw new NumberFormatException("非十进制字符串：" + decimal);
            }
        }
        var arr = StrUtils.splitWithFixedLength(decimal, 2);
        var bytes = new byte[arr.length];
        for (int i = 0; i < arr.length; i++) {
            bytes[i] = (byte) Integer.parseInt(arr[i], 16);
        }
        return bytes;
    }

    /**
     * BCD 码转十进制字符串
     */
    public static String bcd8421ToDecimal(byte[] bytes) {
        int len = bytes.length;
        var sb = new StringBuilder(len * 2);
        for (byte b : bytes) {
            int n = b & 0xff;
            String hexStr = Integer.toHexString(n);
            hexStr = StrUtils.zerofill(hexStr, 2);
            for (int i = 0; i < 2; i++) {
                char c = hexStr.charAt(i);
                if (!Character.isDigit(c)) {
                    throw new NumberFormatException(String.format("非 BCD 码数据：%#x", Integer.parseInt(c + "", 16)));
                }
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
