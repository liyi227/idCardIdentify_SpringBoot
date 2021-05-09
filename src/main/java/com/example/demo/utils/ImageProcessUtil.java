package com.example.demo.utils;

import org.opencv.core.Mat;
import org.opencv.core.RotatedRect;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgcodecs.Imgcodecs.imread;

/**
 * @author ly
 * @since 2021/5/8
 */
public class ImageProcessUtil {

    public static List<BufferedImage> idCardProcess(BufferedImage bufferedImage) {

        List<BufferedImage> lstBufferedImg = new ArrayList<>();

//        long startTime = System.currentTimeMillis();    //识别之前的时间

        // 加载动态库
        URL url = ClassLoader.getSystemResource("lib/opencv/opencv_java452.dll");
        System.load(url.getPath());

        //原图路径
//        String sourceImage = "E:\\Desktop\\OCRTest\\image\\01.png";
        //处理后的图片保存路径
//        String processedImage = sourceImage.substring(0, sourceImage.lastIndexOf(".")) + "after.png";

        //将bufferedImage转换为Mat矩阵图像
        Mat image = ImageConvertUtil.BufImg2Mat(bufferedImage);

        //倾斜校正并标准化
        Mat correctedImg = ImageOpencvUtil.imgCorrection(image);

        Mat img = correctedImg.clone();

        //将倾斜校正标准化后的Mat矩阵图像转换为BufferedImage格式
        BufferedImage newBufferedImage = ImageConvertUtil.Mat2BufImg(correctedImg, ".png");

        //ImageFilterUtil调节亮度
        //获取图片的亮度
        int brightness = ImageFilterUtil.imageBrightness(newBufferedImage);
        BufferedImage brightnessImg;
        //如果亮度>180，则亮度减少60
        if (brightness > 180)
            brightnessImg = ImageFilterUtil.imageBrightness(newBufferedImage, -60);
        else
            brightnessImg = newBufferedImage;

        //ImageFilterUtil灰度化
        BufferedImage grayImage = ImageFilterUtil.gray(brightnessImg);
        //将ImageFilterUtil灰度化后的图片转换为Mat矩阵图像
        Mat matImg = ImageConvertUtil.BufImg2Mat(grayImage);

        //opencv非局部均值去噪（需要三通道的Mat图像）--去噪后仍为三通道
        Mat denoiseImg = ImageOpencvUtil.pyrMeanShiftFiltering(matImg);

        //opencv灰度化--转为单通道
        Mat grayImg = ImageOpencvUtil.gray(denoiseImg);

//        imshow("grayImg", grayImg);

        //膨胀与腐蚀
        Mat dilationImg = ImageOpencvUtil.preprocess(grayImg);
//        imshow("dilation", dilationImg);

        //查找和筛选文字区域
        List<RotatedRect> rects = ImageOpencvUtil.findTextRegionRect(dilationImg);
        if (rects.size() > 6)
            System.out.println("身份证信息文本框获取错误！！！");

//        //用红线画出找到的轮廓
//        for (RotatedRect rotatedRect : rects) {
//            Point[] rectPoint = new Point[4];
//            rotatedRect.points(rectPoint);
//            for (int j = 0; j <= 3; j++) {
//                Imgproc.line(img, rectPoint[j], rectPoint[(j + 1) % 4], new Scalar(0, 0, 255), 2);
//            }
//        }
//        //显示带轮廓的图像
//        imshow("Contour Image", img);

        //截取并显示轮廓图片
        Mat dst;
        System.out.println("rects.size:" + rects.size());
        List<Mat> lstMat = new ArrayList<>();
        for (int i = 0; i < rects.size(); i++) {
            //裁剪识别区域
            dst = ImageOpencvUtil.cropImage(matImg, rects.get(i).boundingRect());
            lstMat.add(dst);
//            imshow("croppedImg" + i, dst);
        }

        for (int i = lstMat.size() - 1; i >= 0; i--) {
            BufferedImage tempBufferedImg = ImageConvertUtil.Mat2BufImg(lstMat.get(i), ".png");
            lstBufferedImg.add(tempBufferedImg);
        }

        return lstBufferedImg;
    }
}
