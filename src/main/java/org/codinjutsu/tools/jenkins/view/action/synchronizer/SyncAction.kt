package org.codinjutsu.tools.jenkins.view.action.synchronizer

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.vfs.VirtualFile
import org.codinjutsu.tools.jenkins.logic.attributes.JenkinsVirtualFileAttributesService
import org.codinjutsu.tools.jenkins.util.JenkinsScriptContentSaver.isJenkinsScriptSaved
import org.jetbrains.annotations.NotNull


/** Sync action event. It will handle the manual sync button action when it is clicked */
class SyncAction : DumbAwareAction() {

    /**
     * Get a virtual file on which the event was triggered
     * @param e the event to get the virtual file
     */
    private fun getVirtualFile(e: AnActionEvent): VirtualFile? {
        return e.getData(CommonDataKeys.VIRTUAL_FILE)
    }

    private fun getDocument(e: AnActionEvent): Document? {
        return FileDocumentManager.getInstance().getDocument(getVirtualFile(e)!!)
    }

    /**
     * Get a virtual file with the sync support on which the event was triggered
     * @param e the event to get the virtual file
     */
    private fun getSupportedVirtualFile(e: AnActionEvent): VirtualFile? {
        return getVirtualFile(e)?.let {
            val attributesService = JenkinsVirtualFileAttributesService.getInstance(e.project!!)
            if (attributesService.hasAttributes(it)) {
                it
            } else {
                null
            }
        }
    }

    /**
     * Get an editor on which the event was triggered
     * @param e the event to get the editor
     */
    private fun getEditor(e: AnActionEvent): Editor? {
        return e.getData(CommonDataKeys.EDITOR)
    }

    /**
     * Perform the manual sync action. The action will be performed as a backgroundable task.
     * After the content of the file is synced with the mainframe, the document of the file will be saved
     * @param e the event instance to get the virtual file, the editor and the project where it was triggered
     */
    override fun actionPerformed(e: AnActionEvent) {
        val vFile = getVirtualFile(e) ?: return
        ProgressManager.getInstance().run(object : Task.Backgroundable(e.project, "Synchronizing ${vFile.name}...", true) {
            override fun run(@NotNull progressIndicator: ProgressIndicator) {
                progressIndicator.fraction = 0.10
                progressIndicator.text = "90% to finish"

                val source = FileEditorManager.getInstance(e.project!!)
                if(!isJenkinsScriptSaved(source, vFile)) return

                progressIndicator.fraction = 1.0
                progressIndicator.text = "FINISHED"
            }
        })
    }

    /**
     * Make the sync action button disabled
     * @param e the action event to get the presentation of the button
     */
    private fun makeDisabled(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = false
    }

    /**
     * Disable or enable the sync action button according to the current editor and remote bytes' equality.
     * The button will be disabled also when the auto sync is enabled
     * @param e the action event to get additional data to check and disable the button
     */
    override fun update(e: AnActionEvent) {
        val file = getSupportedVirtualFile(e) ?: let {
            makeDisabled(e)
            return
        }
        val editor = getEditor(e) ?: let {
            makeDisabled(e)
            return
        }
        e.presentation.isEnabledAndVisible = !(getDocument(e)?.text?.toByteArray() contentEquals file.contentsToByteArray())
    }

}