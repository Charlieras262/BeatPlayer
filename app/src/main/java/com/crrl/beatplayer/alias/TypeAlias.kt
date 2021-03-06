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

package com.crrl.beatplayer.alias

import com.crrl.beatplayer.playback.AudioFocusHelper
import com.crrl.beatplayer.playback.players.BeatPlayer

typealias OnPrepared<T> = T.() -> Unit
typealias OnError<T> = T.(error: Throwable) -> Unit
typealias OnCompletion<T> = T.() -> Unit
typealias OnMetaDataChanged = BeatPlayer.() -> Unit
typealias OnIsPlaying = BeatPlayer.(playing: Boolean, byUi: Boolean) -> Unit
typealias LiveDataFilter<T> = (T) -> Boolean
typealias OnAudioFocusGain = AudioFocusHelper.() -> Unit
typealias OnAudioFocusLoss = AudioFocusHelper.() -> Unit
typealias OnAudioFocusLossTransient = AudioFocusHelper.() -> Unit
typealias OnAudioFocusLossTransientCanDuck = AudioFocusHelper.() -> Unit
