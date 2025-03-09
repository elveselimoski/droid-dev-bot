package com.github.lv.aigent.actions

import com.github.lv.aigent.extensions.addCallbackToComposableAndUsages
import com.github.lv.aigent.extensions.parentComposable
import com.github.lv.aigent.extensions.invokeCallback
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.ui.Messages
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory

class GenerateCallbackAction : AnAction() {

    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        super.update(e)
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return
        e.presentation.isEnabledAndVisible = psiFile is KtFile
    }

    override fun actionPerformed(e: AnActionEvent) {
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) as? KtFile ?: return
        val offset = e.getData(CommonDataKeys.CARET)?.caretModel?.offset ?: return
        val psiElement = e.getData(CommonDataKeys.PSI_FILE)?.findElementAt(offset) ?: return

        val targetFunction = psiElement.parentComposable() ?: return
        val callbackName = Messages.showInputDialog(
            "Enter callback name", "Generate Callback", null
        ) ?: return

        val project = psiFile.project
        val ktPsiFactory = KtPsiFactory(project)
        WriteCommandAction.runWriteCommandAction(project) {
            ktPsiFactory.invokeCallback(psiElement, callbackName)
            ktPsiFactory.addCallbackToComposableAndUsages(targetFunction, callbackName)
        }
    }
}