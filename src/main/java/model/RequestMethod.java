package model;

public enum RequestMethod {
	GET, POST;
	
	public static boolean isPost(String method) {
		return RequestMethod.POST.toString().equals(method);
	}
}
