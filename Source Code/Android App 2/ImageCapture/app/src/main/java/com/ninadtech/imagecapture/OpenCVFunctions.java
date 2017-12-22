package com.ninadtech.imagecapture;

/**
 * Created by ninad on 5/1/2017.
 */
public class OpenCVFunctions {
    public native static void hogueTransform(long addrRgba);
    public native static void convertToGray(long addrRgba,long addrGray);
    public native static void detectBlobs(long addrRgba);
}

