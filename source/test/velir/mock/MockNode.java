package velir.mock;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.jcr.AccessDeniedException;
import javax.jcr.Binary;
import javax.jcr.InvalidItemStateException;
import javax.jcr.InvalidLifecycleTransitionException;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.ItemVisitor;
import javax.jcr.MergeException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.PropertyDefinition;
import javax.jcr.version.ActivityViolationException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionHistory;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * MockNode -
 *
 * There is an official Sling mock (org.apache.sling.commons.testing.jcr.MockNode), but it is poorly done.
 * For instance:
 *
 * org.apache.sling.commons.testing.jcr.MockNode mockNode = new MockNode("whatever");
 * mockNode.setProperty("test", "whatevs");
 * mockNode.getProperty("test").remove();
 * mockNode.hasProperty("test"); // returns true, whyever not?
 *
 * @author Sam Griffin
 * @version $Id: MockNode.java 7660 2014-01-30 15:39:54Z sgriffin $
 */
@SuppressWarnings("deprecation")
public class MockNode implements Node {

	protected static final String NOT_IMPLEMENTED = "not implemented";

	private static final class MockNodeIterator implements NodeIterator {
		private Iterator<MockNode> iterator;
		private final long size;
		private long position;

		public MockNodeIterator (Collection<MockNode> iterable) {
			iterator = iterable.iterator();
			size = iterable.size();
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public Node nextNode() {
			position++;
			return iterator.next();
		}

		@Override
		public void skip(long l) {
			for (long i = 0; i < l; i++) {
				nextNode();
			}
		}

		@Override
		public long getSize() {
			return size;
		}

		@Override
		public long getPosition() {
			return  position;
		}

		@Override
		public Object next() {
			return nextNode();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException(NOT_IMPLEMENTED);
		}
	}
	private static final class MockPropertyIterator implements PropertyIterator {
		private Iterator<Property> iterator;
		private final long size;
		private long position;

		private MockPropertyIterator(Collection<Property> props) {
			iterator = props.iterator();
			size = props.size();
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public Property nextProperty() {
			position++;
			return iterator.next();
		}

		@Override
		public void skip(long l) {
			for (long i = 0; i < l; i++) {
				nextProperty();
			}
		}

		@Override
		public long getSize() {
			return size;
		}

		@Override
		public long getPosition() {
			return position;
		}

		@Override
		public Object next() {
			return nextProperty();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException(NOT_IMPLEMENTED);
		}
	}

	private MockSession session;
	private MockNode parent;
	private NodeType nodeType;
	private String name;
	private Map<String, MockNode> children;
	private Map<String,Property> properties;

	public MockNode(MockNode parent, NodeType nodeType, String name, Map<String,MockNode> children, Map<String,Property> properties) {
		this.parent = parent;
		this.nodeType = nodeType;
		this.name = name;
		this.children = children;
		this.properties = properties;
		try {
			this.session = (MockSession) parent.getSession();
		} catch (RepositoryException re) {
			throw new UnsupportedOperationException("whoops");
		}
	}

	public MockNode(MockNode parent, NodeType nodeType, String name) {
		this.parent = parent;
		this.name = name;
		this.nodeType = nodeType;
		this.children = Maps.newHashMap();
		this.properties = Maps.newHashMap();
		try {
			this.session = (MockSession) parent.getSession();
		} catch (RepositoryException re) {
			throw new UnsupportedOperationException("whoops");
		}
	}

	public MockNode(MockNode parent, String name) {
		this.parent = parent;
		this.name = name;
		this.nodeType = new MockNodeType("nt:unstructured");
		this.children = Maps.newHashMap();
		this.properties = Maps.newHashMap();
		try {
			this.session = (MockSession) parent.getSession();
		} catch (RepositoryException re) {
			throw new UnsupportedOperationException("whoops");
		}
	}

