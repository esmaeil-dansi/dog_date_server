package com.example.dog_datting.db

import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "files_info")
class FileInfo(
    @Id
    var uuid: String = "",
    var packetId: String = "",
    var name: String = ""
)