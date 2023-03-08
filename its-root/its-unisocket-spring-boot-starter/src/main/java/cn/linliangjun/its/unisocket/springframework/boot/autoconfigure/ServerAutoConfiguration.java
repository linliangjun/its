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

import cn.hutool.core.util.StrUtil;
import cn.linliangjun.its.unisocket.server.Server;
import cn.linliangjun.its.unisocket.springframework.boot.component.ServerPropertiesInjectorFactory;
import cn.linliangjun.its.unisocket.util.ValidationUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.Resource;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(ServerProperties.class)
@ConditionalOnProperty(name = "its.unisocket.server.impl-class")
@ComponentScan(basePackages = "cn.linliangjun.its.unisocket.springframework.boot.component")
public class ServerAutoConfiguration {

    @Resource
    private ServerProperties properties;

    @Resource
    private ServerPropertiesInjectorFactory injectorFactory;

    public ServerAutoConfiguration(Validator validator) {
        ValidationUtil.setValidator(validator);
    }

    @SneakyThrows
    @Bean(destroyMethod = "close")
    public Server server() {
        var implClass = properties.getImplClass();
        Server server = implClass.getConstructor().newInstance();
        Map<String, Object> map = properties.toMap();
        var injector = injectorFactory.getInjector(implClass);
        if (injector == null) {
            throw new UnsupportedOperationException("尚未适配当前服务器的实现：" + implClass);
        }else {
            injector.inject(map);
        }
        map = toCameCase(map);
        server.init(map);
        return server;
    }

    private Map<String, Object> toCameCase(Map<String, Object> map) {
        var m = new HashMap<String, Object>();
        map.forEach((k,v) -> m.put(StrUtil.toCamelCase(k, '-'), v));
        return m;
    }
}
