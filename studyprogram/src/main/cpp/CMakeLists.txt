
#
# Copyright (C) The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

cmake_minimum_required(VERSION 3.4.1)

# configure import libs
set(distribution_DIR ${CMAKE_SOURCE_DIR}/../../../extmodules/distribution)

#add_library(lib_module1 STATIC IMPORTED)
#set_target_properties(lib_module1 PROPERTIES IMPORTED_LOCATION
    #${distribution_DIR}/module1/lib/${ANDROID_ABI}/libmodule1.a)

# shared lib will also be tucked into APK and sent to target
# refer to app/build.gradle, jniLibs section for that purpose.
# ${ANDROID_ABI} is handy for our purpose here. Probably this ${ANDROID_ABI} is
# the most valuable thing of this sample, the rest are pretty much normal cmake
add_library(lib_module2 SHARED IMPORTED)
set_target_properties(lib_module2 PROPERTIES IMPORTED_LOCATION
    ${distribution_DIR}/module2/lib/${ANDROID_ABI}/libmodule2.so)

# build application's shared lib
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

add_library(result_str SHARED
            result_str.cpp)

target_include_directories(result_str PRIVATE
                           #${distribution_DIR}/module1/include
                           ${distribution_DIR}/module2/include)

target_link_libraries(result_str
                      android
                      jnigraphics
                      #lib_module1
                      lib_module2
                      log)
