package com.shobdodaily.core.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class HistoryCardEntity(
    @Embedded val card: CardEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "cardId"
    )
    val progress: ProgressEntity?
)
