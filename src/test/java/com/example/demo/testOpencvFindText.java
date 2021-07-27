package com.example.demo;

import com.example.demo.utils.ImageConvertUtil;
import com.example.demo.utils.ImageFilterUtil;
import com.example.demo.utils.ImageOpencvUtil;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;

import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.highgui.HighGui.waitKey;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;

/**
 * @author ly
 * @since 2021/4/30
 */
public class testOpencvFindText {
    private final static int targetDifferenceValue = 20;

    public static void main(String[] args) throws Exception {
        // 加载动态库
        URL url = ClassLoader.getSystemResource("lib/opencv/opencv_java452.dll");
        System.load(url.getPath());

        //原图路径
        String sourceImage = "E:\\Desktop\\OCRTest\\image\\14.png";
        //处理后的图片保存路径
        String processedImage = sourceImage.substring(0, sourceImage.lastIndexOf(".")) + "afterDetection.png";

        //读取图像
        Mat image = imread(sourceImage);
        if (image.empty()) {
            throw new Exception("image is empty");
        }
//        imshow("Original Image", image);

        //倾斜校正
        Mat correctedImg = ImageOpencvUtil.imgCorrection(image);
        imshow("倾斜校正并标准化", correctedImg);

        Mat img = correctedImg.clone();
        BufferedImage bufferedImage = ImageConvertUtil.Mat2BufImg(img, ".png");

        //ImageFilterUtil调节亮度
        int brightness = ImageFilterUtil.imageBrightness(bufferedImage);
        System.out.println("图片亮度 = " + brightness);
        BufferedImage brightnessImg = bufferedImage;
        if (brightness > 180) {
            brightnessImg = ImageFilterUtil.imageBrightness(bufferedImage, -60);
        }

        //涂白
//        BufferedImage paintWhiteImg = ImageFilterUtil.imageRGBDifferenceFilter(brightnessImg, targetDifferenceValue);
        //黑白化
//        BufferedImage blackWhiteImage = ImageFilterUtil.replaceWithWhiteColor(paintWhiteImg);

        //ImageFilterUtil灰度化
        BufferedImage grayImage = ImageFilterUtil.gray(brightnessImg);
        //将ImageFilterUtil灰度化后的图片转换为Mat矩阵图像
        Mat matImg = ImageConvertUtil.BufImg2Mat(grayImage);
        imshow("灰度化", matImg);

        //opencv非局部均值去噪（需要三通道的Mat图像）
        Mat denoiseImg = ImageOpencvUtil.pyrMeanShiftFiltering(matImg);
        imshow("去噪", denoiseImg);

        //opencv灰度化--作用：转为单通道
        Mat grayImg = ImageOpencvUtil.gray(denoiseImg);
//        imshow("grayImg", grayImg);

        Mat binaryImg = ImageOpencvUtil.ImgBinarization(grayImg);
        imshow("二值化", binaryImg);
        imwrite(processedImage, binaryImg);

        //膨胀与腐蚀后的Mat图像
        Mat dilationImg = ImageOpencvUtil.preprocess(grayImg);
        imshow("膨胀与腐蚀", dilationImg);

        //查找和筛选文字区域
        List<RotatedRect> rects = ImageOpencvUtil.findTextRegionRect(dilationImg);

        //用红线画出找到的轮廓
        for (RotatedRect rotatedRect : rects) {
            Point[] rectPoint = new Point[4];
            rotatedRect.points(rectPoint);
            for (int j = 0; j <= 3; j++) {
                Imgproc.line(img, rectPoint[j], rectPoint[(j + 1) % 4], new Scalar(0, 0, 255), 2);
            }
        }
        //显示带轮廓的图像
        imshow("文本定位", img);

        //截取并显示轮廓图片
        Mat dst;
        for (int i = 0; i < rects.size(); i++) {
            dst = ImageOpencvUtil.cropImage(img, rects.get(i).boundingRect());
            //显示截取的关键信息图像
            imshow("需识别身份证信息" + i, dst);
        }

        waitKey();
    }
}
