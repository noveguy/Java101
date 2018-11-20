package examples;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonJSONTest {

	public static void main(String[] args) throws JsonParseException, IOException {
		// TODO Auto-generated method stub
//		JsonParser jsonParser = new JsonFactory().createParser(new File("employee.txt"));

		// converting json to Map
		byte[] mapData = Files.readAllBytes(Paths.get("sample1.json"));
		Map<String, List<String>> myMap = new HashMap<String, List<String>>();

		ObjectMapper objectMapper = new ObjectMapper();
		myMap = objectMapper.readValue(mapData, HashMap.class);
		System.out.println("Map is: " + myMap);

		// another way BUT NOT WORKING (TypeReference might be the cause)
//		objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
//		myMap = objectMapper.readValue(mapData, new TypeReference<HashMap<String, List<String>>>() {
//		});
		System.out.println("Map using TypeReference: " + myMap);
	}

}
