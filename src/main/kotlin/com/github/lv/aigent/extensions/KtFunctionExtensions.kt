package com.github.lv.aigent.extensions

import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.ReferencesSearch
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtParameter

fun KtFunction.addParameterBeforeFirstDefault(parameter: KtParameter) {
    valueParameterList?.addParameterBefore(parameter, valueParameters.firstOrNull { it.hasDefaultValue() })
    CodeStyleManager.getInstance(project).reformat(this)
}

fun KtFunction.findUsages(): List<KtCallExpression> {
    val searchScope = GlobalSearchScope.projectScope(project)
    return ReferencesSearch.search(this, searchScope).findAll().mapNotNull { it.element.parent as? KtCallExpression }
        .distinct().toList()
}

fun KtFunction.hasViewModelArgument() = valueParameters.any { param ->
    param.name?.lowercase()?.equals("viewmodel") == true || param.typeReference?.text?.lowercase()
        ?.equals("viewmodel") == true
}

fun KtFunction.isPreviewComposable() =
    annotationEntries.any { annotation -> annotation.shortName?.asString() == "Preview" }