	// root node constructor
	public MockNode(MockSession session) {
		name = "/";
		this.nodeType = new MockNodeType("rootNode");
		this.children = Maps.newHashMap();
		this.properties = Maps.newHashMap();
		this.session = session;
	}

	@Override
	public String getPath() throws RepositoryException {
		return parent.getPath() + "/" + name;
	}

	@Override
	public Node addNode(String s) throws ItemExistsException, PathNotFoundException, VersionException, ConstraintViolationException, LockException, RepositoryException {
		if (s.contains("/")) {
			Pattern pattern = Pattern.compile("^([^/]*)/(.*)$");
			Matcher matcher = pattern.matcher(s);
			matcher.find();
			MockNode node = new MockNode(this, matcher.group(1));
			children.put(matcher.group(1), node);
			return node.addNode(matcher.group(2));
		}
		MockNode node = new MockNode(this, s);
		children.put(s, node);
		return node;
	}

	@Override
	public Node addNode(String s, String s1) throws ItemExistsException, PathNotFoundException, NoSuchNodeTypeException, LockException, VersionException, ConstraintViolationException, RepositoryException {
		if (s.contains("/")) {
			Pattern pattern = Pattern.compile("^([^/]*)/(.*)$");
			Matcher matcher = pattern.matcher(s);
			matcher.find();
			MockNode node = new MockNode(this, new MockNodeType(s1), matcher.group(1));
			children.put(matcher.group(1), node);
			return node.addNode(matcher.group(2), s1);
		}
		MockNode node = new MockNode(this, new MockNodeType(s1), s);
		children.put(s, node);
		return node;
	}

	@Override
	public void orderBefore(String s, String s1) throws UnsupportedRepositoryOperationException, VersionException, ConstraintViolationException, ItemNotFoundException, LockException, RepositoryException {
		throw new UnsupportedRepositoryOperationException("nope");
	}

	@Override
	public Property setProperty(String s, Value value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		PropertyDefinition propertyDefinition = new MockPropertyDefinition(nodeType);
		Property property = new MockProperty(propertyDefinition, s, this, Lists.newArrayList(value));
		properties.put(s, property);
		return property;
	}

	@Override
	public Property setProperty(String s, Value value, int i) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		throw new ValueFormatException(NOT_IMPLEMENTED);
	}

	@Override
	public Property setProperty(String s, Value[] values) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		PropertyDefinition propertyDefinition = new MockPropertyDefinition(nodeType);
		Property property = new MockProperty(propertyDefinition, s, this, Lists.newArrayList(values));
		properties.put(s, property);
		return property;
	}

