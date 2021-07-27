package com.example.demo.utils;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.*;

import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;
import static org.opencv.imgproc.Imgproc.RETR_EXTERNAL;

/**
 * @author ly
 * @since 2021/4/21
 */
public class ImageOpencvUtil {
    private static final int BLACK = 0;
    private static final int WHITE = 255;
    private static final Size STANDARDSIZE = new Size(673, 425);

    // 私有化构造函数
    private ImageOpencvUtil() {
    }

    /**
     * 伽马校正
     * 伽马校正对图像的修正作用就是通过增强低灰度或高灰度的细节实现的
     * 值越小，对图像低灰度部分的扩展作用就越强，值越大，对图像高灰度部分的扩展作用就越强，
     * 通过不同的值，就可以达到增强低灰度或高灰度部分细节的作用。
     * 在对图像进行伽马变换时，如果输入的图像矩阵是CV_8U,在进行幂运算时，大于255的值会自动截断为255；
     * 所以，先将图像的灰度值归一化到【0,1】范围，然后再进行幂运算
     *
     * @param src
     */
    public static Mat imageBrightness(Mat src) {
        //定义2个与输入图像大小类型一致的空对象
        Mat dst = new Mat(src.size(), src.type());
        Mat dst_1 = new Mat(src.size(), src.type());
        /**
         * 缩放并转换到另外一种数据类型：
         * dst：目的矩阵；
         * type：需要的输出矩阵类型，或者更明确的，是输出矩阵的深度，如果是负值（常用-1）则输出矩阵和输入矩阵类型相同；
         * scale:比例因子（输入矩阵参数*比例因子）；
         * shift：将输入数组元素按比例缩放后添加的值（第三个参数处理后+第四个参数）；
         * CV_64F:64 -表示双精度 32-表示单精度 F - 浮点  Cx - 通道数,例如RGB就是三通道
         */
        src.convertTo(dst, CvType.CV_64F, 1.0 / 255, 0);

        /**
         * 将每个数组元素提升为幂：
         * 对于非整数幂指数，将使用输入数组元素的绝对值。 但是，可以使用一些额外的操作获得负值的真实值。
         * 对于某些幂值（例如整数值0.5和-0.5），使用了专用的更快算法。
         * 不处理特殊值（NaN，Inf）。
         *  @param src 输入Nat矩阵。
         *  @param power 幂的幂指数。
         *  @param dst 输出数组，其大小和类型与输入数组相同。
         */
        Core.pow(dst, 0.7, dst_1);
        /* 缩放并转换到另外一种数据类型：
         * CV_8UC1---8位无符号的单通道---灰度图片
         * CV_8UC3---8位无符号的三通道---RGB彩色图像
         * CV_8UC4---8位无符号的四通道---带透明色的RGB图像
         */
        dst_1.convertTo(dst_1, CvType.CV_8U, 255, 0);

        return dst_1;
    }

    /**
     * 作用：灰度化
     *
     * @param src 需灰度化处理的Mat矩阵图像
     * @return
     */
    public static Mat gray(Mat src) {
        Mat grayImage = new Mat();
        try {
            grayImage = new Mat(src.height(), src.width(), CvType.CV_8UC1);
            Imgproc.cvtColor(src, grayImage, Imgproc.COLOR_BGR2GRAY);
        } catch (Exception e) {
            grayImage = src.clone();
            grayImage.convertTo(grayImage, CvType.CV_8UC1);
            System.out.println("The Image File Is Not The RGB File!已处理...");
        }
        return grayImage;
    }

    /**
     * 作用：均值滤波
     *
     * @param src Mat矩阵图像
     * @return
     */
    public static Mat blur(Mat src) {
        Mat dst = src.clone();
        Imgproc.blur(src, dst, new Size(9, 9), new Point(-1, -1), Core.BORDER_DEFAULT);
        return dst;
    }

    /**
     * 作用：高斯滤波
     *
     * @param src Mat矩阵图像
     * @return
     */
    public static Mat gaussianBlur(Mat src) {
        Mat dst = src.clone();
        Imgproc.GaussianBlur(src, dst, new Size(9, 9), 0, 0, Core.BORDER_DEFAULT);
        return dst;
    }

