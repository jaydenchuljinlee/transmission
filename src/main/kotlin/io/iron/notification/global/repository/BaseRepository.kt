package io.iron.notification.global.repository

interface BaseRepository<T, ID> {
    fun findAll(): List<T>
    fun findById(id: ID): T?
    fun save(entity: T): T
    fun delete(entity: T)
}