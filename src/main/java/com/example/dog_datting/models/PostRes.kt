package com.example.dog_datting.models

data class PostRes(
    var id: Long = 0,
    var ownerId: String = "",
    var title: String = "",
    var time: Long = 0,
    var description: String = "",
    var type: PostType = PostType.BAY,
    var fileUuids: List<String> = ArrayList(),
    var location: Location = Location()
)