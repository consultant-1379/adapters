package com.ericsson.eniq.etl.asn1;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

/**
 * ASN.1 parser handler for Ericsson AXD301 nodes.
 * 
 * @author eheitur
 * 
 */
public class AXDHandler {

	// Default buffer size in bytes for reading the input file. Can be set with
	// setBuffer(size).
	int bufferSize = 100000;

	// BER: Universal class tags
	final static int TAG_EOC = 0;
	final static int TAG_BOOLEAN = 1;
	final static int TAG_INTEGER = 2;
	final static int TAG_BIT_STRING = 3;
	final static int TAG_OCTET_STRING = 4;
	final static int TAG_NULL = 5;
	final static int TAG_OBJECT_IDENTIFIER = 6;
	final static int TAG_OBJECT_DESCRIPTOR = 7;
	final static int TAG_EXTERNAL = 8;
	final static int TAG_REAL = 9;
	final static int TAG_ENUMERATED = 10;
	final static int TAG_EMBEDDED_PDV = 11;
	final static int TAG_UTF8_STRING = 12;
	final static int TAG_RELATIVE_OID = 13;
	final static int TAG_SEQUENCE = 16;
	final static int TAG_SEQUENCE_OF = 16;
	final static int TAG_SET = 17;
	final static int TAG_SET_OF = 17;
	final static int TAG_NUMERIC_STRING = 18;
	final static int TAG_PRINTABL_STRING = 19;
	final static int TAG_T61_STRING = 20;
	final static int TAG_VIDEOTEX_STRING = 21;
	final static int TAG_IA5_STRING = 22;
	final static int TAG_UTC_TIME = 23;
	final static int TAG_GENERALIZED_TIME = 24;
	final static int TAG_GRAPHIC_STRING = 25;
	final static int TAG_VISIBLE_STRING = 26;
	final static int TAG_GENERAL_STRING = 27;
	final static int TAG_UNIVERSAL_STRING = 27;
	final static int TAG_CHARACTER_STRING = 29;
	final static int TAG_BMPSTRING = 30;

	// BER: Class
	final static int CLASS_UNIVERSAL = 1;
	final static int CLASS_APPLICATION = 2;
	final static int CLASS_CONTEXSPESIFIC = 3;
	final static int CLASS_PRIVATE = 4;

	// BER: Primitive/constructed
	final static int CONSTRUCTED = 1;
	final static int PRIMITIVE = 2;

	// Other constants
	final static int END = 0;
	final static int SHORTDEFINEDLENGTH = 1;
	final static int INDEFINEDLENGTH = 2;
	final static int LONGDEFINEDLENGTH = 3;

	private final static String HEADER_RULES = "BEGIN\n"
			+ "Header::=SEQUENCE {\n" + "version INTEGER,\n"
			// + "sender IpAddress,\n"
			+ "sender OCTET STRING,\n" + "nodeType IA5String,\n"
			+ "measType IA5String,\n" + "counters SEQUENCE OF CounterData}\n"
			// + "IpAddress::=OCTET STRING (SIZE(4))\n"
			// + "IpAddress::=IA5String\n"
			+ "CounterData::=OBJECT IDENTIFIER\n" + "END";

	private final static String PDR_RULES = "BEGIN\n" + "Pdr::=SEQUENCE {\n"
			+ "timeStamp INTEGER,\n" + "measurementName IA5String,\n"
			+ "instance IA5String,\n" + "status INTEGER,\n"
			+ "valuelist SEQUENCE OF ValueData}\n" + "ValueData::=INTEGER\n"
			+ "END";

	/**
	 * The file input stream from the input file.
	 */
	private FileInputStream fis;

	/**
	 * The data input stream from the file input stream.
	 */
	private DataInputStream dis;

	/**
	 * Holds the next byte from the input stream.
	 */
	protected byte nextByte = -1;

	/**
	 * End of file flag when reading the input data
	 */
	private boolean eof = false;

