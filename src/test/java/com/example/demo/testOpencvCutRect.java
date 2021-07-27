package com.example.demo;


import com.example.demo.utils.ImageOpencvUtil;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.net.URL;
import java.util.List;

import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.highgui.HighGui.waitKey;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;

/**
 * @author ly
 * @since 2021/4/26
 */
//测试Opencv的cutRect()裁剪方法
public class testOpencvCutRect {
    public static void main(String[] args) throws Exception {
        //加载动态库
        URL url = ClassLoader.getSystemResource("lib/opencv/opencv_java452.dll");
        System.load(url.getPath());

        //原图路径
        String sourceImage = "E:\\Desktop\\OCRTest\\image\\01a.png";
        //处理后的图片保存路径
        String processedImage = sourceImage.substring(0, sourceImage.lastIndexOf(".")) + "afterCut.png";

        //读取图像
        Mat image = imread(sourceImage);
        if (image.empty()) {
            throw new Exception("image is empty");
        }
        imshow("原始图片", image);

        //opencv灰度化
        Mat grayImage = ImageOpencvUtil.gray(image);
        imshow("灰度化", grayImage);

        //二值化
        Mat binaryImg = ImageOpencvUtil.binaryzation(grayImage);
        imshow("二值化", binaryImg);

        //膨胀与腐蚀
//        Mat corrodedImg = ImageOpencvUtil.corrosion(binaryImg);
//        imshow("膨胀腐蚀",corrodedImg);


        //Hough直线检测
        Mat canny = new Mat();
        Mat imgLine = image.clone();
        Imgproc.Canny(image, canny, 50, 200, 3);
        Mat storage = new Mat();
        /**
         * 这里注意第五个参数，表示阈值，阈值越大，表明检测的越精准，速度越快，得到的直线越少（得到的直线都是很有把握的直线）
         * 这里得到的lines是包含rho和theta的，而不包括直线上的点，所以下面需要根据得到的rho和theta来建立一条直线
         * rho：像素精度，以像素为单位的距离分辨率，一般设置为1；
         * theta：角度精度，以弧度为单位的角度分辨率，一般设置为π⁄180；
         * threshold：阈值参数，如某个像素值大于threshold，才会在直线检测时把该像素纳入计算；
         * srn：rho参数的除数距离，默认值为0；
         * stn：theta参数的除数距离，默认值为0；如果srn和stn同时为0，就表示使用经典的霍夫变换。否则，这两个参数应该都为正数。
         * min_theta：检测到的直线的最小角度，小于此的不予考虑；
         * max_theta：检测到的直线的最大角度，大于此的不予考虑。
         */
        Imgproc.HoughLines(canny, storage, 1, Math.PI / 180, 140, 0, 0, 0, 90);
        for (int x = 0; x < storage.rows(); x++) {
            double[] vec = storage.get(x, 0);

            double rho = vec[0]; //圆的半径r
            double theta = vec[1]; //直线的角度

            Point pt1 = new Point();
            Point pt2 = new Point();

            double a = Math.cos(theta);
            double b = Math.sin(theta);

            double x0 = a * rho;
            double y0 = b * rho;

            int lineLength = 1000;

            pt1.x = Math.round(x0 + lineLength * (-b));
            pt1.y = Math.round(y0 + lineLength * (a));
            pt2.x = Math.round(x0 - lineLength * (-b));
            pt2.y = Math.round(y0 - lineLength * (a));

            if (theta >= 0) {
                Imgproc.line(imgLine, pt1, pt2, new Scalar(0, 0, 255), 1, Imgproc.LINE_4, 0);
            }
        }
        imshow("直线检测", imgLine);
        imwrite(processedImage, imgLine);

        //文字区域
        List<RotatedRect> rects = ImageOpencvUtil.findTextRegion(binaryImg);

        Mat dst = image.clone();
        System.out.println(rects.size());
        //用红线画出找到的轮廓
        for (RotatedRect rotatedRect : rects) {
            Point[] rectPoint = new Point[4];
            rotatedRect.points(rectPoint);
            for (int j = 0; j <= 3; j++) {
                Imgproc.line(dst, rectPoint[j], rectPoint[(j + 1) % 4], new Scalar(0, 0, 255), 2);
            }
        }
        //显示带轮廓的图像
        imshow("Contour Image", dst);

        //倾斜矫正
        Mat correctedImg = ImageOpencvUtil.correction(rects, image);
        imshow("校正", correctedImg);

        //倾斜校正后裁剪
        Mat cuttedImg = ImageOpencvUtil.cutRect(correctedImg);
        imshow("裁剪", cuttedImg);

//        imwrite(processedImage, cuttedImg);
        waitKey();
    }
}
