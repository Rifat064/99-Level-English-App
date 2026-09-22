package com.shobdodaily.feature.history.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shobdodaily.core.model.Word
import com.shobdodaily.core.model.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class DictionaryUiState(
    val words: List<Word> = emptyList(),
    val examTags: List<String> = emptyList(),
    val searchQuery: String = "",
    val selectedTag: String? = null
)

@HiltViewModel
class DictionaryViewModel @Inject constructor(
    private val wordRepository: WordRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val selectedTag = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val wordsFlow = combine(searchQuery, selectedTag) { query, tag ->
        Pair(query, tag)
    }.flatMapLatest { (query, tag) ->
        wordRepository.searchWords(query, tag)
    }

    val uiState: StateFlow<DictionaryUiState> = combine(
        wordsFlow,
        wordRepository.getExamTags(),
        searchQuery,
        selectedTag
    ) { words, tags, query, tag ->
        DictionaryUiState(
            words = words,
            examTags = tags,
            searchQuery = query,
            selectedTag = tag
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DictionaryUiState()
    )

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    fun onTagSelect(tag: String?) {
        selectedTag.value = tag
    }
}
