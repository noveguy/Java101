package examples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JSONParsing {
	private static ScriptEngine engine;
	private static boolean isEngineInitiated = false;
	
	private static void initEngine() {
		ScriptEngineManager sem = new ScriptEngineManager();
		engine = sem.getEngineByName("javascript");
		isEngineInitiated = true;
	}
	
	public static void Parse(Path jsonFile) throws Exception {
		// TODO Auto-generated method stub
		if (isEngineInitiated == false) {
			initEngine();
		}
//		Path sample = Paths.get("sample.json");
		
		String json = new String(Files.readAllBytes(jsonFile));
		
		String script = "Java.asJSONCompatible(" + json + ")";
		System.out.println(script);
		try {
			Object result = engine.eval(script);
			Map contents = (Map) result;
			System.out.println("Done");
//			contents.forEach((t,u) -> {
//				System.out.println(t);
//				System.out.println(u);
//			});
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Map Parse(String jsonContent) throws Exception {
		// TODO Auto-generated method stub
		if (isEngineInitiated == false) {
			initEngine();
		}
//		Path sample = Paths.get("sample.json");
//		
//		String json = new String(Files.readAllBytes(sample));
		
		String script = "Java.asJSONCompatible(" + jsonContent + ")";
		System.out.println(script);
		try {
			Object result = engine.eval(script);
			Map contents = (Map) result;
			System.out.println("Done");
			return contents;
//			contents.forEach((t,u) -> {
//				System.out.println(t);
//				System.out.println(u);
//			});
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
