package velir.intellij.cq5.jcr.process;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import javax.jcr.Binary;
import javax.jcr.Node;
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

	private void importR (Node parent, VirtualFile virtualFile) throws RepositoryException, IOException, JDOMException {
		if (virtualFile.isDirectory()) {
			VirtualFile contentFile = virtualFile.findChild(".content.xml");
			Node node;
			if (contentFile != null) {
				InputStream inputStream = contentFile.getInputStream();
				VNode vNode = VNode.makeVNode(inputStream);
				inputStream.close();
				node = parent.addNode(PsiUtils.unmungeNamespace(virtualFile.getName()), vNode.getType());
			} else {
				node = parent.addNode(virtualFile.getName(), "nt:folder");
			}

			// do children
			for (VirtualFile child : virtualFile.getChildren()) {
				importR(node, child);
			}

		} else {
			if ("text/xml".equals(getMimeType(virtualFile.getName()))) {
				InputStream inputStream = virtualFile.getInputStream();
				final Document document = JDOMUtil.loadDocument(inputStream);
				inputStream.close();
				final Element rootElement = document.getRootElement();

				// if this xml defines JCR content
				if ("jcr:root".equals(rootElement.getQualifiedName())) {
					VNode vNode = VNode.makeVNode(rootElement);
					Node unpackDestination;

					// this content applies to the parent node, if it's .content.xml
					if (".content.xml".equals(virtualFile.getName())) {
						unpackDestination = parent;
					}

					// this content is for a node that should be named after this file (E.G. dialog.xml -> dialog)
					else {
						String name = PsiUtils.unmungeNamespace(virtualFile.getName().split("\\.")[0]);
						unpackDestination = parent.addNode(name, vNode.getType());
					}

					// set properties and then recursively unpack
					setProperties(unpackDestination, vNode);
					unpackRecursively(rootElement, unpackDestination);
				}

				// this is just a regular xml file, not a jcr content file. Import in the standard manner
				else {
					importFile(parent, virtualFile);
				}
			}

			// this isn't a special file, import in the standard manner
			else {
				importFile(parent, virtualFile);
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
		InputStream inputStream = file.getInputStream();
		Binary binary = valueFactory.createBinary(inputStream);
		inputStream.close();

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
			Node rootNode = jcrConfiguration.getOrCreateParentNode(target.getPath(), "nt:folder");

			// handle content nodes specially... simply treat import as happening at one level above
			if (".content.xml".equals(target.getName())) {
				Node parent = rootNode.getParent();
				rootNode.remove();
				importR(parent, target.getParent());
			}

			// if this is a jcr content node, we have to remove it here before we try adding it again
			else if ("text/xml".equals(getMimeType(target.getName()))) {
				String name = PsiUtils.unmungeNamespace(target.getName().split("\\.")[0]);
				if (rootNode.hasNode(name)) {
					rootNode.getNode(name).remove();
					rootNode.getSession().save();
				}
				importR(rootNode, target);
			}

			// normal import
			else {
				importR(rootNode, target);
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
