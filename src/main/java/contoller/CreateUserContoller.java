package contoller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

public class CreateUserContoller extends AbstractController {
	@Override
	public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
		User user = DataBase.makeUser(httpRequest.getParameterAll());
		DataBase.addUser(user);

		httpResponse.sendRedirect("/index.html");
	}
}
