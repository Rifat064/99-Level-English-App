package com.shobdodaily.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shobdodaily.core.database.dao.CardDao
import com.shobdodaily.core.database.dao.ProgressDao
import com.shobdodaily.core.database.dao.QuizAttemptDao
import com.shobdodaily.core.database.dao.WordDao
import com.shobdodaily.core.database.model.CardEntity
import com.shobdodaily.core.database.model.ProgressEntity
import com.shobdodaily.core.database.model.QuizAttemptEntity
import com.shobdodaily.core.database.model.WordEntity

@Database(
    entities = [
        CardEntity::class,
        WordEntity::class,
        ProgressEntity::class,
        QuizAttemptEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class ShobdoDailyDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
    abstract fun wordDao(): WordDao
    abstract fun progressDao(): ProgressDao
    abstract fun quizAttemptDao(): QuizAttemptDao
}
