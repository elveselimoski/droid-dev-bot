package com.github.lv.droiddevbot.extensions

fun String.extractMethodName() = removePrefix("on").removeSuffix("Click").replaceFirstChar { it.lowercase() }