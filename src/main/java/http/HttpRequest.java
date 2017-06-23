package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.HttpConstant;
import model.RequestMethod;
import util.HttpCookie;
import util.HttpRequestUtils;
import util.IOUtils;

public class HttpRequest {
	private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

	private String method;
	private String path;
	private Map<String, String> header = new HashMap<>();
	private Map<String, String> parameter = new HashMap<>();
	private int contentLength;
	private String[] headerTokens;
	private String[] urlPathTokens;

	public HttpRequest(InputStream in) {
		initRequest(in);
	}

	private void initRequest(InputStream in) {
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String readLine = bufferedReader.readLine();

			if (readLine == null) {
				return;
			}

			headerTokens = readLine.split(HttpConstant.SPACE);
			method = headerTokens[0];

			urlPathTokens = headerTokens[1].split("\\?");

			setPath();

			while (!(readLine = bufferedReader.readLine()).isEmpty()) {
				int index = readLine.indexOf(HttpConstant.HEADER_SEPAROTR);

				String headerKey = readLine.substring(0, index);
				String headerValue = readLine.substring(index + 2);

				setHeader(headerKey, headerValue);

				if ("Content-Length".equals(headerKey)) {
					contentLength = Integer.parseInt(headerValue);
				}
			}

			setParameter(bufferedReader);

		} catch (Exception exception) {
			log.error("HttpRequest initRequest", exception);
		}
	}

	private void setParameter(BufferedReader bufferedReader) throws IOException {
		if (RequestMethod.isPost(method)) {
			setParameter(IOUtils.readData(bufferedReader, contentLength));
		} else {
			if (urlPathTokens.length > 1) {
				setParameter(urlPathTokens[1]);
			}
		}
	}

	private void setPath() {
		if (RequestMethod.isPost(method)) {
			path = headerTokens[1];
		} else {
			path = urlPathTokens[0];
		}
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getHeader(String key) {
		return header.get(key);
	}

	public Map<String, String> getHeaderAll() {
		return header;
	}

	public void setHeader(Map<String, String> header) {
		this.header = header;
	}

	public void setHeader(String key, String value) {
		header.put(key, value);
	}

	public String getParameter(String key) {
		return parameter.get(key);
	}

	public Map<String, String> getParameterAll() {
		return parameter;
	}

	public void setParameter(String queryString) {
		if (queryString == null || queryString.isEmpty()) {
			return;
		}

		parameter.putAll(HttpRequestUtils.parseQueryString(queryString));
	}

	public void setParameter(Map<String, String> parameter) {
		this.parameter = parameter;
	}

	public HttpCookie getCookies() {
		return new HttpCookie(getHeader("Cookie"));
	}
	
	public HttpSession getSession() {
		return HttpSessions.getSesson(getCookies().getCookie("JSESSIONID"));
	}
}
