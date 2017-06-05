package http;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {
	private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

	private Map<String, String> header = new HashMap<String, String>();
	private DataOutputStream dos;

	public HttpResponse(OutputStream out) {
		dos = new DataOutputStream(out);
	}

	public void addHeader(String key, String value) {
		header.put(key, value);
	}

	public void forward(String forwardPage) {
		try {
			if (forwardPage == null || forwardPage.isEmpty()) {
				throw new IllegalArgumentException("forward Page is empty");
			}
			
			if (forwardPage.endsWith("html")) {
				addHeader("Content-type", "text/html;charset=utf-8");
			} else if (forwardPage.endsWith("css")) {
				addHeader("Content-type", "text/css");
			} else if (forwardPage.endsWith("js")) {
				addHeader("Content-type", "application/javascript");
			}

			byte[] body = Files.readAllBytes(new File("./webapp/" + forwardPage).toPath());

			response200Header(body.length);
			responseBody(body);
		} catch (Exception exception) {
			log.error(exception.getMessage());
		}
	}

	public void forwardBody(String fowardPage) {
		byte[] body = fowardPage.getBytes();

		response200Header(body.length);
		responseBody(body);
	}

	private void response200Header(int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			processHeaders();
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void responseBody(byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	public void sendRedirect(String redirectPage) {
		try {
			dos.writeBytes("HTTP/1.1 302 Found \r\n");
			dos.writeBytes("Location: " + redirectPage + " \r\n");
			processHeaders();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void processHeaders() throws IOException {
		for (Entry<String, String> entry : header.entrySet()) {
			dos.writeBytes(entry.getKey() + ": " + entry.getValue() + "\r\n");
		}
	}
}