    /**
     * 作用：中值滤波
     *
     * @param src Mat矩阵图像
     * @return
     */
    public static Mat medianBlur(Mat src) {
        Mat dst = src.clone();
        Imgproc.medianBlur(src, dst, 7);
        return dst;
    }

    /**
     * 作用：均值迁移滤波
     *
     * @param src Mat矩阵图像
     * @return
     */
    public static Mat pyrMeanShiftFiltering(Mat src) {
        Mat dst = src.clone();
        //sp、sr值越大，函数执行时间越长
//        Imgproc.pyrMeanShiftFiltering(src, dst, 20, 50);
        //todo:修改sp数值
        Imgproc.pyrMeanShiftFiltering(src, dst, 20, 50);
        return dst;
    }

    /**
     * 作用：自适应中值滤波的噪点检测与认定
     *
     * @param src        Mat矩阵图像
     * @param row        像素所处行位置
     * @param col        像素所处列位置
     * @param kernelSize 模板窗口尺寸大小
     * @param maxSize    模板窗口最大尺寸
     * @return
     */
    private static int adaptiveProcess(Mat src, int row, int col, int kernelSize, int maxSize) {
        List<Integer> pixels = new ArrayList<>();
        for (int y = -kernelSize / 2; y <= kernelSize / 2; y++) {
            for (int x = -kernelSize / 2; x <= kernelSize / 2; x++) {
                pixels.add((int) src.get(row + y, col + x)[0]);
            }
        }
        pixels.sort(Comparator.naturalOrder());
        int p_min = pixels.get(0);
        int p_med = pixels.get((kernelSize * kernelSize) / 2);
        int p_max = pixels.get(kernelSize * kernelSize - 1);
        int p_ij = (int) src.get(row, col)[0];
        //p_min < p(i,j) < p_max，则输出p(i,j)，否则输出p_med
        if (p_min < p_med && p_med < p_max) {
            if (p_min < p_ij && p_ij < p_max)
                return p_ij;
            else
                return p_med;
        }
        //如果不满足p_min < p_med < p_max，那么增大窗口M(i,j)尺寸。若M(i,j)的尺寸小于M_max的尺寸，则重复adaptiveProcess()函数；否则输出p_med
        else {
            kernelSize += 2;
            if (kernelSize <= maxSize)
                return adaptiveProcess(src, row, col, kernelSize, maxSize);
            else
                return p_med;
        }
    }

    /**
     * 作用：自适应中值滤波
     *
     * @param src     Mat矩阵图像
     * @param minSize 模板窗口最小尺寸
     * @param maxSize 模板窗口最大尺寸
     * @return 自适应中值滤波处理后的Mat矩阵图像
     */
    public static Mat adaptiveMediaFilter(Mat src, int minSize, int maxSize) {
        Mat dst = new Mat();
        //边缘扩充
        Core.copyMakeBorder(src, dst, maxSize / 2, maxSize / 2, maxSize / 2, maxSize / 2, Core.BORDER_REFLECT);

        int width = dst.cols();
        int height = dst.rows();
        for (int j = maxSize / 2; j < height - maxSize / 2; j++) {
            for (int i = maxSize / 2; i < width - maxSize / 2; i++) {
                adaptiveProcess(dst, j, i, minSize, maxSize);
            }
        }
        return dst;
    }

