package com.techsophy.vsc.auth;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.techsophy.vsc.dao.AppointmentDao;

import sun.misc.BASE64Encoder;

@Component
public class UserAuthentication implements AuthenticationProvider {

	private static final Logger logger = LoggerFactory.getLogger(UserAuthentication.class);

	private static HashMap<String, String> userRolesMap = new HashMap<String, String>();

	private static HashMap<String, String> userTokenMap = new HashMap<String, String>();

	@Autowired
	AppointmentDao appointmentDao;

	@Qualifier("vsc")
	@Autowired
	DataSource vscDataSource;
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String userName = authentication.getName();
		String pwd = authentication.getCredentials().toString();
		List<GrantedAuthority> grantedAuths = new ArrayList<>();
		logger.info("in authenticate() to authenticate user - " + userName);
		if (validateUser(userName, pwd)) {
			grantedAuths = setRoles(userName);
			if (grantedAuths == null) {
				logger.error("User validation failed for - " + userName);
				throw new BadCredentialsException("User validation failed for - " + userName);
			} else if (grantedAuths.size() == 0) {
				logger.error("Roles not configured for user - " + userName);
				throw new BadCredentialsException("Roles not configured for user - " + userName);
			}
			return new UsernamePasswordAuthenticationToken(userName, pwd, grantedAuths);
		} else {
			logger.error("Authentication failed for user = " + userName);
			throw new BadCredentialsException("Authentication failed for user " + userName);
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

	public boolean validateUser(String username, String pwd) {
		logger.info("in validateUser() to validate user - " + username);
		String token = userTokenMap.get(username);
		String encodedString = encodeString(username, pwd);
		boolean valid = false;
		if ((token != null || !"".equals(token)) && encodedString.equals(token)) {
			valid = true;
		} else {
			if (checkUser(username, pwd)) {
				createToken(username, pwd);
				valid = true;
			}
		}
		return valid;
	}

	public boolean checkUser(String username, String pwd) {
		logger.info("in checkUser() to check the user - " + username + " in users table");
		Connection connection = null;
		Statement stmt = null;
		boolean valid = false;
		try {
			connection = vscDataSource.getConnection();
			stmt = connection.createStatement();

			String userQuery = "select * from vsc_users where email='" + username + "' and password='" + pwd + "'";
			ResultSet rs1 = stmt.executeQuery(userQuery);

			while (rs1.next()) {
				valid = true;
				break;

			}
		} catch (Exception e) {
			logger.error("In VSCAuthorization-checkUser method catch block due to " + e);
		} finally {

		}
		return valid;
	}

	public void createToken(String userName, String pwd) {
		userTokenMap.put(userName, encodeString(userName, pwd));
	}

	public String encodeString(String userName, String pwd) {
		String authString = userName + ":" + pwd;
		String authStringEnc = new BASE64Encoder().encode(authString.getBytes());
		String encodedString = "Basic " + new String(authStringEnc);
		return encodedString;
	}

	public List<GrantedAuthority> setRoles(String userName) {
		ArrayList<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		JSONArray roles = new JSONArray();
		String role = "";
		List<GrantedAuthority> grantedAuths = new ArrayList<>();
		String jsonResponse = null;
		JSONObject userRoleInfo;
		try {
			if (userRolesMap.containsKey(userName)) {
				jsonResponse = userRolesMap.get(userName);
				userRoleInfo = new JSONObject(jsonResponse);
				roles = (JSONArray) userRoleInfo.get("roles");
			} else {
				jsonResponse = appointmentDao.getUserRoles(userName);
				userRoleInfo = new JSONObject(jsonResponse);
				roles = (JSONArray) userRoleInfo.get("roles");
			}
			if (roles != null && roles.length() > 0) {
				for (int i = 0; i < roles.length(); i++) {
					userRoleInfo = new JSONObject(roles.get(i).toString());
					role = userRoleInfo.get("role").toString();
					if (!authorities.contains(new SimpleGrantedAuthority(role))) {
						authorities.add(new SimpleGrantedAuthority(role));
					}
					/*
					 * if ((role.equals("Reception") && !authorities.contains(new
					 * SimpleGrantedAuthority("Reception")))) { authorities.add(new
					 * SimpleGrantedAuthority("Reception")); } else if (role.equals("Cashier") &&
					 * !authorities.contains(new SimpleGrantedAuthority("Cashier"))) {
					 * authorities.add(new SimpleGrantedAuthority("Cashier")); } else if
					 * (role.equals("Typing")) { if (!authorities.contains(new
					 * SimpleGrantedAuthority("Typing"))) authorities.add(new
					 * SimpleGrantedAuthority("Typing")); } else if (role.equals("Biometrics")) { if
					 * (!authorities.contains(new SimpleGrantedAuthority("Biometrics")))
					 * authorities.add(new SimpleGrantedAuthority("Biometrics")); } else if
					 * (role.equals("BackOffice")) { if (!authorities.contains(new
					 * SimpleGrantedAuthority("BackOffice"))) authorities.add(new
					 * SimpleGrantedAuthority("BackOffice")); }
					 */
				}
				userRolesMap.put(userName, jsonResponse);
			}
		} catch (Exception e) {
			logger.error("failed in setting roles for user - " + userName + " due to " + e);
			// e.printStackTrace();
		}
		return authorities;
	}

	public void removeUser(String userName) {
		if (userName != null && !"".equalsIgnoreCase(userName)) {
			userRolesMap.remove(userName);
			userTokenMap.remove(userName);
		}
	}

	public String loadUserRoles(String userName) {
		if (userName != null && !"".equalsIgnoreCase(userName)) {
			return userRolesMap.get(userName);
		}
		return "";
	}
}
