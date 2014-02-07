package velir.mock;

import java.security.Principal;
import java.util.Iterator;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;

/**
 * MockAuthorizable -
 *
 * @author Mary Matthews :3
 * @version $Id: MockAuthorizable.java 7200 2013-12-19 21:25:26Z mmatthews $
 */
public class MockAuthorizable implements Authorizable, Group {
	@Override
	public String getID() throws RepositoryException {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean isGroup() {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Principal getPrincipal() throws RepositoryException {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Iterator<org.apache.jackrabbit.api.security.user.Group> declaredMemberOf() throws RepositoryException {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Iterator<org.apache.jackrabbit.api.security.user.Group> memberOf() throws RepositoryException {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void remove() throws RepositoryException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Iterator<String> getPropertyNames() throws RepositoryException {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Iterator<String> getPropertyNames(String s) throws RepositoryException {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean hasProperty(String s) throws RepositoryException {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void setProperty(String s, Value value) throws RepositoryException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void setProperty(String s, Value[] values) throws RepositoryException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Value[] getProperty(String s) throws RepositoryException {
		return new Value[0];  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean removeProperty(String s) throws RepositoryException {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public String getPath() throws UnsupportedRepositoryOperationException, RepositoryException {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Iterator<Authorizable> getDeclaredMembers() throws RepositoryException {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Iterator<Authorizable> getMembers() throws RepositoryException {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean isDeclaredMember(Authorizable authorizable) throws RepositoryException {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean isMember(Authorizable authorizable) throws RepositoryException {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean addMember(Authorizable authorizable) throws RepositoryException {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean removeMember(Authorizable authorizable) throws RepositoryException {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}
}
	
