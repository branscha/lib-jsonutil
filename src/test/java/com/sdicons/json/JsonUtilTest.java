/*******************************************************************************
 * Copyright (c) 2012 Bruno Ranschaert
 * Released under the MIT License: http://opensource.org/licenses/MIT
 * Library "jsonutil"
 ******************************************************************************/
package com.sdicons.json;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.sdicons.json.JsonUtil.PathResolver;

public class JsonUtilTest {

    @Test
    @SuppressWarnings("unchecked")
    public void composeMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        JsonUtil.putObjectInMap("level1.level2.level3.voornaam", map, "Bruno");
        JsonUtil.putObjectInMap("level1.achternaam", map, "Ranschaert");
        JsonUtil.putObjectInMap("level1.level2.level3.straat", map, "Hakelenberg");
        JsonUtil.putObjectInMap("level1.huisnr", map, Integer.valueOf(4));
        JsonUtil.putObjectInMap("level1.level2.married", map, Boolean.TRUE);

        Assert.assertTrue(map.containsKey("level1") && map.get("level1") instanceof Map);
        Map<String, Object> level1 = (Map<String, Object>) map.get("level1");
        Assert.assertTrue(level1.containsKey("achternaam") && "Ranschaert".equals(level1.get("achternaam")));
        Assert.assertTrue(level1.containsKey("huisnr") && (4 == (Integer) level1.get("huisnr")));

        Assert.assertTrue(level1.containsKey("level2") && level1.get("level2") instanceof Map);
        Map<String, Object> level2 = (Map<String, Object>) level1.get("level2");
        Assert.assertTrue(level2.containsKey("married") && Boolean.TRUE.equals(level2.get("married")));

