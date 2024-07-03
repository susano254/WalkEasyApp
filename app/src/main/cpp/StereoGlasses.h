//
// Created by mahmoud on 3/24/24.
//

#ifndef WALKEASYAPP_STEREOGLASSES_H
#define WALKEASYAPP_STEREOGLASSES_H

#include <opencv2/core.hpp>
#include <opencv2/calib3d.hpp>
#include <opencv2/ximgproc.hpp>

using namespace cv;

namespace SG {

    class StereoGlasses {
        public:
        Mat frameLeft, frameRight, rectifiedLeft, rectifiedRight;
        Mat Left_Stereo_Map1, Left_Stereo_Map2;
        Mat Right_Stereo_Map1, Right_Stereo_Map2;
        double fx, fy, cx, cy, b;

        pthread_mutex_t mutex;

        int k = 17;
        int blockSize = 9;
        int P1 = 8*9*blockSize*blockSize; //*blockSize;
        int P2 = 32*9*blockSize*blockSize; //*blockSize;
        int minDisparity = 0;
        int disparityFactor = 9;
        int numDisparities = 16*disparityFactor - minDisparity;
        Mat left_disp, right_disp, disparity, filteredDisparity, coloredDisparity;
        Mat result;

        Ptr<StereoBM> leftMatcher = StereoBM::create(numDisparities, blockSize);
        Ptr<StereoMatcher>rightMatcher = ximgproc::createRightMatcher(leftMatcher);
        cv::Ptr<ximgproc::DisparityWLSFilter> wlsFilter = ximgproc::createDisparityWLSFilter(leftMatcher);

        StereoGlasses(){
            leftMatcher->setBlockSize(blockSize);
            leftMatcher->setNumDisparities(numDisparities);
            leftMatcher->setMinDisparity(minDisparity);
            leftMatcher->setDisp12MaxDiff(1);
            leftMatcher->setPreFilterCap(63);
            leftMatcher->setUniquenessRatio(0);
            leftMatcher->setSpeckleWindowSize(0);
            leftMatcher->setSpeckleRange(32);



            // rightMatcher = ximgproc::createRightMatcher(leftMatcher);
            rightMatcher->setBlockSize(blockSize);
            rightMatcher->setNumDisparities(numDisparities);
            rightMatcher->setMinDisparity(minDisparity);
            rightMatcher->setDisp12MaxDiff(1);
            rightMatcher->setSpeckleWindowSize(9);
            rightMatcher->setSpeckleRange(32);
        }

        void init();
        void getDepthMap(Mat left, Mat right);
        void run();


    };

} // StereoGlasses

#endif //WALKEASYAPP_STEREOGLASSES_H
