package compare;

import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dsm_024 on 2016-06-04.
 */
public class EyeCompare {
    public static double compareFeature(Mat img1, Mat img2) {
        double retVal = 0;

       Imgproc.resize(img2,img2, img1.size());

        // Declare key point of images
        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
        Mat descriptors1 = new Mat();
        Mat descriptors2 = new Mat();

        // Definition of ORB key point detector and descriptor extractors
        FeatureDetector detector = FeatureDetector.create(FeatureDetector.FAST);
        //GFTT, GRID_GFTT,PYRAMTD_GFTT,DYNAMIC_GFTT, FAST, AKAZE
        DescriptorExtractor extractor = DescriptorExtractor.create(3);
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

        // Detect key points
        detector.detect(img1, keypoints1);
        detector.detect(img2, keypoints2);


        KeyPoint[] point1 = keypoints1.toArray();

        KeyPoint[] point2 = keypoints2.toArray();

        List<KeyPoint> pointList1 = new ArrayList<KeyPoint>(Arrays.asList(point1));

        List<KeyPoint> pointList2 = new ArrayList<KeyPoint>(Arrays.asList(point2));

        double temp;
        Point tempPoint;
        Size size = new Size(5, 5);
        // System.out.println("List1's size "+pointList1.size());

     /*   for (int i = 0; i < pointList1.size(); i++) {
            boolean is = false;
            temp = pointList1.get(i).pt.y;
            //System.out.println(i + "번째 " + pointList1.get(i).pt.x + " , " + pointList1.get(i).pt.y);
            Scalar color = new Scalar(110, 220, 0);


            for (int i2 = i + 1; i2 < pointList1.size(); i2++) {
                if (temp <100) {
                    is = true;
                }
            }
            if (is) {
                pointList1.remove(i);
            }
            Imgproc.ellipse(img1, pointList1.get(i).pt, size, 0.0, 0.0, 360.0, color);

        }
        */
        // displayImage(Mat2BufferedImage(img1));

		/*
		 * int circle_radius = 10; circle(img, center, circle_radius, color,
		 * thickness); circle(img, center, circle_radius, color, CV_FILLED);
		 */

        // System.out.println("After List1's size "+pointList1.size());

     /*   for (int i = 0; i < pointList2.size(); i++) {
            boolean is = false;
            temp = pointList2.get(i).pt.y;

            for (int i2 = i + 1; i2 < pointList2.size(); i2++) {
                if (temp == pointList2.get(i2).pt.y) {
                    is = true;
                }
                Scalar color = new Scalar(110, 220, 0);
                Imgproc.ellipse(img2, pointList2.get(i).pt, size, 0.0, 0.0, 360.0, color);
            }
            if (!is) {
                // pointList2.remove(i);
            }

        }*/
        // displayImage(Mat2BufferedImage(img2));

		/*keypoints1.fromList(pointList1);
		keypoints2.fromList(pointList2);*/

        // Extract descriptors
        extractor.compute(img1, keypoints1, descriptors1);
        extractor.compute(img2, keypoints2, descriptors2);

        // Definition of descriptor matcher


        // Match points of two images
        MatOfDMatch matches = new MatOfDMatch();
        // System.out.println("Type of Image1= " + descriptors1.type() + ", Type
        // of Image2= " + descriptors2.type());
        // System.out.println("Cols of Image1= " + descriptors1.cols() + ", Cols
        // of Image2= " + descriptors2.cols());

        // Avoid to assertion failed
        // Assertion failed (type == src2.type() && src1.cols == src2.cols &&
        // (type == CV_32F || type == CV_8U)
        if (descriptors2.cols() == descriptors1.cols()) {
            matcher.match(descriptors1, descriptors2, matches);

            // Check matches of key points
            DMatch[] match = matches.toArray();
            double max_dist = 0;
            double min_dist = 100;
            double avg_dist = 0;

            for (int i = 0; i < descriptors1.rows(); i++) {
                double dist = match[i].distance;
                if (dist < min_dist)
                    min_dist = dist;
                if (dist > max_dist)
                    max_dist = dist;
                avg_dist += match[i].distance;
                // System.out.println("dist = " + dist);
            }


            avg_dist = avg_dist / descriptors1.rows();
            retVal = avg_dist;


            // Extract good images (distances are under 10)
            for (int i = 0; i < descriptors1.rows(); i++) {
                if (match[i].distance <= 10) {
                    retVal++;
                }
            }
            // System.out.println("matching count=" + retVal);
        }


        // System.out.println("estimatedTime=" + estimatedTime + "ms");

        return retVal;

        // Declare key point of images

    }




}
