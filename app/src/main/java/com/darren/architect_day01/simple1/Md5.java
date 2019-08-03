package com.darren.architect_day01.simple1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Md5 {
    
    public static String fileMd5(String filePath) {
        File file = new File(filePath);
        return fileMd5(file);
    }

    public static String fileMd5(File file) {
        if (file == null) {
            return null;
        }
        FileInputStream in = null;
        byte[] data = null;
        try {
            in = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int readCount = 0;
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            while ((readCount = in.read(buffer)) > 0) {
                md5.update(buffer, 0, readCount);
            }
            data = md5.digest();
        } 
        catch (Exception e) {
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        return bytesToHexString(data);
    }

    public static String strMd5(String s) {
        StringBuffer sb = new StringBuffer();
        try {
            java.security.MessageDigest md5 = java.security.MessageDigest
                .getInstance("MD5");
            byte[] digest = md5.digest(s.getBytes());
            sb.append(bytesToHexString(digest));
        } catch (NoSuchAlgorithmException e) {
        }
        return sb.toString();
    }
    
    public static String bytesToHexString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        if (b != null) {
            for (int i=0; i < b.length; i++) {
                sb.append(Integer.toString(( b[i] & 0xff) + 0x100, 16).substring(1));
            }
        }
        return sb.toString();
    }

    
}
