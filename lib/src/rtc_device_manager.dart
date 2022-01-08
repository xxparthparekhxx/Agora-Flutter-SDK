import 'dart:async';

import 'package:agora_rtc_engine/rtc_engine.dart';

import 'rtc_device_manager_impl.dart';

/* class-RtcDeviceManager */
abstract class RtcDeviceManager {
  /// @nodoc
  factory RtcDeviceManager() {
    return RtcDeviceManagerImpl();
  }

  /* api-audio-enumerateAudioPlaybackDevices */
  Future<List<MediaDeviceInfo>> enumerateAudioPlaybackDevices();

  /* api-audio-setAudioPlaybackDevice */
  Future<void> setAudioPlaybackDevice(String deviceId);

  /* api-audio-getAudioPlaybackDevice */
  Future<String?> getAudioPlaybackDevice();

  /* api-audio-getAudioPlaybackDeviceInfo */
  Future<MediaDeviceInfo?> getAudioPlaybackDeviceInfo();

  /* api-audio-setAudioPlaybackDeviceVolume */
  Future<void> setAudioPlaybackDeviceVolume(int volume);

  /* api-audio-getAudioPlaybackDeviceVolume */
  Future<int?> getAudioPlaybackDeviceVolume();

  /* api-audio-setAudioPlaybackDeviceMute */
  Future<void> setAudioPlaybackDeviceMute(bool mute);

  /* api-audio-getAudioPlaybackDeviceMute */
  Future<bool?> getAudioPlaybackDeviceMute();

  /* api-audio-startAudioPlaybackDeviceTest */
  Future<void> startAudioPlaybackDeviceTest(String testAudioFilePath);

  /* api-audio-stopAudioPlaybackDeviceTest */
  Future<void> stopAudioPlaybackDeviceTest();

  /* api-audio-enumerateAudioRecordingDevices */
  Future<List<MediaDeviceInfo>> enumerateAudioRecordingDevices();

  /* api-audio-setAudioRecordingDevice */
  Future<void> setAudioRecordingDevice(String deviceId);

  /* api-audio-getAudioRecordingDevice */
  Future<String?> getAudioRecordingDevice();

  /* api-audio-getAudioRecordingDeviceInfo */
  Future<MediaDeviceInfo?> getAudioRecordingDeviceInfo();

  /* api-audio-setAudioRecordingDeviceVolume */
  Future<void> setAudioRecordingDeviceVolume(int volume);

  /* api-audio-getAudioRecordingDeviceVolume */
  Future<int?> getAudioRecordingDeviceVolume();

  /* api-audio-setAudioRecordingDeviceMute */
  Future<void> setAudioRecordingDeviceMute(bool mute);

  /* api-audio-getAudioRecordingDeviceMute */
  Future<bool?> getAudioRecordingDeviceMute();

  /* api-audio-startAudioRecordingDeviceTest */
  Future<void> startAudioRecordingDeviceTest(int indicationInterval);

  /* api-audio-stopAudioRecordingDeviceTest */
  Future<void> stopAudioRecordingDeviceTest();

  /* api-audio-startAudioDeviceLoopbackTest */
  Future<void> startAudioDeviceLoopbackTest(int indicationInterval);

  /* api-audio-stopAudioDeviceLoopbackTest */
  Future<void> stopAudioDeviceLoopbackTest();

  /* api-video-enumerateVideoDevices */
  Future<List<MediaDeviceInfo>> enumerateVideoDevices();

  ///
  /// Specifies the video capture device with the device ID.
  /// Plugging or unplugging a device does not change its device ID.
  ///
  /// Param [deviceId] The device ID. You can get the device ID by calling enumerateVideoDevices.
  ///
  ///
  Future<void> setVideoDevice(String deviceId);

  ///
  /// Retrieves the current video capture device.
  ///
  ///
  /// **return** 0: Success.
  /// < 0: Failure.
  ///
  /// The video capture device. See .
  /// The video capture device.
  ///
  Future<String?> getVideoDevice();
}
