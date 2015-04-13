package blackdoor.cqbe;

import org.json.JSONObject;

public class Tinker {

	public static void main(String[] args) {
		JSONObject jsObj = new JSONObject();
		Object o = null;
		jsObj.put("testKey2", JSONObject.NULL);
		jsObj.put("testKey", o);
		jsObj.put("false", Boolean.FALSE);
		System.out.println(jsObj);

		
		System.out.println(Character.isJavaIdentifierStart('Ω'));
		System.out.println(Character.isJavaIdentifierPart('Ω'));
	}

}