    /**
     * 作用：绘制灰度化图像的直方图
     *
     * @param grayImage 灰度化图像
     * @return 灰度图的直方图
     */
    public static Mat getHistogram(Mat grayImage) {
        //添加图像
        List<Mat> images = new ArrayList<>();
        images.add(grayImage);

        MatOfInt channels = new MatOfInt(0); // 图像通道数，0表示只有一个通道
        MatOfInt histSize = new MatOfInt(256); // CV_8U类型的图片范围是0~255，共有256个灰度级
        Mat histogramOfGray = new Mat(); // 输出直方图结果，共有256行，行数的相当于对应灰度值，每一行的值相当于该灰度值所占比例
        MatOfFloat histRange = new MatOfFloat(0, 255);

        //计算直方图
        Imgproc.calcHist(images, channels, new Mat(), histogramOfGray, histSize, histRange);

        //按行归一化
        Core.normalize(histogramOfGray, histogramOfGray, 0, histogramOfGray.rows(), Core.NORM_MINMAX, -1, new Mat());

        //创建画布
        int histImgRows = 300;
        int histImgCols = 300;
        int colStep = (int) Math.floor(histImgCols / histSize.get(0, 0)[0]);

        //创建背景，重新建一张图片，绘制直方图
        Mat histImg = new Mat(histImgRows, histImgCols, CvType.CV_8UC3, new Scalar(255, 255, 255));
        //画出每一个灰度级分量的比例
        for (int i = 0; i < histSize.get(0, 0)[0]; i++) {
            Imgproc.line(histImg,
                    new org.opencv.core.Point(colStep * i, histImgRows - 20),
                    new org.opencv.core.Point(colStep * i, histImgRows - Math.round(histogramOfGray.get(i, 0)[0]) - 20),
                    new Scalar(0, 0, 0), 2, 8, 0);
            if (i % 50 == 0) {
                //x轴刻度
                Imgproc.putText(histImg, Integer.toString(i), new org.opencv.core.Point(colStep * i, histImgRows - 5), 1, 1, new Scalar(0, 0, 0));
            }
        }

        return histImg;
    }

    /**
     * 作用：二值化
     *
     * @param grayImage 需二值化处理的灰度化后的Mat矩阵图像
     * @return
     */
    public static Mat ImgBinarization(Mat grayImage) {
        Mat threshImage = new Mat(grayImage.height(), grayImage.width(), CvType.CV_8UC1);

        //局部自适应阈值MEAN_C
//        Imgproc.adaptiveThreshold(grayImage, threshImage, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 19, 10);

        //局部自适应阈值GAUSSIAN_C
//        Imgproc.adaptiveThreshold(grayImage, threshImage, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 19, 10);

        //全局大津法二值化
        Imgproc.threshold(grayImage, threshImage, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);

        //全局三角形算法，白黑不行
//        Imgproc.threshold(grayImage, threshImage, 127, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_TRIANGLE);

//        Imgproc.threshold(grayImage, threshImage, 127, 255, Imgproc.THRESH_TRUNC);//还行
//        Imgproc.threshold(grayImage, threshImage, 127, 255, Imgproc.THRESH_TOZERO);//不行

        return threshImage;
    }

    /**
     * 作用：自适应选取阀值
     *
     * @param src Mat矩阵图像
     * @return
     */
    private static int getAdapThreshold(Mat src) {
        int threshold = 0, thresholdNew = 127;
        int nWhiteCount, nBlackCount;
        int nWhiteSum, nBlackSum;
        int value, i, j;
        int width = src.cols();//图片宽
        int height = src.rows();//图片高

        while (threshold != thresholdNew) {
            nWhiteSum = nBlackSum = 0;
            nWhiteCount = nBlackCount = 0;
            for (j = 0; j < height; j++) {
                for (i = 0; i < width; i++) {
                    value = (int) src.get(j, i)[0];
                    if (value > thresholdNew) {
                        nWhiteCount++;
                        nWhiteSum += value;
                    } else {
                        nBlackCount++;
                        nBlackSum += value;
                    }
                }
            }
            threshold = thresholdNew;
            thresholdNew = (nWhiteSum / nWhiteCount + nBlackSum / nBlackCount) / 2;
        }
        return threshold;
    }

    /**
     * 作用：翻转图像像素
     *
     * @param src Mat矩阵图像
     * @return
     */
    private static Mat turnPixel(Mat src) {
        if (src.channels() != 1) {
            throw new RuntimeException("不是单通道图，需要先灰度化！！！");
        }
        int j, i, value;
        int width = src.cols();
        int height = src.rows();
        for (j = 0; j < height; j++) {
            for (i = 0; i < width; i++) {
                value = (int) src.get(j, i)[0];
                if (value == 0) {
                    src.put(j, i, WHITE);
                } else {
                    src.put(j, i, BLACK);
                }
            }
        }
        return src;
    }

