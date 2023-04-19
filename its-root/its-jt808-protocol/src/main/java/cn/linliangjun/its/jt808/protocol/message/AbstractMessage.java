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

import cn.hutool.core.util.ArrayUtil;
import cn.linliangjun.its.uniprotocol.IncompatibleVersionException;
import lombok.Getter;
import lombok.Setter;

import java.util.StringJoiner;

/**
 * 抽象 JT 808 消息
 */
@Getter
@Setter
public abstract class AbstractMessage {

    /**
     * 消息类型 ID
     */
    private final Integer typeId;

    /**
     * 协议版本
     */
    private final Version version;

    /**
     * 消息体长度
     */
    private Integer bodyLength;

    /**
     * 加密方式
     */
    private Encryption encryption;

    /**
     * 分包标识
     */
    private boolean partial;

    /**
     * 终端手机号
     */
    private String terminalPhoneNum;

    /**
     * 消息流水号
     */
    private Integer serialNum;

    /**
     * 消息包总数，当 {@linkplain #partial 分包标识} 为 {@code false} 时，该字段为 {@code null}
     */
    private Integer packageTotal;

    /**
     * 消息包序号，当 {@linkplain #partial 分包标识} 为 {@code false} 时，该字段为 {@code null}
     */
    private Integer packageNum;

    /**
     * 以消息类型 ID 和协议版本构造消息对象
     *
     * @param typeId  消息类型 ID
     * @param version 协议版本
     */
    public AbstractMessage(Integer typeId, Version version) {
        this.typeId = typeId;
        String[] versions = getCompatibleVersions().split(", ");
        String versionName = version.name();
        if (!ArrayUtil.contains(versions, versionName)) {
            throw new IncompatibleVersionException(getClass().getCanonicalName() + ": " + versionName);
        }
        this.version = version;
    }

    /**
     * 获取兼容的版本，多个版本之间以 ", " 分开
     */
    protected abstract String getCompatibleVersions();

    /**
     * 获取（消息体）描述
     *
     * @see #toString()
     */
    protected abstract String getDescription();

    @Override
    public final String toString() {
        String header = new StringJoiner(", ", "{", "}")
                .add(String.format("typeId=%0#6x", typeId))
                .add("version=" + version)
                .add("bodyLength=" + bodyLength)
                .add("encryption=" + encryption)
                .add("terminalPhoneNum=\"" + terminalPhoneNum + '"')
                .add("serialNum=" + serialNum)
                .add("partial=" + partial + (partial ? ", packageTotal=" + packageTotal + ", packageNum=" + packageNum : ""))
                .toString();
        return new StringJoiner(", ", getClass().getSimpleName() + "{", "}")
                .add("header=" + header)
                .add("body=" + getDescription())
                .toString();
    }

    /**
     * JT/T 808 消息加密方式
     *
     * @author linliangjun
     */
    public enum Encryption {

        RSA, NONE, RESERVE
    }
}
