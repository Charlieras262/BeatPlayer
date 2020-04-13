/*
 * Copyright (c) 2020. Carlos René Ramos López. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.crrl.beatplayer.extensions

import com.crrl.beatplayer.models.MediaItem
import com.crrl.beatplayer.utils.GeneralUtils

fun Int.format(): String {
    return GeneralUtils.formatMilliseconds(this.toLong())
}

fun Int.fix(): Int {
    var value = this
    while (value >= 1000) {
        value -= 1000
    }
    return value
}

fun List<MediaItem>?.toIDList(): LongArray {
    return this?.map { it._id }?.toLongArray() ?: LongArray(0)
}