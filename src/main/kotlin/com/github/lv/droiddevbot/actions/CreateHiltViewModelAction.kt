package com.github.lv.droiddevbot.actions

import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.ui.Messages
import com.intellij.psi.impl.file.PsiDirectoryFactory

class CreateHiltViewModelAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val directory = e.getData(LangDataKeys.IDE_VIEW)?.directories?.firstOrNull() ?: return
        val packageName = PsiDirectoryFactory.getInstance(project).getQualifiedName(directory, true)

        val fileName = Messages.showInputDialog(
            "Enter ViewModel name", "ViewModel Name", null
        ) ?: return

        if (directory.findFile("$fileName.kt") != null) {
            Notifications.Bus.notify(
                Notification(
                    "DroidDevBot",
                    "File conflict",
                    "`$fileName.kt` already exists in this package",
                    NotificationType.ERROR
                ), project
            )
            return
        }

        val template = FileTemplateManager.getInstance(project).getInternalTemplate("HiltViewModel")
        val properties = FileTemplateManager.getInstance(project).defaultProperties.apply {
            put("NAME", fileName)
            put("PACKAGE_NAME", packageName)
        }
        val file = FileTemplateUtil.createFromTemplate(template, fileName, properties, directory)
        file.containingFile.virtualFile?.let { virtualFile ->
            FileEditorManager.getInstance(project).openFile(virtualFile, true)
        }
    }
}