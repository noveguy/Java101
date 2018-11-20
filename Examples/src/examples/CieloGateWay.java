package examples;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jdk.nashorn.internal.parser.JSONParser;

public class CieloGateWay {
//	private String cieloWebServerIp="10.38.32.176";
//	private String cieloWebServerPort="8080";
//	private String userName="huy.dang";
//	private String userPassword="Ampere@1234";
	
	private String cieloWebServerIp="10.38.13.103";
	private String cieloWebServerPort="8080";
	private String serverId = "2";
	private String userName="hnghiem";
	private String userPassword="amcc1234";
	
//	private String cieloWebServerIp = null;
//	private String cieloWebServerPort = null;
//	private String serverId = "2";
//	private String userName = null;
//	private String userPassword = null;
	private String cieloWebServerLoginPath = "accounts/login/";
	private String cieloWebServerDashboardPath = "dashboard/";
	private String cieloWebLoginUrl;
	private String csrfToken;
	private String sessionId;
	private String cieloResponse;

	private boolean cieloUserLogin() {
		boolean status = false;
		try {
			URL cieloLogin = new URL(cieloWebLoginUrl);
			HttpURLConnection cieloPreLogin = (HttpURLConnection) cieloLogin.openConnection();
			
			cieloPreLogin.setRequestMethod("GET");
			cieloPreLogin.setRequestProperty("Connection", "keep-alive");
			cieloPreLogin.setReadTimeout(5000);
			cieloPreLogin.setConnectTimeout(5000);
			cieloPreLogin.setDoOutput(true);
			
			int responseCode = cieloPreLogin.getResponseCode();
			String csrfMiddleWareToken = "";
			
			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader inputCieloPreLogin = new BufferedReader(new InputStreamReader(cieloPreLogin.getInputStream()));
				String inputLine;
//				StringBuffer response = new StringBuffer();
				Pattern csrfTokenString = Pattern.compile("<(input type='hidden' )(name='csrfmiddlewaretoken' value=')(.*?)(' /)>");
				
				while ((inputLine = inputCieloPreLogin.readLine()) != null) {
//					response.append(inputLine);
					Matcher csrfTokenMatcher = csrfTokenString.matcher(inputLine);
					if (csrfTokenMatcher.find()) {
						csrfMiddleWareToken = csrfTokenMatcher.group(3);
					}
					if (csrfMiddleWareToken.length() != 0) {
//						System.out.println(inputLine);
//						System.out.println(csrfMiddleWareToken);
						break;
					}
				}
				inputCieloPreLogin.close();
			} else {
				cieloPreLogin.disconnect();
				return false;
			}
			
			String headerSetCookie = cieloPreLogin.getHeaderField("Set-Cookie");
			String cookieCsrfToken = headerSetCookie.substring(0, headerSetCookie.indexOf(';'));
//			System.out.println(cookieCsrfToken);
			
			cieloPreLogin.disconnect();
			
			HttpURLConnection cieloUserLogin = (HttpURLConnection) cieloLogin.openConnection();
			
			Map<String, String> parameters = new HashMap<>();
			parameters.put("username", userName);
			parameters.put("password", userPassword);
			parameters.put("csrfmiddlewaretoken", csrfMiddleWareToken);

			cieloUserLogin.setRequestMethod("POST");
			cieloUserLogin.setRequestProperty("Cookie", cookieCsrfToken);
			cieloUserLogin.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			cieloUserLogin.setReadTimeout(5000);
			cieloUserLogin.setConnectTimeout(5000);
			cieloUserLogin.setDoOutput(true);
			cieloUserLogin.setInstanceFollowRedirects(false);
			
			DataOutputStream out = new DataOutputStream(cieloUserLogin.getOutputStream());
			out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
			out.flush();
			out.close();
			
			responseCode = cieloUserLogin.getResponseCode();
			System.out.println(responseCode);
			if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
				String cieloUserLoginSetCookie="";
				Map<String, List<String>> responseHeader = cieloUserLogin.getHeaderFields();
				for (Map.Entry<String, List<String>> entry : responseHeader.entrySet()) {
//					System.out.println("Key : " + entry.getKey() 
//			                           + " ,Value : " + entry.getValue());
					if (entry.getKey() != null && entry.getKey().compareTo("Set-Cookie") == 0  ) {
						cieloUserLoginSetCookie = entry.getValue().get(0) + ";" + entry.getValue().get(1);
					}
				}
//				System.out.println(cieloUserLoginSetCookie);
				if (cieloUserLoginSetCookie.length() > 0) {
					Pattern loginCookiePattern = Pattern.compile("(^sessionid=)([a-zA-Z0-9]*)(.*?)(csrftoken=)([a-zA-Z0-9]*)(.*?)");
					Matcher loginCookieMatcher = loginCookiePattern.matcher(cieloUserLoginSetCookie);
					if (loginCookieMatcher.find()) {
						sessionId = loginCookieMatcher.group(2);
						csrfToken = loginCookieMatcher.group(5);
//					System.out.println(sessionId);
//					System.out.println(csrfToken);
					} else {
						return false;
					}
				} else {
					return false;
				}
			} else
				return false;
			