    /**
     * 作用：阀值自适应确定 图像二值化，OTSU效果
     *
     * @param grayImage 灰度化后的Mat矩阵图像
     * @return
     */
    public static Mat binaryzation(Mat grayImage) {
        Mat dst = grayImage.clone();
        if (dst.channels() != 1) {
            throw new RuntimeException("不是单通道图，需要先灰度化！！！");
        }
        int nWhiteSum = 0, nBlackSum = 0;
        int i, j;
        int width = dst.cols();
        int height = dst.rows();
        int value;

        int threshold = getAdapThreshold(dst);

        for (j = 0; j < height; j++) {
            for (i = 0; i < width; i++) {
                value = (int) dst.get(j, i)[0];
                if (value > threshold) {
                    dst.put(j, i, WHITE);
                    nWhiteSum++;
                } else {
                    dst.put(j, i, BLACK);
                    nBlackSum++;
                }
            }
        }
        if (true) {
            // 白底黑字
            if (nBlackSum > nWhiteSum) {
                dst = turnPixel(dst);
            }
        } else {
            // 黑底白字
            if (nWhiteSum > nBlackSum) {
                dst = turnPixel(dst);
            }
        }
        return dst;
    }

    /**
     * 作用：根据二值化图片进行膨胀与腐蚀
     *
     * @param binaryImage 需膨胀腐蚀处理的二值化后的Mat矩阵图像
     * @return
     */
    public static Mat corrosion(Mat binaryImage) {
        Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 1));
        Mat element2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 1));
        //第一次膨胀
        Mat dilate1 = new Mat();
        Imgproc.dilate(binaryImage, dilate1, element2);
