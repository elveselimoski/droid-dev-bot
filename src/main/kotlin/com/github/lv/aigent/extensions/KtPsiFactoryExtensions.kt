package com.github.lv.aigent.extensions

import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtValueArgumentList

fun KtPsiFactory.invokeCallback(psiElement: PsiElement, callbackName: String) {
    val valueArgumentList = psiElement.parent as? KtValueArgumentList
    if (valueArgumentList != null) {
        psiElement.replace(createExpression(callbackName))
    } else {
        psiElement.parent.addBefore(createExpression("$callbackName()"), psiElement)
    }
}

fun KtPsiFactory.addCallbackToComposableAndUsages(targetFunction: KtFunction, callbackName: String) {
    val parameters = targetFunction.valueParameters
    val hasCallback = parameters.any { it.name == callbackName }

    if (hasCallback) {
        println("⚠️ Callback already exists")
        return
    }

    targetFunction.addParameterBeforeFirstDefault(createParameter("$callbackName: () -> Unit"))
    CodeStyleManager.getInstance(targetFunction.project).reformat(targetFunction)
    addCallbackToUsages(targetFunction, callbackName)
}

private fun KtPsiFactory.addCallbackToUsages(targetFunction: KtFunction, callbackName: String) {
    val usages: List<KtCallExpression> = targetFunction.findUsages()
    usages.forEach { callExpression ->
        val parentComposable: KtFunction? = callExpression.parentComposable()
        var shouldCheckForParentUsages = false
        callExpression.addArgumentBeforeFirstDefault(
            createArgument(
                if (parentComposable?.hasViewModelArgument() == true || callExpression.hasViewModelField()) {
                    "$callbackName = { viewModel.${callbackName.extractMethodName()}() }"
                } else if (parentComposable?.isPreviewComposable() == true) {
                    "$callbackName = {}"
                } else if (parentComposable != null) {
                    shouldCheckForParentUsages = true
                    "$callbackName = $callbackName"
                } else {
                    "$callbackName = {}"
                }
            )
        )
        CodeStyleManager.getInstance(targetFunction.project).reformat(callExpression)
        if (shouldCheckForParentUsages) {
            addCallbackToComposableAndUsages(requireNotNull(parentComposable), callbackName)
        }
    }
}