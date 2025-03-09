package com.github.lv.aigent.extensions

fun String.extractMethodName() = removePrefix("on").removeSuffix("Click").replaceFirstChar { it.lowercase() }