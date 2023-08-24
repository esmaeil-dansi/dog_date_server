package com.example.dog_datting.services

import io.minio.GetObjectArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import org.apache.commons.io.IOUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.stereotype.Service
import java.io.*


@Service
class MinioService {
    val logger: Logger = LogManager.getLogger(MinioService::class.java)

    var minioClient: MinioClient = MinioClient.builder()
        .endpoint("http://127.0.0.1:9000")
        .credentials("dogapp", "96112049")
        .build()


    fun saveFile(fileBytes: ByteArray, fileName: String, bucket: String? = "dogapp") {
        try {
            val stream = ByteArrayInputStream(fileBytes)
            minioClient.putObject(
                PutObjectArgs.builder().bucket(bucket)
                    .`object`(fileName).stream(
                        stream, fileBytes.size.toLong(), -1
                    ).build()
            )
        } catch (ex: Exception) {
            logger.error(ex.message)
        }


    }

    fun getFile(fileName: String,bucket: String? = "dogapp"): File? {
        return try {
            val inputStream: InputStream = minioClient.getObject(
                GetObjectArgs.builder().bucket(bucket).`object`(fileName).build()
            )
            val result = File.createTempFile("temp", "")
            val outputStream: OutputStream = FileOutputStream(result)
            IOUtils.copy(inputStream, outputStream)
            return result;
        } catch (ex: Exception) {
            null
        }
    }
}