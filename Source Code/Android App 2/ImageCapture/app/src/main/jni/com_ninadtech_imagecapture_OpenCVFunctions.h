/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
#include "opencv2/opencv.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include <stdio.h>
/* Header for class com_ninadtech_imagecapture_OpenCVFunctions */
using namespace cv;
using namespace std;
#ifndef _Included_com_ninadtech_imagecapture_OpenCVFunctions
#define _Included_com_ninadtech_imagecapture_OpenCVFunctions
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_ninadtech_imagecapture_OpenCVFunctions
 * Method:    hogueTransform
 * Signature: (J)V
 */
 void detectAndDisplayCircles(Mat& frame);
 string to_string(int value);
 int toGray(Mat img, Mat& gray);
JNIEXPORT void JNICALL Java_com_ninadtech_imagecapture_OpenCVFunctions_hogueTransform
  (JNIEnv *, jclass, jlong);
JNIEXPORT void JNICALL Java_com_ninadtech_imagecapture_OpenCVFunctions_convertToGray
  (JNIEnv *, jclass, jlong, jlong);


  JNIEXPORT void JNICALL Java_com_ninadtech_imagecapture_OpenCVFunctions_detectBlobs
    (JNIEnv *, jclass, jlong);
#ifdef __cplusplus
}
#endif
#endif