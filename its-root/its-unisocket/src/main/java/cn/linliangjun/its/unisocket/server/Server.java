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

import java.util.Map;

/**
 * Socket 服务器
 *
 * @author linliangjun
 */
public interface Server {

    /**
     * 初始化服务器
     *
     * @param properties 服务器属性
     */
    void init(Map<String, Object> properties) throws Exception;

    /**
     * 启动服务器
     */
    void launch() throws Exception;

    /**
     * 关闭服务器
     */
    void close() throws Exception;

    /**
     * 获取服务器状态
     */
    Status getStatus();

    /**
     * 服务器状态枚举
     *
     * @author linliangjun
     */
    enum Status {

        /**
         * 新建
         */
        NEW,

        /**
         * 就绪
         */
        RUNNABLE,

        /**
         * 运行中
         */
        RUNNING,

        /**
         * 已关闭
         */
        CLOSED,

        /**
         * 异常
         */
        ERROR
    }
}
