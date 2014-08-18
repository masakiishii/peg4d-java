package org.peg4d;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class BitInputStream extends InputStream {
	private InputStream input;
	private int         buf;
	private int         counter;
	private int         flag;
	public BitInputStream(InputStream istream) {
		this.input = istream;
		this.buf = 0;
		this.counter = 0;
		this.flag = 0;
	}

	@Override
	public int read() throws IOException {	
		this.buf = this.input.read();
		
//		while(this.counter != 8) {
//			this.flag = this.buf & (1 << (7 - this.counter++));
//			if(this.flag > 0) {
//				System.out.print(1);
//			}
//			else {
//				System.out.print(0);
//			}
//		}
//		this.counter = 0;
		return this.buf;
	}
	@Override
	public void close() throws IOException {
		this.input.close();
	}

}
