package com.stalkerone.depthkeyboard

object LanguageDictionaries {
    private val words = mapOf(
        "English (US)" to "the and you are this that with have for from", "English (UK)" to "the and you are this that with have for from",
        "Spanish" to "el la los las que de y en un una para con como", "French" to "le la les des que de et en un une pour avec comme",
        "German" to "der die das und ist ein eine mit für auf nicht", "Italian" to "il la le che di e un una per con come",
        "Portuguese" to "o a os as que de e em um uma para com como", "Turkish" to "bir ve bu için ile çok daha olan ne",
        "Urdu" to "یہ ہے ایک اور کے میں سے کو کا کی", "Arabic" to "من في على هذا هذه التي هو هي مع إلى",
        "Hindi" to "यह है एक और के में से को का की", "Bengali" to "এই হল এক এবং এর মধ্যে থেকে জন্য সঙ্গে",
        "Russian" to "это и в не на я что с он как из", "Greek" to "και το να είναι για με σε από αυτό",
        "Persian" to "این است یک و در از برای با که به", "Thai" to "และ เป็น ที่ ของ ใน มี การ ได้ กับ"
    )
    fun words(language: String, prefix: String): List<String> = words[language].orEmpty().split(' ').filter { it.startsWith(prefix, true) }.take(5)
}
