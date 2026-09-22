package com.shobdodaily.feature.card.ui

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.drawText
import androidx.compose.ui.geometry.Offset
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import androidx.compose.material.icons.filled.Share
import coil3.compose.AsyncImage
import com.shobdodaily.core.model.Card as DailyCard
import com.shobdodaily.core.model.DailyCardPayload
import com.shobdodaily.core.model.UserProgress
import com.shobdodaily.core.model.Word
import com.shobdodaily.core.ui.theme.ShobdoDailyTheme
import com.shobdodaily.feature.card.R
import java.util.Locale

@Composable
fun DailyCardScreen(
    uiState: DailyCardUiState,
    onMarkAsLearned: () -> Unit,
    onToggleBookmark: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var tts: TextToSpeech? by remember { mutableStateOf(null) }

    DisposableEffect(context) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    if (uiState.isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading...")
        }
        return
    }
    
    val payload = uiState.payload
    if (payload == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(uiState.error ?: "Failed to load card")
        }
        return
    }

    val isBookmarked = uiState.progress?.bookmarked == true
    val isLearned = uiState.progress?.completed == true

    val graphicsLayer = rememberGraphicsLayer()
    val coroutineScope = rememberCoroutineScope()
    val textMeasurer = rememberTextMeasurer()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .drawWithContent {
                graphicsLayer.record {
                    this@drawWithContent.drawContent()
                    if (!uiState.isSubscriber) {
                        val textLayoutResult = textMeasurer.measure(
                            text = "ShobdoDaily (Free Version)",
                            style = TextStyle(fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
                        )
                        drawText(
                            textLayoutResult = textLayoutResult,
                            topLeft = Offset(
                                x = size.width - textLayoutResult.size.width - 16.dp.toPx(),
                                y = size.height - textLayoutResult.size.height - 16.dp.toPx()
                            )
                        )
                    }
                }
                drawContent()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AI Scene Image with Speech Bubble Overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            AsyncImage(
                model = payload.card.imagePath,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceVariant),
                error = ColorPainter(MaterialTheme.colorScheme.surfaceVariant)
            )

            if (!payload.card.bubbleText.isNullOrEmpty()) {
                Surface(
                    shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = payload.card.bubbleText!!,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
            
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                // Share Icon
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                                withContext(Dispatchers.IO) {
                                    val cachePath = File(context.cacheDir, "images")
                                    cachePath.mkdirs()
                                    val file = File(cachePath, "shared_card.jpg")
                                    val stream = FileOutputStream(file)
                                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, stream)
                                    stream.close()
                                    
                                    val uri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        file
                                    )
                                    
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "image/jpeg"
                                        putExtra(Intent.EXTRA_STREAM, uri)
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    
                                    context.startActivity(Intent.createChooser(shareIntent, "Share your Daily Card"))
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                // Bookmark Icon
                IconButton(
                    onClick = onToggleBookmark
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) Color.Red else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // English Sentence with TTS
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "➡️", modifier = Modifier.padding(end = 8.dp))
            Text(
                text = payload.card.sentenceEn,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = {
                    tts?.speak(payload.card.sentenceEn, TextToSpeech.QUEUE_FLUSH, null, null)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play TTS",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Vocabulary to focus section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔍", modifier = Modifier.padding(end = 8.dp))
                    Text(
                        text = stringResource(R.string.vocabulary_to_focus),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                WordRow(emoji = "🏖️", word = payload.wordA)
                WordRow(emoji = "💔", word = payload.wordB)
            }
        }

        // What's happening section (Bangla Translation)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🪄", modifier = Modifier.padding(end = 8.dp))
                Text(
                    text = stringResource(R.string.whats_happening),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Text(
                text = payload.card.sentenceBn,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 28.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mark as learned Button
        Button(
            onClick = onMarkAsLearned,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLearned
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = if (isLearned) stringResource(R.string.mark_as_learned) else "Mark as learned")
        }
    }
}

@Composable
private fun WordRow(emoji: String, word: Word) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(text = emoji, modifier = Modifier.padding(end = 8.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = word.word,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = " = (${word.bangla})",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            if (!word.examTag.isNullOrEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = word.examTag!!,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320, name = "Daily Card (Small Phone)")
@Composable
fun DailyCardScreenSmallPreview() {
    ShobdoDailyTheme {
        DailyCardScreen(
            uiState = DailyCardUiState(payload = getMockPayload(), isLoading = false),
            onMarkAsLearned = {},
            onToggleBookmark = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 600, name = "Daily Card (Tablet/Large)")
@Composable
fun DailyCardScreenLargePreview() {
    ShobdoDailyTheme {
        DailyCardScreen(
            uiState = DailyCardUiState(payload = getMockPayload(), isLoading = false),
            onMarkAsLearned = {},
            onToggleBookmark = {}
        )
    }
}

private fun getMockPayload() = DailyCardPayload(
    dayIndex = 1,
    card = DailyCard(
        id = 1L,
        dayIndex = 1,
        season = 1,
        wordAId = 101L,
        wordBId = 102L,
        sentenceEn = "The abject man felt absolute despair after the test.",
        sentenceBn = "পরীক্ষার পর শোচনীয় লোকটি চরম হতাশা অনুভব করেছিল।",
        bubbleText = "I have failed...",
        imagePath = "https://example.com/image.jpg",
        imageBlurhash = null,
        status = "published"
    ),
    wordA = Word(
        id = 101L,
        word = "abject",
        pos = "adj",
        bangla = "শোচনীয়",
        englishGloss = "experienced to the maximum degree",
        examTag = "BCS 35",
        examCategory = "BCS",
        difficulty = 3,
        frequencyRank = 450
    ),
    wordB = Word(
        id = 102L,
        word = "absolute",
        pos = "adj",
        bangla = "চরম",
        englishGloss = "not qualified or diminished in any way",
        examTag = "Janata Bank 2019",
        examCategory = "BANK",
        difficulty = 2,
        frequencyRank = 100
    )
)
