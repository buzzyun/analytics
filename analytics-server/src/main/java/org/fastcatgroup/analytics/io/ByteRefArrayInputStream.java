package org.fastcatgroup.analytics.io;

import java.io.ByteArrayInputStream;

public class ByteRefArrayInputStream extends ByteArrayInputStream {

	public ByteRefArrayInputStream(byte[] buf) {
		super(buf);
	}
	
	public ByteRefArrayInputStream(byte[] buf, int offset, int length) {
		super(buf, offset, length);
	}
	
	public byte[] array(){
		return buf;
	}

}
