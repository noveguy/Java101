package examples;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.amperecomputing.utils.SleepUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jdk.nashorn.internal.parser.JSONParser;

public class HttpTest {

	private final String USER_AGENT = "Mozilla/5.0";

	private static void cieloLogin() throws Exception {
		// TODO Auto-generated method stub
//		URL url = new URL("http://10.38.32.176:8080/accounts/login/");
//		URL cieloHome = new URL("http://10.38.32.176:8080/accounts/login/");
		
		
		URL url = new URL("http://10.38.13.103:8082/accounts/login/");
		URL cieloHome = new URL("http://10.38.13.103:8082/accounts/login/");
		
		HttpURLConnection con = (HttpURLConnection) cieloHome.openConnection();
		Pattern csrfTokenString = Pattern.compile("<(input type='hidden' )(name='csrfmiddlewaretoken' value=')(.*?)(' /)>");
		
		con.setRequestMethod("GET");
		con.setRequestProperty("Connection", "keep-alive");
		con.setReadTimeout(5000);
		con.setConnectTimeout(5000);
		con.setDoOutput(true);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + cieloHome);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		
		String csrfMiddleWareToken = "";

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
			Matcher csrfTokenMatcher = csrfTokenString.matcher(inputLine);
			if (csrfTokenMatcher.find()) {
				csrfMiddleWareToken = csrfTokenMatcher.group(3);
			}
			if (csrfMiddleWareToken.length() != 0) {
				System.out.println(inputLine);
				System.out.println(csrfMiddleWareToken);
				break;
			}
		}
		in.close();

		// print result
//		System.out.println(response.toString());

//		Map<String, List<String>> map = con.getHeaderFields();

//		System.out.println("Printing Response Header...\n");
//
//		for (Map.Entry<String, List<String>> entry : map.entrySet()) {
//			System.out.println("Key : " + entry.getKey() 
//	                           + " ,Value : " + entry.getValue());
//		}

		String headerSetCookie = con.getHeaderField("Set-Cookie");
		System.out.println(headerSetCookie);
		String csrfToken = headerSetCookie.substring(headerSetCookie.indexOf('=') + 1, headerSetCookie.indexOf(';'));
		System.out.println("          " + csrfToken);
		
		con.disconnect();
		
//		SleepUtils.sleep(3000, 0);
		
		HttpURLConnection nextCon = (HttpURLConnection) url.openConnection();

		


		Map<String, String> parameters = new HashMap<>();
		parameters.put("username", "hnghiem");
		parameters.put("password", "amcc1234");

//		RandomString token = new RandomString(64, new SecureRandom());
//		String csrfMiddlewareToken = token.nextString();
//		System.out.println("csrfMiddlewareToken=" + csrfMiddlewareToken);
//
		parameters.put("csrfmiddlewaretoken", csrfMiddleWareToken);

		nextCon.setRequestMethod("POST");
		nextCon.setRequestProperty("Cookie", "csrftoken=" + csrfToken);
		nextCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//		nextCon.setRequestProperty("Connection", "keep-alive");
		nextCon.setReadTimeout(5000);
		nextCon.setConnectTimeout(5000);
		nextCon.setDoOutput(true);
		nextCon.setInstanceFollowRedirects(false);
		
		DataOutputStream out = new DataOutputStream(nextCon.getOutputStream());
		out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
		out.flush();
		out.close();
		
		
		

		responseCode = nextCon.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
		
//		BufferedReader inNext = new BufferedReader(new InputStreamReader(nextCon.getInputStream()));
//		String inputLineNext;
//		StringBuffer responseNext = new StringBuffer();
//
//		while ((inputLineNext = inNext.readLine()) != null) {
//			responseNext.append(inputLineNext);
//			System.out.println(inputLineNext);
//		}
//		inNext.close();

		System.out.println("Printing Response Header...\n");

		Map<String, List<String>> nextMap = nextCon.getHeaderFields();
		for (Map.Entry<String, List<String>> entry : nextMap.entrySet()) {
			System.out.println("Key : " + entry.getKey() 
	                           + " ,Value : " + entry.getValue());
		}
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
//		try {

