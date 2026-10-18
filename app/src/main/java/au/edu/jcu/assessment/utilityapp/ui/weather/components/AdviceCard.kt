package au.edu.jcu.assessment.utilityapp.ui.weather.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import au.edu.jcu.assessment.utilityapp.R
import au.edu.jcu.assessment.utilityapp.domain.ClothingLevel
import au.edu.jcu.assessment.utilityapp.domain.OutfitAdvice
import au.edu.jcu.assessment.utilityapp.domain.UmbrellaAdvice
import au.edu.jcu.assessment.utilityapp.domain.UmbrellaReason
import au.edu.jcu.assessment.utilityapp.domain.UvLevel

/** The core "at a glance" card: one short line each for umbrella, sun protection and clothing. */
@Composable
fun AdviceCard(advice: OutfitAdvice, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.advice_title),
                style = MaterialTheme.typography.titleMedium,
            )
            AdviceRow(emoji = umbrellaEmoji(advice.umbrella.reason), text = umbrellaText(advice.umbrella))
            AdviceRow(emoji = uvEmoji(advice.uvLevel), text = uvText(advice.uvLevel, advice.uvIndexMax))
            AdviceRow(emoji = clothingEmoji(advice.clothing), text = clothingText(advice.clothing))
            advice.strongWindKmh?.let { wind ->
                AdviceRow(emoji = "💨", text = stringResource(R.string.advice_wind, wind))
            }
        }
    }
}

@Composable
private fun AdviceRow(emoji: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Fixed width keeps the text lined up whatever the emoji's width.
        Text(text = emoji, fontSize = 24.sp, modifier = Modifier.width(40.dp))
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}

private fun umbrellaEmoji(reason: UmbrellaReason): String = when (reason) {
    UmbrellaReason.RAINING_NOW -> "☔"
    UmbrellaReason.LIKELY_RAIN -> "🌂"
    UmbrellaReason.NOT_NEEDED -> "👍"
}

@Composable
private fun umbrellaText(umbrella: UmbrellaAdvice): String = when (umbrella.reason) {
    UmbrellaReason.RAINING_NOW -> stringResource(R.string.advice_umbrella_raining_now)
    UmbrellaReason.LIKELY_RAIN -> stringResource(R.string.advice_umbrella_likely, umbrella.rainChance)
    UmbrellaReason.NOT_NEEDED -> stringResource(R.string.advice_umbrella_not_needed, umbrella.rainChance)
}

private fun uvEmoji(level: UvLevel): String = when (level) {
    UvLevel.LOW -> "🙂"
    UvLevel.MODERATE, UvLevel.HIGH -> "🧴"
    UvLevel.VERY_HIGH -> "🧢"
    UvLevel.EXTREME -> "⚠️"
}

@Composable
private fun uvText(level: UvLevel, uvIndexMax: Int): String {
    val textRes = when (level) {
        UvLevel.LOW -> R.string.advice_uv_low
        UvLevel.MODERATE -> R.string.advice_uv_moderate
        UvLevel.HIGH -> R.string.advice_uv_high
        UvLevel.VERY_HIGH -> R.string.advice_uv_very_high
        UvLevel.EXTREME -> R.string.advice_uv_extreme
    }
    return stringResource(textRes, uvIndexMax)
}

private fun clothingEmoji(level: ClothingLevel): String = when (level) {
    ClothingLevel.HOT -> "🩳"
    ClothingLevel.WARM -> "👕"
    ClothingLevel.MILD -> "👚"
    ClothingLevel.COOL, ClothingLevel.COLD -> "🧥"
    ClothingLevel.FREEZING -> "🧣"
}

@Composable
private fun clothingText(level: ClothingLevel): String = stringResource(
    when (level) {
        ClothingLevel.HOT -> R.string.advice_clothing_hot
        ClothingLevel.WARM -> R.string.advice_clothing_warm
        ClothingLevel.MILD -> R.string.advice_clothing_mild
        ClothingLevel.COOL -> R.string.advice_clothing_cool
        ClothingLevel.COLD -> R.string.advice_clothing_cold
        ClothingLevel.FREEZING -> R.string.advice_clothing_freezing
    }
)
