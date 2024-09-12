/*
 * Copyright 2023 The Duckee Project
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
package xyz.duckee.android

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import dagger.hilt.android.HiltAndroidApp
import co.ab180.airbridge.Airbridge
import co.ab180.airbridge.AirbridgeOption
import co.ab180.airbridge.AirbridgeOptionBuilder

@HiltAndroidApp
class DuckeeApplication : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()

        // Initialize Airbridge SDK
        val option = AirbridgeOptionBuilder("undefined", "undefined")
            .build()
        Airbridge.initializeSDK(this, option)
    }

    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .crossfade(true)
            .build()
}
