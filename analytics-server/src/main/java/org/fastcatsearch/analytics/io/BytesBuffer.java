/*
 * Copyright 2013 Websquared, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fastcatsearch.analytics.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BytesBuffer {
	protected static Logger logger = LoggerFactory.getLogger(BytesBuffer.class);
	
	/** The contents of the BytesRef. Should never be {@code null}. */
	public byte[] bytes;

	/** Offset of first valid byte. */
	public int offset;

	/** Length of used bytes. */
	public int length;

	public BytesBuffer(){ }
	
	public BytesBuffer(int size) {
		this.bytes = new byte[size];
		clear();
	}

	public BytesBuffer(byte[] buffer) {
		this.bytes = buffer;
		clear();
	}

	public BytesBuffer(byte[] buffer, int limit) {
		this.bytes = buffer;
		this.length = limit;
	}

	public BytesBuffer(byte[] buffer, int pos, int limit) {
		this.bytes = buffer;
		this.offset = pos;
		this.length = limit;
	}

	public void init(byte[] buffer, int pos, int limit) {
		this.bytes = buffer;
		this.offset = pos;
		this.length = limit;
	}

	public void clear() {
		offset = 0;
		length = bytes.length;
	}

	public int size() {
		return bytes.length;
	}

	public BytesBuffer flip() {
		length = offset;
		offset = 0;
		return this;
	}

	// 현재위치로부터 offset만큼 떨어진 위치의 byte를 리턴해준다.
	// read하지 않고 값을 확인하고 싶을때 사용.
	// 2012-5-5 array[pos+offset] & 0xFF는 음수 byte를 양의 int로 바꾸어 주므로 올바른 값을 리턴할수 없다.
	// byte데이터 그대로를 리턴하도록 한다.
	public byte get(int i) {
		return bytes[offset + i];
	}

	public byte[] array() {
		return bytes;
	}

	public int pos() {
		return offset;
	}

	public int limit() {
		return length;
	}

	public void pos(int pos) {
		this.offset = pos;
	}

	public void skip(int n) {
		this.offset += n;
	}

	public void limit(int limit) {
		this.length = limit;
	}

	public int remaining() {
		return length - offset;
	}

	public byte readByte() {
		return bytes[offset++];
	}

	public int read() {
		return (bytes[offset++] & 0xFF);
	}

//	public int readBack() {
//		offset--;
//		return (bytes[offset] & 0xFF);
//	}

	public void write(int b) {
		bytes[offset++] = (byte) b;
	}

	public void write(byte[] src) {
		System.arraycopy(src, 0, bytes, offset, src.length);
		offset += src.length;
	}

	public void write(byte[] src, int srcPos, int srcLen) {
		System.arraycopy(src, srcPos, bytes, offset, srcLen);
		offset += srcLen;
	}

	public static int compareBuffer(BytesBuffer buf1, BytesBuffer buf2) {
		int len1 = buf1.remaining();
		int len2 = buf2.remaining();

		int len = len1 < len2 ? len1 : len2;
		for (int i = 0; i < len; i++) {
			if (buf1.get(i) != buf2.get(i)) {
				return buf1.get(i) - buf2.get(i);
			}
		}
		return len1 - len2;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = offset; i < length; i++) {
			sb.append(bytes[i]);
			sb.append(" ");
		}

		return sb.toString();
	}

	public String toAlphaString() {
		StringBuilder sb = new StringBuilder();
		for (int i = offset; i < length; i++) {
			sb.append((char) bytes[i]);
		}

		return sb.toString();
	}

	public int length() {
		return length - offset;
	}

	@Override
	public boolean equals(Object obj) {
		BytesBuffer another = (BytesBuffer) obj;
		if (remaining() != another.remaining()) {
			return false;
		}

		for (int i = 0; i < length - offset; i++) {
			if (bytes[offset + i] != another.bytes[another.offset + i]) {
				return false;
			}
		}

		return true;
	}
	
	@Override
	public BytesBuffer clone() {
		return new BytesBuffer(bytes, offset, length);
	}
}
