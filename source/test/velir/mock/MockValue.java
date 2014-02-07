package velir.mock;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import javax.jcr.Binary;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

/**
 * MockValue -
 *
 * @author Sam Griffin
 * @version $Id: MockValue.java 6941 2013-11-26 21:29:23Z KRasmussen $
 */
// TODO - make it work more like real JCR value... for instance, Binary/InputStream should be the same thing
// probably other automatic type conversions, especially for getString
@SuppressWarnings("deprecation")
public class MockValue implements Value {
	private String string;
	private InputStream inputStream;
	private Binary binary;
	private long aLong;
	private double aDouble;
	private BigDecimal bigDecimal;
	private Calendar calendar;
	private boolean aBoolean;
	private int type;

	public MockValue (String string) {
		this.string = string;
		type = PropertyType.STRING;
	}

	public MockValue(InputStream inputStream) {
		this.inputStream = inputStream;
		type = PropertyType.BINARY;
	}

	public MockValue(Binary binary) {
		this.binary = binary;
		type = PropertyType.BINARY;
	}

	public MockValue(long aLong) {
		this.aLong = aLong;
		type = PropertyType.LONG;
	}

	public MockValue(double aDouble) {
		this.aDouble = aDouble;
		type = PropertyType.DOUBLE;
	}

	public MockValue(BigDecimal bigDecimal) {
		this.bigDecimal = bigDecimal;
		type = PropertyType.DECIMAL;
	}

	public MockValue(Calendar calendar) {
		this.calendar = calendar;
		type = PropertyType.DATE;
	}

	public MockValue(boolean aBoolean) {
		this.aBoolean = aBoolean;
		type = PropertyType.BOOLEAN;
	}

	@Override
	public String getString() throws ValueFormatException, IllegalStateException, RepositoryException {
		return string;
	}

	@Override
	public InputStream getStream() throws RepositoryException {
		return inputStream;
	}

	@Override
	public Binary getBinary() throws RepositoryException {
		return binary;
	}

	@Override
	public long getLong() throws ValueFormatException, RepositoryException {
		return aLong;
	}

	@Override
	public double getDouble() throws ValueFormatException, RepositoryException {
		return aDouble;
	}

	@Override
	public BigDecimal getDecimal() throws ValueFormatException, RepositoryException {
		return bigDecimal;
	}

	@Override
	public Calendar getDate() throws ValueFormatException, RepositoryException {
		return calendar;
	}

	@Override
	public boolean getBoolean() throws ValueFormatException, RepositoryException {
		return aBoolean;
	}

	@Override
	public int getType() {
		return type;
	}
}
