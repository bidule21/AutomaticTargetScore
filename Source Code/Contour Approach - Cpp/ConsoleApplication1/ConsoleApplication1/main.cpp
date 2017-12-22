#include<opencv2/opencv.hpp>
#include "opencv2/videoio.hpp"
#include <iostream>

using namespace std;
using namespace cv;
int main()
{
	VideoCapture cap(0);
	if (!cap.isOpened()) {
		cout << "Camera is not open" << endl;
		return -1;
	}
	namedWindow("Video");
	while (char(waitKey(1) != 'q' && cap.isOpened())) {
		Mat frame;
		cap >> frame;
		if (frame.empty()) {
			cout << "Video over" << endl;
		}
		imshow("Video", frame);
	}
	cout << "Hello world!" << endl;
	return 0;
}
