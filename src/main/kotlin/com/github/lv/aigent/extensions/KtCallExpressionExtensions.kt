package com.github.lv.aigent.extensions

import com.intellij.psi.util.parentOfType
import org.jetbrains.kotlin.idea.caches.resolve.resolveToCall
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtValueArgument

fun KtCallExpression.addArgumentBeforeFirstDefault(argument: KtValueArgument) {
    val resultingDescriptor = resolveToCall()?.resultingDescriptor
    val firstDefaultArg = valueArguments.firstOrNull { arg ->
        resultingDescriptor?.valueParameters?.find {
            it.name.asString() == arg.getArgumentName()?.asName?.identifier
        }?.declaresDefaultValue() == true
    }
    valueArgumentList?.addArgumentBefore(argument, firstDefaultArg)
}

fun KtCallExpression.hasViewModelField() =
    parentOfType<KtClass>()?.getProperties()?.any { property -> property.name == "viewModel" } ?: false