	/**
	 * A stack for holding the pending sequence ends.
	 */
	List<Integer> sequenceEnds = new ArrayList<Integer>();

	/**
	 * Offset for the byte data read from the input stream.
	 */
	private int offset = 0;

	/**
	 * The current byte in the data
	 */
	private int bytePointer = 0;

	/**
	 * A byte buffer for storing the data from the input stream.
	 */
	private byte[] bytes;

	/**
	 * Tracking the number of remaining bytes in the buffer
	 */
	private int bytesFromFile = 0;

	/**
	 * The handler for AXD.
	 */
	private ASN1 asn1Parser = null;

	/**
	 * ASN.1 Rule Handler
	 */
	private ASN1RuleHandler asn1Rules = null;

	/**
	 * Constructor.
	 * 
	 * @param asn1
	 *            the asn.1 parser object
	 */
	public AXDHandler(ASN1 asn1) {
		asn1Parser = asn1;
	}

	/**
	 * Constructor.
	 */
	public AXDHandler() {

	}

	/**
	 * @return the offset
	 */
	private int getOffset() {
		return offset;
	}

	/**
	 * Gets the current "next" byte from the byte buffer. Iteration in the
	 * buffer is made with next().
	 * 
	 * @return the "next" byte
	 */
	private byte getDec() {
		return nextByte;
	}

	/**
	 * Reads the next buffer of bytes from the data stream.
	 * 
	 * @throws Exception
	 */
	private void next() throws Exception {

		if (bytesFromFile == 0) {

			bytes = new byte[bufferSize];

			try {
				int bytesRead = -1;

				if (dis.available() > 0) {

					bytesRead = dis.read(bytes, 0, bufferSize);
				}
				if (bytesRead == -1) {

					eof = true;
					offset++;

				} else {

					bytesFromFile = bytesRead;
					bytePointer = 0;

					nextByte = bytes[bytePointer];
					bytePointer++;
					bytesFromFile--;

					if (offset > 0) {
						offset++;
					}

				}
			} catch (Exception e) {
				throw new Exception(e);
			}

		} else {

			nextByte = bytes[bytePointer];
			bytePointer++;
			bytesFromFile--;
			offset++;
		}
	}

	/**
	 * @return the form of the tag
	 */
	private int tagForm() {

		// bitmask 0010000
		final int i = getDec() & 32;
		if (i == 0) {
			return PRIMITIVE;
		} else {
			return CONSTRUCTED;
		}
	}

	/**
	 * Returns the BER Type Class of the "next byte": (Universal (1),
	 * Application (2), Context Specific (3), Private (4)).
	 * 
	 * @return type class
	 */
	private int tagClass() {

		if (((getDec() & 128) == 0) && ((getDec() & 64) == 0)) {
			return CLASS_UNIVERSAL;
		} else if (((getDec() & 128) == 0) && ((getDec() & 64) != 0)) {
			return CLASS_APPLICATION;
		} else if (((getDec() & 128) != 0) && ((getDec() & 64) == 0)) {
			return CLASS_CONTEXSPESIFIC;
		} else {
			return CLASS_PRIVATE;
		}
	}

	/**
	 * @param tagForm
	 * @return
	 */
	private int lengthFormat(final int tagForm) {

		if (tagForm == PRIMITIVE && !((getDec() & 128) != 0)) {
			return SHORTDEFINEDLENGTH;
		} else if (tagForm == PRIMITIVE && ((getDec() & 128) != 0)) {
			return LONGDEFINEDLENGTH;
		} else if (tagForm == CONSTRUCTED && ((getDec() & 128) != 0)) {

			// bit mask 01111111
			final int i = getDec() & 127;
			if (i == 0) {
				return INDEFINEDLENGTH;
			} else {
				return LONGDEFINEDLENGTH;
			}
		} else {
			return SHORTDEFINEDLENGTH;
		}
	}

