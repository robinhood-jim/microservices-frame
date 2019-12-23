package com.robin.msf.util;

import java.util.Random;


public class StringUtils {
    public static final int ASCII_UPPER_START=64;
    public static final int ASCII_LOWER_START=96;
    private static int getRandomUpperChar(Random random){
        return ASCII_UPPER_START+random.nextInt(26)+1;
    }
    private static int getRandomLowerChar(Random random){
        return ASCII_LOWER_START+random.nextInt(26)+1;
    }
    public static String genarateRandomUpperLowerChar(int length){
        StringBuilder builder=new StringBuilder();
        Random random=new Random();
        for(int i=0;i<length;i++){
            if(random.nextFloat()<0.5) {
                builder.append((char)getRandomUpperChar(random));
            } else{
                builder.append((char)getRandomLowerChar(random));
            }
        }
        return builder.toString();
    }
}
