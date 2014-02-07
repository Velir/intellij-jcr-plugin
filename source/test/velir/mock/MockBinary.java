package velir.mock;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import javax.jcr.Binary;
import javax.jcr.RepositoryException;

/**
 * MockBinary -
 *
 * @author Sam Griffin
 * @version $Id$
 */
public class MockBinary implements Binary {
	private final String data;

	public MockBinary(final String data) {
		this.data = data;
	}

	@Override
	public InputStream getStream() throws RepositoryException {
		try {
			return new ByteArrayInputStream(data.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public int read(final byte[] bytes, final long l) throws IOException, RepositoryException {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public long getSize() throws RepositoryException {
		return data.getBytes().length;
	}

	@Override
	public void dispose() {
	}
}
