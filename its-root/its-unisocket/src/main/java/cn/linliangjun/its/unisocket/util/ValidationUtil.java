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

package cn.linliangjun.its.unisocket.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 校验工具类
 *
 * @author linliangjun
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValidationUtil {

    @Setter
    private static Validator validator;

    public static List<String> validate(Object obj, Class<?>... groups) {
        if (validator == null) {
            log.warn("未配置校验器，跳过校验 {}", obj);
            return Collections.emptyList();
        }
        return validator.validate(obj, groups).stream()
                .map(ValidationUtil::getMessage)
                .collect(Collectors.toList());
    }

    private static String getMessage(ConstraintViolation<Object> violation) {
        return violation.getRootBeanClass().getCanonicalName() + "#"
                + violation.getPropertyPath() + ": "
                + violation.getMessage();
    }
}
