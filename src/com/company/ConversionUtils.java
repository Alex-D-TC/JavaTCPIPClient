package com.company;

/**
 * Created by AlexandruD on 10/16/2016.
 */
public class ConversionUtils {


    public static byte[] translateToNetwork(int num) {
        byte[] bytes = new byte[4];

        bytes[3] = Byte.valueOf(Integer.valueOf(num & 255).toString());
        bytes[2] = Byte.valueOf(Integer.valueOf((num >> 8) & 255).toString());
        bytes[1] = Byte.valueOf(Integer.valueOf((num >> 16) & 255).toString());
        bytes[0] = Byte.valueOf(Integer.valueOf((num >> 24) & 255).toString());

        return bytes;
    }

    public static int translateToHost(byte[] bytes) {

        int num = 0;

        num += (bytes[0] << 24);
        num += (bytes[1] << 16);
        num += (bytes[2] << 8);
        num += (bytes[3]);

        return num;
    }

}
