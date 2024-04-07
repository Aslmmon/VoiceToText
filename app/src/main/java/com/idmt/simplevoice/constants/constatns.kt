package com.idmt.simplevoice.constants

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey


val userType = stringPreferencesKey("example_counter")

enum class userTypeEnum { Normal, SuperUser }