//        Imgproc.dilate(binaryImage, dilate1, element2, new Point(-1, -1), 1, 1, new Scalar(1));
        //腐蚀
        Mat erode1 = new Mat();
        Imgproc.erode(dilate1, erode1, element1);
        //第二次膨胀
        Mat dilate2 = new Mat();
        Imgproc.dilate(erode1, dilate2, element2);
        return dilate2;
    }

    /**
     * 作用：获取文字区域
     *
     * @param img 膨胀与腐蚀后的Mat矩阵图像
     * @return
     */
    public static List<RotatedRect> findTextRegion(Mat img) {
        List<RotatedRect> rects = new ArrayList<RotatedRect>();
        //查找轮廓
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        //CV_CHAIN_APPROX_NONE存储所有的轮廓点，相邻的两个点的像素位置差不超过1，即max（abs（x1-x2），abs（y2-y1））==1
        //CV_CHAIN_APPROX_SIMPLE压缩水平方向，垂直方向，对角线方向的元素，只保留该方向的终点坐标，例如一个矩形轮廓只需4个点来保存轮廓信息

        //RETR_EXTERNAL表示只检测外轮廓
//        Imgproc.findContours(img, contours, hierarchy, RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE, new Point(-1, -1));//20.png不能被倾斜校正

        //RETR_CCOMP建立两个等级的轮廓，上面的一层为外边界，里面的一层为内孔的边界信息。如果内孔内还有一个连通物体，这个物体的边界也在顶层。
        Imgproc.findContours(img, contours, hierarchy, Imgproc.RETR_CCOMP, CHAIN_APPROX_SIMPLE, new Point(-1, -1));//20.png可以被倾斜校正

//        Imgproc.findContours(img, contours, hierarchy, RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE, new Point(0, 0));//20.png不能被倾斜校正

        int img_width = img.width();
        int img_height = img.height();
        int size = contours.size();

        //筛选面积小的矩形
        for (int i = 0; i < size; i++) {
            double area = Imgproc.contourArea(contours.get(i));
            if (area < 50000)//原来是1000
                continue;

            //轮廓近似，approxPolyDP函数
            double epsilon = 0.001 * Imgproc.arcLength(new MatOfPoint2f(contours.get(i).toArray()), true);
            MatOfPoint2f approxCurve = new MatOfPoint2f();
            Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), approxCurve, epsilon, true);

            //找到可能有方向的最小矩形
            RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(contours.get(i).toArray()));
            //计算高和宽
            int m_width = rect.boundingRect().width;
            int m_height = rect.boundingRect().height;

            //筛选太窄的矩形，留下扁的
            if (m_width < m_height)
                continue;
            if (img_width == rect.boundingRect().br().x)
                continue;
            if (img_height == rect.boundingRect().br().y)
                continue;
            //将符合条件的rect添加到rects集合中
            rects.add(rect);
        }
        return rects;
    }

    /**
     * 作用：摆正图片
     *
     * @param rects    文字区域
     * @param srcImage 原Mat矩阵图像
     * @return
     */
    public static Mat correction(List<RotatedRect> rects, Mat srcImage) {
        double degree = 0;
        double degreeCount = 0;
        for (int i = 0; i < rects.size(); i++) {
            if (rects.get(i).angle >= -90 && rects.get(i).angle < -45) {
                degree = rects.get(i).angle;
                if (rects.get(i).angle != 0) {
                    degree += 90;
                }
            }
            if (rects.get(i).angle > -45 && rects.get(i).angle <= 0) {
                degree = rects.get(i).angle;
            }
            if (rects.get(i).angle <= 90 && rects.get(i).angle > 45) {
                degree = rects.get(i).angle;
                if (rects.get(i).angle != 0) {
                    degree -= 90;
                }
            }
            if (rects.get(i).angle < 45 && rects.get(i).angle >= 0) {
                degree = rects.get(i).angle;
            }
            if (degree > -5 && degree < 5) {
                degreeCount += degree;
            }
        }
        if (degreeCount != 0) {
            //获取平均水平度数
            degree = degreeCount / rects.size();
        }
        Point center = new Point(srcImage.cols() / 2, srcImage.rows() / 2);
        //获取仿射变换矩阵，center为旋转的中心点，angle为旋转角度，scale为缩放因子
        Mat M = Imgproc.getRotationMatrix2D(center, degree, 1.0);
        Mat dst = new Mat();

        /**
         * 进行图像旋转操作
         *
         * src：输入图像；
         * dst：输出图像，尺寸由dsize指定，图像类型与原图像一致；
         * M：getRotationMatrix2D()方法得到的仿射变换矩阵；
         * dSize：指定的图像输出尺寸；
         * flags：插值算法标识符，默认值为线性插值INTER_LINEAR；
         * borderMode：边界像素模式，默认值为BORDER_CONSTANT；
         * borderValue：边界取值，默认值Scalar()即0。
         */
        Imgproc.warpAffine(srcImage, dst, M, srcImage.size(), Imgproc.INTER_LINEAR, 0, new Scalar(255, 255, 255));
        return dst;
    }

    /**
     * 作用：倾斜矫正并返回标准化图像
     *
     * @param src 需倾斜校正的Mat矩阵图像
     * @return
     */
    public static Mat imgCorrection(Mat src) {
        //灰度化
        Mat grayImg = gray(src);
        //二值化
        Mat binaryImg = binaryzation(grayImg);
        //膨胀与腐蚀
        Mat corrodedImg = corrosion(binaryImg);
        //查找和筛选文字区域
        List<RotatedRect> rects = findTextRegion(corrodedImg);
        //倾斜校正
        Mat correctedImg = correction(rects, src);
        //裁剪
        Mat cuttedImg = cutRect(correctedImg);
        //标准化
        Mat zoomedImg = zoom(cuttedImg);

        return zoomedImg;
    }

    /**
     * Sobel算法，边缘检测
     *
     * @param src Mat矩阵图像
     * @return
     */
    public static Mat sobel(Mat src) {
        //横向
        Mat gray_x = new Mat();
        Imgproc.Sobel(src, gray_x, -1, 1, 0, 3, 1, 0, Core.BORDER_DEFAULT);

        //竖向
        Mat gray_y = new Mat();
        Imgproc.Sobel(src, gray_y, -1, 0, 1, 3, 1, 0, Core.BORDER_DEFAULT);

        //横竖向图像融合
        Mat gray_xy = new Mat();
        Core.addWeighted(gray_x, 0.5, gray_y, 0.5, 0, gray_xy);
        return gray_xy;
    }

    /**
     * canny算法，边缘检测
     *
     * @param src Mat矩阵图像
     * @return
     */
    public static Mat canny(Mat src) {
        Mat dst = src.clone();

        //灰度化
        src = gray(src);
        //去噪可能影响身份证边缘轮廓检测
//        src = gaussianBlur(src);//高斯去噪
        //二值化后会降低边缘轮廓检测效果
//        src = ImgBinarization(src);

        //Canny边缘检测
        //todo threshold1与threshold2影响结果
//        Imgproc.Canny(src, dst, 20, 60, 3, false);
//        Imgproc.Canny(src, dst, 100, 40, 3, false);
//        Imgproc.Canny(src, dst, 150, 40, 3, false);
        Imgproc.Canny(src, dst, 20, 60, 3, false);
//        Imgproc.Canny(src, dst, 10, 30, 3, false);

        //膨胀，连接边缘
        Imgproc.dilate(dst, dst, new Mat(), new Point(-1, -1), 1, 1, new Scalar(1));
        imshow("边缘轮廓检测", dst);
        return dst;
    }

    /**
     * 作用：返回边缘检测之后的最大矩形轮廓
     *
     * @param cannyMat Canny之后的mat矩阵
     * @return
     */
    public static RotatedRect findMaxRect(Mat cannyMat) {
        //边缘检测
        cannyMat = canny(cannyMat);
//        Imgproc.dilate(cannyMat, cannyMat, new Mat(), new Point(-1, -1), 1, 1, new Scalar(0.5));
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();

        //RETR_EXTERNAL表示只检测外轮廓
//        Imgproc.findContours(cannyMat, contours, hierarchy, RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE, new Point(0, 0));
//        Imgproc.findContours(cannyMat, contours, hierarchy, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);

        //RETR_CCOMP建立两个等级的轮廓，上面的一层为外边界，里面的一层为内孔的边界信息。如果内孔内还有一个连通物体，这个物体的边界也在顶层。
        Imgproc.findContours(cannyMat, contours, hierarchy, Imgproc.RETR_CCOMP, CHAIN_APPROX_SIMPLE, new Point(-1, -1));//20.png可以被倾斜校正

        //找出匹配到的最大轮廓
        double area = Imgproc.boundingRect(contours.get(0)).area();
        int index = 0;

        for (int i = 0; i < contours.size(); i++) {
            double tempArea = Imgproc.boundingRect(contours.get(i)).area();
            if (tempArea > area) {
                area = tempArea;
                index = i;
            }
        }

        MatOfPoint2f matOfPoint2f = new MatOfPoint2f(contours.get(index).toArray());
        RotatedRect rect = Imgproc.minAreaRect(matOfPoint2f);
        return rect;
    }

    /**
     * 作用：把矫正后的图像切割出来
     *
     * @param correctMat 图像矫正后的Mat矩阵
     * @return
     */
    public static Mat cutRect(Mat correctMat) {
        //获取最大矩形
        RotatedRect rect = findMaxRect(correctMat);

        Point[] rectPoint = new Point[4];
        rect.points(rectPoint);

        for (Point p : rectPoint) {
            System.out.println(p.x + " , " + p.y);
        }

        int startLeft = (int) Math.abs(rectPoint[0].x);
        int startUp = (int) Math.abs(Math.abs(rectPoint[0].y) < Math.abs(rectPoint[1].y) ? rectPoint[0].y : rectPoint[1].y);
        int width = (int) Math.abs(rectPoint[2].x - rectPoint[0].x);
//        int height = (int) Math.abs(rectPoint[1].y - rectPoint[0].y);
        int height = (int) Math.abs(rectPoint[3].y - rectPoint[1].y);

        System.out.println("startLeft = " + startLeft);
        System.out.println("startUp = " + startUp);
        System.out.println("width = " + width);
        System.out.println("height = " + height);

        //检测的高度过低，则说明拍照时身份证边框没拍全，直接返回correctMat，如检测的不是身份证则不需要这个if()判断
        if (height < 0.5 * width)
            return correctMat;
        if (height > 2 * width)
            return correctMat;

        for (Point p : rectPoint) {
            System.out.println(p.x + " , " + p.y);
        }

        if (startLeft + width > correctMat.width()) {
            width = correctMat.width() - startLeft;
        }
        if (startUp + height > correctMat.height()) {
            height = correctMat.height() - startUp;
        }

        Mat cuttedMat = new Mat(correctMat, new Rect(startLeft, startUp, width, height));
//        try {
//            temp = new Mat(correctMat, new Rect(startLeft, startUp, width, height));
//        } catch (Exception e) {
//            System.out.println(e);
//        }

        return cuttedMat;
    }

    /**
     * 作用：缩放图片
     *
     * @param src 需要缩放的Mat矩阵图像
     * @return
     */
    public static Mat zoom(Mat src) {
        Mat dst = new Mat();
        //区域插值(INTER_AREA):图像放大时类似于线性插值，图像缩小时可以避免波纹出现
        Imgproc.resize(src, dst, STANDARDSIZE, 0, 0, Imgproc.INTER_AREA);
        return dst;
    }

    /**
     * 根据二值化图片进行膨胀与腐蚀
     *
     * @param src 需膨胀腐蚀处理的灰度化后的Mat矩阵图像
     * @return
     */
    public static Mat preprocess(Mat src) {
        //1.Sobel算子，x方向求梯度
        Mat sobel = new Mat();
        Imgproc.Sobel(src, sobel, 0, 1, 0, 3);
        imshow("x", sobel);

        //2.二值化
        Mat binaryImage = new Mat();
        Imgproc.threshold(sobel, binaryImage, 0, 255, Imgproc.THRESH_OTSU | Imgproc.THRESH_BINARY);
//        binaryImage = binaryzation(src);

        //3.腐蚀和膨胀操作核设定
        Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(24, 9));
        //设置高度大小可以控制上下行的膨胀程度，例如3比4的区分能力更强,但也会造成漏检
        Mat element2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(26, 9));

        //4.膨胀一次，让轮廓突出
        Mat dilate1 = new Mat();
