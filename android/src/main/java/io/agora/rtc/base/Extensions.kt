package io.agora.rtc.base

import android.graphics.Rect
import io.agora.rtc2.IRtcEngineEventHandler.*
import io.agora.rtc2.UserInfo

fun UserInfo.toMap(): Map<String, Any?> {
  return hashMapOf(
    "uid" to uid.toUInt().toLong(),
    "userAccount" to userAccount
  )
}

fun LocalAudioStats.toMap(): Map<String, Any?> {
  return hashMapOf(
    "numChannels" to numChannels,
    "sentSampleRate" to sentSampleRate,
    "sentBitrate" to sentBitrate,
//    "txPacketLossRate" to txPacketLossRate
    "internalCodec" to internalCodec
  )
}

fun RtcStats.toMap(): Map<String, Any?> {
  return hashMapOf(
    "connectionId" to connectionId,
    "duration" to totalDuration,
    "txBytes" to txBytes,
    "rxBytes" to rxBytes,
    "txKBitRate" to txKBitRate,
    "txAudioBytes" to txAudioBytes,
    "rxAudioBytes" to rxAudioBytes,
    "txVideoBytes" to txVideoBytes,
    "rxVideoBytes" to rxVideoBytes,
    "rxKBitRate" to rxKBitRate,
    "txAudioKBitRate" to txAudioKBitRate,
    "rxAudioKBitRate" to rxAudioKBitRate,
    "txVideoKBitRate" to txVideoKBitRate,
    "rxVideoKBitRate" to rxVideoKBitRate,
    "lastmileDelay" to lastmileDelay,
    "cpuTotalUsage" to cpuTotalUsage,
    "cpuAppUsage" to cpuAppUsage,
    "userCount" to users,
    "connectTimeMs" to connectTimeMs,
    "txPacketLossRate" to txPacketLossRate,
    "rxPacketLossRate" to rxPacketLossRate,
    "memoryAppUsageRatio" to memoryAppUsageRatio,
    "memoryTotalUsageRatio" to memoryTotalUsageRatio,
    "memoryAppUsageInKbytes" to memoryAppUsageInKbytes
  )
}

fun Rect.toMap(): Map<String, Any?> {
  return hashMapOf(
    "left" to left,
    "top" to top,
    "right" to right,
    "bottom" to bottom
  )
}

fun RemoteAudioStats.toMap(): Map<String, Any?> {
  return hashMapOf(
    "uid" to uid.toUInt().toLong(),
    "quality" to quality,
    "networkTransportDelay" to networkTransportDelay,
    "jitterBufferDelay" to jitterBufferDelay,
    "audioLossRate" to audioLossRate,
    "numChannels" to numChannels,
    "receivedSampleRate" to receivedSampleRate,
    "receivedBitrate" to receivedBitrate,
    "totalFrozenTime" to totalFrozenTime,
    "frozenRate" to frozenRate,
//    "totalActiveTime" to totalActiveTime,
//    "publishDuration" to publishDuration,
//    "qoeQuality" to qoeQuality,
//    "qualityChangedReason" to qualityChangedReason,
    "mosValue" to mosValue
  )
}

fun LocalVideoStats.toMap(): Map<String, Any?> {
  return hashMapOf(
    "uid" to uid.toUInt().toLong(),
    "sentBitrate" to sentBitrate,
    "sentFrameRate" to sentFrameRate,
    "encoderOutputFrameRate" to encoderOutputFrameRate,
    "rendererOutputFrameRate" to rendererOutputFrameRate,
    "targetBitrate" to targetBitrate,
    "targetFrameRate" to targetFrameRate,
//    "qualityAdaptIndication" to qualityAdaptIndication,
    "encodedBitrate" to encodedBitrate,
    "encodedFrameWidth" to encodedFrameWidth,
    "encodedFrameHeight" to encodedFrameHeight,
    "encodedFrameCount" to encodedFrameCount,
    "codecType" to codecType
//    "txPacketLossRate" to txPacketLossRate,
//    "captureFrameRate" to captureFrameRate,
//    "captureBrightnessLevel" to captureBrightnessLevel
  )
}

fun RemoteVideoStats.toMap(): Map<String, Any?> {
  return hashMapOf(
    "uid" to uid.toUInt().toLong(),
    "delay" to delay,
    "width" to width,
    "height" to height,
    "receivedBitrate" to receivedBitrate,
    "decoderOutputFrameRate" to decoderOutputFrameRate,
    "rendererOutputFrameRate" to rendererOutputFrameRate,
    "frameLossRate" to frameLossRate,
    "packetLossRate" to packetLossRate,
    "rxStreamType" to rxStreamType,
    "totalFrozenTime" to totalFrozenTime,
    "frozenRate" to frozenRate,
    "avSyncTimeMs" to avSyncTimeMs
//    "totalActiveTime" to totalActiveTime,
//    "publishDuration" to publishDuration
  )
}

fun AudioVolumeInfo.toMap(): Map<String, Any?> {
  return hashMapOf(
    "uid" to uid.toUInt().toLong(),
    "userId" to userId,
    "volume" to volume
//    "vad" to vad,
//    "channelId" to channelId
  )
}

fun Array<out AudioVolumeInfo>.toMapList(): List<Map<String, Any?>> {
  return List(size) { this[it].toMap() }
}

fun LastmileProbeResult.LastmileProbeOneWayResult.toMap(): Map<String, Any?> {
  return hashMapOf(
    "packetLossRate" to packetLossRate,
    "jitter" to jitter,
    "availableBandwidth" to availableBandwidth
  )
}

fun LastmileProbeResult.toMap(): Map<String, Any?> {
  return hashMapOf(
    "state" to state,
    "rtt" to rtt,
    "uplinkReport" to uplinkReport.toMap(),
    "downlinkReport" to downlinkReport.toMap()
  )
}

fun AgoraFacePositionInfo.toMap(): Map<String, Any?> {
  return hashMapOf(
    "x" to x,
    "y" to y,
    "width" to width,
    "height" to height,
    "distance" to distance
  )
}

fun Array<out AgoraFacePositionInfo>.toMapList(): List<Map<String, Any?>> {
  return List(size) { this[it].toMap() }
}

@ExperimentalUnsignedTypes
internal fun Number.toNativeUInt(): Int {
  return toLong().toUInt().toInt()
}
