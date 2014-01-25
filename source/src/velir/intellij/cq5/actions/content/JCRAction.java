package velir.intellij.cq5.actions.content;

import java.util.List;

import com.intellij.facet.ProjectFacetManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import velir.intellij.cq5.config.JCRConfiguration;
import velir.intellij.cq5.config.JCRMountPoint;
import velir.intellij.cq5.facet.JCRFacet;
import velir.intellij.cq5.facet.JCRFacetType;

public abstract class JCRAction extends AnAction {
	protected boolean isJCREvent (AnActionEvent e) {
		JCRConfiguration jcrConfiguration = getConfiguration(e);
		return jcrConfiguration != null;
	}

	// get the configuration that is relevant to this event
	protected JCRConfiguration getConfiguration (AnActionEvent e) {
		final DataContext context = e.getDataContext();
		final Project project = e.getData(PlatformDataKeys.PROJECT);
		VirtualFile target = (VirtualFile) context.getData(DataConstants.VIRTUAL_FILE);

		// get facets
		List<JCRFacet> facets = ProjectFacetManager.getInstance(project).getFacets(JCRFacetType.JCR_TYPE_ID);
		if (facets.size() == 0) return null;

		// iterate through all facets, looking for one with a mount point this event is contains
		for (JCRFacet jcrFacet : facets) {

			JCRConfiguration jcrConfiguration = jcrFacet.getConfiguration();

			JCRMountPoint jcrMountPoint = jcrConfiguration.getMountPoint(target.getPath());
			if (jcrMountPoint != null) return jcrConfiguration;
		}

		// couldn't find a relevant configuration/mountpoint
		return null;
	}

}
