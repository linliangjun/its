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

package cn.linliangjun.its.unisocket.server;

import cn.hutool.core.bean.BeanUtil;
import cn.linliangjun.its.unisocket.IoModel;
import cn.linliangjun.its.unisocket.util.ValidationUtil;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ValidationException;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * 抽象 Socket 服务器，为具体实现类提供公共的逻辑
 *
 * @author linliangjun
 */
@Slf4j
public abstract class AbstractServer implements Server {

    /**
     * 属性
     */
    @Getter(AccessLevel.PROTECTED)
    private Properties properties;

    /**
     * 状态
     */
    @Getter
    private Status status = Status.NEW;

    @Override
    public final synchronized void init(Map<String, Object> map) throws Exception {
        IoModel model;
        String implClassName = getClass().getCanonicalName();
        try {
            log.info("服务器初始化中...");
            // 检查服务器状态
            checkStatus(Status.NEW);
            // 生成属性对象
            this.properties = BeanUtil.toBean(map, getPropertiesClass());
            // 检查属性值
            var messages = ValidationUtil.validate(properties);
            if (!messages.isEmpty()) {
                status = Status.ERROR;
                throw new ValidationException(String.format("属性校验未通过，" + String.join("；", messages)));
            }
            model = properties.getIoModel();
            if (!isSupported(properties.getIoModel())) {
                throw new UnsupportedOperationException("不支持的 IO 模型：" + model);
            }
            // 初始化
            doInit();
        } catch (Exception e) {
            status = Status.ERROR;
            log.error("服务器初始化失败，实现类：{}，原因：{}", implClassName, getCauseMessage(e));
            throw e;
        }
        status = Status.RUNNABLE;
        log.info("服务器已初始化，实现类：{}，IO 模型：{}", implClassName, model);
    }

    @Override
    public final synchronized void launch() throws Exception {
        try {
            log.info("服务器启动中...");
            // 检查服务器状态
            checkStatus(Status.RUNNABLE);
            doLaunch();
            status = Status.RUNNING;
            log.info("服务器已启动，地址：{}:{}", properties.getHost(), properties.getPort());
        } catch (Exception e) {
            status = Status.ERROR;
            log.error("服务器启动失败，原因：{}", getCauseMessage(e));
            throw e;
        }
    }

    @Override
    public final synchronized void close() throws Exception {
        if (status == Status.NEW || status == Status.CLOSED) {
            return;
        }
        try {
            log.info("服务器正在关闭...");
            doClose();
        } catch (Exception e) {
            status = Status.ERROR;
            throw e;
        }
        status = Status.CLOSED;
        log.info("服务器已关闭");
    }

    private void checkStatus(Status expectStatus) {
        if (status != expectStatus) {
            throw new IllegalStateException(
                    String.format("服务器状态错误，预期状态：%s，当前状态：%s", expectStatus, status)
            );
        }
    }

    private String getCauseMessage(Throwable e) {
        String message = e.getMessage();
        while (message == null) {
            e = e.getCause();
            if (e == null) {
                return "未知";
            }
            message = e.getMessage();
        }
        return message;
    }

    protected abstract void doInit() throws Exception;

    protected abstract void doLaunch() throws Exception;

    protected abstract void doClose() throws Exception;

    protected abstract boolean isSupported(IoModel model);

    /**
     * 获取属性类
     */
    protected abstract Class<? extends Properties> getPropertiesClass();

    /**
     * Socket 服务器属性
     *
     * @author linliangjun
     */
    @Getter
    @Setter
    @ToString
    public abstract static class Properties {

        /**
         * IO 模型
         */
        @NotNull
        private IoModel ioModel;

        /**
         * Socket 服务器主机名
         */
        @NotBlank
        private String host;

        /**
         * Socket 服务器端口
         */
        @Min(0)
        @Max(65535)
        @NotNull
        private Integer port;
    }
}
