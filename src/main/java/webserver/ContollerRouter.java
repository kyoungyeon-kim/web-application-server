package webserver;

import java.util.HashMap;
import java.util.Map;

import contoller.Controller;
import contoller.CreateUserContoller;
import contoller.ListUserController;
import contoller.LoginContoller;

public class ContollerRouter {
	private static Map<String, Controller> contollerMap;

	static {
		contollerMap = new HashMap<String, Controller>();
		contollerMap.put("/user/create", new CreateUserContoller());
		contollerMap.put("/user/login", new LoginContoller());
		contollerMap.put("/user/list", new ListUserController());
	}
	
	public static Controller getContoller(String url) {
		return contollerMap.get(url);
	}
}
