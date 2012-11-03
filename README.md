# JSON Utilities
## A toolbox to parse and render JSON text

### Render JSON

Steps:
1. Create a nested map structure using the utility methods and path expressions.
2. Render the nested map to JSON text.

'''Java
Map<String, String> testMap = new LinkedHashMap<String, String>();
testMap.put("achternaam", "Ranschaert");
testMap.put("voornaam", "Bruno");
String json = JsonUtil.convertToJson(testMap);
'''

This will produce something like:

'''JavaScript
{"achternaam":"Ranschaert", "voornaam":"Bruno"}
'''

### Parse JSON

Steps:
1. Parse the JSON text to a nested map structure.
2. Interrogate the nested map structure using the path expressions.