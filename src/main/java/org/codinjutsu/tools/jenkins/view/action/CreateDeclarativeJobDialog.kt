package org.codinjutsu.tools.jenkins.view.action

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.rows
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import javax.swing.JComponent


val dummyState: CreateJobDialogState
  get() = CreateJobDialogState()


class CreateDeclarativeJobDialog(project: Project?)
  : DialogWrapper(project) {

  val state: CreateJobDialogState = dummyState

  override fun createCenterPanel(): JComponent {
      val sameWidthLabelsGroup = "JENKINS_CREATE_JOB_DIALOG_LABELS_WIDTH_GROUP"

      return panel {
        row {
          label("Name: ")
            .widthGroup(sameWidthLabelsGroup)
          textField()
            .bindText(state::name)
                      .validationOnApply { validateNotEmpty(it.text, it) }
            .horizontalAlign(HorizontalAlign.FILL)
            .focused()
        }
        row {
          label("Description: ")
            .widthGroup(sameWidthLabelsGroup)
          textArea()
            .bindText(state::description)
            .horizontalAlign(HorizontalAlign.FILL)
            .rows(6)
        }
      }
  }

  fun validateNotEmpty(text: String, component: JComponent): ValidationInfo? {
    return if (text.isBlank()) ValidationInfo("This field must not be blank", component) else null
  }

  init {
    title = "Create Job"
    init()
  }
}

data class CreateJobDialogState(
  var name: String = "",
  var description: String = "",
)