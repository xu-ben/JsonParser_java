import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

/**
 * 
 */

/**
 * @author ben
 * 
 */
public final class JSONParser {

	private JSONTokenizer tokenizer;

	private String curToken = null;

	private JSONParser(Reader stream) throws IOException {
		this.tokenizer = new JSONTokenizer(stream);
		curToken = this.tokenizer.getNextToken();
	}

	private Object[] parseArray() throws IOException, ParseException {
		ArrayList<Object> list = new ArrayList<Object>();
		if (!curToken.equals("[")) {// 格式错误
			throw new ParseException();
		}
		// 读掉[
		curToken = this.tokenizer.getNextToken();

		while (!curToken.equals("]")) {// 数组没结束
			Object value = parseValue();
			list.add(value);
			if (curToken.equals(",")) {
				curToken = this.tokenizer.getNextToken();
			}
		}
		// 读掉]
		curToken = this.tokenizer.getNextToken();
		int len = list.size();
		if(len <= 0) {
			return null;
		}
		Object[] ret = new Object[len];
		for(int i = 0; i < len; i++) {
			ret[i] = list.get(i);
		}
		return ret;
	}

	private Object parseValue() throws IOException, ParseException {
		if (curToken.equals("{")) {// 接下来是个对象
			JSONObject ret = parseObject();
			return ret;
		}
		if (curToken.equals("[")) {// 接下来是个数组
			Object[] arr = parseArray();
			return arr;
		}
		if (curToken.equals("true")) {
			return true;
		}
		if (curToken.equals("false")) {
			return false;
		}
		if (curToken.equals("null")) {
			return null;
		}
		if (curToken.charAt(0) == '\"') {// 是个字符串
			String ret = makeString(curToken);
			curToken = this.tokenizer.getNextToken();
			return ret;
		}
		/*
		 * 接下来是个数字
		 */
		Object ret = makeNumber(curToken);
		curToken = this.tokenizer.getNextToken();
		return ret;
	}

	private JSONCollection parseCollection() throws IOException, ParseException {
		/*
		 * 先解析出name
		 */
		String name = curToken.substring(1, curToken.length() - 1);
		curToken = this.tokenizer.getNextToken();
		if (!curToken.equals(":")) {
			throw new ParseException();
		}
		//读掉:
		curToken = this.tokenizer.getNextToken();
		/*
		 * 再解析value
		 */
		Object value = parseValue();
		return new JSONCollection(name, value);
	}

	private JSONObject parseObject() throws IOException, ParseException {
		JSONObject obj = new JSONObject();
		if (!curToken.equals("{")) {// 格式错误
			throw new ParseException();
		}
		// 读掉{
		curToken = this.tokenizer.getNextToken();

		while (!curToken.equals("}")) {// 对象没结束
			JSONCollection col = parseCollection();
			obj.addPair(col);
			if (curToken.equals(",")) {
				curToken = this.tokenizer.getNextToken();
			}
		}
		// 读掉}
		curToken = this.tokenizer.getNextToken();
		return obj;
	}

	/**
	 * 将字符串中的转义字符进行处理
	 * 
	 * @param oristr
	 * @return
	 */
	private String makeString(String oristr) {
		String ret = oristr;
		ret = ret.replaceAll("\\\\\\\\", "\\");
		ret = ret.replaceAll("\\\\/", "/");
		ret = ret.replaceAll("\\\\b", "\b");
		ret = ret.replaceAll("\\\\f", "\f");
		ret = ret.replaceAll("\\\\n", "\n");
		ret = ret.replaceAll("\\\\r", "\r");
		ret = ret.replaceAll("\\\\t", "\t");
		ret = ret.replaceAll("\\\\\"", "\"");
		return ret;
	}

	private Object makeNumber(String numstr) {
		try {
			Integer i = Integer.parseInt(numstr);
			return i;
		} catch (NumberFormatException e) {
		}
		try {
			Long l = Long.parseLong(numstr);
			return l;
		} catch (NumberFormatException e) {
		}
		try {
			Double d = Double.parseDouble(numstr);
			return d;
		} catch (NumberFormatException e) {
		}
		return null;
	}

	public static JSONObject parse(Reader stream) throws IOException,
			ParseException {
		JSONParser p = new JSONParser(stream);
		return p.parseObject();
	}

	public static JSONObject parse(String filePath) throws IOException,
			ParseException {
		if (filePath == null) {
			return null;
		}
		File f = new File(filePath);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			return parse(br);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
