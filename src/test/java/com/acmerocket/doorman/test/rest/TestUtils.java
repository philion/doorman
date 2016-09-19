package com.acmerocket.doorman.test.rest;

import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Date;
import java.util.Random;

import org.junit.Assert;

public class TestUtils {	
    private static final Random RAND = new Random();

    /**
     * Utility class for testing
     */
    private TestUtils() {}

    public static long randomLong() {
        return Math.abs(RAND.nextLong());
    }

    public static int randomInt() {
        return Math.abs(RAND.nextInt());
    }
    
    public static int randomInt(int max) {
        return Math.abs(RAND.nextInt(max));
    }

    public static String randomStr() {
        return randomStr(8);
    }
    
    public static String randomEmail() {
        return randomStr(14) + "@example.com";
    }
    
    public static String randomStr(int length) {
        return new BigInteger(length * 4, RAND).toString(36);
    }
    
    public static void assertEquals(int val, String strVal) {
        Assert.assertEquals(val, Integer.parseInt(strVal));
    }

    public static void assertEquals(long val, String strVal) {
        Assert.assertEquals(val, Long.parseLong(strVal));
    }

    public static void assertEquals(float val, String strVal) {
        Assert.assertEquals(val, Float.parseFloat(strVal), 0.0001);
    }

    public static void assertEquals(double val, String strVal) {
        Assert.assertEquals(val, Double.parseDouble(strVal), 0.0001);
    }

    public static String randomHex() {
        return randomHex(16);
    }
    
    public static String randomHex(int length) {
        return new BigInteger(length * 4, RAND).toString(16).toUpperCase();
    }
    
    private static final long STD_VARIANCE = 250;
    public static void assertClose(Date expected, Date actual) {        
        assertClose(expected, actual, STD_VARIANCE);
    }
    
    public static void assertClose(Date expected, Date actual, long variance) {        
        long delta = Math.abs(expected.getTime() - actual.getTime());
        assertTrue("Expected " + expected + ", got " + actual + ", delta=" + delta + " > " + variance, delta <= variance);
    }
}
