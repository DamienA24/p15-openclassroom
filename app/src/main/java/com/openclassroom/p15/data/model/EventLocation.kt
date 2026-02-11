package com.openclassroom.p15.data.model

import com.google.firebase.firestore.PropertyName

/**
 * Data class representing the location of an event
 */
data class EventLocation(
    @PropertyName("address")
    val address: String = "",

    @PropertyName("latitude")
    val latitude: Double = 0.0,

    @PropertyName("longitude")
    val longitude: Double = 0.0
) {

    constructor() : this(
        address = "",
        latitude = 0.0,
        longitude = 0.0
    )
}
