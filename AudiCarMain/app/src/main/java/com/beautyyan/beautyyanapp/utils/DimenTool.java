package com.beautyyan.beautyyanapp.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 用来自动计算填写其他value里面的dimen值
 */

public class DimenTool {

    public static void gen() {

        File file = new File("./app/src/main/res/values/dimens.xml");
        BufferedReader reader = null;
        //主要针对手机
        StringBuilder sw300= new StringBuilder();
        StringBuilder sw320 = new StringBuilder();
        StringBuilder sw340 = new StringBuilder();
        StringBuilder sw360 = new StringBuilder();
        StringBuilder sw380 = new StringBuilder();
        StringBuilder sw400= new StringBuilder();
        //主要针对pad
        StringBuilder sw600= new StringBuilder();
        StringBuilder sw700= new StringBuilder();
        StringBuilder sw800= new StringBuilder();



        try {
            System.out.println("生成不同分辨率：");
            reader = new BufferedReader(new FileReader(file));
            String tempString;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束

            while ((tempString = reader.readLine()) != null) {

                if (tempString.contains("</dimen>")) {
                    //tempString = tempString.replaceAll(' ', '');
                    String start = tempString.substring(0, tempString.indexOf('>') + 1);
                    String end = tempString.substring(tempString.lastIndexOf('<') - 2);
                    double num = Double.valueOf(tempString.substring(tempString.indexOf('>') + 1, tempString.indexOf("</dimen>") - 2));

                    sw300.append(start).append((int) Math.round(num * 300 / 400)).append(end).append("\n");
                    sw320.append(start).append((int) Math.round(num * 320 / 400)).append(end).append("\n");
                    sw340.append(start).append((int) Math.round(num * 340 / 400)).append(end).append("\n");
                    sw360.append(start).append((int) Math.round(num * 360 / 400)).append(end).append("\n");
                    sw380.append(start).append((int) Math.round(num * 380 / 400)).append(end).append("\n");
                    sw400.append(tempString).append("\n");
                    sw600.append(start).append((int) Math.round(num * 600 / 400)).append(end).append("\n");
                    sw700.append(start).append((int) Math.round(num * 700 / 400)).append(end).append("\n");
                    sw800.append(start).append((int) Math.round(num * 800 / 400)).append(end).append("\n");

                } else {
                    sw300.append(tempString).append("\n");
                    sw320.append(tempString).append("\n");
                    sw340.append(tempString).append("\n");
                    sw360.append(tempString).append("\n");
                    sw380.append(tempString).append("\n");
                    sw400.append(tempString).append("\n");
                    sw600.append(tempString).append("\n");
                    sw700.append(tempString).append("\n");
                    sw800.append(tempString).append("\n");
                }
                line++;
            }
            reader.close();

            String sw300file = "./app/src/main/res/values-sw300dp/dimens.xml";
            String sw320file = "./app/src/main/res/values-sw320dp/dimens.xml";
            String sw340file = "./app/src/main/res/values-sw340dp/dimens.xml";
            String sw360file = "./app/src/main/res/values-sw360dp/dimens.xml";
            String sw380file = "./app/src/main/res/values-sw380dp/dimens.xml";
            String sw400file = "./app/src/main/res/values-sw400dp/dimens.xml";

            String sw600file = "./app/src/main/res/values-sw600dp/dimens.xml";
            String sw700file = "./app/src/main/res/values-sw700dp/dimens.xml";
            String sw800file = "./app/src/main/res/values-sw800dp/dimens.xml";
            writeFile(sw300file, sw300.toString());
            writeFile(sw320file, sw320.toString());
            writeFile(sw340file, sw340.toString());
            writeFile(sw360file, sw360.toString());
            writeFile(sw380file, sw380.toString());
            writeFile(sw400file, sw400.toString());

            writeFile(sw600file, sw600.toString());
            writeFile(sw700file, sw700.toString());
            writeFile(sw800file, sw800.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static void writeFile(String file, String text) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            out.println(text);
        } catch (IOException e) {
            e.printStackTrace();
        }

        out.close();
    }

    public static void main(String[] args) {
        gen();
    }
}