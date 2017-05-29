package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
	private static final String SPACE = " ";

	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

	private Socket connection;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
				connection.getPort());

		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String readLine = bufferedReader.readLine();
			int contentLength = 0;

			log.debug("header : {} ", readLine);

			if (readLine == null) {
				return;
			}

			String[] headerTokens = readLine.split(SPACE);
			String cookieString = "";
			String contentsType = "";
			while (!readLine.isEmpty()) {
				readLine = bufferedReader.readLine();

				if (readLine.startsWith("Content-Length:")) {
					contentLength = Integer.parseInt(readLine.substring(readLine.indexOf(":") + 1).trim());
				}

				if (readLine.startsWith("Cookie:")) {
					cookieString = readLine.substring(readLine.indexOf(":") + 1).trim();
				}

				if (readLine.startsWith("Accept:")) {
					int lastIndex = readLine.indexOf(",");

					if (lastIndex < 0) {
						contentsType = readLine.substring(readLine.indexOf(":") + 1).trim();
					} else {
						contentsType = readLine.substring(readLine.indexOf(":") + 1, lastIndex).trim();
					}
				}

				log.debug("header : {} ", readLine);
			}

			String url = headerTokens[1];

			if ("/user/create".equals(url)) {

				// if (isMethodPost(headerTokens[0])) {
				String bodyContents = IOUtils.readData(bufferedReader, contentLength);
				// } else {
				// url = url.substring(url.indexOf("?") + 1).trim();
				// }

				Map<String, String> bodyContentsMap = HttpRequestUtils.parseQueryString(bodyContents);
				User user = makeUser(bodyContentsMap);
				log.debug("user {}", user.toString());

				DataBase.addUser(user);

				url = "/index.html";
				redirect(out, url);
			} else if ("/user/login".equals(url)) {
				String bodyContents = IOUtils.readData(bufferedReader, contentLength);

				Map<String, String> userDataMap = HttpRequestUtils.parseQueryString(bodyContents);

				String loginUserId = userDataMap.get("userId");
				String loginPassword = userDataMap.get("password");

				User user = DataBase.findUserById(loginUserId);

				if (user == null || !user.getPassword().equals(loginPassword)) { // login
					response(out, "/user/login_failed.html", contentsType, true);
				}

				byte[] body = Files.readAllBytes(new File("./webapp/index.html").toPath());
				DataOutputStream dos = new DataOutputStream(out);
				response302LoginSuccessHeader(dos);
				responseBody(dos, body);
			} else if ("/user/list".equals(url)) {
				Map<String, String> cookies = HttpRequestUtils.parseCookies(cookieString);

				if (isLogined(cookies)) {
					Collection<User> userList = DataBase.findAll();

					byte[] body = replaceListHtml(writeUserList(userList));

					DataOutputStream dos = new DataOutputStream(out);
					response200Header(dos, body.length, contentsType, false);
					responseBody(dos, body);
				} else {
					redirect(out, "index.html");
				}
			} else {
				response(out, url, contentsType, false);
			}

		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private byte[] replaceListHtml(String replaceStr) throws IOException {
		List<String> lines = Files.readAllLines(new File("./webapp/user/list.html").toPath());
		StringBuffer sb = new StringBuffer();
		
		for (String line : lines) {
			if (line.contains("${userList}")) {
				line = line.replace("${userList}", replaceStr);
			}
			
			sb.append(line);
		}
		
		return sb.toString().getBytes();
	}

	private String writeUserList(Collection<User> userList) {
		StringBuilder sb = new StringBuilder();
		int index = 1;

		for (User user : userList) {
			sb.append("<tr>");
			sb.append("<th scope='row'>");
			sb.append(index);
			sb.append("</th>");
			sb.append("<td>");
			sb.append(user.getUserId());
			sb.append("</td>");
			sb.append("<td>");
			sb.append(user.getName());
			sb.append("</td>");
			sb.append("<td>");
			sb.append(user.getEmail());
			sb.append("</td>");
			sb.append("<a href='#' class='btn btn-success' role='button'>수정</a></td>");
			sb.append("</tr>");

			index++;
		}

		return sb.toString();
	}

	private void redirect(OutputStream out, String url) throws IOException {
		byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());

		DataOutputStream dos = new DataOutputStream(out);
		response302Header(dos, url);
		responseBody(dos, body);
	}

	private boolean isLogined(Map<String, String> cookies) {
		return Boolean.parseBoolean(cookies.get("logined"));
	}

	private void response(OutputStream out, String url, String contentsType, boolean isLoginFail) throws IOException {
		byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());

		DataOutputStream dos = new DataOutputStream(out);
		response200Header(dos, body.length, contentsType, isLoginFail);
		responseBody(dos, body);
	}

	// private boolean isMethodPost(String method) {
	// return "POST".equals(method);
	// }

	private User makeUser(Map<String, String> userDataMap) throws UnsupportedEncodingException {
		String userId = userDataMap.get("userId");
		String password = userDataMap.get("password");
		String name = userDataMap.get("name");
		String email = URLDecoder.decode(userDataMap.get("email"), "UTF-8");

		User user = new User(userId, password, name, email);
		return user;
	}

	// private boolean isCreateUrlPath(String url) {
	// return url.startsWith("/user/create");
	// }

	private void response302LoginSuccessHeader(DataOutputStream dos) {
		try {
			dos.writeBytes("HTTP/1.1 302 Found \r\n");
			dos.writeBytes("Location: /index.html \r\n");
			dos.writeBytes("Set-Cookie: logined=true; \r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void response302Header(DataOutputStream dos, String url) {
		try {
			dos.writeBytes("HTTP/1.1 302 Found \r\n");
			dos.writeBytes("Location: " + url + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentsType,
			boolean isLoginFail) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: " + contentsType + ";charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			if (isLoginFail) {
				dos.writeBytes("Set-Cookie: logined=false; \r\n");
			}
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void responseBody(DataOutputStream dos, byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
}
