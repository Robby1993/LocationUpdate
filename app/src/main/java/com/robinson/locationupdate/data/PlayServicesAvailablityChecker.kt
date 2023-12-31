package com.robinson.locationupdate.data

/*
 * Copyright (C) 2021 Google Inc.
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


import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlayServicesAvailabilityChecker @Inject constructor(
    @ApplicationContext private val context: Context,
    private val googleApiAvailability: GoogleApiAvailability
) {
    suspend fun isGooglePlayServicesAvailable(): Boolean = withContext(Dispatchers.Default) {
        when (googleApiAvailability.isGooglePlayServicesAvailable(context)) {
            ConnectionResult.SUCCESS -> true
            else -> false
        }
    }
}
