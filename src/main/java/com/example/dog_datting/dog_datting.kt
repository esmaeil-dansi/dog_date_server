package com.example.dog_datting


import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DogDatting


fun main(args: Array<String>) {
    val logger: Logger = LogManager.getLogger(DogDatting::class.java)
    logger.info("run server ......")
    logger.error("ho have error")
    runApplication<DogDatting>(*args)
}