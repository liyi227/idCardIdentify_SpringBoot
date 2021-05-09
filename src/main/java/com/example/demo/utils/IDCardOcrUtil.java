package com.example.demo.utils;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ly
 * @since 2021/5/8
 */
public class IDCardOcrUtil {

    public static List<Object> idCardOcr(List<BufferedImage> lstBufferedImg) {
        List<Object> lstIdCardInfo = new ArrayList<>();

        ITesseract instance = new Tesseract();    //创建ITesseract接口的实现实例对象

        //java.lang.ClassLoader.getSystemResource()方法返回一个URL对象读取资源，如果资源不能被找到则返回null。
        URL tessDataUrl = ClassLoader.getSystemResource("tessdata");    //file:/E:/Desktop/OCRTest/Tess4jOcr/Tess4jOcr/Tess4jOcr/target/classes/tessdata
        String path = tessDataUrl.getPath().substring(1);    //url.getPath()-/E:/Desktop/OCRTest/Tess4jOcr/Tess4jOcr/Tess4jOcr/target/classes/tessdata
        instance.setDatapath(path); //path为tessdata文件夹目录位置
        instance.setLanguage("chi_sim");    //中英文混合识别需用 + 分隔，chi_sim：简体中文，eng：英文
        instance.setTessVariable("user_defined_dpi", "300");    //Warning: Invalid resolution 0 dpi. Using 70 instead.
        String result = null;

        System.out.println("识别结果如下：");
        try {
            //识别姓名
            String name = "";
            instance.setLanguage("chi_sim");    //中英文混合识别需用 + 分隔，chi_sim：简体中文，eng：英文
            name = instance.doOCR(lstBufferedImg.get(0)).replaceAll("[^\\u4e00-\\u9fa5]", "").trim();
            System.out.println("姓名：" + name);
            lstIdCardInfo.add(name);

            //识别身份证号
            String idNumber = "";
            instance.setLanguage("eng");    //中英文混合识别需用 + 分隔，chi_sim：简体中文，eng：英文
            idNumber = instance.doOCR(lstBufferedImg.get(lstBufferedImg.size() - 1)).replaceAll("[^0-9xX]", "");


            //根据身份证号获取性别
            char sex;
            if (Integer.parseInt(idNumber.substring(16, 17)) % 2 == 0) {
                sex = '女';
            } else {
                sex = '男';
            }
            System.out.println("性别：" + sex);
            lstIdCardInfo.add(sex);

            //识别名族
            String nation = "";
            instance.setLanguage("chi_sim");
            nation = instance.doOCR(lstBufferedImg.get(1)).trim();
            System.out.println("名族：" + nation);
            lstIdCardInfo.add(nation);

            //根据身份证号获得出生年、月、日
            int year = Integer.parseInt(idNumber.substring(6, 10));
            int month = Integer.parseInt(idNumber.substring(10, 12));
            int day = Integer.parseInt(idNumber.substring(12, 14));
            System.out.println("出生：" + year + "年" + month + "月" + day + "日");
            lstIdCardInfo.add(year);
            lstIdCardInfo.add(month);
            lstIdCardInfo.add(day);

            //识别地址
            String address = "";
            instance.setLanguage("chi_sim");    //中英文混合识别需用 + 分隔，chi_sim：简体中文，eng：英文
            for (int i = 2; i < lstBufferedImg.size() - 1; i++) {
                address += instance.doOCR(lstBufferedImg.get(i)).trim();
            }
            address = address.replaceAll("[^\\s\\u4e00-\\u9fa5\\-0-9]+", "").replaceAll(" +", "").trim();
            System.out.println("地址：" + address);
            lstIdCardInfo.add(address);

            System.out.println("身份证号：" + idNumber);
            lstIdCardInfo.add(idNumber);

            long endTime = System.currentTimeMillis();    //识别结束的时间
//            System.out.println("识别用时：" + (endTime - startTime) + "ms");    //识别图片耗时
        } catch (TesseractException e) {
            System.out.println(e.getMessage());
        }

        return lstIdCardInfo;
    }

}
