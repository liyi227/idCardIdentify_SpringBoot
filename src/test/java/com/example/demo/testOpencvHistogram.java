package com.example.demo;

import com.example.demo.utils.ImageConvertUtil;
import com.example.demo.utils.ImageFilterUtil;
import com.example.demo.utils.ImageOpencvUtil;

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
 * @since 2021/5/18
 */
//测绘绘制灰度化图像直方图
public class testOpencvHistogram {
    public static void main(String[] args) throws Exception {
        // 加载动态库
        URL url = ClassLoader.getSystemResource("lib/opencv/opencv_java452.dll");
        System.load(url.getPath());

        //原图路径
        String sourceImage = "E:\\Desktop\\OCRTest\\image\\04.png";
        //直方图保存路径--在原来的图片主名后加上_Histogram
        String processedImage = sourceImage.substring(0, sourceImage.lastIndexOf(".")) + "_Histogram.png";

        //读取图像
        File image = new File(sourceImage);
        BufferedImage bufferedImage = ImageIO.read(image);

        //调用ImageFilterUtil的灰度化gray()方法
        BufferedImage grayImage = ImageFilterUtil.gray(bufferedImage);

        //将灰度化后的图像转为Mat矩阵图像
        Mat grayImg = ImageConvertUtil.BufImg2Mat(grayImage);
        imshow("graImg", grayImg);

        //均值迁移滤波
        Mat denoiseImg = ImageOpencvUtil.pyrMeanShiftFiltering(grayImg);
        imshow("Processed Image", denoiseImg);

        grayImg = ImageOpencvUtil.gray(denoiseImg);

        //绘制直方图
        Mat histogram = ImageOpencvUtil.getHistogram(denoiseImg);
        imshow("Histogram", histogram);

        //保存到字符串processedImage对应位置
        imwrite(processedImage, histogram);
        waitKey();
    }
}
