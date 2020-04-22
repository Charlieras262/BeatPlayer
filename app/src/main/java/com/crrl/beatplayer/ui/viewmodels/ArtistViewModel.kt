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

package com.crrl.beatplayer.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crrl.beatplayer.models.Artist
import com.crrl.beatplayer.repository.ArtistsRepository

class ArtistViewModel(private val repository: ArtistsRepository) : ViewModel() {

    private val artists: MutableLiveData<List<Artist>>? = MutableLiveData()

    init {
        update()
    }

    fun update() {
        Thread {
            artists!!.postValue(repository.getAllArtist())
        }.start()
    }

    fun getArtists(): LiveData<List<Artist>>? {
        return artists
    }
}
