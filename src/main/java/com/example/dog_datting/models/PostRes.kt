package com.example.dog_datting.models

data class PostRes(
    var id: Long = 0,
    var ownerId: String = "",
    var title: String = "",
    var time: Long = 0,
    var description: String = "",
    var type: PostType = PostType.BAY,
    var fileUuids: List<String> = ArrayList(),
    var location: Location = Location(),
    var locationInfo: Location? = null,
    var likes: Int = 0,
    var myFavorite: Boolean = false,
    var topics: List<String> = ArrayList(),
)

data class PlaceRes(
    var id: Long = 0,
    var name: String = "",
    var owner: String = "",
    var description: String = "",
    var fileUuids: List<String> = ArrayList(),
    var location: Location = Location(),
    var locationInfo: Location? = null,
    var type: PlaceType = PlaceType.ALL,
)

data class ShopRes(
    var id: Long = 0,
    var title: String = "",
    var ownerId: String = "",
    var description: String = "",
    var avatar: String = "",
    var shopOId: String = ""
)