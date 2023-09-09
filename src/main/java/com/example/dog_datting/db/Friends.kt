package com.example.dog_datting.db

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
data class Friends(
    @GeneratedValue
    @Id
    var id: Long = 0,
    @ManyToOne
    var owner: User = User(),
    @ManyToOne
    var user: User = User()


)