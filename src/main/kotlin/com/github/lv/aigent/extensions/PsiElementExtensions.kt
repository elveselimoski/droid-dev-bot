package com.github.lv.aigent.extensions

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtFunction

fun PsiElement.parentComposable(): KtFunction? {
    return PsiTreeUtil.findFirstParent(this) {
        (it is KtFunction) && it.annotationEntries.any { annotation -> annotation.text == "@Composable" }
    } as? KtFunction
}