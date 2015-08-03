import java.io.IOException;
import java.io.Reader;

/**
 * 
 */

/**
 * @author ben
 *
 */
public final class JSONTokenizer {
	
	/**
	 * 输入流
	 */
	private Reader stream;

	/**
	 * 当前字符的存放下标
	 */
	private int cur = 0;
	
	/**
	 * 读入字符的缓冲区
	 * TODO 使之可设定
	 */
	private byte[] buf = new byte[4096];
	
	public JSONTokenizer(Reader stream) throws IOException {
		if(stream == null) {
			throw new IOException();
		}
		this.stream = stream;
		buf[cur] = (byte) this.stream.read();
	}
	
	private void skip() throws IOException {
		while(buf[cur] == ' ' || buf[cur] == '\t' || buf[cur] == '\n' || buf[cur] == '\f' || buf[cur] == '\b' || buf[cur] == '\r') {
			buf[cur] = (byte) this.stream.read();
		}
	}
	
	public String getNextToken() throws IOException {
		skip();
		
		/*
		 * 读到文件结束符
		 */
		if(buf[cur] == -1) {
			return null;
		}
		
		/*
		 * 单字符token
		 */
		String kws = "{},:[]";
		if(kws.indexOf(buf[cur]) >= 0) {
			String ret = new String(buf, 0, 1);
			buf[cur = 0] = (byte) this.stream.read();
			return ret;
		}
		
		/*
		 * 双引号开头，表明是一个字符串
		 */
		if(buf[cur] == '\"') {
			buf[++cur] = (byte) this.stream.read();
			while(buf[cur] != '\"') {
				if(buf[cur] == '\\') {
					buf[++cur] = (byte) this.stream.read();
				}
				buf[++cur] = (byte) this.stream.read();
			}
			String ret = new String(buf, 0, cur + 1);
			buf[cur = 0] = (byte) this.stream.read();
			return ret;
		}
		
		while(buf[cur] != ',' && buf[cur] != '}') {
			buf[++cur] = (byte) this.stream.read();
		}
		String ret = new String(buf, 0, cur);
		buf[0] = buf[cur];
		cur = 0;
		return ret;
	}
	

}
