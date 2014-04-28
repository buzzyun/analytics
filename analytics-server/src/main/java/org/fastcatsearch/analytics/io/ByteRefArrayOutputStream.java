package org.fastcatsearch.analytics.io;

import java.io.ByteArrayOutputStream;

public class ByteRefArrayOutputStream extends ByteArrayOutputStream {
	
	
	public ByteRefArrayOutputStream() {
		super();
	}
	
	public ByteRefArrayOutputStream(int size) {
		super(size);
	}
	
	public byte[] array(){
		return buf;
	}
	
	public int length(){
		return count;
	}
}
