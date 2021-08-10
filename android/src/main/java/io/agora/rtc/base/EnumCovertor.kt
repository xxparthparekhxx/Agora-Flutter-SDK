package io.agora.rtc.base

import io.agora.rtc2.Constants
import io.agora.rtc2.internal.EncryptionConfig
import io.agora.rtc2.live.LiveInjectStreamConfig
import io.agora.rtc2.live.LiveTranscoding
import io.agora.rtc2.video.CameraCapturerConfiguration
import io.agora.rtc2.video.VideoEncoderConfiguration

fun intToFrameRate(intValue: Int): VideoEncoderConfiguration.FRAME_RATE {
  for (value in VideoEncoderConfiguration.FRAME_RATE.values()) {
    if (value.value == intValue) {
      return value
    }
  }
  throw RuntimeException("VideoEncoderConfiguration.FRAME_RATE not contains $intValue")
}

fun intToOrientationMode(intValue: Int): VideoEncoderConfiguration.ORIENTATION_MODE {
  for (value in VideoEncoderConfiguration.ORIENTATION_MODE.values()) {
    if (value.value == intValue) {
      return value
    }
  }
  throw RuntimeException("VideoEncoderConfiguration.ORIENTATION_MODE not contains $intValue")
}

fun intToDegradationPreference(intValue: Int): VideoEncoderConfiguration.DEGRADATION_PREFERENCE {
  for (value in VideoEncoderConfiguration.DEGRADATION_PREFERENCE.values()) {
    if (value.value == intValue) {
      return value
    }
  }
  throw RuntimeException("VideoEncoderConfiguration.DEGRADATION_PREFERENCE not contains $intValue")
}

fun intToMirrorMode(intValue: Int): VideoEncoderConfiguration.MIRROR_MODE_TYPE {
  for (value in VideoEncoderConfiguration.MIRROR_MODE_TYPE.values()) {
    if (value.value == intValue) {
      return value
    }
  }
  throw RuntimeException("VideoEncoderConfiguration.MIRROR_MODE_TYPE not contains $intValue")
}

fun intToLiveTranscodingAudioSampleRate(intValue: Int): LiveTranscoding.AudioSampleRateType {
  for (value in LiveTranscoding.AudioSampleRateType.values()) {
    if (LiveTranscoding.AudioSampleRateType.getValue(value) == intValue) {
      return value
    }
  }
  throw RuntimeException("LiveTranscoding.AudioSampleRateType not contains $intValue")
}

fun intToLiveInjectStreamConfigAudioSampleRate(intValue: Int): LiveInjectStreamConfig.AudioSampleRateType {
  for (value in LiveInjectStreamConfig.AudioSampleRateType.values()) {
    if (LiveInjectStreamConfig.AudioSampleRateType.getValue(value) == intValue) {
      return value
    }
  }
  throw RuntimeException("LiveInjectStreamConfig.AudioSampleRateType not contains $intValue")
}

fun intToAudioCodecProfile(intValue: Int): LiveTranscoding.AudioCodecProfileType {
  for (value in LiveTranscoding.AudioCodecProfileType.values()) {
    if (LiveTranscoding.AudioCodecProfileType.getValue(value) == intValue) {
      return value
    }
  }
  throw RuntimeException("LiveTranscoding.AudioCodecProfileType not contains $intValue")
}

fun intToVideoCodecProfile(intValue: Int): LiveTranscoding.VideoCodecProfileType {
  for (value in LiveTranscoding.VideoCodecProfileType.values()) {
    if (LiveTranscoding.VideoCodecProfileType.getValue(value) == intValue) {
      return value
    }
  }
  throw RuntimeException("LiveTranscoding.VideoCodecProfileType not contains $intValue")
}

//fun intToCapturerOutputPreference(intValue: Int): CameraCapturerConfiguration.CAPTURER_OUTPUT_PREFERENCE {
//  for (value in CameraCapturerConfiguration.CAPTURER_OUTPUT_PREFERENCE.values()) {
//    if (value.value == intValue) {
//      return value
//    }
//  }
//  throw RuntimeException("CameraCapturerConfiguration.CAPTURER_OUTPUT_PREFERENCE not contains $intValue")
//}

fun intToCameraDirection(intValue: Int): CameraCapturerConfiguration.CAMERA_DIRECTION {
  for (value in CameraCapturerConfiguration.CAMERA_DIRECTION.values()) {
    if (value.value == intValue) {
      return value
    }
  }
  throw RuntimeException("CameraCapturerConfiguration.CAMERA_DIRECTION not contains $intValue")
}

fun intToEncryptionMode(intValue: Int): EncryptionConfig.EncryptionMode {
  for (value in EncryptionConfig.EncryptionMode.values()) {
    if (value.value == intValue) {
      return value
    }
  }
  throw RuntimeException("EncryptionConfig.EncryptionMode not contains $intValue")
}

fun intToAreaCode(intValue: Int): Constants.AreaCode {
  for (value in Constants.AreaCode.values()) {
    if (Constants.AreaCode.getValue(value) == intValue) {
      return value
    }
  }
  throw RuntimeException("Constants.AreaCode not contains $intValue")
}