//        Imgproc.dilate(binaryImage, dilate1, element2);
        Imgproc.dilate(binaryImage, dilate1, element2, new Point(-1, -1), 1, 1, new Scalar(1));

        //5.腐蚀一次，去掉细节，表格线等。这里去掉的是竖直的线
        Mat erode1 = new Mat();
        Imgproc.erode(dilate1, erode1, element1);

        //6.再次膨胀，让轮廓明显一些
        Mat dilate2 = new Mat();
//        Imgproc.dilate(erode1, dilate2, element2);
        Imgproc.dilate(erode1, dilate2, element2, new Point(-1, -1), 1, 1, new Scalar(1));

        return dilate2;
    }

    /**
     * 作用：获取文字区域矩形框
     *
     * @param img 膨胀与腐蚀后的Mat矩阵图像
     * @return
     */
    public static List<RotatedRect> findTextRegionRect(Mat img) {
        //保存姓名、名族、地址、身份证号信息的矩形框
        List<RotatedRect> rects = new ArrayList<RotatedRect>();
        //保存性别、名族、出生年月日信息的矩形框，并将名族信息矩形框添加到rects中
        List<RotatedRect> _rects = new ArrayList<RotatedRect>();

        //查找轮廓
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
//        Imgproc.findContours(img, contours, hierarchy, RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE, new Point(-1, -1));//11.png不能被倾斜校正
        Imgproc.findContours(img, contours, hierarchy, Imgproc.RETR_CCOMP, CHAIN_APPROX_SIMPLE, new Point(-1, -1));//11.png可以被倾斜校正
//        Imgproc.findContours(img, contours, hierarchy, RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE, new Point(0, 0));//11.png不能被倾斜校正

        int img_width = img.width();
        int img_height = img.height();
        int size = contours.size();
        //身份证号宽度
        int idWidth = Imgproc.minAreaRect(new MatOfPoint2f(contours.get(0).toArray())).boundingRect().width;
        //身份证号矩形框在rects中的索引下标
        int index = 0;
        //筛选面积小的矩形
        for (int i = 0; i < size; i++) {
            double area = Imgproc.contourArea(contours.get(i));
            if (area < 600)//原来是1000
                continue;

            //轮廓近似，approxPolyDP函数
            double epsilon = 0.001 * Imgproc.arcLength(new MatOfPoint2f(contours.get(i).toArray()), true);
            MatOfPoint2f approxCurve = new MatOfPoint2f();
            Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), approxCurve, epsilon, true);

            //找到最小矩形
            RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(contours.get(i).toArray()));
            //计算高和宽
            int m_width = rect.boundingRect().width;
            int m_height = rect.boundingRect().height;

            System.out.println("width = " + m_width);

            if (m_height < 20)
                continue;
