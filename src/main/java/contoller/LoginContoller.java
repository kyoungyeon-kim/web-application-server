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
		String redirectPage = "/index.html";
		
		User user = DataBase.findUserById(loginUserId);

		if (user == null || !user.getPassword().equals(loginPassword)) {
			redirectPage = "/user/login_failed.html";
		}

		httpRequest.getSession().setAttribute("user", user);
		httpResponse.sendRedirect(redirectPage);
	}
}
