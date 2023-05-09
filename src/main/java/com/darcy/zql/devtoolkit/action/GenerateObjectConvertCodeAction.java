package com.darcy.zql.devtoolkit.action;

import java.awt.FlowLayout;
import java.awt.TextField;
import java.util.Collection;

import javax.swing.JPanel;

import org.jetbrains.annotations.NotNull;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.TreeClassChooserFactory;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.impl.java.stubs.index.JavaShortClassNameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;

public class GenerateObjectConvertCodeAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        assert project != null;

        JPanel panel = new JPanel(new FlowLayout());
        panel.setVisible(true);

//        TextFieldWithBrowseButton browseButton = new TextFieldWithBrowseButton();
//        browseButton.addBrowseFolderListener(new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFileDescriptor()));
//        panel.add(browseButton);

        Collection<PsiClass> officialAccountMessage = JavaShortClassNameIndex.getInstance()
                .get("OfficialAccountMessage", project, GlobalSearchScope.allScope(project));
        System.out.println(officialAccountMessage);
        

//        final PsiShortNamesCache cache = PsiShortNamesCache.getInstance(project);
//        @NotNull PsiClass[] dtos = cache.getClassesByName("Dto", GlobalSearchScope.allScope(project));
//        System.out.println(dtos);

        //        TreeClassChooser chooser = TreeClassChooserFactory.getInstance(project).createProjectScopeChooser("Class 文件选择");
//        chooser.showDialog();
//        PsiClass selected = chooser.getSelected();
//        System.out.println(selected);

//        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor(
//                JavaFileType.INSTANCE);
//        VirtualFile virtualFile = FileChooser.chooseFile(descriptor, event.getProject(), null);
//        System.out.println(virtualFile);
    }
}
