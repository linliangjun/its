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

package cn.linliangjun.its.unisocket.springframework.boot.autoconfigure;

import cn.linliangjun.its.unisocket.IoModel;
import cn.linliangjun.its.unisocket.server.Server;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Validated
@ConfigurationProperties("its.unisocket.server")
public class ServerProperties {

    /**
     * Socket 服务器实现类
     */
    @NotNull
    private Class<? extends Server> implClass;

    /**
     * IO 模型，默认为 {@linkplain IoModel#NIO}
     */
    @NotNull
    private IoModel ioModel = IoModel.NIO;

    /**
     * Socket 服务器主机名，默认为 localhost
     */
    @NotBlank
    private String host = "localhost";

    /**
     * Socket 服务器端口，默认为 9091
     */
    @NotNull
    @Range(max = 65535)
    private Integer port = 9091;

    /**
     * Socket 服务器实现类具体属性
     */
    private Map<String, Object> implProperties;

    public Map<String, Object> toMap() {
        Map<String, Object> map = implProperties == null ? new HashMap<>() : new HashMap<>(implProperties);
        map.put("ioModel", ioModel);
        map.put("host", host);
        map.put("port", port);
        return map;
    }
}
