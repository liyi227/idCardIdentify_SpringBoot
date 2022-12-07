package com.example.demo;

import com.example.demo.utils.ImageConvertUtil;
import com.example.demo.utils.ImageOpencvUtil;
import com.example.demo.utils.ImageFilterUtil;

import org.opencv.core.Mat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.highgui.HighGui.waitKey;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;

/**
 * @author ly
 * @since 2021/5/17
 */
public class testOpencvBlur {
    public static void main(String[] args) throws Exception {
        // 加载动态库
        URL url = ClassLoader.getSystemResource("lib/opencv/opencv_java452.dll");
        System.load(url.getPath());

        //原图路径
        String sourceImage = "E:\\blur\\01.png";
        //去噪后的图片保存路径--在原来的图片主名后加上afterDenoiseOpenCV
        // 均值滤波处理后的图片
        String blurImage = sourceImage.substring(0, sourceImage.lastIndexOf(".")) + "blur.png";
        // 中值滤波处理后的图片
        String medianBlurImage = sourceImage.substring(0, sourceImage.lastIndexOf(".")) + "medianBlur.png";
        // 高斯滤波处理后的图片
        String gaussianBlurImage = sourceImage.substring(0, sourceImage.lastIndexOf(".")) + "gaussianBlur.png";
        // 自适应中值滤波处理后的图片
        String adaptiveMediaFilterImage = sourceImage.substring(0, sourceImage.lastIndexOf(".")) + "adaptiveMediaFilter.png";
        // 均值迁移滤波处理后的图片
        String pyrMeanShiftFilteringImage = sourceImage.substring(0, sourceImage.lastIndexOf(".")) + "pyrMeanShiftFiltering.png";

        //读取图像
        File image = new File(sourceImage);
        BufferedImage bufferedImage = ImageIO.read(image);

        //调用ImageFilterUtil的灰度化gray()方法
        BufferedImage grayImage = ImageFilterUtil.gray(bufferedImage);

        //将灰度化后的图像转为Mat矩阵图像
        Mat grayImg = ImageConvertUtil.BufImg2Mat(grayImage);
        imshow("graImg", grayImg);

        //调用ImageOpencvUtil的去噪方法
        //均值滤波
        Mat blurImg = ImageOpencvUtil.blur(grayImg);
        //中值滤波
        Mat medianBlurImg = ImageOpencvUtil.medianBlur(grayImg);
        //高斯滤波
        Mat gaussianBlurImg = ImageOpencvUtil.gaussianBlur(grayImg);
        //自适应中值滤波
        Mat adaptiveMediaFilterImg= ImageOpencvUtil.adaptiveMediaFilter(grayImg, 3, 7);
        //均值迁移滤波
        Mat pyrMeanShiftFilteringImg = ImageOpencvUtil.pyrMeanShiftFiltering(grayImg);

        //展示去噪处理后图像
//        imshow("Processed Image", pyrMeanShiftFilteringImg);

        //保存到字符串processedImage对应位置
        imwrite(blurImage, blurImg);
        imwrite(medianBlurImage, medianBlurImg);
        imwrite(gaussianBlurImage, gaussianBlurImg);
        imwrite(adaptiveMediaFilterImage, adaptiveMediaFilterImg);
        imwrite(pyrMeanShiftFilteringImage, pyrMeanShiftFilteringImg);

        waitKey();
    }
}
