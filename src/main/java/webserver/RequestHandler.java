package webserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import contoller.Controller;
import http.HttpRequest;
import http.HttpResponse;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

	private Socket connection;
	private HttpRequest httpRequest;
	private HttpResponse httpResponse;
	private Controller contoller;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
				connection.getPort());

		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream();) {
			httpRequest = new HttpRequest(in);
			httpResponse = new HttpResponse(out);

			String url = getDefaultPath(httpRequest.getPath());

			contoller = ContollerRouter.getContoller(url);

			if (contoller == null) {
				httpResponse.forward(url);
			} else {
				contoller.service(httpRequest, httpResponse);
			}

		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private String getDefaultPath(String url) {
		if ("/".equals(url)) {
			return "index.html";
		}

		return url;
	}
}
