package com.zhuqianli.devtoolkit.java.lang.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocToken;
import com.zhuqianli.devtoolkit.java.lang.JavaDocComment;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class PsiDocCommentImpl implements JavaDocComment {

    @Nullable
    PsiDocComment docComment;

    public PsiDocCommentImpl(@Nullable PsiDocComment docComment) {
        this.docComment = docComment;
    }

    @Override
    public List<String> getDescContent() {
        if (docComment == null) {
            return Collections.emptyList();
        }
        List<String> comments = Lists.newArrayList();
        PsiElement[] descriptionElements = docComment.getDescriptionElements();
        for (PsiElement descriptionElement : descriptionElements) {
            if (descriptionElement instanceof PsiDocToken) {
                comments.add(descriptionElement.getText().trim());
            }
        }
        return comments;
    }

    @Override
    public List<String> getTagContent(String tagName) {
        if (docComment == null) {
            return Collections.emptyList();
        }
        List<String> result = Lists.newArrayList();
        for (PsiDocTag tag : docComment.getTags()) {
            if (tag.getNameElement().getText().equals("@see")) {
                if (tag.getValueElement() != null) {
                    result.add(tag.getValueElement().getText());
                }
            }
        }
        return result;
    }

}
