LOCAL_PATH := $(call my-dir)
EXT_LIB_ROOT := $(LOCAL_PATH)/../../../extmodules/distribution
EXT_GENLIBS_ROOT := $(LOCAL_PATH)/../../../extmodules/genlibs

include $(CLEAR_VARS)
LOCAL_MODULE := local_photodeal
LOCAL_SRC_FILES := $(EXT_LIB_ROOT)/libpicturedeal/lib/$(TARGET_ARCH_ABI)/libphotodeal.so
LOCAL_EXPORT_C_INCLUDES := $(EXT_LIB_ROOT)/libpicturedeal/include
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_CFLAGS := -std=c++11
LOCAL_MODULE := jnibusi
LOCAL_SRC_FILES := jnibusi.cpp
LOCAL_LDLIBS    := -llog -landroid
LOCAL_SHARED_LIBRARIES := local_photodeal
include $(BUILD_SHARED_LIBRARY)