	/**
	 * @param lengthFormat
	 * @return
	 * @throws Exception
	 */
	private int getLenght(final int lengthFormat) throws Exception {

		if (lengthFormat == INDEFINEDLENGTH) {
			return -1;
		} else if (lengthFormat == LONGDEFINEDLENGTH) {

			int v = 0;
			// bit mask 01111111
			final int lenghtOfLength = getDec() & 127;
			for (int i = lenghtOfLength - 1; i >= 0; i--) {
				next();
				v += (getDec() & 255) << (i * 8);
			}

			return v;

		} else if (lengthFormat == SHORTDEFINEDLENGTH) {
			return getDec() & 127;
		} else {
			throw new Exception("Invalid length format encountered.");
		}
	}

	/**
	 * @return tagId of the next byte
	 */
	private int tagID() {

		// bitmask 00011111
		return getDec() & 31;
	}

	/**
	 * @throws Exception
	 */
	public void parse() throws Exception {

		// First set the rules to match the header. The rules will later be set
		// to match the PDR when the header has been parsed.
		this.setRules(HEADER_RULES);

		// A flag for parsing the header or PDRs. Will be set to true when
		// header has been parsed.
		boolean headerParsed = false;

		// A flag for PDR rules being set. Will be set to true when the header
		// has been parsed and the new PDR rules have been set.
		boolean pdrRulesSet = false;

		// Read the next byte buffer from the input stream.
		next();

		while (true) {

			// Handle all pending sequence ends
			while (!sequenceEnds.isEmpty()
					&& ((Integer) sequenceEnds.get(0)).intValue() == getOffset()) {

				// Pop the next sequence end and execute it
				sequenceEnds.remove(0);
				String endSequence = asn1Rules.endSequence();
				asn1Parser.seqEnd(tagID(), endSequence);

				// Check if this was the end of the header sequence (i.e. end of
				// the counters sequence). If yes, then mark the header as
				// parsed.
				// if (endSequence.equalsIgnoreCase("CounterData")) {
				// headerParsed = true;
				// log.fine("Header parsed.");
				// }

				// Check if this was the end of a PDR sequence. If yes, then
				// mark the PDR rules as not set. This will cause the rules to
				// be set again and the next PDR parsed correctly.
				//
				// NOTE: The rule handler does not return the ending of the
				// Header or the PDR sequence correctly (with the name). Now we
				// use the MAIN definition to identify ending of either the
				// header or the PDR.
				//
				// if (endSequence.equalsIgnoreCase("Pdr")) {
				if (endSequence.equalsIgnoreCase("MAIN")) {
					headerParsed = true;
					pdrRulesSet = false;
				}
			}

			// Check if the header parsing has ended and PDR rules are marked as
			// not set. If yes, the update the rule set to the PDR rules.
			if (headerParsed && !pdrRulesSet) {
				this.setRules(PDR_RULES);
				pdrRulesSet = true;
			}

			// Check for the end of the file. If found, then stop parsing.
			if (eof) {
				break;
			}

			// Get the tag form (Primitive/Constructed) for the next tag
			final int tagForm = tagForm();

			// Handle the next tag based on the type class, type tagId, and tag
			// form.
			if (tagID() == TAG_EOC && tagClass() == CLASS_UNIVERSAL) {
				// Class: Universal
				// Form: Primitive
				// TagId: End of Contents (EOC)

				next();
				asn1Parser.seqEnd(tagID(), asn1Rules.endSequence());
				next();
				continue;

			} else if (tagForm() == PRIMITIVE
					&& tagClass() == CLASS_CONTEXSPESIFIC) {
				// Class: Context-specific
				// Form: Primitive
				// TagId: -

				final int tagid = tagID();

				next();

				final int length = getDec();

				byte[] result = new byte[length];
				for (int ii = 0; ii < length; ii++) {
					next();
					result[ii] = getDec();
				}

				asn1Parser.primitive(tagid, "N/A", result, asn1Rules
						.primitive(tagid));
				next();
				continue;

			} else if (tagForm() == CONSTRUCTED
					&& tagClass() == CLASS_CONTEXSPESIFIC) {
				// Class: Context-specific
				// Form: Constructed
				// TagId: -

				// SEQUENCE START
				final int tagid = tagID();
				next();
				final int length = getLenght(lengthFormat(tagForm));

				if (length > 0) {
					sequenceEnds.add(new Integer(getOffset() + length + 1));
					Collections.sort(sequenceEnds);
				}

				// if empty sequence (=0) do not trigger seqStart
				// if (getDec() != 0) {
				if (length != 0) {
					asn1Parser.seqStart(tagid, length, asn1Rules
							.startSequence());
				}

				next();
				continue;

			} else if (tagID() == TAG_SEQUENCE) {
				// Class: Universal
				// Form: Constructed
				// TagId: SEQUENCE

				// SEQUENCE START
				final int tagid = tagID();
				next();
				final int length = getLenght(lengthFormat(tagForm));

				if (length > 0) {
					sequenceEnds.add(new Integer(getOffset() + length + 1));
					Collections.sort(sequenceEnds);
				}
				asn1Parser.seqStart(tagid, length, asn1Rules.startSequence());
				next();
				continue;

			} else {
				// Class: -
				// Form: -
				// TagId: -

				final int tagid = tagID();
				next();

				final int length = (int) getDec();

				byte[] result = new byte[length];
				for (int ii = 0; ii < length; ii++) {
					next();
					result[ii] = getDec();
				}

				asn1Parser.primitive(tagid, "N/A", result, asn1Rules
						.primitive(tagid));
				next();
				continue;
			}
		}
	}

