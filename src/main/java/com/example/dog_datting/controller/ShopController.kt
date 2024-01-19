package com.example.dog_datting.controller

import com.example.dog_datting.db.*
import com.example.dog_datting.dto.NewShopDto
import com.example.dog_datting.dto.NewShopItemDto
import com.example.dog_datting.models.ShopItemRes
import com.example.dog_datting.models.ShopRes
import com.example.dog_datting.repo.AdminRequestsRepo
import com.example.dog_datting.repo.FileInfoRepo
import com.example.dog_datting.repo.ShopItemRepo
import com.example.dog_datting.repo.ShopRepo
import com.example.dog_datting.services.ShopService
import org.apache.logging.log4j.LogManager
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
class ShopController(
    private val adminRequestsRepo: AdminRequestsRepo,
    private val shopRepo: ShopRepo,
    private val fileInfoRepo: FileInfoRepo,
    private val shopItemRepo: ShopItemRepo,
    private val shopService: ShopService

) {

    private val logger = LogManager.getLogger(ShopController::class.java)

    @PostMapping(path = ["/createNewShop"])
    @ResponseBody
    fun createNewShop(@RequestBody shopDto: NewShopDto): ResponseEntity<Long?> {
        try {
            val shop = Shop()
            shop.description = shopDto.description
            shop.name = shopDto.title
            shop.ownerId = shopDto.ownerId
            shop.shopId = shopDto.shopId
            shop.avatar = shopDto.avatar
            shop.link = shopDto.link
            shop.itemsUuid = shopDto.itemsUuid
            val s = shopRepo.save(shop)
            adminRequestsRepo.save(
                AdminRequests(
                    time = System.currentTimeMillis(),
                    type = AdminRequestType.SHOP,
                    shop = s,
                    requester = shopDto.ownerId
                )
            )
            return ResponseEntity.ok().body(s.id)
        } catch (e: Exception) {
            logger.error(e.message)
        }
        return ResponseEntity.internalServerError().build()

    }

    @GetMapping(path = ["/fetchShops/{lastId}"])
    fun fetchPost(@PathVariable("lastId") lastId: Int): List<ShopRes>? {
        return shopRepo.findBySubmittedTrueAndIdGreaterThanOrderByIdDesc(lastId.toLong())!!
            .map { s -> shopService.shopMapper(s)!! }
    }


    @GetMapping(path = ["/deleteShop/{shopId}"])
    fun deleteShop(@PathVariable("shopId") shopId: Long): ResponseEntity<String> {
        return try {
            shopRepo.deleteById(shopId)
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.internalServerError().build()
        }
    }


    @GetMapping(path = ["/deleteShopItem/{itemId}"])
    fun deleteShopItem(@PathVariable("itemId") itemId: Long): ResponseEntity<String> {
        return try {
            shopItemRepo.deleteById(itemId)
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.internalServerError().build()
        }

    }

    @PostMapping(path = ["/createNewShopItem"])
    @ResponseBody
    fun createNewShopItem(@RequestBody item: NewShopItemDto): ResponseEntity<Long?> {
        try {
            val s = shopItemRepo.save(
                ShopItems(
                    details = item.details,
                    name = item.name,
                    shopId = item.shopId,
                    price = item.price,
                    fileUuid = item.fileUuid
                )
            )

            return ResponseEntity.ok().body(s.id)
        } catch (e: Exception) {
            logger.error(e.message)
        }
        return ResponseEntity.internalServerError().build()

    }

    @GetMapping(path = ["/getShopItems/{shopId}"])
    fun getShopItems(@PathVariable("shopId") shopId: String): List<ShopItemRes> {
        val res = shopItemRepo.findByShopIdOrderByIdDesc(shopId)
        if (res != null) {

            val result = ArrayList<ShopItemRes>()
            res.forEach { item ->
                val shopItems =
                    ShopItemRes(id = item.id, name = item.name, details = item.details, price = item.price)
                val info = fileInfoRepo.getByPacketId(item.fileUuid)
                if (info != null) {
                    shopItems.fileUuids = info.map { f -> f.uuid }
                }
                result.add(shopItems)
            }
            return result
        }
        return ArrayList()


    }

}