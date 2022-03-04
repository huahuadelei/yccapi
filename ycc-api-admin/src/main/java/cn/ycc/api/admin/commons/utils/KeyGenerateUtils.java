package cn.ycc.api.admin.commons.utils;


import java.util.Random;

public abstract class KeyGenerateUtils {

    private static final char[] CHARS;
    private static final char[] NUMS;

    static {
        CHARS = new char[26];
        NUMS = new char[10];
        for (int i = 0; i < 26; i++) {
            CHARS[i] = (char) (i + 97);
        }

        for (int i = 0; i < 10; i++) {
            NUMS[i] = (char) i;
        }
        System.out.println();
    }

    private static final Random RANDOM = new Random();

    public static String generateHybridKey(int maxNum) {

        char[] newChars = new char[maxNum];

        for (int i = 0; i < maxNum; i++) {
            int type = RANDOM.nextInt(2);
            newChars[i] = type == 0 ? CHARS[RANDOM.nextInt(CHARS.length)] : (char)((int)NUMS[RANDOM.nextInt(NUMS.length)]+48);
        }
        return new String(newChars);
    }

    public static void main(String[] args) {
        System.out.println(KeyGenerateUtils.generateHybridKey(4));
    }
}
