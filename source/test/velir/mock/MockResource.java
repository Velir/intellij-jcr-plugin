/*
 * Copyright (c) 2013 RWJF.
 */
package velir.mock;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.sling.api.adapter.Adaptable;
import org.apache.sling.api.adapter.AdapterFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;

/**
 * MockResource -
 *
 * @author Sebastien Bernard
 * @version $Id: MockResource.java 7537 2014-01-22 23:37:39Z ChristopherL $
 */
public class MockResource implements Resource, Adaptable {
	private ValueMap valueMap;
	private List<Resource> children;
	private String name;
	private AdapterFactory adapter;

	private final static MockResource EMPTY_RESOURCE = new MockResource(Collections.<String, Object>emptyMap(), null, null);

	private MockResource(Map<String, Object> properties, List<Resource> children, String name) {
		this.valueMap = new ValueMapDecorator(properties);
		this.children = children == null ? Collections.<Resource>emptyList() : children;
		this.name = name;
	}

	public static final Resource newMockResource(Map<String, Object> properties) {
		return properties == null ? EMPTY_RESOURCE : new MockResource(properties, null, null);
	}

	public static final Resource emptyResource() {
		return EMPTY_RESOURCE;
	}

	public static final Resource newMockResource(String name) {
		return name == null ? EMPTY_RESOURCE : new MockResource(Collections.<String, Object>emptyMap(), null, name);
	}

	public static final Resource newMockResource(Map<String, Object> properties, List<Resource> children) {
		return properties == null && children == null ? EMPTY_RESOURCE : new MockResource(properties, children, null);
	}

	public static final Resource newMockResource(String name, Map<String, Object> properties, List<Resource> children) {
		return name == null && properties == null && children == null ? EMPTY_RESOURCE : new MockResource(properties, children, name);
	}

	@Override
	public String getPath() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Resource getParent() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Iterator<Resource> listChildren() {
		return children.iterator();
	}

	@Override
	public Iterable<Resource> getChildren() {
		return children;
	}

	@Override
	public Resource getChild(String relPath) {
		String[] nodes = relPath.split("/");

		Resource child = getChildByName(children, nodes[0]);
		for (int i = 1; i < nodes.length; i++) {
			if (child == null) {
				break;
			}

			child = getChildByName(child.getChildren(), nodes[i]);
		}

		return child;
	}

	private static Resource getChildByName(Iterable<Resource> children, String nodename) {
		if (children == null) {
			return null;
		}

		for (Resource child : children) {
			if (nodename.equals(child.getName())) {
				return child;
			}
		}

		return null;
	}

	@Override
	public String getResourceType() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public String getResourceSuperType() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean isResourceType(String resourceType) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public ResourceMetadata getResourceMetadata() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public ResourceResolver getResourceResolver() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
		if (type == ValueMap.class) {
			return (AdapterType) valueMap;
		}

		if (adapter != null) {
			return adapter.getAdapter(this, type);
		}

		return null;
	}

	public AdapterFactory getAdapter() {
		return adapter;
	}

	public void setAdapter(AdapterFactory adapter) {
		this.adapter = adapter;
	}
}
