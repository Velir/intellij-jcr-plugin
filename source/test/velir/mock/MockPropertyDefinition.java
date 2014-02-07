package velir.mock;

import javax.jcr.Value;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.PropertyDefinition;

/**
 * MockPropertyDefinition -
 *
 * @author Sam Griffin
 * @version $Id: MockPropertyDefinition.java 6941 2013-11-26 21:29:23Z KRasmussen $
 */
public class MockPropertyDefinition implements PropertyDefinition {

	private NodeType nodeType;
	private boolean autoCreated;
	private boolean protected_;

	public MockPropertyDefinition(NodeType nodeType, boolean autoCreated, boolean protected_) {
		this.nodeType = nodeType;
		this.autoCreated = autoCreated;
		this.protected_ = protected_;
	}

	public MockPropertyDefinition(NodeType nodeType) {
		this.nodeType = nodeType;
		autoCreated = false;
		protected_ = false;
	}

	@Override
	public NodeType getDeclaringNodeType() {
		return nodeType;
	}

	@Override
	public int getRequiredType() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public String[] getValueConstraints() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public Value[] getDefaultValues() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public boolean isMultiple() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public String[] getAvailableQueryOperators() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public boolean isFullTextSearchable() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public boolean isQueryOrderable() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public String getName() {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public boolean isAutoCreated() {
		return autoCreated;
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
		return protected_;
	}
}
