package velir.intellij.cq5.actions.content;

import javax.activation.MimetypesFileTypeMap;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.BackgroundFromStartOption;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import velir.intellij.cq5.config.JCRConfiguration;
import velir.intellij.cq5.jcr.process.JCRPushProcess;

public class DestructiveImport extends JCRAction {
	private static final Logger log = com.intellij.openapi.diagnostic.Logger.getInstance(JCRAction.class);
	public static final String JCR_MIXIN_TYPES = "jcr:mixinTypes";
	private static final MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();

	@Override
	public void actionPerformed(AnActionEvent anActionEvent) {
		final DataContext context = anActionEvent.getDataContext();
		final VirtualFile target = (VirtualFile) context.getData(DataConstants.VIRTUAL_FILE);
		final JCRConfiguration jcrConfiguration = getConfiguration(anActionEvent);
		final Application application = ApplicationManager.getApplication();
		final Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);

		Task task = new Task.Backgroundable(project, "JCR Import", true, BackgroundFromStartOption.getInstance()) {
			public void run(@NotNull ProgressIndicator progressIndicator) {
				progressIndicator.setIndeterminate(true);
				progressIndicator.setText("Importing to JCR...");
				application.runReadAction(new JCRPushProcess(target, jcrConfiguration, mimetypesFileTypeMap));
			}
		};
		ProgressManager.getInstance().run(task);
	}

	public void update(AnActionEvent e) {
		final Presentation presentation = e.getPresentation();
		boolean enabled = isJCREvent(e);

		presentation.setVisible(enabled);
		presentation.setEnabled(enabled);
	}

}
