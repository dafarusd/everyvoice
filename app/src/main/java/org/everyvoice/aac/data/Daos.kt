package org.everyvoice.aac.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY sortOrder")
    fun observeAll(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories ORDER BY sortOrder")
    suspend fun all(): List<CategoryEntity>

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun count(): Int

    @Insert
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Query("DELETE FROM categories")
    suspend fun deleteAll()
}

@Dao
interface ButtonDao {

    @Query("SELECT * FROM buttons WHERE categoryId = :categoryId ORDER BY sortOrder, id")
    fun observeByCategory(categoryId: String): Flow<List<ButtonEntity>>

    @Query("SELECT * FROM buttons ORDER BY categoryId, sortOrder, id")
    suspend fun all(): List<ButtonEntity>

    @Query("SELECT COALESCE(MAX(sortOrder), -1) FROM buttons WHERE categoryId = :categoryId")
    suspend fun maxSortOrder(categoryId: String): Int

    @Insert
    suspend fun insert(button: ButtonEntity): Long

    @Insert
    suspend fun insertAll(buttons: List<ButtonEntity>)

    @Update
    suspend fun update(button: ButtonEntity)

    @Delete
    suspend fun delete(button: ButtonEntity)

    @Query("DELETE FROM buttons")
    suspend fun deleteAll()
}
