# JSON Utilities
## A toolbox to parse and render JSON text
### What is it
A utility class containing a number of static methods to read and write [JSON text][1].

* No external dependencies, the implementation only uses standard Java.
* Parsing to generic Java Map/List instances.
* Optional support to get/put values in the nested map data structure by using path expressions. This is only a convenience, you can use the nested Maps directly  as well.
* Thoroughly profiled and tested.

### Render JSON

Steps:

1. Create a nested map structure using the utility methods and path expressions.
2. Render the nested map to JSON text.

```Java
// Create a map.
Map<String, String> testMap = new LinkedHashMap<String, String>();
JsonUtil.putObjectInMap("lastName", map, "Ranschaert");
JsonUtil.putObjectInMap("firstName", map, "Bruno");
// Render to JSON.
String json = JsonUtil.convertToJson(testMap);
```

This will produce something like:

```JavaScript
{"lastName":"Ranschaert", "firstName":"Bruno"}
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

### Build the project

The project is Maven based, it contains a single utility class and its corresponding JUnit test.

```
mvn clean install
```

### Links

* [JSON Format][1]

[1]: http://www.json.org/