package velir.mock;

import javax.jcr.Value;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.jcr.nodetype.PropertyDefinition;

import com.google.common.base.Objects;

/**
 * MockNodeType -
 *
 * @author Sam Griffin
 * @version $Id: MockNodeType.java 6941 2013-11-26 21:29:23Z KRasmussen $
 */
@SuppressWarnings("deprecation")
public class MockNodeType implements NodeType {
	private String name;

	public MockNodeType(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public NodeType[] getSupertypes() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public NodeType[] getDeclaredSupertypes() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public NodeTypeIterator getSubtypes() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public NodeTypeIterator getDeclaredSubtypes() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public boolean isNodeType(String s) {
		return Objects.equal(s, name);
	}

	@Override
	public PropertyDefinition[] getPropertyDefinitions() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public NodeDefinition[] getChildNodeDefinitions() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public boolean canSetProperty(String s, Value value) {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public boolean canSetProperty(String s, Value[] values) {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public boolean canAddChildNode(String s) {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public boolean canAddChildNode(String s, String s1) {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public boolean canRemoveItem(String s) {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public boolean canRemoveNode(String s) {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public boolean canRemoveProperty(String s) {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public String[] getDeclaredSupertypeNames() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public boolean isAbstract() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public boolean isMixin() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public boolean hasOrderableChildNodes() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public boolean isQueryable() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public String getPrimaryItemName() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public PropertyDefinition[] getDeclaredPropertyDefinitions() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public NodeDefinition[] getDeclaredChildNodeDefinitions() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}
}
