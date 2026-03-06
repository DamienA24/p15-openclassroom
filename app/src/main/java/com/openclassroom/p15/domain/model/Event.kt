package com.openclassroom.p15.domain.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Event(
    @DocumentId
    val id: String = "",

    @PropertyName("title")
    val title: String = "",

    @PropertyName("description")
    val description: String = "",

    @PropertyName("date")
    val date: Timestamp = Timestamp.now(),

    @PropertyName("location")
    val location: EventLocation = EventLocation(),

    @PropertyName("imageUrl")
    val imageUrl: String = "",

    @PropertyName("creatorId")
    val creatorId: String = "",

    @PropertyName("creatorName")
    val creatorName: String = "",

    @PropertyName("createdAt")
    val createdAt: Timestamp = Timestamp.now(),

    @PropertyName("updatedAt")
    val updatedAt: Timestamp = Timestamp.now()
) {
    constructor() : this(
        id = "",
        title = "",
        description = "",
        date = Timestamp.now(),
        location = EventLocation(),
        imageUrl = "",
        creatorId = "",
        creatorName = "",
        createdAt = Timestamp.now(),
        updatedAt = Timestamp.now()
    )
}