	@Override
	public Property setProperty(String s, Value[] values, int i) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		throw new ValueFormatException(NOT_IMPLEMENTED);
	}

	@Override
	public Property setProperty(String s, String[] as) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		List<String> strings = Lists.newArrayList(as);
		List<Value> stringValues = Lists.transform(strings, new Function<String, Value>() {
			@Override
			public Value apply(String s) {
				return new MockValue(s);
			}
		});
		return setProperty(s, stringValues.toArray(new Value[stringValues.size()]));
	}

	@Override
	public Property setProperty(String s, String[] as, int i) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		throw new ValueFormatException(NOT_IMPLEMENTED);
	}

	@Override
	public Property setProperty(String s, String s1) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		return setProperty(s, new MockValue(s1));
	}

	@Override
	public Property setProperty(String s, String s1, int i) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		throw new ValueFormatException(NOT_IMPLEMENTED);
	}

	@Override
	public Property setProperty(String s, InputStream inputstream) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		return setProperty(s, new MockValue(inputstream));
	}

	@Override
	public Property setProperty(String s, Binary binary) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		return setProperty(s, new MockValue(binary));
	}

	@Override
	public Property setProperty(String s, boolean flag) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		return setProperty(s, new MockValue(flag));
	}

	@Override
	public Property setProperty(String s, double d) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		return setProperty(s, new MockValue(d));
	}

	@Override
	public Property setProperty(String s, BigDecimal bigdecimal) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		return setProperty(s, new MockValue(bigdecimal));
	}

	@Override
	public Property setProperty(String s, long l) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		return setProperty(s, new MockValue(l));
	}

	@Override
	public Property setProperty(String s, Calendar calendar) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		return setProperty(s, new MockValue(calendar));
	}

	@Override
	public Property setProperty(String s, Node node) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		throw new ValueFormatException(NOT_IMPLEMENTED);
	}

	@Override
	public Node getNode(String s) throws PathNotFoundException, RepositoryException {
		if (s.contains("/")) {
			Pattern pattern = Pattern.compile("^([^/]*)/(.*)$");
			Matcher matcher = pattern.matcher(s);
			matcher.find();
			return children.get(matcher.group(1)).getNode(matcher.group(2));
		}

		return children.get(s);
	}

	@Override
	public NodeIterator getNodes() throws RepositoryException {
		return new MockNodeIterator(children.values());
	}

	@Override
	public NodeIterator getNodes(String s) throws RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public NodeIterator getNodes(String[] as) throws RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public Property getProperty(String s) throws PathNotFoundException, RepositoryException {
		return properties.get(s);
	}

	@Override
	public PropertyIterator getProperties() throws RepositoryException {
		return new MockPropertyIterator(properties.values());
	}

	@Override
	public PropertyIterator getProperties(String s) throws RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public PropertyIterator getProperties(String[] as) throws RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public Item getPrimaryItem() throws ItemNotFoundException, RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public String getUUID() throws UnsupportedRepositoryOperationException, RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public String getIdentifier() throws RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public int getIndex() throws RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public PropertyIterator getReferences() throws RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public PropertyIterator getReferences(String s) throws RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public PropertyIterator getWeakReferences() throws RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public PropertyIterator getWeakReferences(String s) throws RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public boolean hasNode(String s) throws RepositoryException {
		if (s.contains("/")) {
			Pattern pattern = Pattern.compile("^([^/]*)/(.*)$");
			Matcher matcher = pattern.matcher(s);
			matcher.find();
			return children.containsKey(matcher.group(1)) && children.get(matcher.group(1)).hasNode(matcher.group(2));
		}

		return children.containsKey(s);
	}

	@Override
	public boolean hasProperty(String s) throws RepositoryException {
		return properties.containsKey(s);
	}

	@Override
	public boolean hasNodes() throws RepositoryException {
		return ! children.isEmpty();
	}

	@Override
	public boolean hasProperties() throws RepositoryException {
		return ! properties.isEmpty();
	}

	@Override
	public NodeType getPrimaryNodeType() throws RepositoryException {
		return nodeType;
	}

	@Override
	public NodeType[] getMixinNodeTypes() throws RepositoryException {
		return new NodeType[0];  //TODO: implement
	}

	@Override
	public boolean isNodeType(String s) throws RepositoryException {
		return s.equals(nodeType.getName());
	}

	@Override
	public void setPrimaryType(String s) throws NoSuchNodeTypeException, VersionException, ConstraintViolationException, LockException, RepositoryException {
		nodeType = new MockNodeType(s);
	}

	@Override
	public void addMixin(String s) throws NoSuchNodeTypeException, VersionException, ConstraintViolationException, LockException, RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public void removeMixin(String s) throws NoSuchNodeTypeException, VersionException, ConstraintViolationException, LockException, RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public boolean canAddMixin(String s) throws NoSuchNodeTypeException, RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public NodeDefinition getDefinition() throws RepositoryException {
		NodeType declaringNodeType = new MockNodeType("declaringNode");
		return new MockNodeDefinition(declaringNodeType, nodeType, "nodeDefinition");
	}

	@Override
	public Version checkin() throws VersionException, UnsupportedRepositoryOperationException, InvalidItemStateException, LockException, RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public void checkout() throws UnsupportedRepositoryOperationException, LockException, ActivityViolationException, RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public void doneMerge(Version version) throws VersionException, InvalidItemStateException, UnsupportedRepositoryOperationException, RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public void cancelMerge(Version version) throws VersionException, InvalidItemStateException, UnsupportedRepositoryOperationException, RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public void update(String s) throws NoSuchWorkspaceException, AccessDeniedException, LockException, InvalidItemStateException, RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public NodeIterator merge(String s, boolean flag) throws NoSuchWorkspaceException, AccessDeniedException, MergeException, LockException, InvalidItemStateException, RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public String getCorrespondingNodePath(String s) throws ItemNotFoundException, NoSuchWorkspaceException, AccessDeniedException, RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public NodeIterator getSharedSet() throws RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public void removeSharedSet() throws VersionException, LockException, ConstraintViolationException, RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public void removeShare() throws VersionException, LockException, ConstraintViolationException, RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public boolean isCheckedOut() throws RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public void restore(String s, boolean flag) throws VersionException, ItemExistsException, UnsupportedRepositoryOperationException, LockException, InvalidItemStateException, RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public void restore(Version version, boolean b) throws VersionException, ItemExistsException, InvalidItemStateException, UnsupportedRepositoryOperationException, LockException, RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public void restore(Version version, String s, boolean b) throws PathNotFoundException, ItemExistsException, VersionException, ConstraintViolationException, UnsupportedRepositoryOperationException, LockException, InvalidItemStateException, RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public void restoreByLabel(String s, boolean flag) throws VersionException, ItemExistsException, UnsupportedRepositoryOperationException, LockException, InvalidItemStateException, RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public VersionHistory getVersionHistory() throws UnsupportedRepositoryOperationException, RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public Version getBaseVersion() throws UnsupportedRepositoryOperationException, RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public Lock lock(boolean flag, boolean flag1) throws UnsupportedRepositoryOperationException, LockException, AccessDeniedException, InvalidItemStateException, RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public Lock getLock() throws UnsupportedRepositoryOperationException, LockException, AccessDeniedException, RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public void unlock() throws UnsupportedRepositoryOperationException, LockException, AccessDeniedException, InvalidItemStateException, RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public boolean holdsLock() throws RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public boolean isLocked() throws RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public void followLifecycleTransition(String s) throws UnsupportedRepositoryOperationException, InvalidLifecycleTransitionException, RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public String[] getAllowedLifecycleTransistions() throws UnsupportedRepositoryOperationException, RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public String getName() throws RepositoryException {
		return name;
	}

	@Override
	public Item getAncestor(int i) throws ItemNotFoundException, AccessDeniedException, RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public Node getParent() throws ItemNotFoundException, AccessDeniedException, RepositoryException {
		return parent;
	}

	@Override
	public int getDepth() throws RepositoryException {
		return parent.getDepth() + 1;
	}

	@Override
	public Session getSession() throws RepositoryException {
		return session;
	}

	@Override
	public boolean isNode() {
		return true;
	}

	@Override
	public boolean isNew() {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public boolean isModified() {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public boolean isSame(Item item) throws RepositoryException {
		return getPath().equals(item.getPath());
	}

	@Override
	public void accept(ItemVisitor itemVisitor) throws RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public void save() throws AccessDeniedException, ItemExistsException, ConstraintViolationException, InvalidItemStateException, ReferentialIntegrityException, VersionException, LockException, NoSuchNodeTypeException, RepositoryException {
	}

	@Override
	public void refresh(boolean b) throws InvalidItemStateException, RepositoryException {
		throw new RepositoryException(NOT_IMPLEMENTED);
	}

	@Override
	public void remove() throws VersionException, LockException, ConstraintViolationException, AccessDeniedException, RepositoryException {
		parent.children.remove(name);
	}

	public void removeProperty(String propertyName) {
		properties.remove(propertyName);
	}
}
