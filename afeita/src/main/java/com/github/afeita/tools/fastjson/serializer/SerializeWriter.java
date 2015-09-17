/*
 * Copyright 1999-2101 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.afeita.tools.fastjson.serializer;

import static com.github.afeita.tools.fastjson.parser.CharTypes.replaceChars;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.charset.Charset;

import com.github.afeita.tools.fastjson.JSON;
import com.github.afeita.tools.fastjson.parser.CharTypes;
import com.github.afeita.tools.fastjson.util.IOUtils;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public final class SerializeWriter extends Writer {

	/**
	 * The buffer where data is stored.
	 */
	protected char buf[];

	/**
	 * The number of chars in the buffer.
	 */
	protected int count;

	private final static ThreadLocal<char[]> bufLocal = new ThreadLocal<char[]>();

	private int features;

	public SerializeWriter() {
		this.features = JSON.DEFAULT_GENERATE_FEATURE;

		buf = bufLocal.get(); // new char[1024];
		if (buf == null) {
			buf = new char[1024];
		} else {
			bufLocal.set(null);
		}
	}

	/**
	 * Creates a new CharArrayWriter.
	 */
	public SerializeWriter(SerializerFeature... features) {
		buf = bufLocal.get(); // new char[1024];
		if (buf == null) {
			buf = new char[1024];
		} else {
			bufLocal.set(null);
		}

		int featuresValue = 0;
		for (SerializerFeature feature : features) {
			featuresValue |= feature.getMask();
		}
		this.features = featuresValue;
	}

	/**
	 * Creates a new CharArrayWriter with the specified initial size.
	 * 
	 * @param initialSize
	 *            an int specifying the initial buffer size.
	 * @exception IllegalArgumentException
	 *                if initialSize is negative
	 */
	public SerializeWriter(int initialSize) {
		if (initialSize <= 0) {
			throw new IllegalArgumentException("Negative initial size: "
					+ initialSize);
		}
		buf = new char[initialSize];
	}

	public void config(SerializerFeature feature, boolean state) {
		if (state) {
			features |= feature.getMask();
		} else {
			features &= ~feature.getMask();
		}
	}

	public boolean isEnabled(SerializerFeature feature) {
		return SerializerFeature.isEnabled(this.features, feature);
	}

	/**
	 * Writes a character to the buffer.
	 */
	@Override
	public void write(int c) {
		int newcount = count + 1;
		if (newcount > buf.length) {
			expandCapacity(newcount);
		}
		buf[count] = (char) c;
		count = newcount;
	}

	public void write(char c) {
		int newcount = count + 1;
		if (newcount > buf.length) {
			expandCapacity(newcount);
		}
		buf[count] = c;
		count = newcount;
	}

	/**
	 * Writes characters to the buffer.
	 * 
	 * @param c
	 *            the data to be written
	 * @param off
	 *            the start offset in the data
	 * @param len
	 *            the number of chars that are written
	 */
	@Override
	public void write(char c[], int off, int len) {
		if (off < 0 || off > c.length || len < 0 || off + len > c.length
				|| off + len < 0) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return;
		}

		int newcount = count + len;
		if (newcount > buf.length) {
			expandCapacity(newcount);
		}
		System.arraycopy(c, off, buf, count, len);
		count = newcount;

	}

	public void expandCapacity(int minimumCapacity) {
		int newCapacity = (buf.length * 3) / 2 + 1;

		if (newCapacity < minimumCapacity) {
			newCapacity = minimumCapacity;
		}
		char newValue[] = new char[newCapacity];
		System.arraycopy(buf, 0, newValue, 0, count);
		buf = newValue;
	}

	/**
	 * Write a portion of a string to the buffer.
	 * 
	 * @param str
	 *            String to be written from
	 * @param off
	 *            Offset from which to start reading characters
	 * @param len
	 *            Number of characters to be written
	 */
	@Override
	public void write(String str, int off, int len) {
		int newcount = count + len;
		if (newcount > buf.length) {
			expandCapacity(newcount);
		}
		str.getChars(off, off + len, buf, count);
		count = newcount;
	}

	/**
	 * Writes the contents of the buffer to another character stream.
	 * 
	 * @param out
	 *            the output stream to write to
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public void writeTo(Writer out) throws IOException {
		out.write(buf, 0, count);
	}

	public void writeTo(OutputStream out, String charset) throws IOException {
		byte[] bytes = new String(buf, 0, count).getBytes(charset);
		out.write(bytes);
	}

	@Override
	public SerializeWriter append(CharSequence csq) {
		String s = (csq == null ? "null" : csq.toString());
		write(s, 0, s.length());
		return this;
	}

	@Override
	public SerializeWriter append(CharSequence csq, int start, int end) {
		String s = (csq == null ? "null" : csq).subSequence(start, end)
				.toString();
		write(s, 0, s.length());
		return this;
	}

	/**
	 * Appends the specified character to this writer.
	 * <p>
	 * An invocation of this method of the form <tt>out.append(c)</tt> behaves
	 * in exactly the same way as the invocation
	 * 
	 * <pre>
	 * out.write(c)
	 * </pre>
	 * 
	 * @param c
	 *            The 16-bit character to append
	 * @return This writer
	 * @since 1.5
	 */
	@Override
	public SerializeWriter append(char c) {
		write(c);
		return this;
	}

	/**
	 * Resets the buffer so that you can use it again without throwing away the
	 * already allocated buffer.
	 */
	public void reset() {
		count = 0;
	}

	/**
	 * Returns a copy of the input data.
	 * 
	 * @return an array of chars copied from the input data.
	 */
	public char[] toCharArray() {
		char[] newValue = new char[count];
		System.arraycopy(buf, 0, newValue, 0, count);
		return newValue;
	}

	public byte[] toBytes(String charsetName) {
		if (charsetName == null) {
			charsetName = "UTF-8";
		}

		Charset cs = Charset.forName(charsetName);
		SerialWriterStringEncoder encoder = new SerialWriterStringEncoder(cs);

		return encoder.encode(buf, 0, count);
	}

	/**
	 * Returns the current size of the buffer.
	 * 
	 * @return an int representing the current size of the buffer.
	 */
	public int size() {
		return count;
	}

	/**
	 * Converts input data to a string.
	 * 
	 * @return the string.
	 */
	@Override
	public String toString() {
		return new String(buf, 0, count);
	}

	/**
	 * Flush the stream.
	 */
	@Override
	public void flush() {
	}

	/**
	 * Close the stream. This method does not release the buffer, since its
	 * contents might still be required. Note: Invoking this method in this
	 * class will have no effect.
	 */
	@Override
	public void close() {
		bufLocal.set(buf);
	}

	public void writeBooleanArray(boolean[] array) throws IOException {
		int[] sizeArray = new int[array.length];
		int totalSize = 2;
		for (int i = 0; i < array.length; ++i) {
			if (i != 0) {
				totalSize++;
			}
			boolean val = array[i];
			int size;
			if (val) {
				size = 4; // "true".length();
			} else {
				size = 5; // "false".length();
			}
			sizeArray[i] = size;
			totalSize += size;
		}

		int newcount = count + totalSize;
		if (newcount > buf.length) {
			expandCapacity(newcount);
		}

		buf[count] = '[';

		int currentSize = count + 1;
		for (int i = 0; i < array.length; ++i) {
			if (i != 0) {
				buf[currentSize++] = ',';
			}

			boolean val = array[i];
			if (val) {
				// System.arraycopy("true".toCharArray(), 0, buf, currentSize,
				// 4);
				buf[currentSize++] = 't';
				buf[currentSize++] = 'r';
				buf[currentSize++] = 'u';
				buf[currentSize++] = 'e';
			} else {
				buf[currentSize++] = 'f';
				buf[currentSize++] = 'a';
				buf[currentSize++] = 'l';
				buf[currentSize++] = 's';
				buf[currentSize++] = 'e';
			}
		}
		buf[currentSize] = ']';

		count = newcount;
	}

	@Override
	public void write(String text) {
		if (text == null) {
			writeNull();
			return;
		}

		int length = text.length();
		int newcount = count + length;
		if (newcount > buf.length) {
			expandCapacity(newcount);
		}
		text.getChars(0, length, buf, count);
		count = newcount;
		return;

	}

	public void writeInt(int i) {
		if (i == Integer.MIN_VALUE) {
			write("-2147483648");
			return;
		}

		int size = (i < 0) ? IOUtils.stringSize(-i) + 1 : IOUtils.stringSize(i);

		int newcount = count + size;
		if (newcount > buf.length) {
			expandCapacity(newcount);
		}

		IOUtils.getChars(i, newcount, buf);

		count = newcount;
	}

	public void writeShortArray(short[] array) throws IOException {
		int[] sizeArray = new int[array.length];
		int totalSize = 2;
		for (int i = 0; i < array.length; ++i) {
			if (i != 0) {
				totalSize++;
			}
			short val = array[i];
			int size = IOUtils.stringSize(val);
			sizeArray[i] = size;
			totalSize += size;
		}

		int newcount = count + totalSize;
		if (newcount > buf.length) {
			expandCapacity(newcount);
		}

		buf[count] = '[';

		int currentSize = count + 1;
		for (int i = 0; i < array.length; ++i) {
			if (i != 0) {
				buf[currentSize++] = ',';
			}

			short val = array[i];
			currentSize += sizeArray[i];
			IOUtils.getChars(val, currentSize, buf);
		}
		buf[currentSize] = ']';

		count = newcount;
	}

	public void writeByteArray(byte[] array) {
		int[] sizeArray = new int[array.length];
		int totalSize = 2;
		for (int i = 0; i < array.length; ++i) {
			if (i != 0) {
				totalSize++;
			}
			byte val = array[i];
			int size = IOUtils.stringSize(val);
			sizeArray[i] = size;
			totalSize += size;
		}

		int newcount = count + totalSize;
		if (newcount > buf.length) {
			expandCapacity(newcount);
		}

		buf[count] = '[';

		int currentSize = count + 1;
		for (int i = 0; i < array.length; ++i) {
			if (i != 0) {
				buf[currentSize++] = ',';
			}

			byte val = array[i];
			currentSize += sizeArray[i];
			IOUtils.getChars(val, currentSize, buf);
		}
		buf[currentSize] = ']';

		count = newcount;
	}

	public void writeIntArray(int[] array) {
		int[] sizeArray = new int[array.length];
		int totalSize = 2;
		for (int i = 0; i < array.length; ++i) {
			if (i != 0) {
				totalSize++;
			}
			int val = array[i];
			int size;
			if (val == Integer.MIN_VALUE) {
				size = "-2147483648".length();
			} else {
				size = (val < 0) ? IOUtils.stringSize(-val) + 1 : IOUtils
						.stringSize(val);
			}
			sizeArray[i] = size;
			totalSize += size;
		}

		int newcount = count + totalSize;
		if (newcount > buf.length) {
			expandCapacity(newcount);
		}

		buf[count] = '[';

		int currentSize = count + 1;
		for (int i = 0; i < array.length; ++i) {
			if (i != 0) {
				buf[currentSize++] = ',';
			}

			int val = array[i];
			if (val == Integer.MIN_VALUE) {
				System.arraycopy("-2147483648".toCharArray(), 0, buf,
						currentSize, sizeArray[i]);
				currentSize += sizeArray[i];
			} else {
				currentSize += sizeArray[i];
				IOUtils.getChars(val, currentSize, buf);
			}
		}
		buf[currentSize] = ']';

		count = newcount;
	}

	public void writeIntAndChar(int i, char c) {
		if (i == Integer.MIN_VALUE) {
			write("-2147483648");
			write(c);
			return;
		}

		int size = (i < 0) ? IOUtils.stringSize(-i) + 1 : IOUtils.stringSize(i);

		int newcount0 = count + size;
		int newcount1 = newcount0 + 1;

		if (newcount1 > buf.length) {
			expandCapacity(newcount1);
		}

		IOUtils.getChars(i, newcount0, buf);
		buf[newcount0] = c;

		count = newcount1;
	}

	public void writeLongAndChar(long i, char c) throws IOException {
		if (i == Long.MIN_VALUE) {
			write("-9223372036854775808");
			write(c);
			return;
		}

		int size = (i < 0) ? IOUtils.stringSize(-i) + 1 : IOUtils.stringSize(i);

		int newcount0 = count + size;
		int newcount1 = newcount0 + 1;

		if (newcount1 > buf.length) {
			expandCapacity(newcount1);
		}

		IOUtils.getChars(i, newcount0, buf);
		buf[newcount0] = c;

		count = newcount1;
	}

	public void writeLong(long i) {
		if (i == Long.MIN_VALUE) {
			write("-9223372036854775808");
			return;
		}

		int size = (i < 0) ? IOUtils.stringSize(-i) + 1 : IOUtils.stringSize(i);

		int newcount = count + size;
		if (newcount > buf.length) {
			expandCapacity(newcount);
		}

		IOUtils.getChars(i, newcount, buf);

		count = newcount;
	}

	public void writeNull() {
		int newcount = count + 4;
		if (newcount > buf.length) {
			expandCapacity(newcount);
		}
		buf[count] = 'n';
		buf[count + 1] = 'u';
		buf[count + 2] = 'l';
		buf[count + 3] = 'l';
		count = newcount;
	}

	public void writeLongArray(long[] array) {
		int[] sizeArray = new int[array.length];
		int totalSize = 2;
		for (int i = 0; i < array.length; ++i) {
			if (i != 0) {
				totalSize++;
			}
			long val = array[i];
			int size;
			if (val == Long.MIN_VALUE) {
				size = "-9223372036854775808".length();
			} else {
				size = (val < 0) ? IOUtils.stringSize(-val) + 1 : IOUtils
						.stringSize(val);
			}
			sizeArray[i] = size;
			totalSize += size;
		}

		int newcount = count + totalSize;
		if (newcount > buf.length) {
			expandCapacity(newcount);
		}

		buf[count] = '[';

		int currentSize = count + 1;
		for (int i = 0; i < array.length; ++i) {
			if (i != 0) {
				buf[currentSize++] = ',';
			}

			long val = array[i];
			if (val == Long.MIN_VALUE) {
				System.arraycopy("-9223372036854775808".toCharArray(), 0, buf,
						currentSize, sizeArray[i]);
				currentSize += sizeArray[i];
			} else {
				currentSize += sizeArray[i];
				IOUtils.getChars(val, currentSize, buf);
			}
		}
		buf[currentSize] = ']';

		count = newcount;
	}

	private void writeStringWithDoubleQuote(String text) {
		// final boolean[] specicalFlags_doubleQuotes =
		// CharTypes.specicalFlags_doubleQuotes;
		// final int len_flags = specicalFlags_doubleQuotes.length;

		if (text == null) {
			writeNull();
			return;
		}

		int len = text.length();
		int newcount = count + len + 2;
		if (newcount > buf.length) {
			expandCapacity(newcount);
		}

		int start = count + 1;
		int end = start + len;

		buf[count] = '\"';
		text.getChars(0, len, buf, start);

		count = newcount;

		int specialCount = 0;
		int lastSpecialIndex = -1;
		char lastSpecial = '\0';
		for (int i = start; i < end; ++i) {
			char ch = buf[i];
			if (ch == '\b' || ch == '\n'
					|| ch == '\r'
					|| ch == '\f'
					|| ch == '\\'
					|| ch == '"' //
					|| (ch == '\t' && isEnabled(SerializerFeature.WriteTabAsSpecial))) {
				specialCount++;
				lastSpecialIndex = i;
				lastSpecial = ch;
			}
		}

		newcount += specialCount;
		if (newcount > buf.length) {
			expandCapacity(newcount);
		}
		count = newcount;

		if (specialCount == 1) {
			System.arraycopy(buf, lastSpecialIndex + 1, buf,
					lastSpecialIndex + 2, end - lastSpecialIndex - 1);
			buf[lastSpecialIndex] = '\\';
			buf[++lastSpecialIndex] = replaceChars[lastSpecial];
		} else if (specialCount > 1) {
			System.arraycopy(buf, lastSpecialIndex + 1, buf,
					lastSpecialIndex + 2, end - lastSpecialIndex - 1);
			buf[lastSpecialIndex] = '\\';
			buf[++lastSpecialIndex] = replaceChars[lastSpecial];
			end++;
			for (int i = lastSpecialIndex - 2; i >= start; --i) {
				char ch = buf[i];

				if (ch == '\b' || ch == '\n'
						|| ch == '\r'
						|| ch == '\f'
						|| ch == '\\'
						|| ch == '"' //
						|| (ch == '\t' && isEnabled(SerializerFeature.WriteTabAsSpecial))) {
					System.arraycopy(buf, i + 1, buf, i + 2, end - i - 1);
					buf[i] = '\\';
					buf[i + 1] = replaceChars[ch];
					end++;
				}
			}
		}

		buf[count - 1] = '\"';
	}

	public void writeKeyWithDoubleQuote(String text) {
		final boolean[] specicalFlags_doubleQuotes = CharTypes.specicalFlags_doubleQuotes;

		int len = text.length();
		int newcount = count + len + 3;
		if (newcount > buf.length) {
			expandCapacity(newcount);
		}

		int start = count + 1;
		int end = start + len;

		buf[count] = '\"';
		text.getChars(0, len, buf, start);

		count = newcount;

		for (int i = start; i < end; ++i) {
			char ch = buf[i];
			if (ch < specicalFlags_doubleQuotes.length
					&& specicalFlags_doubleQuotes[ch] //
					|| (ch == '\t' && isEnabled(SerializerFeature.WriteTabAsSpecial))) {
				newcount++;
				if (newcount > buf.length) {
					expandCapacity(newcount);
				}
				count = newcount;

				System.arraycopy(buf, i + 1, buf, i + 2, end - i - 1);
				buf[i] = '\\';
				buf[++i] = replaceChars[ch];
				end++;
			}
		}

		buf[count - 2] = '\"';
		buf[count - 1] = ':';
	}

	public void writeFieldNull(char seperator, String name) {
		write(seperator);
		writeFieldName(name);
		writeNull();
	}

	public void writeFieldNullString(char seperator, String name) {
		write(seperator);
		writeFieldName(name);
		if (isEnabled(SerializerFeature.WriteNullStringAsEmpty)) {
			writeString("");
		} else {
			writeNull();
		}
	}

	public void writeFieldNullBoolean(char seperator, String name) {
		write(seperator);
		writeFieldName(name);
		if (isEnabled(SerializerFeature.WriteNullBooleanAsFalse)) {
			write("false");
		} else {
			writeNull();
		}
	}

	public void writeFieldNullList(char seperator, String name) {
		write(seperator);
		writeFieldName(name);
		if (isEnabled(SerializerFeature.WriteNullListAsEmpty)) {
			write("[]");
		} else {
			writeNull();
		}
	}

	public void writeFieldNullNumber(char seperator, String name) {
		write(seperator);
		writeFieldName(name);
		if (isEnabled(SerializerFeature.WriteNullNumberAsZero)) {
			write('0');
		} else {
			writeNull();
		}
	}

	public void writeFieldValue(char seperator, String name, char value) {
		write(seperator);
		writeFieldName(name);
		if (value == 0) {
			writeString("\u0000");
		} else {
			writeString(Character.toString(value));
		}
	}

	public void writeFieldValue(char seperator, String name, boolean value) {
		write(seperator);
		writeFieldName(name);
		if (value) {
			write("true");
		} else {
			write("false");
		}
	}

	public void writeFieldValue(char seperator, String name, int value) {
		write(seperator);
		writeFieldName(name);
		writeInt(value);
	}

	public void writeFieldValue(char seperator, String name, long value) {
		write(seperator);
		writeFieldName(name);
		writeLong(value);
	}

	public void writeFieldValue(char seperator, String name, float value) {
		write(seperator);
		writeFieldName(name);
		if (value == 0) {
			write('0');
		} else if (Float.isNaN(value)) {
			writeNull();
		} else if (Float.isInfinite(value)) {
			writeNull();
		} else {
			String text = Float.toString(value);
			if (text.endsWith(".0")) {
				text = text.substring(0, text.length() - 2);
			}
			write(text);
		}
	}

	public void writeFieldValue(char seperator, String name, double value) {
		write(seperator);
		writeFieldName(name);
		if (value == 0) {
			write('0');
		} else if (Double.isNaN(value)) {
			writeNull();
		} else if (Double.isInfinite(value)) {
			writeNull();
		} else {
			String text = Double.toString(value);
			if (text.endsWith(".0")) {
				text = text.substring(0, text.length() - 2);
			}
			write(text);
		}
	}

	public void writeFieldValue(char seperator, String name, String value) {
		if (isEnabled(SerializerFeature.QuoteFieldNames)) {
			if (isEnabled(SerializerFeature.UseSingleQuotes)) {
				write(seperator);
				writeFieldName(name);
				if (value == null) {
					writeNull();
				} else {
					writeString(value);
				}
			} else {
				writeFieldValueStringWithDoubleQuote(seperator, name, value);
			}
		} else {
			write(seperator);
			writeFieldName(name);
			if (value == null) {
				writeNull();
			} else {
				writeString(value);
			}
		}
	}

	private void writeFieldValueStringWithDoubleQuote(char seperator,
			String name, String value) {
		int nameLen = name.length();
		int valueLen;

		int newcount = count;

		if (value == null) {
			valueLen = 4;
			newcount += nameLen + 8;
		} else {
			valueLen = value.length();
			newcount += nameLen + valueLen + 6;
		}

		if (newcount > buf.length) {
			expandCapacity(newcount);
		}

		buf[count] = seperator;

		int nameStart = count + 2;
		int nameEnd = nameStart + nameLen;

		buf[count + 1] = '\"';
		name.getChars(0, nameLen, buf, nameStart);

		count = newcount;

		int specialCount = 0;
		int lastSpecialIndex = -1;
		char lastSpecial = '\0';
		for (int i = nameStart; i < nameEnd; ++i) {
			char ch = buf[i];
			if (ch == '\b' || ch == '\n'
					|| ch == '\r'
					|| ch == '\f'
					|| ch == '\\'
					|| ch == '"' //
					|| (ch == '\t' && isEnabled(SerializerFeature.WriteTabAsSpecial))) {
				specialCount++;
				lastSpecialIndex = i;
				lastSpecial = ch;
			}
		}

		if (specialCount > 0) {
			newcount += specialCount;
			if (newcount > buf.length) {
				expandCapacity(newcount);
			}
			count = newcount;
		}

		if (specialCount == 1) {
			System.arraycopy(buf, lastSpecialIndex + 1, buf,
					lastSpecialIndex + 2, nameEnd - lastSpecialIndex - 1);
			buf[lastSpecialIndex] = '\\';
			buf[++lastSpecialIndex] = replaceChars[lastSpecial];
			nameEnd++;
		} else if (specialCount > 1) {
			System.arraycopy(buf, lastSpecialIndex + 1, buf,
					lastSpecialIndex + 2, nameEnd - lastSpecialIndex - 1);
			buf[lastSpecialIndex] = '\\';
			buf[++lastSpecialIndex] = replaceChars[lastSpecial];
			nameEnd++;
			for (int i = lastSpecialIndex - 2; i >= nameStart; --i) {
				char ch = buf[i];

				if (ch == '\b' || ch == '\n'
						|| ch == '\r'
						|| ch == '\f'
						|| ch == '\\'
						|| ch == '"' //
						|| (ch == '\t' && isEnabled(SerializerFeature.WriteTabAsSpecial))) {
					System.arraycopy(buf, i + 1, buf, i + 2, nameEnd - i - 1);
					buf[i] = '\\';
					buf[i + 1] = replaceChars[ch];
					nameEnd++;
				}
			}
		}

		buf[nameEnd] = '\"';

		int index = nameEnd + 1;
		buf[index++] = ':';

		if (value == null) {
			buf[index++] = 'n';
			buf[index++] = 'u';
			buf[index++] = 'l';
			buf[index++] = 'l';
			return;
		}

		buf[index++] = '"';

		int valueStart = index;
		int valueEnd = valueStart + valueLen;

		value.getChars(0, valueLen, buf, valueStart);

		specialCount = 0;
		lastSpecialIndex = -1;
		lastSpecial = '\0';
		for (int i = valueStart; i < valueEnd; ++i) {
			char ch = buf[i];
			if (ch == '\b' || ch == '\n'
					|| ch == '\r'
					|| ch == '\f'
					|| ch == '\\'
					|| ch == '"' //
					|| (ch == '\t' && isEnabled(SerializerFeature.WriteTabAsSpecial))) {
				specialCount++;
				lastSpecialIndex = i;
				lastSpecial = ch;
			}
		}

		if (specialCount > 0) {
			newcount += specialCount;
			if (newcount > buf.length) {
				expandCapacity(newcount);
			}
			count = newcount;
		}

		if (specialCount == 1) {
			System.arraycopy(buf, lastSpecialIndex + 1, buf,
					lastSpecialIndex + 2, valueEnd - lastSpecialIndex - 1);
			buf[lastSpecialIndex] = '\\';
			buf[++lastSpecialIndex] = replaceChars[lastSpecial];
		} else if (specialCount > 1) {
			System.arraycopy(buf, lastSpecialIndex + 1, buf,
					lastSpecialIndex + 2, valueEnd - lastSpecialIndex - 1);
			buf[lastSpecialIndex] = '\\';
			buf[++lastSpecialIndex] = replaceChars[lastSpecial];
			valueEnd++;
			for (int i = lastSpecialIndex - 2; i >= valueStart; --i) {
				char ch = buf[i];

				if (ch == '\b' || ch == '\n'
						|| ch == '\r'
						|| ch == '\f'
						|| ch == '\\'
						|| ch == '"' //
						|| (ch == '\t' && isEnabled(SerializerFeature.WriteTabAsSpecial))) {
					System.arraycopy(buf, i + 1, buf, i + 2, valueEnd - i - 1);
					buf[i] = '\\';
					buf[i + 1] = replaceChars[ch];
					valueEnd++;
				}
			}
		}

		buf[count - 1] = '\"';
	}

	// writeStringWithSingleQuote

	public void writeFieldValue(char seperator, String name, Enum<?> value) {
		write(seperator);
		writeFieldName(name);
		if (value == null) {
			writeNull();
		} else {
			if (isEnabled(SerializerFeature.WriteEnumUsingToString)) {
				writeString(value.name());
			} else {
				writeInt(value.ordinal());
			}
		}
	}

	public void writeFieldValue(char seperator, String name, BigDecimal value) {
		write(seperator);
		writeFieldName(name);
		if (value == null) {
			writeNull();
		} else {
			write(value.toString());
		}
	}

	public void writeString(String text, char seperator) {
		writeString(text);
		write(seperator);
	}

	public void writeString(String text) {
		if (isEnabled(SerializerFeature.UseSingleQuotes)) {
			writeStringWithSingleQuote(text);
		} else {
			writeStringWithDoubleQuote(text);
		}
	}

	private void writeStringWithSingleQuote(String text) {
		if (text == null) {
			int newcount = count + 4;
			if (newcount > buf.length) {
				expandCapacity(newcount);
			}
			"null".getChars(0, 4, buf, count);
			count = newcount;
			return;
		}

		int len = text.length();
		int newcount = count + len + 2;
		if (newcount > buf.length) {
			expandCapacity(newcount);
		}

		int start = count + 1;
		int end = start + len;

		buf[count] = '\'';
		text.getChars(0, len, buf, start);
		count = newcount;

		int specialCount = 0;
		int lastSpecialIndex = -1;
		char lastSpecial = '\0';
		for (int i = start; i < end; ++i) {
			char ch = buf[i];
			if (ch == '\b' || ch == '\n'
					|| ch == '\r'
					|| ch == '\f'
					|| ch == '\\'
					|| ch == '\'' //
					|| (ch == '\t' && isEnabled(SerializerFeature.WriteTabAsSpecial))) {
				specialCount++;
				lastSpecialIndex = i;
				lastSpecial = ch;
			}
		}

		newcount += specialCount;
		if (newcount > buf.length) {
			expandCapacity(newcount);
		}
		count = newcount;

		if (specialCount == 1) {
			System.arraycopy(buf, lastSpecialIndex + 1, buf,
					lastSpecialIndex + 2, end - lastSpecialIndex - 1);
			buf[lastSpecialIndex] = '\\';
			buf[++lastSpecialIndex] = replaceChars[lastSpecial];
		} else if (specialCount > 1) {
			System.arraycopy(buf, lastSpecialIndex + 1, buf,
					lastSpecialIndex + 2, end - lastSpecialIndex - 1);
			buf[lastSpecialIndex] = '\\';
			buf[++lastSpecialIndex] = replaceChars[lastSpecial];
			end++;
			for (int i = lastSpecialIndex - 2; i >= start; --i) {
				char ch = buf[i];

				if (ch == '\b' || ch == '\n'
						|| ch == '\r'
						|| ch == '\f'
						|| ch == '\\'
						|| ch == '\'' //
						|| (ch == '\t' && isEnabled(SerializerFeature.WriteTabAsSpecial))) {
					System.arraycopy(buf, i + 1, buf, i + 2, end - i - 1);
					buf[i] = '\\';
					buf[i + 1] = replaceChars[ch];
					end++;
				}
			}
		}

		buf[count - 1] = '\'';
	}

	public void writeFieldName(String key) {
		if (isEnabled(SerializerFeature.UseSingleQuotes)) {
			if (isEnabled(SerializerFeature.QuoteFieldNames)) {
				writeKeyWithSingleQuote(key);
			} else {
				writeKeyWithSingleQuoteIfHasSpecial(key);
			}
		} else {
			if (isEnabled(SerializerFeature.QuoteFieldNames)) {
				writeKeyWithDoubleQuote(key);
			} else {
				writeKeyWithDoubleQuoteIfHasSpecial(key);
			}
		}
	}

	private void writeKeyWithSingleQuote(String text) {
		final boolean[] specicalFlags_singleQuotes = CharTypes.specicalFlags_singleQuotes;

		int len = text.length();
		int newcount = count + len + 3;
		if (newcount > buf.length) {
			expandCapacity(newcount);
		}

		int start = count + 1;
		int end = start + len;

		buf[count] = '\'';
		text.getChars(0, len, buf, start);
		count = newcount;

		for (int i = start; i < end; ++i) {
			char ch = buf[i];
			if (ch < specicalFlags_singleQuotes.length
					&& specicalFlags_singleQuotes[ch] //
					|| (ch == '\t' && isEnabled(SerializerFeature.WriteTabAsSpecial))) {
				newcount++;
				if (newcount > buf.length) {
					expandCapacity(newcount);
				}
				count = newcount;

				System.arraycopy(buf, i + 1, buf, i + 2, end - i - 1);
				buf[i] = '\\';
				buf[++i] = replaceChars[ch];
				end++;
			}
		}

		buf[count - 2] = '\'';
		buf[count - 1] = ':';
	}

	private void writeKeyWithDoubleQuoteIfHasSpecial(String text) {
		final boolean[] specicalFlags_doubleQuotes = CharTypes.specicalFlags_doubleQuotes;

		int len = text.length();
		int newcount = count + len + 1;
		if (newcount > buf.length) {
			expandCapacity(newcount);
		}

		int start = count;
		int end = start + len;

		text.getChars(0, len, buf, start);
		count = newcount;

		boolean hasSpecial = false;

		for (int i = start; i < end; ++i) {
			char ch = buf[i];
			if (ch < specicalFlags_doubleQuotes.length
					&& specicalFlags_doubleQuotes[ch]) {
				if (!hasSpecial) {
					newcount += 3;
					if (newcount > buf.length) {
						expandCapacity(newcount);
					}
					count = newcount;

					System.arraycopy(buf, i + 1, buf, i + 3, end - i - 1);
					System.arraycopy(buf, 0, buf, 1, i);
					buf[start] = '"';
					buf[++i] = '\\';
					buf[++i] = replaceChars[ch];
					end += 2;
					buf[count - 2] = '"';

					hasSpecial = true;
				} else {
					newcount++;
					if (newcount > buf.length) {
						expandCapacity(newcount);
					}
					count = newcount;

					System.arraycopy(buf, i + 1, buf, i + 2, end - i);
					buf[i] = '\\';
					buf[++i] = replaceChars[ch];
					end++;
				}
			}
		}

		buf[count - 1] = ':';
	}

	private void writeKeyWithSingleQuoteIfHasSpecial(String text) {
		final boolean[] specicalFlags_singleQuotes = CharTypes.specicalFlags_singleQuotes;

		int len = text.length();
		int newcount = count + len + 1;
		if (newcount > buf.length) {
			expandCapacity(newcount);
		}

		int start = count;
		int end = start + len;

		text.getChars(0, len, buf, start);
		count = newcount;

		boolean hasSpecial = false;

		for (int i = start; i < end; ++i) {
			char ch = buf[i];
			if (ch < specicalFlags_singleQuotes.length
					&& specicalFlags_singleQuotes[ch]) {
				if (!hasSpecial) {
					newcount += 3;
					if (newcount > buf.length) {
						expandCapacity(newcount);
					}
					count = newcount;

					System.arraycopy(buf, i + 1, buf, i + 3, end - i - 1);
					System.arraycopy(buf, 0, buf, 1, i);
					buf[start] = '\'';
					buf[++i] = '\\';
					buf[++i] = replaceChars[ch];
					end += 2;
					buf[count - 2] = '\'';

					hasSpecial = true;
				} else {
					newcount++;
					if (newcount > buf.length) {
						expandCapacity(newcount);
					}
					count = newcount;

					System.arraycopy(buf, i + 1, buf, i + 2, end - i);
					buf[i] = '\\';
					buf[++i] = replaceChars[ch];
					end++;
				}
			}
		}

		buf[newcount - 1] = ':';
	}
}