//			cieloLogin();

			CieloGateWay cieloCon = new CieloGateWay();
			cieloCon.setWebServerIp("10.38.13.103");
			cieloCon.setWebServerPort(8082);
			cieloCon.setUserName("hnghiem");
			cieloCon.setPassword("amcc1234");
			boolean status = cieloCon.openCieloConnection();
			
			if (status) {
				System.out.println("sessionId=" + cieloCon.getSessionId());
				System.out.println("csrftoken=" + cieloCon.getCsrfToken());
			} else {
				System.out.println(status);
			}
			
			
//			Map boardInfos = null;
			if (cieloCon.getBoardsFromCielo()) {
				String jsonBoardInfos = cieloCon.getCieloReponse();
//				boardInfos = JSONParsing.Parse(jsonBoardInfos);
//				System.out.println(boardInfos.toString());
				
				JSONArray boardInfos = new JSONArray(jsonBoardInfos);
				
				Iterator<Object> infos = boardInfos.iterator();
				while(infos.hasNext()) {
					JSONObject infoObject = (JSONObject)infos.next();
//					System.out.println(infoObject.toString());
//					if (infoObject.get("Serial") != null) {
//						System.out.println(infoObject.get("Serial"));
//					}
					try {
						JSONObject detail = infoObject.getJSONObject("Info");
						JSONObject hwInfo = detail.getJSONObject("Hardware");
						Iterator<String> hwInfoKeys = hwInfo.keys();
						while(hwInfoKeys.hasNext()) {
							System.out.println(hwInfo.getString(hwInfoKeys.next()));
						}
						
						System.out.println(hwInfo.toString());
					} catch (Exception e) {
						continue;
					}
				}
				
				cieloCon.releaseBoardForCielo("OSP-A3_104");
				
				if (cieloCon.requestBoardFromCielo("OSP-A3_104")) {
					String jsonRequestBoardInfos = cieloCon.getCieloReponse();
					if (jsonRequestBoardInfos != null) {
						try {
							JSONArray requestBoardInfos = new JSONArray(jsonRequestBoardInfos);
							Iterator<Object> reqInfo = requestBoardInfos.iterator();
							while (reqInfo.hasNext()) {
								JSONObject info = (JSONObject) reqInfo.next();
//								System.out.println(info.toString());
								if(info.has("Port")) {
									System.out.println(info.get("Port"));
								} 
								if (info.has("Server")) {
									System.out.println(info.get("Server"));
								}
							}
//							System.out.println(requestBoardInfos.get("Server"));
//							System.out.println(requestBoardInfos.get("Port"));
						} catch (Exception e) {
							System.out.println("Cant get server & port");
						}
					}
					
					cieloCon.releaseBoardForCielo("OSP-A3_104");
				}

				Map<String, List<String>> myMap = new HashMap<String, List<String>>();
				ObjectMapper objectMapper = new ObjectMapper();
//				myMap = objectMapper.readValue(jsonBoardInfos, HashMap.class);
//				System.out.println("Map is: " + myMap);

				//read JSON like DOM Parser
				JsonNode rootNode = objectMapper.readTree(jsonBoardInfos);
//				JsonNode idNode = rootNode.path("id");
//				System.out.println("id = " + idNode.asInt());
				
//				JsonNode Data = rootNode.findPath("Data");
//				System.out.println(Data.toString());
//				
//				if(Data.has(0)) {
//					for (JsonNode boardNode : Data) {
//						JsonNode boardSerial = boardNode.findValue("Serial");
//						if (boardSerial == null || boardSerial.isNull()) {
//							continue;
//						}
//						System.out.println("We have board " + boardSerial.asText());
//					}
//				}
				
//				List<JsonNode> allSerials = rootNode.findValues("Serial");
//				System.out.println(allSerials.toString());
//				if(cieloCon.requestBoardFromCielo("OSP-A3_094")) {
//					String JsonBoardRequestInfo = cieloCon.getCieloReponse();
//					ObjectMapper objMapper = new ObjectMapper();
//					JsonNode boardRequestInfoRootNode = objMapper.readTree(JsonBoardRequestInfo);
//					JsonNode boardRequestDataNode = boardRequestInfoRootNode.findPath("Data");
//					
//					if (boardRequestDataNode != null && boardRequestDataNode.isNull() == false) {
//						System.out.println(boardRequestDataNode.toString());
//						if (boardRequestDataNode.isNull() == false) {
//							String ipControlServer = boardRequestDataNode.findValue("Server").asText();
//							String portControlServer = boardRequestDataNode.findValue("Port").asText();
//							
//							System.out.println(ipControlServer);
//							System.out.println(portControlServer);
//						}
//					}
//					
//					
//					
//;				}
				
				
				
//				JsonNode idNode = rootNode.path("Serial");
//				System.out.println("Serial = " + idNode.asText());

//				JsonNode phoneNosNode = rootNode.path("phoneNumbers");
//				Iterator<JsonNode> elements = phoneNosNode.elements();
//				while (elements.hasNext()) {
//					JsonNode phone = elements.next();
//					System.out.println("Phone No = " + phone.asLong());
//				}


//				for (Map.Entry<String, List<String>> entry : myMap.entrySet()) {
//					System.out.println("Key : " + entry.getKey() + " ,Value : " + entry.getValue());
//				}
			}
			
