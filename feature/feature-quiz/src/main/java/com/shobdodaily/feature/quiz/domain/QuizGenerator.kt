package com.shobdodaily.feature.quiz.domain

import com.shobdodaily.core.model.Card
import com.shobdodaily.core.model.Word
import kotlin.math.abs
import kotlin.random.Random

class QuizGenerator {

    fun generateQuiz(
        userId: String,
        weekIndex: Int,
        weeklyCards: List<Card>,
        fullWordPool: List<Word>
    ): Quiz {
        val seed = (userId + weekIndex).hashCode().toLong()
        val random = Random(seed)

        require(weeklyCards.size == 7) { "A weekly quiz requires exactly 7 cards." }

        // Create a map to quickly look up words by their ID
        val wordPoolMap = fullWordPool.associateBy { it.id }

        // We need 12 questions. 
        // 2 Sentence Recall (covers 2 cards = 4 words)
        // 10 other questions (covers the remaining 5 cards = 10 words)
        val shuffledCards = weeklyCards.shuffled(random)
        
        val sentenceRecallCards = shuffledCards.take(2)
        val singleWordCards = shuffledCards.drop(2)
        
        val singleWords = singleWordCards.flatMap { 
            listOfNotNull(wordPoolMap[it.wordAId], wordPoolMap[it.wordBId]) 
        }.shuffled(random)

        val questions = mutableListOf<Question>()

        // 1. Sentence Recall (2 questions)
        for (card in sentenceRecallCards) {
            val wordA = wordPoolMap[card.wordAId] ?: continue
            val wordB = wordPoolMap[card.wordBId] ?: continue
            
            // Distractors: other word pairs from the week
            val distractorCards = shuffledCards.filter { it.id != card.id }.shuffled(random).take(3)
            val options = mutableListOf("${wordA.word}, ${wordB.word}")
            for (dCard in distractorCards) {
                val dWordA = wordPoolMap[dCard.wordAId]
                val dWordB = wordPoolMap[dCard.wordBId]
                if (dWordA != null && dWordB != null) {
                    options.add("${dWordA.word}, ${dWordB.word}")
                }
            }
            questions.add(
                Question.SentenceRecall(
                    targetWord = wordA,
                    secondaryWord = wordB,
                    imagePath = card.imagePath,
                    options = options.shuffled(random)
                )
            )
        }

        // 2. Single word questions (10 questions)
        // We have 4 types: Eng->Bn, Bn->Eng, FillInTheBlank, OddOneOut
        // Let's distribute: 3 Eng->Bn, 3 Bn->Eng, 2 FillInTheBlank, 2 OddOneOut
        val questionTypes = listOf(
            0, 0, 0, // Eng->Bn
            1, 1, 1, // Bn->Eng
            2, 2,    // FillInTheBlank
            3, 3     // OddOneOut
        ).shuffled(random)

        for (i in singleWords.indices) {
            val target = singleWords[i]
            val qType = questionTypes[i]
            
            when (qType) {
                0 -> {
                    // Eng -> Bn
                    val options = generateDistractors(target, fullWordPool, random) { it.bangla }.toMutableList()
                    options.add(target.bangla)
                    questions.add(Question.EnglishToBangla(target, options.shuffled(random)))
                }
                1 -> {
                    // Bn -> Eng
                    val options = generateDistractors(target, fullWordPool, random) { it.word }.toMutableList()
                    options.add(target.word)
                    questions.add(Question.BanglaToEnglish(target, options.shuffled(random)))
                }
                2 -> {
                    // Fill In The Blank
                    // Find the card for this word to get the sentence
                    val card = singleWordCards.firstOrNull { it.wordAId == target.id || it.wordBId == target.id }
                    if (card != null) {
                        // Very naive replacement (in a real scenario, we might need regex for exact word boundary and ignoring punctuation)
                        val blankSentence = card.sentenceEn.replace(Regex("\\b${target.word}\\b", RegexOption.IGNORE_CASE), "____")
                        val options = generateDistractors(target, fullWordPool, random) { it.word }.toMutableList()
                        options.add(target.word)
                        questions.add(Question.FillInTheBlank(target, blankSentence, options.shuffled(random)))
                    } else {
                        // Fallback to Eng->Bn if card not found (shouldn't happen)
                        val options = generateDistractors(target, fullWordPool, random) { it.bangla }.toMutableList()
                        options.add(target.bangla)
                        questions.add(Question.EnglishToBangla(target, options.shuffled(random)))
                    }
                }
                3 -> {
                    // Odd One Out
                    // 3 words from last week (random from singleWords, not same category), 1 from different category (the target)
                    // Wait, the rule is: "Three words from last week plus one from a different exam_category."
                    // Let's assume the 3 words from last week form the "common" category or just the base. The 1 from different category is the answer?
                    // Actually, if we just pick 3 words from last week that share the same category, and target is from a DIFFERENT category.
                    // Or target is just any word from last week, and we pick 3 distractors from the full pool that share a DIFFERENT category.
                    // Let's do: target is the Odd one out. It has category X. We pick 3 words from full pool that have category Y (where Y != X).
                    // This satisfies "1 from different category" (the target itself) and 3 from another.
                    val targetCategory = target.examCategory
                    val distractorCandidates = fullWordPool.filter { it.examCategory != targetCategory && it.examCategory != null }
                    
                    val chosenCategory = distractorCandidates.randomOrNull(random)?.examCategory
                    val sameCategoryDistractors = distractorCandidates.filter { it.examCategory == chosenCategory }.shuffled(random).take(3)
                    
                    val options = sameCategoryDistractors.map { it.word }.toMutableList()
                    // If we couldn't find 3, just fill with random
                    while (options.size < 3) {
                        val fallback = fullWordPool.random(random).word
                        if (!options.contains(fallback) && fallback != target.word) {
                            options.add(fallback)
                        }
                    }
                    options.add(target.word)
                    questions.add(Question.OddOneOut(target, options.shuffled(random), commonCategory = chosenCategory))
                }
            }
        }

        return Quiz(weekIndex, questions.shuffled(random))
    }

    private fun generateDistractors(
        target: Word,
        fullPool: List<Word>,
        random: Random,
        propertySelector: (Word) -> String
    ): List<String> {
        val targetProperty = propertySelector(target)
        val targetPos = target.pos
        val targetDiff = target.difficulty ?: 3
        
        // Rules: same pos, difficulty within ±1, not a synonym (simple check: not same string)
        val candidates = fullPool.filter {
            val diff = it.difficulty
            it.id != target.id &&
            it.pos == targetPos &&
            diff != null && abs(diff - targetDiff) <= 1 &&
            propertySelector(it) != targetProperty &&
            !propertySelector(it).contains(targetProperty, ignoreCase = true)
        }.shuffled(random)

        val selected = candidates.take(3).map(propertySelector).toMutableList()
        
        // Fallback if not enough candidates
        val allFallback = fullPool.filter { it.id != target.id && propertySelector(it) != targetProperty }.shuffled(random)
        var i = 0
        while (selected.size < 3 && i < allFallback.size) {
            val fallbackProp = propertySelector(allFallback[i])
            if (!selected.contains(fallbackProp)) {
                selected.add(fallbackProp)
            }
            i++
        }
        
        return selected
    }
}
