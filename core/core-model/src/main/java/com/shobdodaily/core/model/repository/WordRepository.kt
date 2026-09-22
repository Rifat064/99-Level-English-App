package com.shobdodaily.core.model.repository

import com.shobdodaily.core.model.Word
import kotlinx.coroutines.flow.Flow

interface WordRepository {
    fun searchWords(query: String, tag: String?): Flow<List<Word>>
    fun getExamTags(): Flow<List<String>>
}
