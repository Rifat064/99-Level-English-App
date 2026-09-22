package com.shobdodaily.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shobdodaily.core.database.model.CardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Query("SELECT * FROM cards WHERE dayIndex = :dayIndex")
    fun observeCardByDayIndex(dayIndex: Int): Flow<CardEntity?>

    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun getCardById(id: Long): CardEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(vararg cards: CardEntity)

    @Query("DELETE FROM cards")
    suspend fun clearCards()

    @androidx.room.Transaction
    @Query("SELECT * FROM cards WHERE dayIndex <= :limitDayIndex ORDER BY dayIndex DESC")
    fun observeHistoryCards(limitDayIndex: Int): Flow<List<com.shobdodaily.core.database.model.HistoryCardEntity>>

    @Query("SELECT * FROM cards WHERE dayIndex BETWEEN :startDay AND :endDay")
    suspend fun getCardsForWeek(startDay: Int, endDay: Int): List<CardEntity>
}
