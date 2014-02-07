package velir.intellij.cq5.jcr.process;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import velir.intellij.cq5.actions.content.DestructiveImport;
import velir.intellij.cq5.config.JCRConfiguration;
import velir.intellij.cq5.jcr.model.AbstractProperty;
import velir.intellij.cq5.jcr.model.VNode;
import velir.intellij.cq5.jcr.model.VProperty;
import velir.intellij.cq5.util.PsiUtils;

/**
* JCRPushProcess -
*
* @author Sam Griffin
* @version $Id$
*/
public class JCRPushProcess implements Runnable {
	private static final Logger log = com.intellij.openapi.diagnostic.Logger.getInstance(JCRPushProcess.class);
	private final VirtualFile target;
	private final JCRConfiguration jcrConfiguration;

	public JCRPushProcess(final VirtualFile target, final JCRConfiguration jcrConfiguration) {
		this.target = target;
		this.jcrConfiguration = jcrConfiguration;
	}

	private void importR (Node node, VirtualFile directory) throws RepositoryException, IOException, JDOMException {
		// do directories
		for (VirtualFile child : directory.getChildren()) {
			if (child.isDirectory()) {
				VirtualFile contentFile = child.findChild(".content.xml");
				Node subNode = null;

				// if this is a typed node
				if (contentFile != null) {
					final Document document = JDOMUtil.loadDocument(contentFile.getInputStream());
					final Element rootElement = document.getRootElement();

					VNode vNode = VNode.makeVNode(rootElement);
					vNode.setName(child.getName());

					subNode = node.addNode(PsiUtils.unmungeNamespace(vNode.getName()), vNode.getType());
					setProperties(subNode, vNode);

					// recurse to handle packed xml files
					unpackRecursively(rootElement, subNode);
				}
				// if this is just a folder node
				else {
					subNode = node.addNode(child.getName(), "nt:folder");
				}

				importR(subNode, child);
			}
			else {
				// only do files that aren't property node definitions
				if (! ".content.xml".equals(child.getName())) {

					// see if this is a packed node definition, and try to handle it
					if ("text/xml".equals(getMimeType(child.getName()))) {
						final Document document = JDOMUtil.loadDocument(child.getInputStream());
						final Element rootElement = document.getRootElement();

						// if this is a packed xml definition, start recursively generating jcr nodes
						if ("jcr:root".equals(rootElement.getQualifiedName())) {
							final VNode rootNode = VNode.makeVNode(rootElement);

							// set root jcr node name by filename, rather than element name (since it's always jcr:root)
							String name = PsiUtils.unmungeNamespace(child.getName().split("\\.")[0]);
							final Node childNode = node.addNode(name, rootNode.getType());
							setProperties(childNode, rootNode);

							// start recursion
							unpackRecursively(rootElement, childNode);
						}

						// this is a regular file, import it as is
						else {
							importFile(node, child);
						}
					}

					// this is a regular file, import it as is
					else {
						importFile(node, child);
					}
				}
			}
		}
	}

	/**
	 * descend through xml elements, generating an equivalent jcr structure
	 * @param element - root xml element
	 * @param node    - root jcr node
	 */
	private void unpackRecursively (Element element, Node node) throws IOException, JDOMException, RepositoryException {

		for (Object o : element.getChildren()) {
			Element childElement = (Element) o;
			VNode vNode = VNode.makeVNode(childElement);
			Node childNode = node.addNode(vNode.getName(), vNode.getType());
			setProperties(childNode, vNode);

			unpackRecursively(childElement, childNode);
		}
	}

