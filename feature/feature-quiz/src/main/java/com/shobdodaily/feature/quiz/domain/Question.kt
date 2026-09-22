package com.shobdodaily.feature.quiz.domain

import com.shobdodaily.core.model.Word

sealed class Question {
    abstract val targetWord: Word
    abstract val options: List<String>
    abstract val correctAnswer: String

    data class EnglishToBangla(
        override val targetWord: Word,
        override val options: List<String>,
        override val correctAnswer: String = targetWord.bangla
    ) : Question()
    
    data class BanglaToEnglish(
        override val targetWord: Word,
        override val options: List<String>,
        override val correctAnswer: String = targetWord.word
    ) : Question()
    
    data class FillInTheBlank(
        override val targetWord: Word,
        val sentenceWithBlank: String,
        override val options: List<String>,
        override val correctAnswer: String = targetWord.word
    ) : Question()
    
    data class SentenceRecall(
        override val targetWord: Word,
        val secondaryWord: Word,
        val imagePath: String,
        override val options: List<String>, 
        override val correctAnswer: String = "${targetWord.word}, ${secondaryWord.word}"
    ) : Question()
    
    data class OddOneOut(
        override val targetWord: Word,
        override val options: List<String>,
        override val correctAnswer: String = targetWord.word,
        val commonCategory: String?
    ) : Question()
}
