package com.example.dog_datting.models

import com.example.dog_datting.db.AdminRequestType
import com.example.dog_datting.db.Doctor

data class UserId(
    val id: String = "", val username: String = "", val name: String = "", val isAdmin: Boolean = false
)

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
    var commentsCount: Int = 0
)

data class PlaceRes(
    var id: Long = 0,
    var name: String = "",
    var owner: String = "",
    var description: String = "",
    var fileUuids: List<String> = ArrayList(),
    var location: Location = Location(),
    var locationInfo: Location? = null,
    var type: String = "",
)

data class ShopRes(
    val id: Long = 0,
    var shopId: String = "",
    var ownerId: String = "",
    var description: String = "",
    var submitted: Boolean = false,
    var name: String = "",
    var avatar: String = "",
    var link: String = "",
    var itemPath: List<String> = ArrayList(),

    )

data class ChatResult(
    var userId: String,
    var message: String,
    var name: String,
    var lastTime: Long,
    var lastMessageId: Int
)


data class AdminRequestsRes(
    var id: Long = 0,
    var time: Long = 0,
    var requester: String = "",
    var place: PlaceRes? = null,
    var shop: ShopRes? = null,
    var doctor: Doctor? = null,
    var type: AdminRequestType = AdminRequestType.PLACE
)

class SavePostRes(
    val time: Long,
    var id: Int
)

data class ShopItemRes(
    val id: Long,
    val price: Double,
    val name: String,
    val details: String,
    var fileUuids: List<String> = ArrayList()
)


data class HeathRes(
    val id: Long = 0,
    var animalUid: String = "",
    var body: String = "",
    var fileUuids: List<String> = ArrayList()
)

data class UserRes(
    val uuid: String = "",
    val username: String = "",
    val firstname: String = "",
    val mate: Boolean = false,
    val walk: Boolean = false,
    var playingPartner: Boolean = false,
    var info: String = "",
    var certified: String = "",
    var casually: String = "",
    var interests: String = "",
)