	/**
	 * Initializes the handler. Sets the file input and data input streams.
	 * 
	 * @param f
	 *            the file input stream
	 * @throws Exception
	 */
	public void init(final InputStream f) throws Exception {
		fis = (FileInputStream) f;
		dis = new DataInputStream(fis);
	}

	/**
	 * Initializes the handler. Sets the file input and data input streams.
	 * 
	 * @param file
	 *            the file
	 * @throws Exception
	 */
	public void init(final File file) throws Exception {
		fis = new FileInputStream(file);
		dis = new DataInputStream(fis);
	}

	/**
	 * Creates a new ASN.1 Rule Handler and sets the rules for it.
	 * 
	 * @param file
	 * @throws Exception
	 */
	public void setRules(final File file) throws Exception {
		asn1Rules = new ASN1RuleHandler();
		asn1Rules.createRules(file);
	}

	/**
	 * Creates a new ASN.1 Rule Handler and sets the rules for it.
	 * 
	 * @param rules
	 * @throws Exception
	 */
	public void setRules(final String rules) throws Exception {
		asn1Rules = new ASN1RuleHandler();
		asn1Rules.createRules(rules);
	}

	/**
	 * Modifies the buffer size for reading the input file
	 * 
	 * @param buffer
	 *            size of the buffer in bytes.
	 * @throws Exception
	 */
	public void setBuffer(final int buffer) throws Exception {
		this.bufferSize = buffer;
	}

