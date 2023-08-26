package com.example.dog_datting.db

import com.example.dog_datting.models.PostType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

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
    var location: Location = Location()


)