package contoller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

public class LoginContoller extends AbstractController {
	@Override
	public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
		String loginUserId = httpRequest.getParameter("userId");
		String loginPassword = httpRequest.getParameter("password");
		String loginCookie = "login=true;";
		String redirectPage = "/index.html";
		
		User user = DataBase.findUserById(loginUserId);

		if (user == null || !user.getPassword().equals(loginPassword)) {
			loginCookie = "login=false;";
			redirectPage = "/user/login_failed.html";
		}

		httpResponse.addHeader("Set-Cookie", loginCookie);
		httpResponse.sendRedirect(redirectPage);
	}
}
