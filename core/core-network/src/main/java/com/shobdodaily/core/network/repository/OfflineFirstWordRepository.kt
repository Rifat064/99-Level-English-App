package com.shobdodaily.core.network.repository

import com.shobdodaily.core.database.dao.WordDao
import com.shobdodaily.core.model.Word
import com.shobdodaily.core.model.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineFirstWordRepository @Inject constructor(
    private val wordDao: WordDao
) : WordRepository {

    override fun searchWords(query: String, tag: String?): Flow<List<Word>> {
        return wordDao.searchWords(query, tag).map { entities ->
            entities.map { entity ->
                Word(
                    id = entity.id,
                    word = entity.word,
                    pos = entity.pos,
                    bangla = entity.bangla,
                    englishGloss = entity.englishGloss,
                    examTag = entity.examTag,
                    examCategory = entity.examCategory,
                    difficulty = entity.difficulty,
                    frequencyRank = entity.frequencyRank
                )
            }
        }
    }

    override fun getExamTags(): Flow<List<String>> {
        return wordDao.getExamTags()
    }
}
