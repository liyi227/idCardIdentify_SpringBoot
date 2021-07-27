package com.example.demo;

import com.example.demo.utils.ImageOpencvUtil;
import org.opencv.core.Mat;
import org.opencv.core.RotatedRect;

import java.net.URL;
import java.util.List;

import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.highgui.HighGui.waitKey;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;

/**
 * @author ly
 * @since 2021/5/20
 */
public class testOpencvSobel {
    public static void main(String[] args) throws Exception {
        // 加载动态库
        URL url = ClassLoader.getSystemResource("lib/opencv/opencv_java452.dll");
        System.load(url.getPath());

        //原图路径
        String sourceImage = "E:\\Desktop\\OCRTest\\image\\14.png";
        //处理后的图片保存路径
        String processedImage = sourceImage.substring(0, sourceImage.lastIndexOf(".")) + "afterSobel.png";

        // 读取图像
        Mat image = imread(sourceImage);
        if (image.empty()) {
            throw new Exception("image is empty");
        }
        imshow("原始图片", image);

        //opencv灰度化
        Mat grayImage = ImageOpencvUtil.gray(image);

        //二值化
        Mat binaryImage = ImageOpencvUtil.binaryzation(grayImage);

        //膨胀与腐蚀
        Mat corrodedImage = ImageOpencvUtil.corrosion(binaryImage);

        //文字区域
        List<RotatedRect> rects = ImageOpencvUtil.findTextRegion(corrodedImage);

        //倾斜矫正
        Mat correctedImg = ImageOpencvUtil.correction(rects, image);
        imshow("校正", correctedImg);

        Mat gray=ImageOpencvUtil.gray(correctedImg);

        //轮廓
        Mat sobelImg = ImageOpencvUtil.sobel(gray);
        imshow("边缘检测", sobelImg);

        imwrite(processedImage, sobelImg);
        waitKey();
    }
}
