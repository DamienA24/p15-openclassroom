package com.openclassroom.p15.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

/**
 * Data class representing a User in Firestore
 */
data class User(
    @DocumentId
    val uid: String = "",

    @PropertyName("email")
    val email: String = "",

    @PropertyName("firstName")
    val firstName: String = "",

    @PropertyName("lastName")
    val lastName: String = "",

    @PropertyName("avatarUrl")
    val avatarUrl: String = "",

    @PropertyName("notificationsEnabled")
    val notificationsEnabled: Boolean = true,

    @PropertyName("createdAt")
    val createdAt: Timestamp = Timestamp.now(),

    @PropertyName("updatedAt")
    val updatedAt: Timestamp = Timestamp.now()
) {
    val fullName: String
        get() = "$firstName $lastName"

    constructor() : this(
        uid = "",
        email = "",
        firstName = "",
        lastName = "",
        avatarUrl = "",
        notificationsEnabled = true,
        createdAt = Timestamp.now(),
        updatedAt = Timestamp.now()
    )
}