			status = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		
		return status;
	}

	public void setWebServerIp(String serverIP) {
		cieloWebServerIp = serverIP;
	}
	
	public void setWebServerPort (int Port) {
		if (Port > 0 && Port < 65536) {
			cieloWebServerPort = Integer.toString(Port);
		}
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public void setPassword(String password) {
		this.userPassword = password;
	}
	
	public String getCsrfToken() {
		if (this.csrfToken.length() != 0)
			return this.csrfToken;

		return null;
	}

	public String getSessionId() {
		if (this.sessionId.length() != 0)
			return this.sessionId;

		return null;
	}
	
	public String getCieloReponse() {
		return cieloResponse;
	}

	public boolean openCieloConnection() {
		boolean status = false;
		if (cieloWebServerIp == null || cieloWebServerPort == null || userName == null || userPassword == null)
			return status;
		cieloWebLoginUrl = "http://" + cieloWebServerIp + ":" + cieloWebServerPort + "/" + cieloWebServerLoginPath;
		System.out.println(cieloWebLoginUrl);
		status = cieloUserLogin();
		return status;
	}
	
	public boolean releaseBoardForCielo(String boardSerial) {

		try {
			URL cieloDashBoard = new URL("http://" + cieloWebServerIp + ":" + "8080" + "/");
			HttpURLConnection con = (HttpURLConnection) cieloDashBoard.openConnection();
			con.setRequestMethod("POST");

			Map<String, String> parameters = new HashMap<>();
//			parameters.put("Action", "RequestBoards");
//			parameters.put("Serial", boardSerial);
//			parameters.put("ServerId", serverId);
//			parameters.put("UserId", userName);
//			parameters.put("Password", userPassword);
//			parameters.put("Conditions", String.format("{\"Board\":{\"Serials\" : \"%s\"}}", boardSerial));
			
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.createObjectNode();
			
			JsonNode serialsNode = mapper.createObjectNode();
			((ObjectNode) serialsNode).put("Serials", boardSerial);
			
			JsonNode boardNode = mapper.createObjectNode();
			((ObjectNode) boardNode).set("Board", serialsNode);
			
//			JsonNode conditionNode = mapper.createObjectNode();
//			((ObjectNode) conditionNode).set("Conditions", boardNode);
			
			((ObjectNode) rootNode).put("Action", "ReleaseBoards");
			((ObjectNode) rootNode).put("UserId", userName);
			((ObjectNode) rootNode).put("Password", userPassword);
			((ObjectNode) rootNode).set("Conditions", boardNode);
			
			JSONObject root = new JSONObject();
			JSONObject serials = new JSONObject();
			JSONObject board = new JSONObject();
			
			serials.put("Serials", boardSerial);
			board.put("Board", serials);
			root.put("Action", "ReleaseBoards");
			root.put("UserId", userName);
			root.put("Password", userPassword);
			root.put("Conditions", board);

//			con.setRequestProperty("Cookie", "csrftoken=" + this.csrfToken + ";" + " sessionid=" + this.sessionId);
//			con.setRequestProperty("X-CSRFToken", this.csrfToken);

			con.setReadTimeout(5000);
			con.setConnectTimeout(5000);

			con.setDoOutput(true);
			DataOutputStream out = new DataOutputStream(con.getOutputStream());
//			out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
//			out.writeBytes(rootNode.toString());
			out.writeBytes(root.toString());
			out.flush();
			out.close();

			int responseCode = con.getResponseCode();

			// print result
			System.out.println("\nSending 'POST' request to URL : " + cieloDashBoard);
			System.out.println("Post parameters : " + ParameterStringBuilder.getParamsString(parameters));
			System.out.println("Response Code : " + responseCode);

			
			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				
				cieloResponse = response.toString();
				System.out.println(cieloResponse);
			} else {
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
			
	}
	
	public boolean requestBoardFromCielo(String boardSerial) {
		try {
			URL cieloDashBoard = new URL("http://" + cieloWebServerIp + ":" + "8080" + "/");
			HttpURLConnection con = (HttpURLConnection) cieloDashBoard.openConnection();
			con.setRequestMethod("POST");

			Map<String, String> parameters = new HashMap<>();
//			parameters.put("Action", "RequestBoards");
//			parameters.put("Serial", boardSerial);
//			parameters.put("ServerId", serverId);
//			parameters.put("UserId", userName);
//			parameters.put("Password", userPassword);
//			parameters.put("Conditions", String.format("{\"Board\":{\"Serials\" : \"%s\"}}", boardSerial));
			
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.createObjectNode();
			
			JsonNode serialsNode = mapper.createObjectNode();
			((ObjectNode) serialsNode).put("Serials", boardSerial);
			
			JsonNode boardNode = mapper.createObjectNode();
			((ObjectNode) boardNode).set("Board", serialsNode);
			
			((ObjectNode) rootNode).put("Action", "RequestBoards");
			((ObjectNode) rootNode).put("UserId", userName);
			((ObjectNode) rootNode).put("Password", userPassword);
			((ObjectNode) rootNode).set("Conditions", boardNode);

			JSONObject root = new JSONObject();
			JSONObject serials = new JSONObject();
			JSONObject board = new JSONObject();
			
			serials.put("Serials", boardSerial);
			board.put("Board", serials);
			root.put("Action", "RequestBoards");
			root.put("UserId", userName);
			root.put("Password", userPassword);
			root.put("Conditions", board);
			
//			con.setRequestProperty("Cookie", "csrftoken=" + this.csrfToken + ";" + " sessionid=" + this.sessionId);
//			con.setRequestProperty("X-CSRFToken", this.csrfToken);

			con.setReadTimeout(20000);
			con.setConnectTimeout(20000);

			con.setDoOutput(true);
			DataOutputStream out = new DataOutputStream(con.getOutputStream());
//			out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
//			out.writeBytes(rootNode.toString());
			out.writeBytes(root.toString());
			out.flush();
			out.close();

			int responseCode = con.getResponseCode();

			// print result
			System.out.println("\nSending 'POST' request to URL : " + cieloDashBoard);
			System.out.println("Post parameters : " + ParameterStringBuilder.getParamsString(parameters));
			System.out.println("Response Code : " + responseCode);

			
			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				
				cieloResponse = response.toString();
				System.out.println(cieloResponse);
			} else {
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	
	public boolean getBoardsFromCielo() {
		try {
			URL cieloDashBoard = new URL("http://" + cieloWebServerIp + ":" + "8080" + "/");
			HttpURLConnection con = (HttpURLConnection) cieloDashBoard.openConnection();
			con.setRequestMethod("POST");

			Map<String, String> parameters = new HashMap<>();
//			parameters.put("Action", "GetBoards");
//			parameters.put("UserId", userName);
//			parameters.put("Password", userPassword);
//			parameters.put("Conditions", serverId);
			
//			ObjectMapper mapper = new ObjectMapper();
//			JsonNode rootNode = mapper.createObjectNode();
//
//			((ObjectNode) rootNode).put("Action", "GetBoards");
//			((ObjectNode) rootNode).put("UserId", userName);
//			((ObjectNode) rootNode).put("Password", userPassword);

			JSONObject root = new JSONObject();
			root.put("Action", "GetBoards");
			root.put("UserId", userName);
			root.put("Password", userPassword);
			
			con.setRequestProperty("Content-Type", "application/json");
//			con.setRequestProperty("Cookie", "csrftoken=" + this.csrfToken + ";" + " sessionid=" + this.sessionId);
//			con.setRequestProperty("X-CSRFToken", this.csrfToken);

			con.setReadTimeout(5000);
			con.setConnectTimeout(5000);

			con.setDoOutput(true);
			DataOutputStream out = new DataOutputStream(con.getOutputStream());
//			out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
//			out.writeBytes(rootNode.toString());
			out.writeBytes(root.toString());
			out.flush();
			out.close();
			
			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'POST' request to URL : " + cieloDashBoard);
			System.out.println("Post parameters : " + ParameterStringBuilder.getParamsString(parameters));
			System.out.println("Response Code : " + responseCode);

			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				// print result
				if (response.length() > 0) {
					cieloResponse = response.toString();
					System.out.println(cieloResponse);
				}
			} else {
				return false;
			}
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}

