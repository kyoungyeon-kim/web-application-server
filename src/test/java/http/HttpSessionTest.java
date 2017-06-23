package http;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class HttpSessionTest {
	private HttpSession sut;

	@Before
	public void setUp() {
		sut = new HttpSession("11");
	}
	
	@Test
	public void httpSession_getId() {
		assertNotNull(sut.getId());
	}

	@Test
	public void httpSession_setAttribute() {
		sut.setAttribute("key", "test");
		
		assertEquals(sut.getAttribute("key"), "test");
	}
	
	@Test
	public void httpSession_removeAttribute() {
		sut.setAttribute("key", "test");
		
		assertEquals(sut.getAttribute("key"), "test");
		
		sut.removeAttribute("key");
		assertNull(sut.getAttribute("key"));
	}
}
