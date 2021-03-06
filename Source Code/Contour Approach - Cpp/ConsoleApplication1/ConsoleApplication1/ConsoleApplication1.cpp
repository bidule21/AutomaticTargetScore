#include "stdafx.h"
#define M_PI 3.14159265358979323846264338327950288

#include<opencv2/opencv.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include "opencv2/videoio.hpp"
#include <iostream>
#include <cmath>
#include <string>

using namespace cv;
using namespace std;

/// Function header
void thresh_callback(int, void*);
Mat src; Mat src_gray;
int thresh = 100;
int max_thresh = 255;
RNG rng(12345);


/** @function main */
int main(int argc, char** argv)
{
	
	VideoCapture cap(0);
	if (!cap.isOpened()) {
		cout << "Camera is not open" << endl;
		return -1;
	}
	
	cap.set(CV_CAP_PROP_FRAME_WIDTH, 640);
	cap.set(CV_CAP_PROP_FRAME_HEIGHT, 480);
	
	while (char(waitKey(1) != 'q' && cap.isOpened())) {
		Mat frame;
		cap >> frame;
		if (frame.empty()) {
			cout << "Video over" << endl;
			break;
		}
		/// Create Window
		
		
		cvtColor(frame, src_gray, COLOR_BGR2GRAY);
		blur(src_gray, src_gray, Size(3, 3));

		
		namedWindow("src", WINDOW_AUTOSIZE);
		imshow("src", frame);

		createTrackbar(" thresh:", "src", &thresh, max_thresh, thresh_callback);
		thresh_callback(0, 0);
		
	}

	

	
	return(0);
}

/** @function thresh_callback */
void thresh_callback(int, void*)
{
	Mat canny_output;
	vector<vector<Point> > contours;
	vector<Vec4i> hierarchy;

	/// Detect edges using canny
	Canny(src_gray, canny_output, thresh, thresh * 2, 3);
	/// Find contours
	findContours(canny_output, contours, hierarchy, CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE, Point(0, 0));
	
	/// Draw contours
	Mat drawing = Mat::zeros(canny_output.size(), CV_8UC3);
	for (int i = 0; i< contours.size(); i++)
	{
		Scalar color = Scalar(rng.uniform(0, 255), rng.uniform(0, 255), rng.uniform(0, 255));
		drawContours(drawing, contours, i,color, 2, 8, hierarchy, 0, Point());
	}
	///Get moments
	vector<Moments> mu(contours.size());
	for (int i = 0; i < contours.size(); i++)
	{
		mu[i] = moments(contours[i], false);
	}

	/// Get the mass centers:
	vector<Point2f> mc(contours.size());
	for (int i = 0; i < contours.size(); i++)
	{
		mc[i] = Point2f(mu[i].m10 / mu[i].m00, mu[i].m01 / mu[i].m00);

		int font = FONT_HERSHEY_SIMPLEX;
		putText(drawing, std::to_string(mc[i].x).append(",").append(std::to_string(mc[i].y)), mc[i], font, 1, (255, 255, 255), 2, LINE_AA);
	}
	
	try {
		/// Show in a window
		namedWindow("Contours", CV_WINDOW_AUTOSIZE);
		imshow("Contours", drawing);
	}
	catch (exception& e)
	{
		cout << e.what() << '\n';
	}
	
}