//			JSONParser parser = new JSONParser(USER_AGENT, null, status);
			
//			URI uri = new URI("http://10.38.13.103:8080");
			URL url = new URL("http://10.38.13.103:8082/dashboard/");
			
//			HttpURLConnection con = (HttpURLConnection) url.openConnection();
//			con.setRequestMethod("POST");
			
//			Map<String, String> parameters = new HashMap<>();
//			parameters.put("Action", "GetBoards");
//			parameters.put("Action", "RequestBoard");
//			parameters.put("Action", "ReleaseBoard");
//			parameters.put("Serial", "OSP-A2-075 ");
//			parameters.put("ServerId", "1");
			
//			con.setRequestProperty("Cookie", "csrftoken=rSkkWb6EAf6WmpNc9Ehitr9fWKTfV7qBrSAWDfIoGAcyVBJYOslGM2buKNOjHLd6; sessionid=azl4hx5tqn6fk1jx9w1lz9eglz3kyvpm");
//			con.setRequestProperty("X-CSRFToken", "rSkkWb6EAf6WmpNc9Ehitr9fWKTfV7qBrSAWDfIoGAcyVBJYOslGM2buKNOjHLd6");
			
//			con.setRequestProperty("Cookie", "csrftoken=" + cieloCon.getCsrfToken() + ";" + " sessionid=" + cieloCon.getSessionId());
//			con.setRequestProperty("X-CSRFToken", cieloCon.getCsrfToken());
//			
//			con.setReadTimeout(5000);
//			con.setConnectTimeout(5000);
//			
//			con.setDoOutput(true);
//			DataOutputStream out = new DataOutputStream(con.getOutputStream());
//			out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
//			out.flush();
//			out.close();
//			
//			int responseCode = con.getResponseCode();
//			System.out.println("\nSending 'POST' request to URL : " + url);
//			System.out.println("Post parameters : " + ParameterStringBuilder.getParamsString(parameters));
//			System.out.println("Response Code : " + responseCode);
//
//			BufferedReader in = new BufferedReader(
//			        new InputStreamReader(con.getInputStream()));
//			String inputLine;
//			StringBuffer response = new StringBuffer();
//
//			while ((inputLine = in.readLine()) != null) {
//				response.append(inputLine);
//			}
//			in.close();

			// print result
//			System.out.println(response.toString());
//		} catch (URISyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			e.getMessage();
//		}
	}

}
