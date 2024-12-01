package com.pardeep.realtimedatabaseassignment2

import com.google.firebase.database.Exclude

data class DataClass(
    var id: String? = "", var time: String? = ""
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "time" to time
        )
    }


}