	/**
	 * @param rule
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public String doData(final ASN1Rule rule, final byte[] data)
			throws Exception {
		if (rule.type.equalsIgnoreCase("BOOLEAN")) {
			return "" + doBoolean(data);
		} else if (rule.type.equalsIgnoreCase("INTEGER")) {
			return "" + doInt(data);
		} else if (rule.type.equalsIgnoreCase("REAL")) {
			return "" + doReal(data);
		} else if (rule.type.equalsIgnoreCase("PrintableString")) {
			return doString(rule, data);
		} else if (rule.type.equalsIgnoreCase("GraphicString")) {
			return doString(rule, data);
		} else if (rule.type.equalsIgnoreCase("UTF8String")) {
			return doString(rule, data);
		} else if (rule.type.equalsIgnoreCase("IA5String")) {
			return doString(rule, data);
		} else if (rule.type.equalsIgnoreCase("OBJECT IDENTIFIER")) {
			return doOid(data);
		} else {
			return doString(rule, data);
		}
	}

	/**
	 * @param data
	 * @return
	 */
	public double doReal(final byte[] data) {

		final byte[] bytes = (byte[]) data;

		int sign = bytes[0] & 64;
		if (sign == 0) {
			sign = 1;
		} else {
			sign = -1;
		}

		int base = bytes[0] & 48;
		if (base == 0) {
			base = 2;
		} else if (base == 16) {
			base = 8;
		} else if (base == 32) {
			base = 16;
		}

		final int scaleFactor = bytes[0] & 12;
		int exponentLength = bytes[0] & 3;
		int start = 1;

		if (exponentLength == 0) {
			exponentLength = 1;
		} else if (exponentLength == 1) {
			exponentLength = 2;
		} else if (exponentLength == 2) {
			exponentLength = 1;
		} else if (exponentLength == 3) {
			exponentLength = bytes[2];
			start = 2;
		}

		int exponent = 0;
		int ii = (exponentLength - 1) * 8;
		for (int i = start; i < start + exponentLength; i++) {
			exponent = (bytes[i] << ii);
			ii -= 8;
		}

		long mantissa = 0;

		for (int i = start + 1; i < bytes.length; i++) {
			mantissa = (mantissa << 8) | (bytes[i] & 255);
		}

		return sign * mantissa * Math.pow(2, scaleFactor)
				* Math.pow(base, exponent);
	}

	/**
	 * @param data
	 * @return
	 */
	public int doInt(final byte[] data) {

		final byte[] bytes = (byte[]) data;
		int r = 0;
		for (int i = 0; i < bytes.length; i++) {
			r += ((bytes[i] & 0xff) << (8 * ((bytes.length - 1) - i)));
		}

		return r;
	}

	/**
	 * @param data
	 * @return
	 */
	public boolean doBoolean(final byte[] data) {

		final byte[] bytes = (byte[]) data;

		if (bytes[0] == 0) {
			return false;
		}
		return true;

	}

	/**
	 * @param rule
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public String doString(final ASN1Rule rule, final byte[] data)
			throws Exception {

		if (rule.type.equalsIgnoreCase("UTF8String")) {

			final byte[] bytes = (byte[]) data;
			return new String(bytes, 0, bytes.length, "UTF8");

		} else if (rule.type.equalsIgnoreCase("OCTET STRING")) {
			return HexBin.encode(data);
		} else {

			final byte[] bytes = (byte[]) data;
			return new String(bytes, 0, bytes.length);

		}

	}

	/**
	 * Reads the byte data and returns an Object Identifier as a string. The
	 * spaces in the identifier will be replaced by dots. Note: There is a
	 * special handling for the first byte, since only two bits are taken from
	 * the byte.
	 * 
	 * @param data
	 * @return the OID as a dotted string
	 * @throws Exception
	 */
	public String doOid(final byte[] oid) throws Exception {

		String retValue = "";

		for (int i = 0; i < oid.length; i++) {

			// Get the value. Note: there is a special handling for the first
			// byte, since it defines the two first numbers. Note2: If the
			// number is >= 128, then the number occupies two bytes instead of
			// just one.
			if (i == 0) {
				int b = oid[0] % 40;
				int a = (oid[0] - b) / 40;
				retValue = a + "." + b;

			} else {
				if ((oid[i] & 0xff) < 128) {
					retValue = retValue + oid[i];
				}

				else {
					retValue = retValue
							+ ((((oid[i] & 0xff) - 128) * 128) + (oid[i + 1] & 0xff));
					i++;
				}
			}

			// Fill in the dot
			if (i < oid.length - 1) {
				retValue = retValue + ".";
			}
		}

		return retValue;
	}

}
