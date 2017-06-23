package contoller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

public class ListUserController extends AbstractController {
	private static final Logger log = LoggerFactory.getLogger(ListUserController.class);

	@Override
	public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
		if (httpRequest.getSession().getAttribute("user") != null) {
			Collection<User> userList = DataBase.findAll();
			httpResponse.forwardBody(replaceListHtml(writeUserList(userList)));
		} else {
			httpResponse.sendRedirect("/index.html");
		}
	}

	private String replaceListHtml(String replaceStr) {
		StringBuffer sb = new StringBuffer();
		try {
			List<String> lines = Files.readAllLines(new File("./webapp/user/list.html").toPath());
			String replaceValue = "${userList}";

			for (String line : lines) {
				if (line.contains(replaceValue)) {
					line = line.replace(replaceValue, replaceStr);
				}

				sb.append(line);
			}

		} catch (IOException exception) {
			log.error(exception.getMessage());
		}

		return sb.toString();
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
			sb.append("<td><a href='#' class='btn btn-success' role='button'>수정</a></td>");
			sb.append("</tr>");

			index++;
		}

		return sb.toString();
	}

}
