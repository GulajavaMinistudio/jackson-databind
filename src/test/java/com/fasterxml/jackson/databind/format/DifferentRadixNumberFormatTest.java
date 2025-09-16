package com.fasterxml.jackson.databind.format;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.testutil.DatabindTestUtil;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DifferentRadixNumberFormatTest extends DatabindTestUtil {

    private static class IntegerWrapper {
        public Integer value;

        public IntegerWrapper() {}
        public IntegerWrapper(Integer v) { value = v; }
    }

    private static class IntWrapper {
        public int value;

        public IntWrapper() {}
        public IntWrapper(int v) { value = v; }
    }

    private static class AnnotatedMethodIntWrapper {
        private int value;

        public AnnotatedMethodIntWrapper() {
        }
        public AnnotatedMethodIntWrapper(int v) {
            value = v;
        }

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "16")
        public int getValue() {
            return value;
        }
    }

    private static class AllIntegralTypeWrapper {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "2")
        public byte byteValue;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "2")
        public Byte ByteValue;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "2")
        public short shortValue;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "2")
        public Short ShortValue;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "2")
        public int intValue;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "2")
        public Integer IntegerValue;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "2")
        public long longValue;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "2")
        public Long LongValue;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "2")
        public BigInteger bigInteger;

        public AllIntegralTypeWrapper() {
        }

        public AllIntegralTypeWrapper(byte byteValue, Byte ByteValue, short shortValue, Short ShortValue, int intValue,
                                      Integer IntegerValue, long longValue, Long LongValue, BigInteger bigInteger) {
            this.byteValue = byteValue;
            this.ByteValue = ByteValue;
            this.shortValue = shortValue;
            this.ShortValue = ShortValue;
            this.intValue = intValue;
            this.IntegerValue = IntegerValue;
            this.longValue = longValue;
            this.LongValue = LongValue;
            this.bigInteger = bigInteger;
        }
    }

    @Test
    void testIntegerSerializedAsHexString()
            throws JsonProcessingException {
        ObjectMapper mapper = newJsonMapper();
        mapper.configOverride(Integer.class).setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.STRING).withPattern("16"));
        IntegerWrapper initialIntegerWrapper = new IntegerWrapper(10);
        String json = mapper.writeValueAsString(initialIntegerWrapper);
        String expectedJson = "{'value':'a'}";

        assertEquals(a2q(expectedJson), json);

        IntegerWrapper readBackIntegerWrapper = mapper.readValue(a2q(expectedJson), IntegerWrapper.class);

        assertNotNull(readBackIntegerWrapper);
        assertEquals(initialIntegerWrapper.value, readBackIntegerWrapper.value);
    }


    @Test
    void testIntSerializedAsHexString()
            throws JsonProcessingException {
        ObjectMapper mapper = newJsonMapper();
        mapper.configOverride(int.class)
              .setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.STRING).withPattern("16"));
        IntWrapper intialIntWrapper = new IntWrapper(10);
        String expectedJson = "{'value':'a'}";

        String json = mapper.writeValueAsString(intialIntWrapper);

        assertEquals(a2q(expectedJson), json);

        IntWrapper readBackIntWrapper = mapper.readValue(a2q(expectedJson), IntWrapper.class);

        assertNotNull(readBackIntWrapper);
        assertEquals(intialIntWrapper.value, readBackIntWrapper.value);

    }

    @Test
    void testAnnotatedAccessorSerializedAsHexString()
            throws JsonProcessingException {
        ObjectMapper mapper = newJsonMapper();
        AnnotatedMethodIntWrapper initialIntWrapper = new AnnotatedMethodIntWrapper(10);
        String expectedJson = "{'value':'a'}";

        String json = mapper.writeValueAsString(initialIntWrapper);

        assertEquals(a2q(expectedJson), json);

        AnnotatedMethodIntWrapper readBackIntWrapper = mapper.readValue(a2q(expectedJson), AnnotatedMethodIntWrapper.class);

        assertNotNull(readBackIntWrapper);
        assertEquals(initialIntWrapper.value, readBackIntWrapper.value);
    }

    @Test
    void testUsingBaseSettingRadixToSerializeAsHexString()
            throws JsonProcessingException {
        ObjectMapper mapper = newJsonMapper();
        mapper.configOverride(Integer.class)
              .setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.STRING));
        mapper.setRadix(16);
        IntegerWrapper intialIntegerWrapper = new IntegerWrapper(10);
        String expectedJson = "{'value':'a'}";

        String json = mapper.writeValueAsString(intialIntegerWrapper);

        assertEquals(a2q(expectedJson), json);

        IntegerWrapper readBackIntegerWrapper = mapper.readValue(a2q(expectedJson), IntegerWrapper.class);

        assertNotNull(readBackIntegerWrapper);
        assertEquals(intialIntegerWrapper.value, readBackIntegerWrapper.value);
    }

    @Test
    void testAllIntegralTypesGetSerializedWithRadix()
            throws JsonProcessingException {
        ObjectMapper mapper = newJsonMapper();
        mapper.configOverride(Integer.class)
              .setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.STRING));
        mapper.setRadix(16);
        AllIntegralTypeWrapper intialIntegralTypeWrapper = new AllIntegralTypeWrapper((byte) 1,
                (byte) 2, (short) 3, (short) 4, 5, 6, 7L, 8L, new BigInteger("9"));
        String expectedJson = "{'byteValue':'1','ByteValue':'10','shortValue':'11','ShortValue':'100','intValue':'101','IntegerValue':'110','longValue':'111','LongValue':'1000','bigInteger':'1001'}";

        String json = mapper.writeValueAsString(intialIntegralTypeWrapper);

        AllIntegralTypeWrapper readbackIntegralTypeWrapper = mapper.readValue(a2q(expectedJson), AllIntegralTypeWrapper.class);

        assertNotNull(readbackIntegralTypeWrapper);
        assertEquals(intialIntegralTypeWrapper.byteValue, readbackIntegralTypeWrapper.byteValue);
        assertEquals(intialIntegralTypeWrapper.ByteValue, readbackIntegralTypeWrapper.ByteValue);
        assertEquals(intialIntegralTypeWrapper.shortValue, readbackIntegralTypeWrapper.shortValue);
        assertEquals(intialIntegralTypeWrapper.ShortValue, readbackIntegralTypeWrapper.ShortValue);
        assertEquals(intialIntegralTypeWrapper.intValue, readbackIntegralTypeWrapper.intValue);
        assertEquals(intialIntegralTypeWrapper.IntegerValue, readbackIntegralTypeWrapper.IntegerValue);
        assertEquals(intialIntegralTypeWrapper.longValue, readbackIntegralTypeWrapper.longValue);
        assertEquals(intialIntegralTypeWrapper.LongValue, readbackIntegralTypeWrapper.LongValue);
        assertEquals(intialIntegralTypeWrapper.bigInteger, readbackIntegralTypeWrapper.bigInteger);
    }
}
