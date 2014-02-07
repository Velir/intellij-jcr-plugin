package velir.mock;

import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;

/**
 * MockNodeDefinition -
 *
 * @author Sam Griffin
 * @version $Id: MockNodeDefinition.java 6941 2013-11-26 21:29:23Z KRasmussen $
 */
public class MockNodeDefinition implements NodeDefinition {
	private NodeType declaringNodeType;
	private NodeType primaryNodeType;
	private String name;

	public MockNodeDefinition(NodeType declaringNodeType, NodeType primaryNodeType, String name) {
		this.declaringNodeType = declaringNodeType;
		this.primaryNodeType = primaryNodeType;
		this.name = name;
	}

	@Override
	public NodeType getDeclaringNodeType() {
		return declaringNodeType;
	}

	@Override
	public NodeType[] getRequiredPrimaryTypes() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public String[] getRequiredPrimaryTypeNames() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public NodeType getDefaultPrimaryType() {
		return primaryNodeType;
	}

	@Override
	public String getDefaultPrimaryTypeName() {
		return primaryNodeType.getName();
	}

	@Override
	public boolean allowsSameNameSiblings() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isAutoCreated() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public boolean isMandatory() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public int getOnParentVersion() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public boolean isProtected() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}
}
