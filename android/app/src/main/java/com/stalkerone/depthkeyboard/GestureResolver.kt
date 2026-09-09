package com.stalkerone.depthkeyboard

import kotlin.math.min

object GestureResolver {
    private val english = "the and you are for not with this that have from was were will your what can all one about would there their which when make like time just know take people into good some could them other than then look only come over think also back after use two how our work first well way even new want because these give day most us".split(' ')

    fun resolve(path: String, language: String): String? {
        val trace = collapse(path.lowercase()).filter { it.isLetter() }
        if (trace.length < 2) return null
        val candidates = (english + LanguageDictionaries.words(language, "")).distinct()
        return candidates.minByOrNull { score(trace, collapse(it.lowercase())) }?.takeIf { score(trace, collapse(it.lowercase())) <= maxOf(2, trace.length / 2) }
    }

    private fun collapse(value: String): String = buildString { value.forEach { if (lastOrNull() != it) append(it) } }
    private fun score(a: String, b: String): Int {
        val n = min(a.length, b.length)
        var mismatch = kotlin.math.abs(a.length - b.length)
        for (i in 0 until n) if (a[i] != b[i]) mismatch++
        return mismatch
    }
}
