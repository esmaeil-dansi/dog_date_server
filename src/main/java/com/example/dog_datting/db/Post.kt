package com.example.dog_datting.db

import com.example.dog_datting.models.PostType
import com.example.dog_datting.models.UserId
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
class Post(
    @Id
    @GeneratedValue
    var id: Long = 0,
    var ownerId: String = "",
    var title: String = "",
    var description: String = "",
    var type: PostType = PostType.BAY,
    var fileUuid: String = "",
    var time: Long = 0,
    @ManyToOne
    var location: Location = Location(),

    @ManyToOne
    var locationInfo: Location? = null,
    var likesCount: Int = 0,
    var commentsCount: Int = 0,

    var topics: String = ""


)