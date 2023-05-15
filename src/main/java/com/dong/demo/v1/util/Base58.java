package com.dong.demo.v1.util;

import java.math.BigInteger;

public class Base58 {

    private static final String ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
    private static final BigInteger BASE = BigInteger.valueOf(58);

    public static String encode(byte[] input) {
        BigInteger number = new BigInteger(1, input);
        StringBuilder result = new StringBuilder();
        while (number.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] quotientAndRemainder = number.divideAndRemainder(BASE);
            BigInteger quotient = quotientAndRemainder[0];
            BigInteger remainder = quotientAndRemainder[1];
            result.insert(0, ALPHABET.charAt(remainder.intValue()));
            number = quotient;
        }
        for (byte b : input) {
            if (b == 0) {
                result.insert(0, ALPHABET.charAt(0));
            } else {
                break;
            }
        }
        return result.toString();
    }

    public static byte[] decode(String input) {
        BigInteger number = BigInteger.ZERO;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            int digit = ALPHABET.indexOf(c);
            if (digit == -1) {
                throw new IllegalArgumentException("Invalid character for Base58 encoding: " + c);
            }
            number = number.multiply(BASE).add(BigInteger.valueOf(digit));
        }
        byte[] bytes = number.toByteArray();
        int zeros = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) != ALPHABET.charAt(0)) {
                break;
            }
            zeros++;
        }
        byte[] result = new byte[zeros + bytes.length];
        System.arraycopy(bytes, 0, result, zeros, bytes.length);
        return result;
    }
}
