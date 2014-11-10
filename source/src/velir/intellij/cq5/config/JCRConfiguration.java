package velir.intellij.cq5.config;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.swing.*;

import com.day.cq.commons.jcr.JcrUtil;
import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.ProjectFacetManager;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.util.xmlb.annotations.Transient;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.jcr2dav.Jcr2davRepositoryFactory;
import org.jdom.Element;
import org.jetbrains.annotations.Nls;
import velir.intellij.cq5.facet.JCRFacet;
import velir.intellij.cq5.facet.JCRFacetType;
import velir.intellij.cq5.jcr.model.VNodeDefinition;

@State(
		name = "JCRConfiguration",
		storages = {
				@Storage(id = "default", file="$MODULE_FILE$")
		}
)
public class JCRConfiguration implements FacetConfiguration, PersistentStateComponent<JCRConfiguration.State> {
	public static final Logger log = com.intellij.openapi.diagnostic.Logger.getInstance(JCRConfiguration.class);

	public static class State {
		private static final String REPOSITORY_URL = "http://localhost:4502/crx/server";
		private static final String USERNAME = "admin";
		private static final String PASSWORD = "admin";
		private static final String WORKSPACE = "crx.default";

		public String url;
		public String username;
		public String password;
		public String workspace;
		public List<String> fileSystemMountPoints;
		public List<String> jcrMountPoints;

		public State () {
			url = REPOSITORY_URL;
			username = USERNAME;
			password = PASSWORD;
			workspace = WORKSPACE;
			fileSystemMountPoints = new LinkedList<String>();
			jcrMountPoints = new LinkedList<String>();
		}

		public State (State state) {
			url = state.url;
			username = state.username;
			password = state.password;
			workspace = state.workspace;
			setMountPoints(state.getMountPoints());
		}

		@Override
		public boolean equals(Object obj) {
			if (! (obj instanceof State)) return false;
			State state = (State) obj;

			// do simple properties
			boolean propsEqual = url.equals(state.url)
					&& username.equals(state.username)
					&& password.equals(state.password)
					&& workspace.equals(state.workspace);

			// get all mount points and compare them
			List<JCRMountPoint> myMountPoints = getMountPoints();
			List<JCRMountPoint> theirMountPoints = state.getMountPoints();
			if (myMountPoints.size() != theirMountPoints.size()) return false;
			for (int i = 0; i < myMountPoints.size(); i++) {
				propsEqual = propsEqual && myMountPoints.get(i).equals(theirMountPoints.get(i));
			}

			return propsEqual;
		}

		@Transient
		public List<JCRMountPoint> getMountPoints() {
			List<JCRMountPoint> mountPoints = new LinkedList<JCRMountPoint>();
			for (int i = 0; i < fileSystemMountPoints.size(); i++) {
				mountPoints.add(new JCRMountPoint(fileSystemMountPoints.get(i), jcrMountPoints.get(i)));
			}
			return mountPoints;
		}

		@Transient
		public void setMountPoints (List<JCRMountPoint> mountPoints) {
			fileSystemMountPoints = new LinkedList<String>();
			jcrMountPoints = new LinkedList<String>();
			for (JCRMountPoint jcrMountPoint : mountPoints) {
				fileSystemMountPoints.add(jcrMountPoint.getFileSystemMountPoint());
				jcrMountPoints.add(jcrMountPoint.getJcrNode());
			}
		}

		public void setConnectionSettings (State state) {

		}
	}

	private State state;

	public JCRConfiguration() {
		state = new State();
	}

	public JCRConfiguration(State state) {
		this.state = state;
	}

	/**
	 * gets the first configuration it can find, from any facet from any open project
	 */
	public static JCRConfiguration getAConfiguration () {

		JCRFacet jcrFacet = null;
		for (Project project : ProjectManager.getInstance().getOpenProjects()) {
			List<JCRFacet> facetList = ProjectFacetManager.getInstance(project).getFacets(JCRFacetType.JCR_TYPE_ID);
			if (facetList.size() > 0) return facetList.get(0).getJcrConfiguration();
		}

		// could not find a JCRFacet in any open projects
		return null;
	}

	public void processNewConnectionSettings () {
		try {
			VNodeDefinition.buildDefinitions(getSession());
		} catch (RepositoryException re) {
			log.warn("could not build node definitions", re);
		}
	}

	public State getState() {
		return state;
	}

