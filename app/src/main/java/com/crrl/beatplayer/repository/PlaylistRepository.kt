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

package com.crrl.beatplayer.repository

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.crrl.beatplayer.db.DBHelper
import com.crrl.beatplayer.extensions.toList
import com.crrl.beatplayer.models.Playlist
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.utils.BeatConstants

interface PlaylistRepository {
    @Throws(IllegalStateException::class)
    fun createPlaylist(name: String, songs: List<Song>): Long
    fun addToPlaylist(id: Long, songs: List<Song>): Long
    fun getPlayLists(): List<Playlist>
    fun getPlaylist(id: Long): Playlist
    fun existPlaylist(name: String): Boolean
    fun getPlayListsCount(): Int
    fun getSongsInPlaylist(playlistId: Long): List<Song>
    fun removeFromPlaylist(playlistId: Long, id: Long)
    fun deletePlaylist(playlistId: Long): Int
}

class PlaylistRepositoryImplementation(context: Context) : DBHelper(context),
    PlaylistRepository {

    companion object {
        const val TABLE_PLAYLIST = "playlist"
        const val TABLE_SONGS = "playlist_songs"

        const val COLUMN_ID = "id"

        const val COLUMN_NAME = "name"
        const val COLUMN_SONG_COUNT = "song_count"

        const val COLUMN_TITLE = "title"
        const val COLUMN_ARTIST = "artist"
        const val COLUMN_ALBUM = "album"
        const val COLUMN_DURATION = "duration"
        const val COLUMN_TRACK = "track_num"
        const val COLUMN_ARTIST_ID = "artist_id"
        const val COLUMN_ALBUM_ID = "album_id"
        const val COLUMN_PLAYLIST = "playlist"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(getCreateFavoritesQuery())
        db?.execSQL(getCreateSongsQuery())
        db?.execSQL(getIndexQuery())
        db?.execSQL(getPlusTriggerQuery())
        db?.execSQL(getMinusTriggerQuery())
        db?.execSQL(getDeleteSongs())
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PLAYLIST")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SONGS")
        onCreate(db)
    }

    override fun createPlaylist(name: String, songs: List<Song>): Long {
        val playlist = Playlist(getPlayListsCount() + 1L, name, 0)
        insertRow(TABLE_PLAYLIST, playlist.columns(), playlist.values()).toLong()
        addToPlaylist(playlist.id, songs)
        return playlist.id
    }

    override fun addToPlaylist(id: Long, songs: List<Song>): Long {
        val array = songs.map {
            it.playListId = id
            val contentValues = ContentValues()
            val values = it.values()
            it.columns(BeatConstants.PLAY_LIST_TYPE).mapIndexed { i, column ->
                contentValues.put(column, values[i])
            }
            contentValues
        }
        return bulkInsert(TABLE_SONGS, array.toTypedArray()).toLong()
    }

    override fun getPlayLists(): List<Playlist> {
        return getRow(TABLE_PLAYLIST, "*").toList(true) {
            Playlist.fromCursor(this)
        }
    }

    override fun getPlaylist(id: Long): Playlist {
        getRow(TABLE_PLAYLIST, "*", "$COLUMN_ID = ?", arrayOf("$id")).use {
            return if (it.moveToFirst()) {
                Playlist.fromCursor(it)
            } else {
                Playlist()
            }
        }
    }

    override fun existPlaylist(name: String): Boolean {
        getRow(TABLE_PLAYLIST, "*", "$COLUMN_NAME = ?", arrayOf(name)).use {
            return it.moveToFirst()
        }
    }

    override fun getPlayListsCount(): Int {
        return getRow(TABLE_PLAYLIST, "*").count
    }

    override fun getSongsInPlaylist(playlistId: Long): List<Song> {
        val cursor = getRow(TABLE_SONGS, "*", "$COLUMN_PLAYLIST = ?", arrayOf("$playlistId"))
        return cursor.toList(true) {
            Song.createFromPlaylistCursor(this)
        }
    }

    fun getSong(): List<Song> {
        val cursor = getRow(TABLE_SONGS, "*")
        return cursor.toList(true) {
            Song.createFromPlaylistCursor(this)
        }
    }

    override fun removeFromPlaylist(playlistId: Long, id: Long) {
        deleteRow(TABLE_SONGS, "$COLUMN_ID = ?", arrayOf("$id"))
    }

    override fun deletePlaylist(playlistId: Long): Int {
        return deleteRow(TABLE_PLAYLIST, "$COLUMN_ID = ?", arrayOf("$playlistId"))
    }

    private fun getCreateFavoritesQuery(): String {
        return "CREATE TABLE $TABLE_PLAYLIST (" +
                "$COLUMN_ID INTEGER PRIMARY KEY, " +
                "$COLUMN_NAME TEXT, " +
                "$COLUMN_SONG_COUNT INTEGER" +
                ")"
    }

    private fun getCreateSongsQuery(): String {
        return "CREATE TABLE $TABLE_SONGS (" +
                "$COLUMN_ID INTEGER, " +
                "$COLUMN_TITLE TEXT, " +
                "$COLUMN_ARTIST TEXT, " +
                "$COLUMN_ALBUM TEXT, " +
                "$COLUMN_DURATION INTEGER, " +
                "$COLUMN_TRACK INTEGER, " +
                "$COLUMN_ARTIST_ID INTEGER, " +
                "$COLUMN_ALBUM_ID INTEGER, " +
                "$COLUMN_PLAYLIST INTEGER, " +
                "FOREIGN KEY($COLUMN_PLAYLIST) REFERENCES $TABLE_PLAYLIST($COLUMN_ID), " +
                "PRIMARY KEY($COLUMN_ID, $COLUMN_PLAYLIST)" +
                ")"
    }

    private fun getPlusTriggerQuery(): String {
        return "CREATE TRIGGER PLUS_${TABLE_SONGS}_COUNT\n" +
                "AFTER INSERT ON $TABLE_SONGS\n" +
                "BEGIN\n" +
                "    UPDATE $TABLE_PLAYLIST SET $COLUMN_SONG_COUNT = $COLUMN_SONG_COUNT + 1 WHERE $COLUMN_ID = NEW.$COLUMN_PLAYLIST;\n" +
                "END"
    }

    private fun getMinusTriggerQuery(): String {
        return "CREATE TRIGGER MINUS_${TABLE_SONGS}_COUNT\n" +
                "AFTER DELETE ON $TABLE_SONGS\n" +
                "BEGIN\n" +
                "    UPDATE $TABLE_PLAYLIST SET $COLUMN_SONG_COUNT = $COLUMN_SONG_COUNT - 1 WHERE $COLUMN_ID = OLD.$COLUMN_PLAYLIST;\n" +
                "END"
    }

    private fun getDeleteSongs(): String {
        return "CREATE TRIGGER DELETE_$TABLE_SONGS\n" +
                "AFTER DELETE ON $TABLE_PLAYLIST\n" +
                "BEGIN\n" +
                "    DELETE FROM $TABLE_SONGS WHERE $COLUMN_PLAYLIST = OLD.$COLUMN_ID;\n" +
                "END"
    }

    private fun getIndexQuery(): String {
        return "CREATE INDEX idx_$TABLE_SONGS ON $TABLE_SONGS($COLUMN_ID, $COLUMN_PLAYLIST)"
    }
}