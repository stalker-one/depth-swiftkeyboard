package com.stalkerone.depthkeyboard

object KeyboardLayouts {
    val languages = listOf("English (US)", "English (UK)", "Urdu", "Arabic", "Hindi", "Bengali", "Persian", "Russian", "Greek", "Thai", "Spanish", "French", "German", "Italian", "Portuguese", "Turkish")
    private val qwerty = listOf("qwertyuiop", "asdfghjkl", "zxcvbnm")
    private val azerty = listOf("azertyuiop", "qsdfghjklm", "wxcvbn")
    private val qwertz = listOf("qwertzuiop", "asdfghjkl", "yxcvbnm")
    private val dvorak = listOf("',.pyfgcrl", "aoeuidhtns", ";qjkxbmwvz")
    private val colemak = listOf("qwfpgjluy;", "arstdhneio", "zxcvbkm,.")
    private val urdu = listOf("ضصثقفغعهخحج", "شسیبلاتنمک", "ظطزرذدپوچ")
    private val arabic = listOf("ضصثقفغعهخحج", "شسيبلاتنمك", "ئءؤرلاىةوزظ")
    private val hindi = listOf("ौैाीूबहतयर", "मकनवलसपज", "दगचटडणशष")
    private val bengali = listOf("ৌৈাীূবহতযর", "মকনবসপজ", "দগচটডণশষ")
    private val persian = listOf("ضصثقفغعهخحج", "شسیبلاتنمک", "ظطزرذدپوک")
    private val russian = listOf("йцукенгшщзх", "фывапролджэ", "ячсмитьбю")
    private val greek = listOf(";ςερτυθιοπ", "ασδφγηξκλ", "ζχψωβνμ")
    private val thai = listOf("ัไำพะัีรนย", "ฟหกดเ้่าส", "ผปแอิทมใฝ")

    fun rows(language: String, layout: String): List<String> = when {
        language.equals("Urdu", true) -> urdu
        language.equals("Arabic", true) -> arabic
        language.equals("Hindi", true) -> hindi
        language.equals("Bengali", true) -> bengali
        language.equals("Persian", true) -> persian
        language.equals("Russian", true) -> russian
        language.equals("Greek", true) -> greek
        language.equals("Thai", true) -> thai
        layout.equals("AZERTY", true) -> azerty
        layout.equals("QWERTZ", true) -> qwertz
        layout.equals("DVORAK", true) -> dvorak
        layout.equals("COLEMAK", true) -> colemak
        else -> qwerty
    }

    fun accents(base: Char): List<String> = when (base.lowercaseChar()) {
        'a' -> listOf("á", "à", "â", "ä", "ã", "å")
        'e' -> listOf("é", "è", "ê", "ë")
        'i' -> listOf("í", "ì", "î", "ï")
        'o' -> listOf("ó", "ò", "ô", "ö", "õ")
        'u' -> listOf("ú", "ù", "û", "ü")
        'c' -> listOf("ç")
        'n' -> listOf("ñ")
        else -> emptyList()
    }
}
