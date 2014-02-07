package velir.mock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.ValueFormatException;

/**
 * MockValueFactory -
 *
 * @author Sam Griffin
 * @version $Id$
 */
public class MockValueFactory implements ValueFactory {
	@Override
	public Value createValue(final String s) {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public Value createValue(final String s, final int i) throws ValueFormatException {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public Value createValue(final long l) {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public Value createValue(final double v) {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public Value createValue(final BigDecimal bigDecimal) {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public Value createValue(final boolean b) {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public Value createValue(final Calendar calendar) {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public Value createValue(final InputStream inputStream) {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public Value createValue(final Binary binary) {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public Value createValue(final Node node) throws RepositoryException {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public Value createValue(final Node node, final boolean b) throws RepositoryException {
		throw new UnsupportedOperationException(MockNode.NOT_IMPLEMENTED);
	}

	@Override
	public Binary createBinary(final InputStream inputStream) throws RepositoryException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int bytesRead;
		try {
			while ((bytesRead = inputStream.read(buffer)) != -1)
			{
				byteArrayOutputStream.write(buffer, 0, bytesRead);
			}

			return new MockBinary(byteArrayOutputStream.toString());
		} catch (IOException e) {
			throw new RepositoryException(e);
		}
	}
}