	// sets the properties in a node to the same as a vnode
	private void setProperties (Node node, VNode vNode) throws RepositoryException {
		ValueFactory valueFactory = node.getSession().getValueFactory();

		// handle mixins specially
		if (vNode.hasProperty(DestructiveImport.JCR_MIXIN_TYPES)) {
			String[] mixinNames = (String[]) vNode.getProperty(DestructiveImport.JCR_MIXIN_TYPES).getValue();
			for (String mixin : mixinNames) node.addMixin(mixin);
		}

		for (String propName : vNode.getSortedPropertyNames()) {

			// only copy non-ignored properties
			if (! ignoreProperty(propName)) {
				VProperty vProperty = vNode.getProperty(propName);

				// handle simple property cases
				if (AbstractProperty.STRING_PREFIX.equals(vProperty.getType())) {
					node.setProperty(propName, (String) vProperty.getValue());
				} else if (AbstractProperty.LONG_PREFIX.equals(vProperty.getType())) {
					node.setProperty(propName, (Long) vProperty.getValue());
				} else if (AbstractProperty.DOUBLE_PREFIX.equals(vProperty.getType())) {
					node.setProperty(propName, (Double) vProperty.getValue());
				} else if (AbstractProperty.BOOLEAN_PREFIX.equals(vProperty.getType())) {
					node.setProperty(propName, (Boolean) vProperty.getValue());
				} else if (AbstractProperty.DATE_PREFIX.equals(vProperty.getType())) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime((Date) vProperty.getValue());
					node.setProperty(propName, calendar);
				} else if (AbstractProperty.STRING_ARRAY_PREFIX.equals(vProperty.getType())) {
					node.setProperty(propName, (String[]) vProperty.getValue());
				} else {
					// set a property to get a property to set it's value
					Property property = node.setProperty(propName, "");

					if (AbstractProperty.LONG_ARRAY_PREFIX.equals(vProperty.getType())) {
						Long[] ls = (Long[]) vProperty.getValue();
						Value[] values = new Value[ls.length];
						for (int i = 0; i < ls.length; i++) {
							values[i] = valueFactory.createValue(ls[i]);
						}
						node.setProperty(propName, values);
					} else if (AbstractProperty.DOUBLE_ARRAY_PREFIX.equals(vProperty.getType())) {
						Double[] ds = (Double[]) vProperty.getValue();
						Value[] values = new Value[ds.length];
						for (int i = 0; i < ds.length; i++) {
							values[i] = valueFactory.createValue(ds[i]);
						}
						node.setProperty(propName, values);
					} else if (AbstractProperty.BOOLEAN_ARRAY_PREFIX.equals(vProperty.getType())) {
						Boolean[] bs = (Boolean[]) vProperty.getValue();
						Value[] values = new Value[bs.length];
						for (int i = 0; i < bs.length; i++) {
							values[i] = valueFactory.createValue(bs[i]);
						}
						node.setProperty(propName, values);
					} else if (AbstractProperty.DATE_ARRAY_PREFIX.equals(vProperty.getType())) {
						Date[] ds = (Date[]) vProperty.getValue();
						Value[] values = new Value[ds.length];
						Calendar calendar = Calendar.getInstance();
						for (int i = 0; i < ds.length; i++) {
							calendar.setTime((Date) ds[i]);
							values[i] = valueFactory.createValue(calendar);
						}
						node.setProperty(propName, values);
					} else {
						log.error("bad property type for " + propName);
					}
				}
			}
		}
	}

	private boolean ignoreProperty (String name) {
		return  AbstractProperty.JCR_PRIMARYTYPE.equals(name)
			|| DestructiveImport.JCR_MIXIN_TYPES.equals(name)
			|| "jcr:created".equals(name)
			|| "jcr:createdBy".equals(name);
	}

	private void importFile (Node node, VirtualFile file) throws RepositoryException, IOException {
		ValueFactory valueFactory = node.getSession().getValueFactory();
		Binary binary = valueFactory.createBinary(file.getInputStream());

		Node fileNode = node.hasNode(file.getName()) ? node.getNode(file.getName()) : node.addNode(file.getName(), "nt:file");
		Node contentNode = fileNode.hasNode("jcr:content") ? fileNode.getNode("jcr:content") : fileNode.addNode("jcr:content", "nt:resource");
		contentNode.setProperty("jcr:mimeType", getMimeType(file.getName()));
		contentNode.setProperty("jcr:data", binary);
	}

	private String getMimeType (String fileName) {
		String[] dotParts = fileName.split("\\.");
		if (dotParts.length == 1) return "text/plain";
		String endPart = dotParts[dotParts.length - 1].toLowerCase();
		if ("jpg".equals(endPart)) return "image/jpeg";
		if ("ico".equals(endPart)) return "image/vnd.microsoft.icon";
		if ("gif".equals(endPart)) return "image/gif";
		if ("png".equals(endPart)) return "image/png";
		if ("jsp".equals(endPart)) return "text/plain";
		if ("css".equals(endPart)) return "text/css";
		if ("xml".equals(endPart)) return "text/xml";
		if ("js".equals(endPart)) return "application/x-javascript";

		// default
		return "text/plain";
	}

	@Override
	public void run() {
		try {
			Node rootNode = null;
			if (target.isDirectory()) {
				// if this node is a "typed" node, get the VNode version of it
				VNode vNode = null;
				Element rootElement = null;
				VirtualFile contentFile = target.findChild(".content.xml");
				if (contentFile != null) {
					rootElement = JDOMUtil.loadDocument(contentFile.getInputStream()).getRootElement();
					vNode = VNode.makeVNode(rootElement);
				}

				// get/create rootNode
				String nodeType = vNode == null ? "nt:folder" : vNode.getType();
				rootNode = jcrConfiguration.getNodeCreative(target.getPath(),
					"nt:folder", nodeType);

				// wipe out existing nodes
				NodeIterator nodeIterator = rootNode.getNodes();
				while (nodeIterator.hasNext()) {
					Node node = nodeIterator.nextNode();
					node.remove();
				}

				// TODO : remove existing properties from rootNode

				// set root node properties, if it has them
				if (vNode != null) {
					setProperties(rootNode, vNode);
					unpackRecursively(rootElement, rootNode);
				}

				// start import to jcr
				importR(rootNode, target);

			} else {
				rootNode = jcrConfiguration.getNodeCreative(target.getParent().getPath(),
					"nt:folder", "nt:folder");
				importFile(rootNode, target);
			}
			rootNode.getSession().save();

		} catch (RepositoryException re) {
			log.error("could not import nodes to jcr", re);
		} catch (IOException ioe) {
			log.error("could not import nodes to jcr", ioe);
		} catch (JDOMException jde) {
			log.error("could not import nodes to jcr", jde);
		}
	}
}
