package com.shobdodaily.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shobdodaily.core.database.model.WordEntity

@Dao
interface WordDao {
    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getWordById(id: Long): WordEntity?

    @Query("SELECT * FROM words WHERE id IN (:ids)")
    suspend fun getWordsByIds(ids: List<Long>): List<WordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(vararg words: WordEntity)

    @Query("DELETE FROM words")
    suspend fun clearWords()

    @Query("SELECT * FROM words")
    suspend fun getAllWords(): List<WordEntity>

    @Query("""
        SELECT * FROM words 
        WHERE (:query = '' OR word LIKE '%' || :query || '%' OR bangla LIKE '%' || :query || '%' OR englishGloss LIKE '%' || :query || '%')
        AND (:tag IS NULL OR examTag LIKE '%' || :tag || '%')
        ORDER BY word ASC
    """)
    fun searchWords(query: String, tag: String?): kotlinx.coroutines.flow.Flow<List<WordEntity>>

    @Query("SELECT DISTINCT examTag FROM words WHERE examTag IS NOT NULL AND examTag != '' ORDER BY examTag ASC")
    fun getExamTags(): kotlinx.coroutines.flow.Flow<List<String>>
}
