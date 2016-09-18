package com.acmerocket.doorman.util;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;


public final class Utils {
    public final static String TEXT_CSV = "text/csv";
    public final static MediaType TEXT_CSV_TYPE = new MediaType("text", "csv");
    public static final int DEFAULT_NUMBER = -1;
    public static final Splitter WHITESPACE_SPLITTER = Splitter.on(CharMatcher.WHITESPACE).omitEmptyStrings();

    private static final Logger LOG = LoggerFactory.getLogger(Utils.class);
    //private static final Random RAND = new Random();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Utility class
     */
    private Utils() {}

    public static int toInt(Object value) {
        return toInt(value, DEFAULT_NUMBER);
    }

    public static int toInt(Object value, int defValue) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value != null) {
            try {
                return Integer.parseInt(value.toString());
            } catch (NumberFormatException nfe) {
                LOG.debug("Unable to convert {} into int", value);
            }
        }

        return defValue;
    }

    public static long toLong(Object value) {
        return toLong(value, DEFAULT_NUMBER);
    }

    public static long toLong(Object value, long defValue) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        } else if (value != null) {
            try {
                return Long.parseLong(value.toString());
            } catch (NumberFormatException nfe) {
                LOG.debug("Unable to convert {} into int", value);
            }
        }

        return defValue;
    }

    public static Number toNumberX(Object value) throws IllegalArgumentException {
        Number num = toNumber(value, null);
        if (num == null) {
            throw new IllegalArgumentException("Invalid number: " + value);
        }
        return num;
    }

    public static boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        } else if (value instanceof String) {
            String str = (String) value;
            if (str.trim().length() == 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean toBooleanX(Object value) throws IllegalArgumentException {
        String strVal = value.toString().toLowerCase();
        if (Boolean.TRUE.toString().equals(strVal)) {
            return true;
        } else if (Boolean.FALSE.toString().equals(strVal)) {
            return false;
        } else {
            throw new IllegalArgumentException("Invalid boolean: " + value);
        }
    }

    public static Number toNumber(Object value, Number defValue) {
        if (value instanceof Number) {
            return (Number) value;
        } else if (value != null) {
            try {
                return Long.parseLong(value.toString());
            } catch (NumberFormatException nfe) { /* ignored */ }
            // not an int, try double
            try {
                return Double.parseDouble(value.toString());
            } catch (NumberFormatException nfe) { /* ignored */ }
            // not a double, either
        }
        LOG.trace("Unable to parse {}, returning default: {}", value, defValue);
        return defValue;
    }

    public static String toJson(Object obj) {
        if (obj == null) return "";
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to marshal " + obj, e);
        }
    }

    public static <T> T fromJson(String json, Class<T> valueType) {
        try {
            return MAPPER.readValue(json, valueType);
        } catch (IOException e) {
            throw new RuntimeException("Unable to unmarshal " + json, e);
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> valueType) {
        try {
            return MAPPER.readValue(json, valueType);
        } catch (IOException e) {
            throw new RuntimeException("Unable to unmarshal " + json, e);
        }
    }

    public static <T> T jsonConvert(Object object, Class<T> toType) {
        if (object == null) return null;
        try {
            return MAPPER.readValue(MAPPER.writeValueAsString(object), toType);
        } catch (IOException e) {
            throw new RuntimeException("Cannot convert to " + toType.getName() + " via JSON: " + object, e);
        }
    }

    public static <T> T jsonConvert(Object object, TypeReference<T> toTypeReference) {
        if (object == null) return null;
        try {
            return MAPPER.readValue(MAPPER.writeValueAsString(object), toTypeReference);
        } catch (IOException e) {
            throw new RuntimeException("Cannot convert to " + toTypeReference.toString() + " via JSON: " + object, e);
        }
    }

    public static ObjectMapper mapper() {
        return MAPPER;
    }

//    public static void configureMapper(ObjectMapperFactory mapperFactory) {
//        // turn off time-stamps, make sure format is correct redmine#265
//        mapperFactory.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        mapperFactory.setDateFormat(Dates.STD_DATE_FORMAT);
//
//        // suppress nulls
//        mapperFactory.setSerializationInclusion(Include.NON_NULL);
//        mapperFactory.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
//
//        // Indent!
//        mapperFactory.enable(SerializationFeature.INDENT_OUTPUT);
//    }

//    public static String md5(String secret) {
//        try {
//            MessageDigest md = MessageDigest.getInstance("MD5");
//            md.update(secret.getBytes());
//            byte byteData[] = md.digest();
//            return new String(Hex.encodeHex(byteData));
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException("Unable to load MD5 digest", e);
//        }
//    }
//
//    public static String md5(File file) {
//        try {
//            return Files.hash(file, Hashing.md5()).toString();
//        } catch (IOException e) {
//            throw new RuntimeException("Unable to calc MD5 for " + file, e);
//        }
//    }

    public static String replace(String template, Map<String, String> params) {
        StrSubstitutor strSubstitutor = new StrSubstitutor(params, "{", "}");
        strSubstitutor.setValueDelimiter(":");
        return strSubstitutor.replace(template);
    }

    public static String normalizePostalCode(String postalCode) {
        if (postalCode == null || postalCode.length() < 5) return null;
        if (postalCode.length() == 5 || postalCode.contains("-")) return postalCode.substring(0, 5);   // U.S. ZIP code
        return postalCode.replaceAll(" ", "").toUpperCase();                                           // Canadian postal code
    }
}