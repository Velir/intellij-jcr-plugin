package velir.intellij.cq5.actions.content;

import java.io.IOException;
import java.util.Map;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import velir.intellij.cq5.jcr.model.VNode;
import velir.intellij.cq5.jcr.model.VNodeDefinition;

public abstract class AContextNodeCreation extends ANewNode {
	private static final Logger log = LoggerFactory.getLogger(AContextNodeCreation.class);

	protected abstract int getChildNumber();

	Computable<VNode> getNewVNode;

	@Override
	public void update(AnActionEvent e) {
		final DataContext dataContext = e.getDataContext();
		final Presentation presentation = e.getPresentation();
		Application application = ApplicationManager.getApplication();
		final Project project = e.getData(PlatformDataKeys.PROJECT);

		final PsiElement element = (PsiElement)dataContext.getData(LangDataKeys.PSI_ELEMENT.getName());

		boolean enabled = false;

		if (isJCREvent(e) && element instanceof PsiDirectory) {
			PsiDirectory psiDirectory = (PsiDirectory) element;
			final PsiFile contentFile = psiDirectory.findFile(".content.xml");
			if (contentFile != null) {

				// get node data from content file (just need the primaryType)
				VNode vNode = application.runReadAction(new Computable<VNode>() {
					public VNode compute() {
						try {
							return VNode.makeVNode(contentFile.getVirtualFile().getInputStream(), contentFile.getContainingDirectory().getName());
						} catch (IOException ioe) {
							log.debug("Could not read node xml", ioe);
						} catch (JDOMException jde) {
							log.debug("Could not read node xml", jde);
						}
						return null;
					}
				});

				// get the node definition for this type of node
				if (vNode != null) {
					VNodeDefinition vNodeDefinition = VNodeDefinition.getDefinition(vNode.getType());
					if (vNodeDefinition != null) {
						Map<String, String> childSuggestions = vNodeDefinition.getChildSuggestions();
						// if this action's child number is less than the size of all children for this node definition
						// pull out that child suggestion
						if (childSuggestions.size() > getChildNumber()) {
							String[] keys = new String[childSuggestions.size()];
							keys = childSuggestions.keySet().toArray(keys);
							final String childName = keys[getChildNumber()];
							final String childType = childSuggestions.get(childName);

							// set presentation
							presentation.setText("New child node: " + childName + " (" + childType + ")");
							enabled = true;

							// set VNode maker
							getNewVNode = new Computable<VNode>() {
								public VNode compute() {
									return new VNode(childName, childType);
								}
							};
						}
					}
				}
			}
		}

		presentation.setVisible(enabled);
		presentation.setEnabled(enabled);
	}

	@Override
	public VNode getNode() {
		return getNewVNode.compute();
	}
}
