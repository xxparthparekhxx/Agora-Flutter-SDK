#!/usr/bin/env bash

set -e

AGORA_FLUTTER_PROJECT_PATH=$(pwd)
IRIS_PROJECT_PATH=$1
BUILD_TYPE=$2
ABIS="arm64-v8a armeabi-v7a x86_64"

for ABI in ${ABIS};
do
    echo "Copying $IRIS_PROJECT_PATH/build/android/$ABI/output/rtc/$BUILD_TYPE/libAgoraRtcWrapper.so to $AGORA_FLUTTER_PROJECT_PATH/android/libs/$ABI/libAgoraRtcWrapper.so"
    bash $IRIS_PROJECT_PATH/rtc/ci/build-android.sh build $ABI Debug
    mkdir -p "$AGORA_FLUTTER_PROJECT_PATH/android/libs/$ABI/" && \
    cp -r "$IRIS_PROJECT_PATH/build/android/$ABI/output/rtc/$BUILD_TYPE/libAgoraRtcWrapper.so" \
          "$AGORA_FLUTTER_PROJECT_PATH/android/libs/$ABI/libAgoraRtcWrapper.so" 
done;

echo "Copying $IRIS_PROJECT_PATH/build/android/arm64-v8a/output/rtc/AgoraRtcWrapper.jar to $AGORA_FLUTTER_PROJECT_PATH/android/libs/AgoraRtcWrapper.jar"
cp -r "$IRIS_PROJECT_PATH/build/android/arm64-v8a/output/rtc/AgoraRtcWrapper.jar" "$AGORA_FLUTTER_PROJECT_PATH/android/libs/AgoraRtcWrapper.jar"

echo "Copying "$IRIS_PROJECT_PATH/third_party/agora/rtc/include/"" to "$AGORA_FLUTTER_PROJECT_PATH/integration_test_app/iris_integration_test/third_party/agora/rtc/include/"
cp -r "$IRIS_PROJECT_PATH/third_party/agora/rtc/include/" "$AGORA_FLUTTER_PROJECT_PATH/integration_test_app/iris_integration_test/third_party/agora/rtc/include/"