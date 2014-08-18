package org.peg4d;

import java.io.*;
import java.util.ArrayList;

public class BitOutputStream extends OutputStream {
    private OutputStream  output;
    private int           buf;
    private int           counter;
	public BitOutputStream(OutputStream ostream) {
		this.buf = 0;
		this.counter = 0;
		this.output = ostream;
	}
	public void write(ArrayList<Boolean> b) throws IOException {
		for(int i = 0; i < b.size(); i++) {
			this.write(b.get(i));
		}
	}
	
	public void write(boolean b) throws IOException {
		this.buf |= (b ? 1 : 0) << (7 - this.counter++);
		if(this.counter == 8) {
			this.output.write(this.buf);
			this.counter = 0;
			this.buf = 0;
		}
	}
	
	@Override
	public void flush() throws IOException {
		this.output.flush();
	}
	
	@Override
	public void write(int b) throws IOException {
		this.output.write(b);
	}
	
	@Override
	public void close() throws IOException {
		while(this.counter > 0){
			this.write(false);
		}
		this.output.flush();
		this.output.close();
	}
}
