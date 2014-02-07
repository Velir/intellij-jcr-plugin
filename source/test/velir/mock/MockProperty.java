package velir.mock;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import javax.jcr.AccessDeniedException;
import javax.jcr.Binary;
import javax.jcr.InvalidItemStateException;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.ItemVisitor;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.PropertyDefinition;
import javax.jcr.version.VersionException;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * MockProperty -
 *
 * @author Sam Griffin
 * @version $Id: MockProperty.java 7660 2014-01-30 15:39:54Z sgriffin $
 */
//TODO make this operate more like a real Property, for instance have a clear divide between single/multiple properties
@SuppressWarnings("deprecation")
public class MockProperty implements Property {
	private String name;
	private Node parent;
	private List<Value> values;
	private PropertyDefinition propertyDefinition;

	public MockProperty(PropertyDefinition propertyDefinition, String name, Node parent, List<Value> values) {
		this.propertyDefinition = propertyDefinition;
		this.name = name;
		this.parent = parent;
		this.values = values;
	}

	@Override
	public String getPath() throws RepositoryException {
		return parent.getPath() + "/" + name;
	}

	@Override
	public void setValue(Value value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		values = Lists.newArrayList(value);
	}

	@Override
	public void setValue(Value[] values) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		this.values = Lists.newArrayList(values);
	}

	@Override
	public void setValue(String s) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		setValue(new MockValue(s));
	}

	@Override
	public void setValue(String[] strings) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		List<String> strings1 = Lists.newArrayList(strings);
		values = Lists.transform(strings1, new Function<String, Value>() {
			@Override
			public Value apply(String s) {
				return new MockValue(s);
			}
		});
	}

	@Override
	public void setValue(InputStream inputStream) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		setValue(new MockValue(inputStream));
	}

	@Override
	public void setValue(Binary binary) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		setValue(new MockValue(binary));
	}

	@Override
	public void setValue(long l) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		setValue(new MockValue(l));
	}

	@Override
	public void setValue(double v) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		setValue(new MockValue(v));
	}

	@Override
	public void setValue(BigDecimal bigDecimal) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		setValue(new MockValue(bigDecimal));
	}

	@Override
	public void setValue(Calendar calendar) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		setValue(new MockValue(calendar));
	}

	@Override
	public void setValue(boolean b) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		setValue(new MockValue(b));
	}

	@Override
	public void setValue(Node node) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		//TODO: implement
		throw new ValueFormatException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public Value getValue() throws ValueFormatException, RepositoryException {
		return values.get(0);
	}

	@Override
	public Value[] getValues() throws ValueFormatException, RepositoryException {
		return values.toArray(new Value[values.size()]);
	}

	@Override
	public String getString() throws ValueFormatException, RepositoryException {
		return getValue().getString();
	}

	@Override
	public InputStream getStream() throws ValueFormatException, RepositoryException {
		return getValue().getStream();
	}

	@Override
	public Binary getBinary() throws ValueFormatException, RepositoryException {
		return getValue().getBinary();
	}

	@Override
	public long getLong() throws ValueFormatException, RepositoryException {
		return getValue().getLong();
	}

	@Override
	public double getDouble() throws ValueFormatException, RepositoryException {
		return getValue().getDouble();
	}

	@Override
	public BigDecimal getDecimal() throws ValueFormatException, RepositoryException {
		return getValue().getDecimal();
	}

	@Override
	public Calendar getDate() throws ValueFormatException, RepositoryException {
		return getValue().getDate();
	}

	@Override
	public boolean getBoolean() throws ValueFormatException, RepositoryException {
		return getValue().getBoolean();
	}

	@Override
	public Node getNode() throws ItemNotFoundException, ValueFormatException, RepositoryException {
		throw new ItemNotFoundException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public Property getProperty() throws ItemNotFoundException, ValueFormatException, RepositoryException {
		throw new ValueFormatException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public long getLength() throws ValueFormatException, RepositoryException {
		throw new ValueFormatException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public long[] getLengths() throws ValueFormatException, RepositoryException {
		throw new ValueFormatException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public PropertyDefinition getDefinition() throws RepositoryException {
		return propertyDefinition;
	}

	@Override
	public int getType() throws RepositoryException {
		return getValue().getType();
	}

	@Override
	public boolean isMultiple() throws RepositoryException {
		return values.size() > 1;
	}

	@Override
	public String getName() throws RepositoryException {
		return name;
	}

	@Override
	public Item getAncestor(int i) throws ItemNotFoundException, AccessDeniedException, RepositoryException {
		throw new ItemExistsException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public Node getParent() throws ItemNotFoundException, AccessDeniedException, RepositoryException {
		return parent;
	}

	@Override
	public int getDepth() throws RepositoryException {
		throw new RepositoryException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public Session getSession() throws RepositoryException {
		return parent.getSession();
	}

	@Override
	public boolean isNode() {
		return false;
	}

	@Override
	public boolean isNew() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public boolean isModified() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public boolean isSame(Item item) throws RepositoryException {
		return getPath().equals(item.getPath());
	}

	@Override
	public void accept(ItemVisitor itemVisitor) throws RepositoryException {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
		//TODO: implement
	}

	@Override
	public void save() throws AccessDeniedException, ItemExistsException, ConstraintViolationException, InvalidItemStateException, ReferentialIntegrityException, VersionException, LockException, NoSuchNodeTypeException, RepositoryException {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
		//TODO: implement
	}

	@Override
	public void refresh(boolean b) throws InvalidItemStateException, RepositoryException {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
		//TODO: implement
	}

	@Override
	public void remove() throws VersionException, LockException, ConstraintViolationException, AccessDeniedException, RepositoryException {
		MockNode parent = (MockNode) getParent();
		parent.removeProperty(name);
	}
}
