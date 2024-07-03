//
// Created by mahmoud on 3/24/24.
//

#include <android/log.h>
#include "StereoGlasses.h"


namespace SG {
    void StereoGlasses::init() {
        __android_log_print(ANDROID_LOG_INFO, "StereoGlassesInfo", "Init running");
        // Use the rotation matrixes for stereo rectification and camera intrinsics for undistorting the image
        // Compute the rectification map (mapping between the original image pixels and
        // their transformed values after applying rectification and undistortion) for left and right camera frames
        float new_mtxL_arr[] = {
                601.4346923828125, 0, 309.1313368132032,
                0, 603.6869506835938, 253.085649887249,
                0, 0, 1
        };
        float new_mtxR_arr[] {
                608.9773559570312, 0, 303.3466741101074,
                0, 609.688720703125, 240.115708415542,
                0, 0, 1
        };
        float  distL_arr[] = {
                -0.07207857185555731, 0.3702109229807098, -0.001033895048862672, -0.002113478862088626, -0.7795669503389046
        };
        float  distR_arr[] = {
                -0.03710126850135417, -0.1562190279237428, -0.0006885435733324113, -0.003325590629556568, 0.6356048318908557
        };
        float  rect_l_arr[] = {
                0.9985343257075832, -0.0470054938467536, -0.0268269255026565,
                0.04681369496626913, 0.998873699986078, -0.007733656297477665,
                0.02716023466959338, 0.006466453768891886, 0.999610177333318
        };
        float  rect_r_arr[] = {
                0.9964467320530139, 0.0003576923945795528, -0.08422459401516917,
                0.0002410672480103206, 0.9999747741026808, 0.007098805876643879,
                0.08422500856308147, -0.007093885708354536, 0.9964215095621459
        };
        float  proj_mat_l_arr[] = {
                606.6878356933594, 0, 346.6963882446289, 0,
                0, 606.6878356933594, 247.1882648468018, 0,
                0, 0, 1, 0
        };
        float  proj_mat_r_arr[] = {
                606.6878356933594, 0, 346.6963882446289, -2245.23721291054,
                0, 606.6878356933594, 247.1882648468018, 0,
                0, 0, 1, 0
        };

        // mtxL = Mat(3, 3, CV_32FC1, cameraMatrixL);
        Mat new_mtxL = Mat(3, 3, CV_32FC1, new_mtxL_arr);
        Mat new_mtxR = Mat (3, 3, CV_32FC1, new_mtxR_arr);
        Mat distL = Mat (5, 1, CV_32FC1, distL_arr);
        Mat distR = Mat (5, 1, CV_32FC1, distR_arr);
        Mat rect_l = Mat (3, 3, CV_32FC1, rect_l_arr);
        Mat rect_r = Mat (3, 3, CV_32FC1, rect_r_arr);
        Mat proj_mat_l = Mat (3, 4, CV_32FC1, proj_mat_l_arr);
        Mat proj_mat_r = Mat (3, 4, CV_32FC1, proj_mat_r_arr);
        //init the mutex
        pthread_mutex_init(&mutex, NULL);

        // Check that matrices are properly initialized
        __android_log_print(ANDROID_LOG_INFO, "StereoGlassesInfo", "new_mtxL: %s", new_mtxL.empty() ? "empty" : "initialized");
        __android_log_print(ANDROID_LOG_INFO, "StereoGlassesInfo", "new_mtxR: %s", new_mtxR.empty() ? "empty" : "initialized");
        __android_log_print(ANDROID_LOG_INFO, "StereoGlassesInfo", "distL: %s", distL.empty() ? "empty" : "initialized");
        __android_log_print(ANDROID_LOG_INFO, "StereoGlassesInfo", "distR: %s", distR.empty() ? "empty" : "initialized");

        __android_log_print(ANDROID_LOG_INFO, "StereoGlassesInfo", "Rectifying StereoGlasses");
        cv::initUndistortRectifyMap(new_mtxL, distL, rect_l, proj_mat_l, Size(640, 480), CV_16SC2, Left_Stereo_Map1, Left_Stereo_Map2);
        cv::initUndistortRectifyMap(new_mtxR, distR, rect_r, proj_mat_r, Size(640, 480), CV_16SC2, Right_Stereo_Map1, Right_Stereo_Map2);


        __android_log_print(ANDROID_LOG_INFO, "StereoGlassesInfo", "Left Stereo Map 1: %d", Left_Stereo_Map1.rows);
        __android_log_print(ANDROID_LOG_INFO, "StereoGlassesInfo", "Left Stereo Map 2: %d", Left_Stereo_Map2.rows);
        __android_log_print(ANDROID_LOG_INFO, "StereoGlassesInfo", "Finished Init StereoGlasses");
    }

    void StereoGlasses::run() {
        Mat grayL, grayR, rectFrameL, rectFrameR;
        __android_log_print(ANDROID_LOG_INFO, "StereoGlassesInfo", "Running StereoGlasses");
        while(true) {
            if (!frameLeft.empty() && !frameRight.empty()) {
                cvtColor(frameLeft, grayL, COLOR_BGR2GRAY);
                cvtColor(frameRight, grayR, COLOR_BGR2GRAY);

                __android_log_print(ANDROID_LOG_INFO, "StereoGlassesInfo", "Remapping StereoGlasses");
                cv::remap(grayL, rectFrameL, Left_Stereo_Map1, Left_Stereo_Map2, INTER_LINEAR);
                cv::remap(grayR, rectFrameR, Right_Stereo_Map1, Right_Stereo_Map2, INTER_LINEAR);
                __android_log_print(ANDROID_LOG_INFO, "StereoGlassesInfo", "Generating Depth Map");
                getDepthMap(rectFrameL, rectFrameR);

                // empty the frames
                frameLeft.release();
                frameRight.release();
            }
        }

    }

    void StereoGlasses::getDepthMap(Mat left, Mat right) {
        leftMatcher->compute(left, right, left_disp);
        rightMatcher->compute(left, right, right_disp);

        left_disp = left_disp / numDisparities;
        right_disp = right_disp / numDisparities;
        left_disp.convertTo(left_disp, CV_32F, 1/16.0);
        right_disp.convertTo(right_disp, CV_32F, 1/16.0);


        pthread_mutex_lock(&mutex);
//        wlsFilter->filter(left_disp, left, filteredDisparity, right_disp, Rect(), right);
        left_disp.copyTo(filteredDisparity);
        filteredDisparity.convertTo(filteredDisparity, CV_8UC1, 255.0);
        pthread_mutex_unlock(&mutex);

        __android_log_print(ANDROID_LOG_INFO, "StereoGlassesInfo", "filteredDisparity: %d", filteredDisparity.rows);
        __android_log_print(ANDROID_LOG_INFO, "StereoGlassesInfo", "Finished Depth Map");
    }
} // StereoGlasses