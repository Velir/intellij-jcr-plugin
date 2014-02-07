package velir.intellij.cq5.jcr.process;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.intellij.openapi.vfs.VirtualFile;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import velir.intellij.cq5.config.JCRConfiguration;
import velir.mock.MockSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * JCRPushProcessTest -
 *
 * @author Sam Griffin
 * @version $Id$
 */
@RunWith(MockitoJUnitRunner.class)
public class JCRPushProcessTest {

	@Mock
	VirtualFile virtualFile;

	@Mock
	JCRConfiguration jcrConfiguration;

	JCRPushProcess jcrPushProcess;

	@Before
	public void setup () {
		jcrPushProcess = new JCRPushProcess(virtualFile, jcrConfiguration);
	}

	private Node makeNode () throws RepositoryException {
		MockSession mockSession = new MockSession();
		return mockSession.getRootNode().addNode("/test/path");
	}

	private String inputStreamToString (InputStream inputStream) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = inputStream.read(buffer)) != -1)
		{
			byteArrayOutputStream.write(buffer, 0, bytesRead);
		}

		return byteArrayOutputStream.toString();
	}

	@Test
	public void saveFileUnderfolder () throws Exception {
		Node node = makeNode();

		when(virtualFile.isDirectory()).thenReturn(true);
		when(virtualFile.getPath()).thenReturn("/test/path");
		when(jcrConfiguration.getNodeCreative("/test/path", "nt:folder", "nt:folder")).thenReturn(node);

		VirtualFile child = mock(VirtualFile.class);
		VirtualFile[] children = { child };
		when(virtualFile.getChildren()).thenReturn(children);
		when(child.isDirectory()).thenReturn(false);
		when(child.getName()).thenReturn("test.txt");
		when(child.getInputStream()).thenReturn(new ByteArrayInputStream("test data".getBytes("UTF-8")));

		jcrPushProcess.run();
		assertTrue(node.hasNode("test.txt"));
		assertTrue(node.hasNode("test.txt/jcr:content"));
		assertTrue(node.getNode("test.txt").isNodeType("nt:file"));
		Node contentNode = node.getNode("test.txt/jcr:content");
		InputStream inputStream = contentNode.getProperty("jcr:data").getBinary().getStream();
		assertEquals("test data", inputStreamToString(inputStream));
	}

	@Test
	public void saveFileUnderFolderUnderFolder () throws Exception {
		Node node = makeNode();

		when(virtualFile.isDirectory()).thenReturn(true);
		when(virtualFile.getPath()).thenReturn("/test/path");
		when(jcrConfiguration.getNodeCreative("/test/path", "nt:folder", "nt:folder")).thenReturn(node);

		VirtualFile child = mock(VirtualFile.class);
		VirtualFile[] children = { child };
		when(virtualFile.getChildren()).thenReturn(children);
		when(child.getName()).thenReturn("one");
		when(child.isDirectory()).thenReturn(true);

		VirtualFile grandChild = mock(VirtualFile.class);
		VirtualFile[] grandChildren =  { grandChild };
		when(child.getChildren()).thenReturn(grandChildren);
		when(grandChild.getName()).thenReturn("test.txt");
		when(grandChild.getInputStream()).thenReturn(new ByteArrayInputStream("test data".getBytes("UTF-8")));

		jcrPushProcess.run();
		assertTrue(node.hasNode("one/test.txt/jcr:content"));
	}

	@Test
	public void saveFile () throws Exception {
		Node node = makeNode();

		VirtualFile parentFile = mock(VirtualFile.class);

		when(virtualFile.isDirectory()).thenReturn(false);
		when(virtualFile.getParent()).thenReturn(parentFile);
		when(parentFile.getPath()).thenReturn("/test/path");
		when(jcrConfiguration.getNodeCreative("/test/path", "nt:folder", "nt:folder")).thenReturn(node);
		when(virtualFile.getName()).thenReturn("test.txt");
		when(virtualFile.getInputStream()).thenReturn(new ByteArrayInputStream("test data".getBytes("UTF-8")));

		jcrPushProcess.run();
		assertTrue(node.hasNode("test.txt/jcr:content"));
	}

	private String getTestContentString () {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<jcr:root xmlns:jcr=\"http://www.jcp.org/jcr/1.0\"\n"
			+ "jcr:primaryType=\"testType\"\n"
			+ "testprop=\"testval\"/>";
	}

	@Test
	public void saveContentFileUnderFolder () throws Exception {
		Node node = makeNode();

		when(virtualFile.isDirectory()).thenReturn(true);
		when(virtualFile.getPath()).thenReturn("/test/path");
		when(jcrConfiguration.getNodeCreative("/test/path", "nt:folder", "testType")).thenReturn(node);

		VirtualFile contentFile = mock(VirtualFile.class);
		when(virtualFile.findChild(".content.xml")).thenReturn(contentFile);
		when(contentFile.getInputStream()).thenReturn(new ByteArrayInputStream(getTestContentString().getBytes("UTF-8")));
		when(virtualFile.getChildren()).thenReturn(new VirtualFile[0]);

		jcrPushProcess.run();
		//assertTrue(node.isNodeType("testType"));
		assertEquals("testval", node.getProperty("testprop").getString());
	}

}
