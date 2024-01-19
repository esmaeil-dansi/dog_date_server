package com.example.dog_datting.services

import com.example.dog_datting.db.Shop
import com.example.dog_datting.models.ShopRes
import com.example.dog_datting.repo.FileInfoRepo
import org.springframework.stereotype.Service
import javax.annotation.Nullable


@Service
class ShopService(private val fileInfoRepo: FileInfoRepo) {


    fun shopMapper(shop: Shop?): ShopRes? {
        if (shop == null) {
            return null
        }

        val shopRes = ShopRes(
            name = shop.name,
            id = shop.id,
            description = shop.description,
            link = shop.link,
            avatar = shop.avatar,
            shopId = shop.shopId,
            ownerId = shop.ownerId
        )
        val info = fileInfoRepo.getByPacketId(shop.itemsUuid)
        if (info != null) {
            shopRes.itemPath = info.map { f -> f.uuid }
        }
        return shopRes

    }
}