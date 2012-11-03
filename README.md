# JSON Utilities
## A toolbox to parse and render JSON text

### Render JSON

Steps:
1. Create a nested map structure using the utility methods and path expressions.
2. Render the nested map to JSON text.

```Java
// Create a map.
Map<String, String> testMap = new LinkedHashMap<String, String>();
JsonUtil.putObjectInMap("achternaam", map, "Ranschaert");
JsonUtil.putObjectInMap("voornaam", map, "Bruno");
// Render to JSON.
String json = JsonUtil.convertToJson(testMap);
```

This will produce something like:

```JavaScript
{"achternaam":"Ranschaert", "voornaam":"Bruno"}
```

### Parse JSON

Steps:
1. Parse the JSON text to a nested map structure.
2. Interrogate the nested map structure using the path expressions.

```JavaScript
{
    "glossary": {
        "title": "example glossary",
		"GlossDiv": {
            "title": "S",
			"GlossList": {
                "GlossEntry": {
                    "ID": "SGML",
					"SortAs": "SGML",
					"GlossTerm": "Standard Generalized Markup Language",
					"Acronym": "SGML",
					"Abbrev": "ISO 8879:1986",
					"GlossDef": {
                        "para": "A meta-markup language, used to create markup languages such as DocBook.",
						"GlossSeeAlso": ["GML", "XML"]
                    },
					"GlossSee": "markup"
                }
            }
        }
    }
}
```


```Java
// Parse the JSON text into a map.
Map<String, Object> map = (Map<String,Object>) JsonUtil.parseJson(example);
// Interrogate the map.
JsonUtil.getStringFromMap("glossary.title", map)
JsonUtil.getObjectFromMap("glossary.GlossDiv.GlossList.GlossEntry.GlossDef.GlossSeeAlso", map)
JsonUtil.getStringFromMap("glossary.GlossDiv.GlossList.GlossEntry.GlossDef.GlossSeeAlso[1]", map
```