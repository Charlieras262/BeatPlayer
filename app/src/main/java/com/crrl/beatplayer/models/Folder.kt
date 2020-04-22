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

package com.crrl.beatplayer.models

import android.database.Cursor
import com.crrl.beatplayer.extensions.fixedName
import com.crrl.beatplayer.extensions.fixedPath
import com.crrl.beatplayer.utils.PlayerConstants.FOLDER_TYPE
import com.google.gson.Gson
import java.io.File

class Folder(
    val id: Long,
    val name: String,
    val albumId: Long,
    val path: String,
    val songIds: MutableList<Long> = mutableListOf()
) : MediaItem(id) {
    companion object {
        fun createFromCursor(cursor: Cursor): Folder {
            return Folder(
                cursor.getLong(0),
                File(File(cursor.getString(2)).parent!!).fixedName(),
                cursor.getLong(1),
                File(File(cursor.getString(2)).parent!!).fixedPath()
            )
        }
    }

    override fun compare(other: MediaItem): Boolean {
        other as Folder
        return id == other.id && name == other.name && albumId == other.albumId && path == other.path && songIds.size == other.songIds.size
    }

    override fun toString(): String {
        return Gson().toJson(this)
    }

    fun toFavorite(): Favorite {
        return Favorite(id, name, path, albumId, 0, songIds.size, FOLDER_TYPE)
    }
}