	public void loadState(State state) {
		this.state = state;
	}

	public FacetEditorTab[] createEditorTabs(FacetEditorContext facetEditorContext, FacetValidatorsManager facetValidatorsManager) {
		return new FacetEditorTab[] {
			new FacetEditorTab() {
				JcrConnectionSettings JCRConnectionSettings = new JcrConnectionSettings(state);

				@Nls
				public String getDisplayName() {
					return "Connection";
				}

				public JComponent createComponent() {
					return JCRConnectionSettings.createComponent();
				}

				public boolean isModified() {
					return JCRConnectionSettings.isModified();
				}

				public void reset() {
					JCRConnectionSettings.reset();
				}

				public void apply() {
					if (JCRConnectionSettings.isModified()) {
						state = new State(JCRConnectionSettings.getState());
						processNewConnectionSettings();
					}
				}

				public void disposeUIResources() {
					JCRConnectionSettings.disposeUIResources();
				}
			},
			new FacetEditorTab() {
				JCRMountPointSettings jcrMountPointSettings = new JCRMountPointSettings(state.getMountPoints());

				@Nls
				public String getDisplayName() {
					return "Mount Points";
				}

				public JComponent createComponent() {
					return jcrMountPointSettings.createComponent();
				}

				public boolean isModified() {
					return jcrMountPointSettings.isModified();
				}

				public void reset() {
					jcrMountPointSettings.reset();
				}

				public void apply() {
					if (jcrMountPointSettings.isModified()) {
						state.setMountPoints(jcrMountPointSettings.getMountPoints());
					}
				}

				public void disposeUIResources() {
				}
			}
		};
	}

	@Deprecated
	public void readExternal(Element element) throws InvalidDataException {
		//deprecated, so will not implement
	}

	@Deprecated
	public void writeExternal(Element element) throws WriteExternalException {
		//deprecated, so will not implement
	}

	/**
	 * resets to default settings
	 */
	public void reset () {
		state = new State();
	}

	/**
	 * Will retrieve a repository factory for getting the crx repository.
	 *
	 * @return
	 */
	private RepositoryFactory getRepositoryFactory() {
		//return a new jcr2dav repository factory.
		return new Jcr2davRepositoryFactory();
	}

	/**
	 * Will return the crx repository.
	 *
	 * @return
	 */
	public Repository getRepository() throws RepositoryException {
		//get our repository factory
		RepositoryFactory factory = getRepositoryFactory();

		//create our parameters to pass into our factory
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(JcrUtils.REPOSITORY_URI, state.url);

		//get our repository from our factory
		return factory.getRepository(parameters);
	}

	/**
	 * Will return our credentials to login to the repository.
	 *
	 * @return
	 */
	private Credentials getCredentials() {
		return new SimpleCredentials(state.username, state.password.toCharArray());
	}

	/**
	 * Will return a session to the crx repository.
	 *
	 * @return
	 */
	public Session getSession() throws RepositoryException {
		//get our repository
		Repository rep = getRepository();

		// abort if we couldn't get a repository
		if (rep == null) throw new RepositoryException("Could not get repository (velir code)");

		//login to our repository and return our session
		return rep.login(getCredentials(), state.workspace);
	}

	/**
	 * get a mount point that contains the file system path, if there is one
	 * @param path
	 * @return
	 */
	public JCRMountPoint getMountPoint (String path) {
		for (JCRMountPoint jcrMountPoint : state.getMountPoints()) {
			if (jcrMountPoint.contains(path)) return jcrMountPoint;
		}

		return null;
	}

	public Node getNode (String path) throws RepositoryException {
		JCRMountPoint jcrMountPoint = getMountPoint(path);
		String jcrPath = jcrMountPoint.getJcrPath(path);
		return getSession().getNode(jcrPath);
	}


	/**
	 * if the node doesn't exist, create it. If it does exist, wipe it out. Return it regardless
	 * @param path
	 * @return
	 * @throws RepositoryException
	 */
	public Node getOrCreateParentNode(String path, String intermediateType) throws RepositoryException {
		JCRMountPoint jcrMountPoint = getMountPoint(path);
		String jcrPath = jcrMountPoint.getJcrPath(path);
		Session session = getSession();

		Node node = JcrUtil.createPath(jcrPath, intermediateType, session);
		Node parent = node.getParent();
		node.remove();
		session.save();
		return parent;
	}
}
