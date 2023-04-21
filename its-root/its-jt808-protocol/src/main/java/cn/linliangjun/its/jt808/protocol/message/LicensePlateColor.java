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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.var;

/**
 * 车牌颜色枚举
 *
 * @author linliangjun
 */
@RequiredArgsConstructor
public enum LicensePlateColor {

    BLUE(1),
    YELLOW(2),
    BLACK(3),
    WHITE(4),
    GREEN(5),
    OTHER(9);

    @Getter
    private final int value;

    public static LicensePlateColor getInstance(int value) {
        for (var plateColor : LicensePlateColor.values()) {
            if (plateColor.value == value) {
                return plateColor;
            }
        }
        return null;
    }
}
