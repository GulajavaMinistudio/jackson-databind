package com.fasterxml.jackson.databind.exc;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class BasicExceptionTest extends BaseMapTest
{
    static class User {
        public String user;
    }

    static class Users {
        public ArrayList<User> userList;
    }

    private final ObjectMapper MAPPER = newJsonMapper();

    public void testBadDefinition() throws Exception
    {
        JavaType t = TypeFactory.defaultInstance().constructType(String.class);
        JsonParser p = MAPPER.createParser("[]");
        InvalidDefinitionException e = new InvalidDefinitionException(p,
               "Testing", t);
        assertEquals("Testing", e.getOriginalMessage());
        assertEquals(String.class, e.getType().getRawClass());
        assertNull(e.getBeanDescription());
        assertNull(e.getProperty());
        assertSame(p, e.getProcessor());
        p.close();

        // and via factory method:
        BeanDescription beanDef = ObjectMapperTestAccess.beanDescriptionForSer(MAPPER, getClass());
        e = InvalidDefinitionException.from(p, "Testing",
                beanDef, (BeanPropertyDefinition) null);
        assertEquals(beanDef.getType(), e.getType());
        assertNotNull(e);

        // and the other constructor too
        e = new InvalidDefinitionException(p,
                "Testing", t);
        assertEquals("Testing", e.getOriginalMessage());
        assertEquals(String.class, e.getType().getRawClass());

        // and factory
        JsonGenerator g = MAPPER.createGenerator(new StringWriter());
        e = InvalidDefinitionException.from(g, "Testing",
                beanDef, (BeanPropertyDefinition) null);
        assertEquals(beanDef.getType(), e.getType());
        assertNotNull(e);
        
        g.close();
    }

    public void testIgnoredProperty() throws Exception
    {
        // first just construct valid instance with some variations
        JsonParser p = MAPPER.createParser("{ }");
        IgnoredPropertyException e = IgnoredPropertyException.from(p,
                this, // to get class from
                "testProp", Collections.<Object>singletonList("x"));
        assertNotNull(e);

        e = IgnoredPropertyException.from(p,
                getClass(),
                "testProp", null);
        assertNotNull(e);
        assertNull(e.getKnownPropertyIds());
        p.close();

        // also, verify failure if null passed for "value"
        try {
            IgnoredPropertyException.from(p, null,
                    "testProp", Collections.<Object>singletonList("x"));
            fail("Should not pass");
        } catch (NullPointerException e2) {
        }
    }

    public void testUnrecognizedProperty() throws Exception
    {
        JsonParser p = MAPPER.createParser("{ }");
        UnrecognizedPropertyException e = UnrecognizedPropertyException.from(p, this,
                "testProp", Collections.<Object>singletonList("y"));
        assertNotNull(e);
        assertEquals(getClass(), e.getReferringClass());
        Collection<Object> ids = e.getKnownPropertyIds();
        assertNotNull(ids);
        assertEquals(1, ids.size());
        assertTrue(ids.contains("y"));

        e = UnrecognizedPropertyException.from(p, getClass(),
                "testProp", Collections.<Object>singletonList("y"));

        assertEquals(getClass(), e.getReferringClass());
        p.close();
    }

    // [databind#2128]: ensure Location added once and only once
    // [databind#2482]: ensure Location is the original one
    public void testLocationAddition() throws Exception
    {
        String problemJson = "{\n\t\"userList\" : [\n\t{\n\t user : \"1\"\n\t},\n\t{\n\t \"user\" : \"2\"\n\t}\n\t]\n}";
        try {
            MAPPER.readValue(problemJson, Users.class);
            fail("Should not pass");
        } catch (JsonMappingException e) { // becomes "generic" due to wrapping for passing path info
            String msg = e.getMessage();
            String[] str = msg.split(" at \\[");
            if (str.length != 2) {
                fail("Should only get one 'at [' marker, got "+(str.length-1)+", source: "+msg);
            }
            JsonLocation loc = e.getLocation();
//          String expectedLocation = "line: 4, column: 4";
            assertEquals(4, loc.getLineNr());
            assertEquals(4, loc.getColumnNr());
        }
    }
}
