package http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.junit.Test;

import http.HttpResponse;

public class HttpResponseTest {
	public String testDirectory = "./src/test/resources/";

	@Test
	public void responseFoward() throws Exception {
		HttpResponse response = new HttpResponse(createOutputStream("Http_Forward.txt"));
		response.forward("/index.html");
	}
	
	@Test
	public void responseRedirect() throws Exception {
		HttpResponse response = new HttpResponse(createOutputStream("Http_Redirect.txt"));
		response.sendRedirect("/index.html");
	}

	@Test
	public void responseCookies() throws Exception {
		HttpResponse response = new HttpResponse(createOutputStream("Http_Cookie.txt"));
		response.addHeader("Set-Cookie", "login=true");
		response.forward("/index.html");
	}

	
	private OutputStream createOutputStream(String fileName) throws FileNotFoundException {
		return new FileOutputStream(new File(testDirectory + fileName));
	}
}
