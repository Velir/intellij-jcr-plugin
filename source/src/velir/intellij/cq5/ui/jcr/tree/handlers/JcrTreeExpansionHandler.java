package velir.intellij.cq5.ui.jcr.tree.handlers;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import velir.intellij.cq5.jcr.Connection;
import velir.intellij.cq5.ui.jcr.tree.JcrTree;
import velir.intellij.cq5.ui.jcr.tree.JcrTreeNode;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * Class used to handle tree expansion and collapse events.
 */
public class JcrTreeExpansionHandler implements TreeExpansionListener {
	/**
	 * Event that will fire when the tree is expanded.
	 *
	 * @param evt TreeExpansionEvent containing our event objects.
	 */
	public void treeExpanded(TreeExpansionEvent evt) {
		//get the path that was expanded.
		TreePath path = evt.getPath();

		//get the last node of the path.  this is the node we need to populate
		JcrTreeNode node = (JcrTreeNode) path.getLastPathComponent();

		//declare our session.
		Session session = null;
		try {
			//get our project
			Project project = ProjectManager.getInstance().getDefaultProject();

			//get a session to our repository.
			Connection connection = Connection.getInstance(project);
			session = connection.getSession();

			//populate our children and if any were added update our tree.
			if (node.populateChildren(true, session)) {
				//get our jcr tree
				JcrTree tree = (JcrTree) evt.getSource();

				//update our node structure.
				((DefaultTreeModel) tree.getModel()).nodeStructureChanged(node);
			}
		} catch (RepositoryException rex) {
			//TODO: Log exception.
		} finally {
			if (session != null) {
				session.logout();
			}
		}
	}

	/**
	 * Event that will fire when the tree is collapsed.
	 *
	 * @param evt TreeExpansionEvent containing our event objects.
	 */
	public void treeCollapsed(TreeExpansionEvent evt) {
		//do nothing
	}
}
