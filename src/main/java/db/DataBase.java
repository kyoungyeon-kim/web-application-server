package db;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import model.User;

public class DataBase {
	private static final Logger log = LoggerFactory.getLogger(DataBase.class);

	private static Map<String, User> users = Maps.newHashMap();

	public static void addUser(User user) {
		users.put(user.getUserId(), user);
	}

	public static User findUserById(String userId) {
		return users.get(userId);
	}

	public static Collection<User> findAll() {
		return users.values();
	}

	public static User makeUser(Map<String, String> userDataMap) {
		String userId = userDataMap.get("userId");
		String password = userDataMap.get("password");
		String name = userDataMap.get("name");
		String email = "";

		try {
			email = URLDecoder.decode(userDataMap.get("email"), "UTF-8");
		} catch (UnsupportedEncodingException exception) {
			log.error(exception.getMessage());
		}

		User user = new User(userId, password, name, email);
		return user;
	}
}
