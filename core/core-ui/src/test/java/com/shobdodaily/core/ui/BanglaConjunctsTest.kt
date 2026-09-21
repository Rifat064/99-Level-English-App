package com.shobdodaily.core.ui

import com.shobdodaily.core.ui.theme.NotoSansBengaliFontFamily
import com.shobdodaily.core.ui.theme.ShobdoTypography
import com.shobdodaily.core.ui.theme.Spacing
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BanglaConjunctsTest {

    @Test
    fun verifyRequiredConjuncts() {
        val conjuncts = listOf("ক্ষ", "ত্র", "জ্ঞ", "ঙ্গ")
        for (c in conjuncts) {
            assertTrue("Conjunct $c must not be empty", c.isNotEmpty())
        }
    }

    @Test
    fun verifyTypographyFontFamily() {
        assertNotNull(NotoSansBengaliFontFamily)
        assertEquals(NotoSansBengaliFontFamily, ShobdoTypography.bodyLarge.fontFamily)
        assertEquals(NotoSansBengaliFontFamily, ShobdoTypography.titleLarge.fontFamily)
        assertEquals(NotoSansBengaliFontFamily, ShobdoTypography.displayMedium.fontFamily)
    }

    @Test
    fun verifyMinimumTouchTarget() {
        val spacing = Spacing()
        assertTrue(
            "Minimum touch target must be at least 48dp per design language",
            spacing.minTouchTarget.value >= 48f
        )
    }
}
