package contoller;

import http.HttpRequest;
import http.HttpResponse;
import model.RequestMethod;

public abstract class AbstractController implements Controller {

	public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
		String method = httpRequest.getMethod();

		if (RequestMethod.isPost(method)) {
			doPost(httpRequest, httpResponse);
		} else {
			doGet(httpRequest, httpResponse);
		}
	}

	protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
	}

	protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
	}
}
