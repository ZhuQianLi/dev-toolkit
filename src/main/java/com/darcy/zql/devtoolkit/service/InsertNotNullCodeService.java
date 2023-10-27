package com.darcy.zql.devtoolkit.service;

import com.darcy.zql.devtoolkit.utils.PsiAnnotationUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;

public class InsertNotNullCodeService {

    public void insertNotNullCode(AnActionEvent event) {
        DataContext dataContext = event.getDataContext();
        PsiElement psiElement = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
        PsiClass psiClass = (PsiClass) psiElement;
        assert psiClass != null;

        PsiField[] allFields = psiClass.getAllFields();
        for (int i = allFields.length - 1; i >= 0; i--) {
            PsiField field = allFields[i];

            if (PsiAnnotationUtils.existsNullable(field) || PsiAnnotationUtils.existsNotNull(field)) {
                continue;
            }
            insertNotNullCodeForField(event, field);
        }
    }

    private void insertNotNullCodeForField(AnActionEvent event, PsiField field) {
        Editor editor = CommonDataKeys.EDITOR.getData(event.getDataContext());
        assert editor != null;
        Document document = editor.getDocument();
        int textOffset = field.getTextOffset();
        int lineStartOffset = document.getLineStartOffset(document.getLineNumber(textOffset));

        String blankSpace = "    ";
        WriteCommandAction.runWriteCommandAction(event.getProject(), () -> {
            document.insertString(lineStartOffset, blankSpace + "@NotNull\n");
        });
    }

}