//            if (m_width < m_height * 1.2)
//                continue;

            //过滤太窄的矩形，留下扁的
            if (m_width * 1.2 < m_height)
                continue;
            if (img_width == rect.boundingRect().br().x)
                continue;
            if (img_height == rect.boundingRect().br().y)
                continue;

            //将符合条件的rect添加到rects集合中
            rects.add(rect);
        }

        //遍历找到身份证矩形框的宽度大小及在rects中的索引下标index
        for (int i = 0; i < rects.size(); i++) {
            int tempIdWidth = rects.get(i).boundingRect().width;
            if (tempIdWidth > idWidth) {
                idWidth = tempIdWidth;
                index = i;
            }
        }
        System.out.println("身份证号下标：" + index);

        //将身份证矩形框存储到索引为0的位置
        if (index != 0) {
            RotatedRect rotatedRect = rects.get(index);
            rects.set(index, rects.get(0));
            rects.set(0, rotatedRect);
            index = 0;
        }
        System.out.println("修改索引后的身份证号下标：" + index);

        //如果身份证号周围有矩形框（公民身份证号码文本矩形框），则将其从rects中移除
        while (idWidth == rects.get(index).boundingRect().width) {
            if (Math.abs(rects.get(index).center.y - rects.get(index + 1).center.y) < 10) {
                rects.remove(index + 1);
            }
            break;
        }

        /*for (int i = 0; i < rects.size(); i++) {
            if (rects.get(i).center.x > rects.get(index).center.x)
                rects.remove(i);
        }
        //将上面的for循环代码--删除身份证号矩形框右边的矩形框改为Iterator迭代器实现

        //下面的代码可能会漏掉一些符合if条件即需要被删除的元素，因为在删除某个元素后，
        //List对象rects的大小发生了变化，而元素索引也在变化，所以会导致在遍历的时候漏掉某些元素。
        for (int i = 0; i < rects.size(); i++) {
            if (rects.get(i).center.x - rects.get(index).center.x < 0 && 80 < rects.get(i).center.y && rects.get(i).center.y < 200) {
                _rects.add(rects.get(i));
                rects.remove(i);
            }
        }*/

        //使用下面的迭代器实现循环rects并删除rects的元素
        Iterator<RotatedRect> iterator = rects.iterator();
        while (iterator.hasNext()) {
            RotatedRect rect = iterator.next();
            //删除身份证号矩形框右边的矩形框，删除身份证号码下边的矩形框
            if (rect.center.x > rects.get(index).center.x||rect.center.y > rects.get(index).center.y)
                iterator.remove();
            //将高度处于（80，200）位置的矩形框添加到_rects中
//            else if (rect.center.x < rects.get(index).center.x && 80 < rect.center.y && rect.center.y < 200) {
//                _rects.add(rect);
//                iterator.remove();
//            }
        }

        //_rects.get(_rects.size() - 2)为 名族信息 矩形框
//        if (_rects.size() >= 2) {
//            for (int i = rects.size() - 1; i >= 0; i--) {
//                //rects按照rects.get(i).center.y从大到小排列，则将名族信息矩形框插入到原来姓名信息矩形框的所在位置
//                if (_rects.get(_rects.size() - 2).center.y > rects.get(i).center.y && _rects.get(_rects.size() - 2).center.y < rects.get(i - 1).center.y) {
//                    rects.add(i, _rects.get(_rects.size() - 2));
//                    break;
//                }
//            }
//        }

        System.out.println("中心坐标x：");
        for (int i = 0; i < rects.size(); i++) {
            System.out.println(rects.get(i).center.x);
        }
        System.out.println("中心坐标y：");
        for (int i = 0; i < rects.size(); i++) {
            System.out.println(rects.get(i).center.y);
        }
        System.out.println("rects.size:" + rects.size());

        return rects;
    }

    /**
     * 作用：提取文字矩形区域图片
     *
     * @param src  需裁减识别的图片
     * @param rect 文字矩形区域
     * @return
     */
    public static Mat cropImage(Mat src, Rect rect) {
        Mat dst = null;
        if (!src.empty())
            dst = new Mat(src, rect);
        return dst;
    }
}