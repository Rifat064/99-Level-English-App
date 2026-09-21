package com.shobdodaily.core.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.shobdodaily.core.ui.theme.LocalSpacing
import com.shobdodaily.core.ui.theme.ShobdoDailyTheme

@Composable
fun BanglaFontPreviewContent(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(spacing.lg),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        Text(
            text = "Bangla Conjunct Verification",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
        )

        ConjunctVerificationCard()
        SampleCardAnatomy()
    }
}

@Composable
private fun ConjunctVerificationCard() {
    val spacing = LocalSpacing.current
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(spacing.md)) {
            Text(
                text = "Required Conjuncts (যুক্তবর্ণ):",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(spacing.xs))
            // Testing the target conjuncts: ক্ষ ত্র জ্ঞ ঙ্গ
            Text(
                text = "ক্ষ  ত্র  জ্ঞ  ঙ্গ",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun SampleCardAnatomy() {
    val spacing = LocalSpacing.current
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            Text(
                text = "Sample Card Anatomical Preview",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.secondary,
            )

            Text(
                text = "➡️ The meticulous scholar delivered an articulate speech.",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            WordTranslationRow("🏖️ meticulous = খুঁতখুঁতে, সূক্ষ্ম", "[BCS 35]")
            WordTranslationRow("💔 articulate = স্পষ্টভাষী, প্রাঞ্জল", "[Janata Bank 2019]")

            Spacer(modifier = Modifier.height(spacing.xs))

            Text(
                text = "🪄 কী ঘটছে:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.tertiary,
            )
            Text(
                text = "সূক্ষ্ম ও যত্নশীল পণ্ডিতটি একটি স্পষ্ট ও প্রাঞ্জল বক্তব্য প্রদান করলেন।",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 26.sp,
            )
        }
    }
}

@Composable
private fun WordTranslationRow(meaning: String, examTag: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = meaning,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = examTag,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Preview(name = "Bangla Conjuncts - Light Theme", showBackground = true, widthDp = 320)
@Composable
private fun BanglaFontPreviewLight() {
    ShobdoDailyTheme(darkTheme = false) {
        Surface {
            BanglaFontPreviewContent()
        }
    }
}

@Preview(
    name = "Bangla Conjuncts - Dark Theme",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 320,
)
@Composable
private fun BanglaFontPreviewDark() {
    ShobdoDailyTheme(darkTheme = true) {
        Surface {
            BanglaFontPreviewContent()
        }
    }
}
