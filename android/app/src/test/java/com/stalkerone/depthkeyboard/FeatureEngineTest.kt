package com.stalkerone.depthkeyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FeatureEngineTest {
    @Test fun autocorrectsCommonTypos() {
        assertEquals("the", FeatureEngine.autocorrect("teh"))
        assertEquals("Don't", FeatureEngine.autocorrect("Dont"))
    }

    @Test fun predictsNextWords() {
        assertTrue(FeatureEngine.predictions("please").contains("send"))
    }

    @Test fun capitalizesSentence() {
        assertEquals("Hello world", FeatureEngine.capitalizeSentence("hello world"))
    }

    @Test fun doubleSpaceBecomesPeriod() {
        assertEquals("Hello. ", FeatureEngine.handleDoubleSpace("Hello  "))
    }

    @Test fun doubleSpaceWithoutTwoSpacesIsIgnored() {
        assertEquals(null, FeatureEngine.handleDoubleSpace("Hello "))
    }

    @Test fun aiPromptsAreSafeAndProviderAgnostic() {
        assertTrue(FeatureEngine.tonePrompt("professional", "hello").contains("professional"))
        assertTrue(FeatureEngine.translatePrompt("Urdu", "hello").contains("Urdu"))
    }
}
