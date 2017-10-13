//
// Created by Administrator on 2017/3/4 0004.
//
#include "programs_studyprogram_core_businessdeal_JniBusi.h"
#include <photodeal.h>
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, "result_str.cpp", __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG , "result_str.cpp", __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO  , "result_str.cpp", __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN  , "result_str.cpp", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , "result_str.cpp", __VA_ARGS__)

jint Java_programs_studyprogram_core_businessdeal_JniBusi_versionCode(JNIEnv* env,jobject /* this */){
    return getphotosize();
}