        Assert.assertTrue(level2.containsKey("level3") && level2.get("level3") instanceof Map);
        Map<String, Object> level3 = (Map<String, Object>) level2.get("level3");
        Assert.assertTrue(level3.containsKey("voornaam") && "Bruno".equals(level3.get("voornaam")));
        Assert.assertTrue(level3.containsKey("straat") && "Hakelenberg".equals(level3.get("straat")));
    }

    @Test
    public void convertCombination() {
        Map<String, Object> testMap1 = new LinkedHashMap<String, Object>();
        Map<String, Object> testMap2 = new LinkedHashMap<String, Object>();
        Map<String, Object> testMap3 = new LinkedHashMap<String, Object>();

        testMap1.put("first", testMap2);
        testMap1.put("second", testMap3);
        testMap1.put("third", Boolean.TRUE);

        testMap2.put("oele", "boele");

        testMap3.put("makkis", "voele");
        List<String> testList = new LinkedList<String>();
        testList.addAll(Arrays.asList("a", "b", "c", "d"));
        testMap3.put("alfabet", testList);

        // {"first":{"oele":"boele"},"second":{"makkis":"voele","alfabet":["a","b","c","d"]},
        // "third":true}
        Assert.assertEquals("{\"first\":{\"oele\":\"boele\"},\"second\":{\"makkis\":\"voele\",\"alfabet\":[\"a\",\"b\",\"c\",\"d\"]},\"third\":true}", JsonUtil.convertToJson(testMap1));
    }

    @Test
    public void convertListTest() {
        List<String> testList = new LinkedList<String>();
        testList.addAll(Arrays.asList("Uno", "Duo", "Tres", "Quattuor"));
        Assert.assertEquals("[\"Uno\",\"Duo\",\"Tres\",\"Quattuor\"]", JsonUtil.convertToJson(testList));
    }

    @Test
    public void convertMapTest() {
        Map<String, String> testMap = new LinkedHashMap<String, String>();
        testMap.put("achternaam", "Ranschaert");
        testMap.put("voornaam", "Bruno");
        Assert.assertEquals("{\"achternaam\":\"Ranschaert\",\"voornaam\":\"Bruno\"}", JsonUtil.convertToJson(testMap));
    }
    
    @Test
    public void convertMapPrettyTest() {
        Map<String, Object> testMap = new LinkedHashMap<String, Object>();
        testMap.put("achternaam", "Ranschaert");
        testMap.put("voornaam", "Bruno");
        testMap.put("medals", new LinkedHashMap<String, String>());
        Assert.assertEquals("{\n  \"achternaam\":\"Ranschaert\",\n  \"voornaam\":\"Bruno\",\n  \"medals\":\n    {\n\n    }\n}", JsonUtil.convertToJson(testMap, true));
    }

    @Test
    public void mapAccess() {
        Map<String, Object> map = new HashMap<String, Object>();
        JsonUtil.putObjectInMap("level1.level2.level3.voornaam", map, "Bruno");
        JsonUtil.putObjectInMap("level1.achternaam", map, "Ranschaert");
        JsonUtil.putObjectInMap("level1.level2.level3.straat", map, "Hakelenberg");
        JsonUtil.putObjectInMap("level1.huisnr", map, Integer.valueOf(4));
        JsonUtil.putObjectInMap("level1.level2.married", map, Boolean.TRUE);
        JsonUtil.putObjectInMap("level1.level2.list[0]", map, Boolean.FALSE);
        JsonUtil.putObjectInMap("level1.level2.list[3]", map, 3);
        JsonUtil.putObjectInMap("level1.level2.list[7]", map, "7");
        JsonUtil.putObjectInMap("level1.level2.objlst[0].hallo.nl", map, "wereld");
        JsonUtil.putObjectInMap("level1.level2.objlst[0].hallo.en", map, "world");
        JsonUtil.putObjectInMap("level1.level2.objlst[1].dag", map, "hey");
        JsonUtil.putObjectInMap("level1.level2.objlst[2]", map, 123);
        JsonUtil.putObjectInMap("level1.level2.objlst[3 ][ 1][ 7 ]", map, "3d-array");

        Assert.assertEquals(true, (boolean) JsonUtil.getBoolFromMap("level1.level2.married", map));
        Assert.assertEquals("Bruno", JsonUtil.getStringFromMap("level1.level2.level3.voornaam", map));
        Assert.assertEquals("Hakelenberg", JsonUtil.getStringFromMap("level1.level2.level3.straat", map));
        Assert.assertEquals("Ranschaert", JsonUtil.getStringFromMap("level1.achternaam", map));
        Assert.assertEquals(4, (int) JsonUtil.getIntFromMap("level1.huisnr", map));

        Assert.assertTrue(JsonUtil.getObjectFromMap("level1.level2.list", map) instanceof List);
        Assert.assertEquals(false, (boolean) JsonUtil.getBoolFromMap("level1.level2.list[0]", map));
        Assert.assertEquals(3, (int) JsonUtil.getIntFromMap("level1.level2.list[3]", map));
        Assert.assertEquals("7", JsonUtil.getStringFromMap("level1.level2.list[7]", map));

        Assert.assertTrue(JsonUtil.getObjectFromMap("level1.level2.objlst", map) instanceof List);
        Assert.assertEquals("wereld", JsonUtil.getStringFromMap("level1.level2.objlst[0].hallo.nl", map));
        Assert.assertEquals("world", JsonUtil.getStringFromMap("level1.level2.objlst[0].hallo.en", map));
        Assert.assertEquals("hey", JsonUtil.getStringFromMap("level1.level2.objlst[1].dag", map));
        Assert.assertEquals("3d-array", JsonUtil.getStringFromMap("level1.level2.objlst[3][1][7]", map));

        // Paths that lead nowhere.
        // Index too large
        Assert.assertEquals(null, JsonUtil.getStringFromMap("level1.level2.objlst[200].hallo.en", map));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void mapAccess2() {
        Map<String, Object> map = new HashMap<String, Object>();
        JsonUtil.putObjectInMap("level1.level2.level3.voornaam", map, "Bruno");

        // The structure contains a map at path "leve1", and we try to access it
        // as an array.
        // It is structurally wrong to do so.
        JsonUtil.putObjectInMap("level1[0]", map, "BAD");
     // We should never arrive here.;
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void mapAccess3() {
        Map<String, Object> map = new HashMap<String, Object>();
        JsonUtil.putObjectInMap("level1[1].level2.level3.voornaam", map, "Bruno");

        // The structure contains an array at path "leve1", and we try to access
        // it as a map.
        // It is structurally wrong to do so.
        JsonUtil.putObjectInMap("level1.test", map, "BAD");
        // We should never arrive here.;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void parseCombination() {
        String json = "{\"first\":{\"oele\":\"boele\"},\"second\":{\"makkis\":\"voele\",\"alfabet\":[\"a\",\"b\",\"c\",false]}}";
        Object map = JsonUtil.parseJson(json);
        Assert.assertTrue(map instanceof Map);
        Map<String, Object> result = (Map<String, Object>) map;
        Assert.assertTrue(result.containsKey("first"));
        Assert.assertTrue(result.containsKey("second"));
    }

    @Test
    public void parseListTest() {
        String json = "[\"Uno\", 2, \"Tres\"]";
        Object list = JsonUtil.parseJson(json);
        Assert.assertTrue(list instanceof List);
    }

    @Test
    public void parseObjectTest() {
        String json = "{ \"key1\":\"val1\", \"key2\": \"val2\"}";
        Object map = JsonUtil.parseJson(json);
        Assert.assertTrue(map instanceof Map);
    }
    
    @Test
    public void parseNullTest() {
        String json = "null";
        Object obj = JsonUtil.parseJson(json);
        Assert.assertNull(obj);
    }
    
    @Test
    public void renderNullTest() {
        String json = JsonUtil.convertToJson(null);
        Assert.assertEquals("Expected 'null' since it is a valid JSON identifier.", "null", json);
    }
    
    @Test
    @SuppressWarnings("rawtypes")
    public void parseNullInObject() {
        String json = "{ \"key1\":null, \"key2\": \"val2\"}";
        Object map = JsonUtil.parseJson(json);
        Assert.assertTrue(map instanceof Map);
        Assert.assertTrue(((Map) map).containsKey("key1"));
        Assert.assertNull(((Map) map).get("key1"));
    }
    
    @Test
    public void parseBoolTest() {
        String json = "true";
        Object obj = JsonUtil.parseJson(json);
        Assert.assertTrue(obj instanceof Boolean);
        Assert.assertTrue((Boolean) obj);

        json = "false";
        obj = JsonUtil.parseJson(json);
        Assert.assertTrue(obj instanceof Boolean);
        Assert.assertFalse((Boolean) obj);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseInvalidTest() {
        String json = "INVALIDTEXT";
        JsonUtil.parseJson(json);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseInvalidTest2() {
        // Open ended object.
        String json = "{ 123";
        JsonUtil.parseJson(json);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseInvalidTest3() {
        // Open ended list.
        String json = "[ 123";
        JsonUtil.parseJson(json);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseInvalidTest4() {
        // Bad , placement.
        String json = "[ , ]";
        JsonUtil.parseJson(json);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseInvalidTest5() {
        // Bad , placement.
        String json = "";
        JsonUtil.parseJson(json);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseInvalidTest6() {
        // Bad object content.
        String json = "{[]}";
        JsonUtil.parseJson(json);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseInvalidTest7() {
        // Unclosed object
        String json = "{\"key\":\"value\" x";
        JsonUtil.parseJson(json);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void parseInvalidTest8() {
        // Expected : but received a number.
        String json = "{\"key\" 123.50";
        JsonUtil.parseJson(json);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidMapAccess1() {
        Map<String, Object> map = new HashMap<String, Object>();
        // Index far too large.
        JsonUtil.putObjectInMap("level1[9999999999999999999999999999999999999999999999999]", map, Boolean.TRUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidMapAccess2() {
        Map<String, Object> map = new HashMap<String, Object>();
        // Index far too large.
        Assert.assertEquals(null, JsonUtil.getStringFromMap("level1[9999999999999999999999999999999999999999999999999]", map));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void invalidMapAccess3() {
        // Cannot add intermediate containers to null, there is no place to
        // store new nodes.
        PathResolver path = JsonUtil.compilePath("uno.duo.tres");
        path.put(null,  "oele");
    }

    @Test
    public void parseIntegerTest() {
        String json = "123";
        Object num = JsonUtil.parseJson(json);
        Assert.assertTrue(num instanceof Number);
        Assert.assertEquals(((Number) num).intValue(), 123);
    }

    @Test
    public void renderIntegerTest() {
        String json = JsonUtil.convertToJson(123);
        Assert.assertEquals("Expected '123' since it is a valid JSON identifier.", "123", json);
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void mapGetters() {
        String json = "{ \"intval\":123, \"intval2\":\"345\",\"nulval\": null, \"strval\":\"oele\", \"boolval\":true, \"boolval2\":\"true\", \"boolval3\":\"false\", \"boolval4\":\"bad\"}";
        Object map = JsonUtil.parseJson(json);
        Assert.assertTrue(map instanceof Map);

        Assert.assertEquals(JsonUtil.getIntFromMap("intval", (Map) map), (Integer) 123);
        Assert.assertEquals(JsonUtil.getIntFromMap("intval2", (Map) map), (Integer) 345);
        Assert.assertEquals(JsonUtil.getStringFromMap("strval", (Map) map), "oele");
        Assert.assertTrue(JsonUtil.getBoolFromMap("boolval", (Map) map));
        Assert.assertTrue(JsonUtil.getBoolFromMap("boolval2", (Map) map));
        Assert.assertFalse(JsonUtil.getBoolFromMap("boolval3", (Map) map));
        Assert.assertNull(JsonUtil.getBoolFromMap("boolval4", (Map) map));
        Assert.assertNull(JsonUtil.getIntFromMap("boolval4", (Map) map));

        Assert.assertNull(JsonUtil.getIntFromMap("no-intval", (Map) map));
        Assert.assertNull(JsonUtil.getIntFromMap("strval", (Map) map));
        Assert.assertNull(JsonUtil.getStringFromMap("no-strval", (Map) map));
        Assert.assertNull(JsonUtil.getBoolFromMap("no-boolval", (Map) map));
        Assert.assertNull(JsonUtil.getBoolFromMap("intval", (Map) map));
        Assert.assertNull(JsonUtil.getIntFromMap((String) null, (Map) map));
        Assert.assertNull(JsonUtil.getIntFromMap(".", (Map) map));
        Assert.assertNull(JsonUtil.getIntFromMap("this.leads.to.nowhere", (Map) map));
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testComparison() {
        String left = "{ \"intval\":123, \"intval2\":\"345\",\"nulval\": null, \"strval\":\"oele\", \"boolval\":true, \"boolval2\":\"true\"}";
        String right = "{ \"intval\":123, \"intval2\":\"345\",\"nulval\": null, \"strval\":\"boele\", \"boolval\":true, \"aiai\":\"true\"}";
        Object leftMap = JsonUtil.parseJson(left);
        Object rightMap = JsonUtil.parseJson(right);

        // Calculate the differences.
        List<String> differences = JsonUtil.compareMaps((Map<String, Object>) leftMap, (Map<String, Object>) rightMap);

        // Examine the differences.
        Assert.assertTrue(differences.size() == 3);
        Assert.assertTrue(JsonUtil.hasAddition(differences, "aiai"));
        Assert.assertTrue(JsonUtil.hasRemoval(differences, "boolval2"));
        Assert.assertTrue(JsonUtil.hasChange(differences, "strval"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testComparison2() {
        String left = "{\"first\":{\"oele\":\"Xoele\"},\"second\":{\"makkis\":\"voele\",\"alfabet\":[\"a\",\"b\",\"c\",false]}}";
        String right = "{\"first\":{\"oele\":\"Yoele\"},\"second\":{\"letters\":[\"a\",\"b\",\"c\",false]}}";
        Object leftMap = JsonUtil.parseJson(left);
        Object rightMap = JsonUtil.parseJson(right);

        // Calculate the differences.
        List<String> differences = JsonUtil.compareMaps((Map<String, Object>) leftMap, (Map<String, Object>) rightMap);

        // Examine the differences.
        Assert.assertTrue(differences.size() == 4);
        Assert.assertTrue(JsonUtil.hasChange(differences, "first.oele"));
        Assert.assertTrue(JsonUtil.hasRemoval(differences, "second.alfabet"));
        Assert.assertTrue(JsonUtil.hasRemoval(differences, "second.makkis"));
        Assert.assertTrue(JsonUtil.hasAddition(differences, "second.letters"));

        // Wrong statements.
        Assert.assertFalse(JsonUtil.hasAddition(differences, "watisdat.hier"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testComparison3() {
        // Difference between key with null value and
        // absence of the key.
        String left = "{\"key\":null}";
        String right = "{}";
        Object leftMap = JsonUtil.parseJson(left);
        Object rightMap = JsonUtil.parseJson(right);
        List<String> differences = JsonUtil.compareMaps((Map<String, Object>) leftMap, (Map<String, Object>) rightMap);
        Assert.assertTrue(differences.size() == 1);

        // Difference between absence of the key and a
        // key with null value.
        left = "{}";
        right = "{\"key\":null}";
        leftMap = JsonUtil.parseJson(left);
        rightMap = JsonUtil.parseJson(right);
        differences = JsonUtil.compareMaps((Map<String, Object>) leftMap, (Map<String, Object>) rightMap);
        Assert.assertTrue(differences.size() == 1);
        Assert.assertTrue(JsonUtil.hasAddition(differences, "key"));

        // Special case where a key contains null values.
        left = "{\"key\":null}";
        right = "{\"key\":null}";
        leftMap = JsonUtil.parseJson(left);
        rightMap = JsonUtil.parseJson(right);
        differences = JsonUtil.compareMaps((Map<String, Object>) leftMap, (Map<String, Object>) rightMap);
        Assert.assertTrue(differences.size() == 0);
    }
    
    @Test
    public void testCrockfordExample1() {
        String example = "{\n" + 
                "    \"glossary\": {\n" + 
                "        \"title\": \"example glossary\",\n" + 
                "        \"GlossDiv\": {\n" + 
                "            \"title\": \"S\",\n" + 
                "            \"GlossList\": {\n" + 
                "                \"GlossEntry\": {\n" + 
                "                    \"ID\": \"SGML\",\n" + 
                "                    \"SortAs\": \"SGML\",\n" + 
                "                    \"GlossTerm\": \"Standard Generalized Markup Language\",\n" + 
                "                    \"Acronym\": \"SGML\",\n" + 
                "                    \"Abbrev\": \"ISO 8879:1986\",\n" + 
                "                    \"GlossDef\": {\n" + 
                "                        \"para\": \"A meta-markup language, used to create markup languages such as DocBook.\",\n" + 
                "                        \"GlossSeeAlso\": [\"GML\", \"XML\"]\n" + 
                "                    },\n" + 
                "                    \"GlossSee\": \"markup\"\n" + 
                "                }\n" + 
                "            }\n" + 
                "        }\n" + 
                "    }\n" + 
                "}";
        Object map = JsonUtil.parseJson(example);
        Assert.assertTrue(map instanceof Map);
        Assert.assertTrue(JsonUtil.getObjectFromMap("glossary", (Map<?, ?>) map) instanceof Map);
        Assert.assertEquals(JsonUtil.getStringFromMap("glossary.title", (Map<?, ?>) map), "example glossary");
        Assert.assertTrue(JsonUtil.getObjectFromMap("glossary.GlossDiv.GlossList.GlossEntry.GlossDef.GlossSeeAlso", (Map<?, ?>) map) instanceof List);
        Assert.assertEquals(JsonUtil.getStringFromMap("glossary.GlossDiv.GlossList.GlossEntry.GlossDef.GlossSeeAlso[1]", (Map<?, ?>) map), "XML");
    }
    
    @Test
    public void testCrockfordExample2() {
        String example = "{\"menu\": {\n" + 
                "  \"id\": \"file\",\n" + 
                "  \"value\": \"File\",\n" + 
                "  \"popup\": {\n" + 
                "    \"menuitem\": [\n" + 
                "      {\"value\": \"New\", \"onclick\": \"CreateNewDoc()\"},\n" + 
                "      {\"value\": \"Open\", \"onclick\": \"OpenDoc()\"},\n" + 
                "      {\"value\": 'Close', \"onclick\": \"CloseDoc()\"}\n" + 
                "    ]\n" + 
                "  }\n" + 
                "}}";
        Object map = JsonUtil.parseJson(example);
        Assert.assertTrue(map instanceof Map);
    }
    
    @Test
    public void testCrockfordExample2Bis() {
        // This example uses single quotes for strings.
        // The JSON utility should adjust automatically for this case as well.
        // A JSON string in Java code looks much more elegant in this way.
        String example = "{'menu': {" + 
            "  'id': 'file'," + 
            "  'value': 'File'," + 
            "  'popup': {" + 
            "    'menuitem': [" + 
            "      {'value': 'New', 'onclick': 'CreateNewDoc()'}," + 
            "      {'value': 'Open', 'onclick': 'OpenDoc()'}," + 
            "      {'value': 'Close', 'onclick': 'CloseDoc()'}" + 
            "    ]" + 
            "  }" + 
            "}}";
        Object map = JsonUtil.parseJson(example);
        Assert.assertTrue(map instanceof Map);
    }
    
    @Test
    public void testCrockfordExample3() {
        String example = "{\"widget\": {\n" + 
                "    \"debug\": \"on\",\n" + 
                "    \"window\": {\n" + 
                "        \"title\": \"Sample Konfabulator Widget\",\n" + 
                "        \"name\": \"main_window\",\n" + 
                "        \"width\": 500,\n" + 
                "        \"height\": 500\n" + 
                "    },\n" + 
                "    \"image\": { \n" + 
                "        \"src\": \"Images/Sun.png\",\n" + 
                "        \"name\": \"sun1\",\n" + 
                "        \"hOffset\": 250,\n" + 
                "        \"vOffset\": 250,\n" + 
                "        \"alignment\": \"center\"\n" + 
                "    },\n" + 
                "    \"text\": {\n" + 
                "        \"data\": \"Click Here\",\n" + 
                "        \"size\": 36,\n" + 
                "        \"style\": \"bold\",\n" + 
                "        \"name\": \"text1\",\n" + 
                "        \"hOffset\": 250,\n" + 
                "        \"vOffset\": 100,\n" + 
                "        \"alignment\": \"center\",\n" + 
                "        \"onMouseUp\": \"sun1.opacity = (sun1.opacity / 100) * 90;\"\n" + 
                "    }\n" + 
                "}} ";
        Object map = JsonUtil.parseJson(example);
        Assert.assertTrue(map instanceof Map);
    }
    
    @Test
    public void testCrockfordExample4() {
        String example = "{\"web-app\": {\n" + 
                "  \"servlet\": [   \n" + 
                "    {\n" + 
                "      \"servlet-name\": \"cofaxCDS\",\n" + 
                "      \"servlet-class\": \"org.cofax.cds.CDSServlet\",\n" + 
                "      \"init-param\": {\n" + 
                "        \"configGlossary:installationAt\": \"Philadelphia, PA\",\n" + 
                "        \"configGlossary:adminEmail\": \"ksm@pobox.com\",\n" + 
                "        \"configGlossary:poweredBy\": \"Cofax\",\n" + 
                "        \"configGlossary:poweredByIcon\": \"/images/cofax.gif\",\n" + 
                "        \"configGlossary:staticPath\": \"/content/static\",\n" + 
                "        \"templateProcessorClass\": \"org.cofax.WysiwygTemplate\",\n" + 
                "        \"templateLoaderClass\": \"org.cofax.FilesTemplateLoader\",\n" + 
                "        \"templatePath\": \"templates\",\n" + 
                "        \"templateOverridePath\": \"\",\n" + 
                "        \"defaultListTemplate\": \"listTemplate.htm\",\n" + 
                "        \"defaultFileTemplate\": \"articleTemplate.htm\",\n" + 
                "        \"useJSP\": false,\n" + 
                "        \"jspListTemplate\": \"listTemplate.jsp\",\n" + 
                "        \"jspFileTemplate\": \"articleTemplate.jsp\",\n" + 
                "        \"cachePackageTagsTrack\": 200,\n" + 
                "        \"cachePackageTagsStore\": 200,\n" + 
                "        \"cachePackageTagsRefresh\": 60,\n" + 
                "        \"cacheTemplatesTrack\": 100,\n" + 
                "        \"cacheTemplatesStore\": 50,\n" + 
                "        \"cacheTemplatesRefresh\": 15,\n" + 
                "        \"cachePagesTrack\": 200,\n" + 
                "        \"cachePagesStore\": 100,\n" + 
                "        \"cachePagesRefresh\": 10,\n" + 
                "        \"cachePagesDirtyRead\": 10,\n" + 
                "        \"searchEngineListTemplate\": \"forSearchEnginesList.htm\",\n" + 
                "        \"searchEngineFileTemplate\": \"forSearchEngines.htm\",\n" + 
                "        \"searchEngineRobotsDb\": \"WEB-INF/robots.db\",\n" + 
                "        \"useDataStore\": true,\n" + 
                "        \"dataStoreClass\": \"org.cofax.SqlDataStore\",\n" + 
                "        \"redirectionClass\": \"org.cofax.SqlRedirection\",\n" + 
                "        \"dataStoreName\": \"cofax\",\n" + 
                "        \"dataStoreDriver\": \"com.microsoft.jdbc.sqlserver.SQLServerDriver\",\n" + 
                "        \"dataStoreUrl\": \"jdbc:microsoft:sqlserver://LOCALHOST:1433;DatabaseName=goon\",\n" + 
                "        \"dataStoreUser\": \"sa\",\n" + 
                "        \"dataStorePassword\": \"dataStoreTestQuery\",\n" + 
                "        \"dataStoreTestQuery\": \"SET NOCOUNT ON;select test='test';\",\n" + 
                "        \"dataStoreLogFile\": \"/usr/local/tomcat/logs/datastore.log\",\n" + 
                "        \"dataStoreInitConns\": 10,\n" + 
                "        \"dataStoreMaxConns\": 100,\n" + 
                "        \"dataStoreConnUsageLimit\": 100,\n" + 
                "        \"dataStoreLogLevel\": \"debug\",\n" + 
                "        \"maxUrlLength\": 500}},\n" + 
                "    {\n" + 
                "      \"servlet-name\": \"cofaxEmail\",\n" + 
                "      \"servlet-class\": \"org.cofax.cds.EmailServlet\",\n" + 
                "      \"init-param\": {\n" + 
                "      \"mailHost\": \"mail1\",\n" + 
                "      \"mailHostOverride\": \"mail2\"}},\n" + 
                "    {\n" + 
                "      \"servlet-name\": \"cofaxAdmin\",\n" + 
                "      \"servlet-class\": \"org.cofax.cds.AdminServlet\"},\n" + 
                " \n" + 
                "    {\n" + 
                "      \"servlet-name\": \"fileServlet\",\n" + 
                "      \"servlet-class\": \"org.cofax.cds.FileServlet\"},\n" + 
                "    {\n" + 
                "      \"servlet-name\": \"cofaxTools\",\n" + 
                "      \"servlet-class\": \"org.cofax.cms.CofaxToolsServlet\",\n" + 
                "      \"init-param\": {\n" + 
                "        \"templatePath\": \"toolstemplates/\",\n" + 
                "        \"log\": 1,\n" + 
                "        \"logLocation\": \"/usr/local/tomcat/logs/CofaxTools.log\",\n" + 
                "        \"logMaxSize\": \"\",\n" + 
                "        \"dataLog\": 1,\n" + 
                "        \"dataLogLocation\": \"/usr/local/tomcat/logs/dataLog.log\",\n" + 
                "        \"dataLogMaxSize\": \"\",\n" + 
                "        \"removePageCache\": \"/content/admin/remove?cache=pages&id=\",\n" + 
                "        \"removeTemplateCache\": \"/content/admin/remove?cache=templates&id=\",\n" + 
                "        \"fileTransferFolder\": \"/usr/local/tomcat/webapps/content/fileTransferFolder\",\n" + 
                "        \"lookInContext\": 1,\n" + 
                "        \"adminGroupID\": 4,\n" + 
                "        \"betaServer\": true}}],\n" + 
                "  \"servlet-mapping\": {\n" + 
                "    \"cofaxCDS\": \"/\",\n" + 
                "    \"cofaxEmail\": \"/cofaxutil/aemail/*\",\n" + 
                "    \"cofaxAdmin\": \"/admin/*\",\n" + 
                "    \"fileServlet\": \"/static/*\",\n" + 
                "    \"cofaxTools\": \"/tools/*\"},\n" + 
                " \n" + 
                "  \"taglib\": {\n" + 
                "    \"taglib-uri\": \"cofax.tld\",\n" + 
                "    \"taglib-location\": \"/WEB-INF/tlds/cofax.tld\"}}}";
        
        // Make the parser sweat.
        // Check if there are objects lingering around (leaks).
        for (int i = 0; i < 100; i++) {
            Object map = JsonUtil.parseJson(example);
            Assert.assertTrue(map instanceof Map);
        }
    }
    
    @Test
    public void testCrockfordExample5() {
        String example = "{\"menu\": {\n" + 
                "    \"header\": \"SVG Viewer\",\n" + 
                "    \"items\": [\n" + 
                "        {\"id\": \"Open\"},\n" + 
                "        {\"id\": \"OpenNew\", \"label\": \"Open New\"},\n" + 
                "        null,\n" + 
                "        {\"id\": \"ZoomIn\", \"label\": \"Zoom In\"},\n" + 
                "        {\"id\": \"ZoomOut\", \"label\": \"Zoom Out\"},\n" + 
                "        {\"id\": \"OriginalView\", \"label\": \"Original View\"},\n" + 
                "        null,\n" + 
                "        {\"id\": \"Quality\"},\n" + 
                "        {\"id\": \"Pause\"},\n" + 
                "        {\"id\": \"Mute\"},\n" + 
                "        null,\n" + 
                "        {\"id\": \"Find\", \"label\": \"Find...\"},\n" + 
                "        {\"id\": \"FindAgain\", \"label\": \"Find Again\"},\n" + 
                "        {\"id\": \"Copy\"},\n" + 
                "        {\"id\": \"CopyAgain\", \"label\": \"Copy Again\"},\n" + 
                "        {\"id\": \"CopySVG\", \"label\": \"Copy SVG\"},\n" + 
                "        {\"id\": \"ViewSVG\", \"label\": \"View SVG\"},\n" + 
                "        {\"id\": \"ViewSource\", \"label\": \"View Source\"},\n" + 
                "        {\"id\": \"SaveAs\", \"label\": \"Save As\"},\n" + 
                "        null,\n" + 
                "        {\"id\": \"Help\"},\n" + 
                "        {\"id\": \"About\", \"label\": \"About Adobe CVG Viewer...\"}\n" + 
                "    ]\n" + 
                "}}";
        Object map = JsonUtil.parseJson(example);
        Assert.assertTrue(map instanceof Map);
    }
    
    @Test
    public void testAdobe1() {
        String example = "[ 100, 500, 300, 200, 400 ]";
        Object map = JsonUtil.parseJson(example);
        Assert.assertTrue(map instanceof List);
    }
    
    @Test
    public void testAdobe2() {
        String example = "{\n" + 
                "    \"id\": \"0001\",\n" + 
                "    \"type\": \"donut\",\n" + 
                "    \"name\": \"Cake\",\n" + 
                "    \"ppu\": 0.55,\n" + 
                "    \"batters\":\n" + 
                "        {\n" + 
                "            \"batter\":\n" + 
                "                [\n" + 
                "                    { \"id\": \"1001\", \"type\": \"Regular\" },\n" + 
                "                    { \"id\": \"1002\", \"type\": \"Chocolate\" },\n" + 
                "                    { \"id\": \"1003\", \"type\": \"Blueberry\" },\n" + 
                "                    { \"id\": \"1004\", \"type\": \"Devil's Food\" }\n" + 
                "                ]\n" + 
                "        },\n" + 
                "    \"topping\":\n" + 
                "        [\n" + 
                "            { \"id\": \"5001\", \"type\": \"None\" },\n" + 
                "            { \"id\": \"5002\", \"type\": \"Glazed\" },\n" + 
                "            { \"id\": \"5005\", \"type\": \"Sugar\" },\n" + 
                "            { \"id\": \"5007\", \"type\": \"Powdered Sugar\" },\n" + 
                "            { \"id\": \"5006\", \"type\": \"Chocolate with Sprinkles\" },\n" + 
                "            { \"id\": \"5003\", \"type\": \"Chocolate\" },\n" + 
                "            { \"id\": \"5004\", \"type\": \"Maple\" }\n" + 
                "        ]\n" + 
                "}";
        Object map = JsonUtil.parseJson(example);
        Assert.assertTrue(map instanceof Map);
    }
    
    @Test
    public void testAdobe3() {
        String example = "[\n" + 
                "    {\n" + 
                "        \"id\": \"0001\",\n" + 
                "        \"type\": \"donut\",\n" + 
                "        \"name\": \"Cake\",\n" + 
                "        \"ppu\": 0.55,\n" + 
                "        \"batters\":\n" + 
                "            {\n" + 
                "                \"batter\":\n" + 
                "                    [\n" + 
                "                        { \"id\": \"1001\", \"type\": \"Regular\" },\n" + 
                "                        { \"id\": \"1002\", \"type\": \"Chocolate\" },\n" + 
                "                        { \"id\": \"1003\", \"type\": \"Blueberry\" },\n" + 
                "                        { \"id\": \"1004\", \"type\": \"Devil's Food\" }\n" + 
                "                    ]\n" + 
                "            },\n" + 
                "        \"topping\":\n" + 
                "            [\n" + 
                "                { \"id\": \"5001\", \"type\": \"None\" },\n" + 
                "                { \"id\": \"5002\", \"type\": \"Glazed\" },\n" + 
                "                { \"id\": \"5005\", \"type\": \"Sugar\" },\n" + 
                "                { \"id\": \"5007\", \"type\": \"Powdered Sugar\" },\n" + 
                "                { \"id\": \"5006\", \"type\": \"Chocolate with Sprinkles\" },\n" + 
                "                { \"id\": \"5003\", \"type\": \"Chocolate\" },\n" + 
                "                { \"id\": \"5004\", \"type\": \"Maple\" }\n" + 
                "            ]\n" + 
                "    },\n" + 
                "    {\n" + 
                "        \"id\": \"0002\",\n" + 
                "        \"type\": \"donut\",\n" + 
                "        \"name\": \"Raised\",\n" + 
                "        \"ppu\": 0.55,\n" + 
                "        \"batters\":\n" + 
                "            {\n" + 
                "                \"batter\":\n" + 
                "                    [\n" + 
                "                        { \"id\": \"1001\", \"type\": \"Regular\" }\n" + 
                "                    ]\n" + 
                "            },\n" + 
                "        \"topping\":\n" + 
                "            [\n" + 
                "                { \"id\": \"5001\", \"type\": \"None\" },\n" + 
                "                { \"id\": \"5002\", \"type\": \"Glazed\" },\n" + 
                "                { \"id\": \"5005\", \"type\": \"Sugar\" },\n" + 
                "                { \"id\": \"5003\", \"type\": \"Chocolate\" },\n" + 
                "                { \"id\": \"5004\", \"type\": \"Maple\" }\n" + 
                "            ]\n" + 
                "    },\n" + 
                "    {\n" + 
                "        \"id\": \"0003\",\n" + 
                "        \"type\": \"donut\",\n" + 
                "        \"name\": \"Old Fashioned\",\n" + 
                "        \"ppu\": 0.55,\n" + 
                "        \"batters\":\n" + 
                "            {\n" + 
                "                \"batter\":\n" + 
                "                    [\n" + 
                "                        { \"id\": \"1001\", \"type\": \"Regular\" },\n" + 
                "                        { \"id\": \"1002\", \"type\": \"Chocolate\" }\n" + 
                "                    ]\n" + 
                "            },\n" + 
                "        \"topping\":\n" + 
                "            [\n" + 
                "                { \"id\": \"5001\", \"type\": \"None\" },\n" + 
                "                { \"id\": \"5002\", \"type\": \"Glazed\" },\n" + 
                "                { \"id\": \"5003\", \"type\": \"Chocolate\" },\n" + 
                "                { \"id\": \"5004\", \"type\": \"Maple\" }\n" + 
                "            ]\n" + 
                "    }\n" + 
                "]";
        Object map = JsonUtil.parseJson(example);
        Assert.assertTrue(map instanceof List);
    }
    
    @Test
    public void testAdobe4() {
        String example = "{\n" + 
                "    \"id\": \"0001\",\n" + 
                "    \"type\": \"donut\",\n" + 
                "    \"name\": \"Cake\",\n" + 
                "    \"image\":\n" + 
                "        {\n" + 
                "            \"url\": \"images/0001.jpg\",\n" + 
                "            \"width\": 200,\n" + 
                "            \"height\": 200\n" + 
                "        },\n" + 
                "    \"thumbnail\":\n" + 
                "        {\n" + 
                "            \"url\": \"images/thumbnails/0001.jpg\",\n" + 
                "            \"width\": 32,\n" + 
                "            \"height\": 32\n" + 
                "        }\n" + 
                "}";
        Object map = JsonUtil.parseJson(example);
        Assert.assertTrue(map instanceof Map);
    }
    
    @Test
    public void testGoogle1() {
        String example = "{\n" + 
                "  \"apiVersion\": \"2.0\",\n" + 
                "  \"data\": {\n" + 
                "    \"updated\": \"2010-02-04T19:29:54.001Z\",\n" + 
                "    \"totalItems\": 6741,\n" + 
                "    \"startIndex\": 1,\n" + 
                "    \"itemsPerPage\": 1,\n" + 
                "    \"items\": [\n" + 
                "      {\n" + 
                "        \"id\": \"BGODurRfVv4\",\n" + 
                "        \"uploaded\": \"2009-11-17T20:10:06.000Z\",\n" + 
                "        \"updated\": \"2010-02-04T06:25:57.000Z\",\n" + 
                "        \"uploader\": \"docchat\",\n" + 
                "        \"category\": \"Animals\",\n" + 
                "        \"title\": \"From service dog to SURFice dog\",\n" + 
                "        \"description\": \"Surf dog Ricochets inspirational video ...\",\n" + 
                "        \"tags\": [\n" + 
                "          \"Surf dog\",\n" + 
                "          \"dog surfing\",\n" + 
                "          \"dog\",\n" + 
                "          \"golden retriever\",\n" + 
                "        ],\n" + 
                "        \"thumbnail\": {\n" + 
                "          \"default\": \"http://i.ytimg.com/vi/BGODurRfVv4/default.jpg\",\n" + 
                "          \"hqDefault\": \"http://i.ytimg.com/vi/BGODurRfVv4/hqdefault.jpg\"\n" + 
                "        },\n" + 
                "        \"player\": {\n" + 
                "          \"default\": \"http://www.youtube.com/watch?v=BGODurRfVv4&feature=youtube_gdata\",\n" + 
                "          \"mobile\": \"http://m.youtube.com/details?v=BGODurRfVv4\"\n" + 
                "        },\n" + 
                "        \"content\": {\n" + 
                "          \"1\": \"rtsp://v5.cache6.c.youtube.com/CiILENy73wIaGQn-Vl-0uoNjBBMYDSANFEgGUgZ2aWRlb3MM/0/0/0/video.3gp\",\n" + 
                "          \"5\": \"http://www.youtube.com/v/BGODurRfVv4?f=videos&app=youtube_gdata\",\n" + 
                "          \"6\": \"rtsp://v7.cache7.c.youtube.com/CiILENy73wIaGQn-Vl-0uoNjBBMYESARFEgGUgZ2aWRlb3MM/0/0/0/video.3gp\"\n" + 
                "        },\n" + 
                "        \"duration\": 315,\n" + 
                "        \"rating\": 4.96,\n" + 
                "        \"ratingCount\": 2043,\n" + 
                "        \"viewCount\": 1781691,\n" + 
                "        \"favoriteCount\": 3363,\n" + 
                "        \"commentCount\": 1007,\n" + 
                "        \"commentsAllowed\": true\n" + 
                "      }\n" + 
                "    ]\n" + 
                "  }\n" + 
                "}";
        Object map = JsonUtil.parseJson(example);
        Assert.assertTrue(map instanceof Map);
        Assert.assertEquals(JsonUtil.getStringFromMap("data.items[0].id", (Map<?, ?>) map), "BGODurRfVv4");
        Assert.assertEquals(JsonUtil.getStringFromMap("data.items[0].tags[1]", (Map<?, ?>) map), "dog surfing");
        Assert.assertTrue(JsonUtil.getBoolFromMap("data.items[0].commentsAllowed", (Map<?, ?>) map));
    }
    
    // A technical test to test the handling of an IOException in the 
    // main JSON parsing routine.
    // Code coverage.
    @Test(expected=IllegalArgumentException.class)
    public void parseIOExceptionTest() throws Exception {
        // The tokenizer will immediately fail.
        //
        StreamTokenizer st = mock(StreamTokenizer.class);
        when(st.nextToken()).thenThrow(new IOException("Mock Tokenizer Error"));
        //
        StringBuilder parsed = new StringBuilder();
        JsonUtil.parseJson(st, parsed);
        //
        // We should never arrive here.
    }
    
    // A technical test to invoke an IOException while parsing a JSON object.
    // It is used to test the exception handling in the object parsing procedure.
    // Code coverage.
    @Test(expected=IllegalArgumentException.class)
    public void parseIOExceptionTest2() throws Exception {
        // Create a real tokenizer first.
        final StreamTokenizer st = Mockito.spy(new StreamTokenizer(new StringReader("")));
        // Overwrite the tokenizer behavior. The first tree tokens will behave as the start
        // of a JSON object, but the fourth invocation will throw an exception.
        Answer<?> answer = new Answer<Object>() {
            private int counter = 0;
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                switch(counter) {
                case 0:
                    st.ttype = '{';
                    st.sval = "{";
                    break;
                case 1:
                case 2:
                    st.ttype = '"';
                    st.sval = "key";
                    break;
                default: 
                    throw new IOException("Got you!");
                }
                counter++;
                return null;
            }
        };
        // Impose the mocking behavior on the real tokenizer using
        // the Mockito framework.
        Mockito.doAnswer(answer).when(st).nextToken();
        //
        StringBuilder parsed = new StringBuilder();
        JsonUtil.parseJson(st, parsed);
        //
        // We should never arrive here.
    }
    
    // A technical test to invoke an IOException while parsing a JSON list.
    // It is used to test the exception handling in the list parsing procedure.
    // Code coverage.
    @Test(expected=IllegalArgumentException.class)
    public void parseIOExceptionTest3() throws Exception {
        // Create a real tokenizer first.
        final StreamTokenizer st = Mockito.spy(new StreamTokenizer(new StringReader("")));
        // Overwrite the tokenizer behavior. The first tree tokens will behave as the start
        // of a JSON list, but the fourth invocation will throw an exception.
        Answer<?> answer = new Answer<Object>() {
            private int counter = 0;
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                switch(counter) {
                case 0:
                    st.ttype = '[';
                    st.sval = "[";
                    break;
                case 1:
                case 2:
                    st.ttype = '"';
                    st.sval = "key";
                    break;
                default: 
                    throw new IOException("Got you!");
                }
                counter++;
                return null;
            }
        };
        // Impose the mocking behavior on the real tokenizer using
        // the Mockito framework.
        Mockito.doAnswer(answer).when(st).nextToken();
        //
        StringBuilder parsed = new StringBuilder();
        JsonUtil.parseJson(st, parsed);
        //
        // We should never arrive here.
    }
    
    @Test
    public void bufferedReaderParserTest() {
        Object obj = JsonUtil.parseJson(new BufferedReader(new StringReader("{}")));
        Assert.assertNotNull(obj);
    }
}
