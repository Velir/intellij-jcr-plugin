package velir.mock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessControlException;
import javax.jcr.AccessDeniedException;
import javax.jcr.Credentials;
import javax.jcr.InvalidItemStateException;
import javax.jcr.InvalidSerializedDataException;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.LoginException;
import javax.jcr.NamespaceException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.ValueFactory;
import javax.jcr.Workspace;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.retention.RetentionManager;
import javax.jcr.security.AccessControlManager;
import javax.jcr.version.VersionException;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.principal.PrincipalManager;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * MockSession -
 *
 * @author Sam Griffin
 * @version $Id: MockSession.java 7200 2013-12-19 21:25:26Z mmatthews $
 */
@SuppressWarnings("deprecation")
public class MockSession implements Session, JackrabbitSession{
	private MockNode rootNode;

	public MockSession() {
		this.rootNode = new MockNode(this);
	}

	@Override
	public Repository getRepository() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public String getUserID() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public String[] getAttributeNames() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public Object getAttribute(String s) {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public Workspace getWorkspace() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public Node getRootNode() throws RepositoryException {
		return rootNode;
	}

	@Override
	public Session impersonate(Credentials credentials) throws LoginException, RepositoryException {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public Node getNodeByUUID(String s) throws ItemNotFoundException, RepositoryException {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public Node getNodeByIdentifier(String s) throws ItemNotFoundException, RepositoryException {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public Item getItem(String s) throws PathNotFoundException, RepositoryException {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public Node getNode(String s) throws PathNotFoundException, RepositoryException {
		s = s.replaceFirst("^/", "");
		Node current = rootNode;
		for (String nodeName : s.split("/")) {
			current = current.getNode(nodeName);
		}
		return current;
	}

	@Override
	public Property getProperty(String s) throws PathNotFoundException, RepositoryException {
		String nodePath = s.replaceFirst("/[^/]+$", "");
		String propertyName = s.replaceFirst("^.*/[^/]+$", "$1");
		return getNode(nodePath).getProperty(propertyName);
	}

	@Override
	public boolean itemExists(String s) throws RepositoryException {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public boolean nodeExists(String s) throws RepositoryException {
		Node n;
		try {
			n = getNode(s);
		} catch (Exception e) {
			return false;
		}
		return n != null;
	}

	@Override
	public boolean propertyExists(String s) throws RepositoryException {
		Property p;
		try {
			p = getProperty(s);
		} catch (Exception e) {
			return false;
		}
		return p != null;
	}

	@Override
	public void move(String s, String s2) throws ItemExistsException, PathNotFoundException, VersionException, ConstraintViolationException, LockException, RepositoryException {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public void removeItem(String s) throws VersionException, LockException, ConstraintViolationException, AccessDeniedException, RepositoryException {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public void save() throws AccessDeniedException, ItemExistsException, ReferentialIntegrityException, ConstraintViolationException, InvalidItemStateException, VersionException, LockException, NoSuchNodeTypeException, RepositoryException {
	}

	@Override
	public void refresh(boolean b) throws RepositoryException {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public boolean hasPendingChanges() throws RepositoryException {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public ValueFactory getValueFactory() throws UnsupportedRepositoryOperationException, RepositoryException {
		return new MockValueFactory();
	}

	@Override
	public boolean hasPermission(String s, String s2) throws RepositoryException {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public void checkPermission(String s, String s2) throws AccessControlException, RepositoryException {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public boolean hasCapability(String s, Object o, Object[] objects) throws RepositoryException {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public ContentHandler getImportContentHandler(String s, int i) throws PathNotFoundException, ConstraintViolationException, VersionException, LockException, RepositoryException {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public void importXML(String s, InputStream inputStream, int i) throws IOException, PathNotFoundException, ItemExistsException, ConstraintViolationException, VersionException, InvalidSerializedDataException, LockException, RepositoryException {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public void exportSystemView(String s, ContentHandler contentHandler, boolean b, boolean b2) throws PathNotFoundException, SAXException, RepositoryException {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public void exportSystemView(String s, OutputStream outputStream, boolean b, boolean b2) throws IOException, PathNotFoundException, RepositoryException {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
		//TODO: implement
	}

	@Override
	public void exportDocumentView(String s, ContentHandler contentHandler, boolean b, boolean b2) throws PathNotFoundException, SAXException, RepositoryException {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
		//TODO: implement
	}

	@Override
	public void exportDocumentView(String s, OutputStream outputStream, boolean b, boolean b2) throws IOException, PathNotFoundException, RepositoryException {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
		//TODO: implement
	}

	@Override
	public void setNamespacePrefix(String s, String s2) throws NamespaceException, RepositoryException {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
		//TODO: implement
	}

	@Override
	public String[] getNamespacePrefixes() throws RepositoryException {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public String getNamespaceURI(String s) throws NamespaceException, RepositoryException {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public String getNamespacePrefix(String s) throws NamespaceException, RepositoryException {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public void logout() {
		rootNode = null;
	}

	@Override
	public boolean isLive() {
		return rootNode != null;
	}

	@Override
	public void addLockToken(String s) {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
		//TODO: implement
	}

	@Override
	public String[] getLockTokens() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public void removeLockToken(String s) {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
		//TODO: implement
	}

	@Override
	public AccessControlManager getAccessControlManager() throws UnsupportedRepositoryOperationException, RepositoryException {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public RetentionManager getRetentionManager() throws UnsupportedRepositoryOperationException, RepositoryException {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public PrincipalManager getPrincipalManager() throws AccessDeniedException, UnsupportedRepositoryOperationException, RepositoryException {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public UserManager getUserManager() throws AccessDeniedException, UnsupportedRepositoryOperationException, RepositoryException {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}
}
