package io.agora.rtc.base

import android.graphics.Rect
import io.agora.rtc2.IRtcEngineEventHandlerEx
import io.agora.rtc2.RtcConnection
import io.agora.rtc2.UserInfo

class RtcChannelEvents {
  companion object {
    const val Warning = "Warning"
    const val Error = "Error"
    const val JoinChannelSuccess = "JoinChannelSuccess"
    const val RejoinChannelSuccess = "RejoinChannelSuccess"
    const val LeaveChannel = "LeaveChannel"
    const val ClientRoleChanged = "ClientRoleChanged"
    const val UserJoined = "UserJoined"
    const val UserOffline = "UserOffline"
    const val ConnectionStateChanged = "ConnectionStateChanged"
    const val ConnectionLost = "ConnectionLost"
    const val TokenPrivilegeWillExpire = "TokenPrivilegeWillExpire"
    const val RequestToken = "RequestToken"
    const val ActiveSpeaker = "ActiveSpeaker"
    const val VideoSizeChanged = "VideoSizeChanged"
    const val RemoteVideoStateChanged = "RemoteVideoStateChanged"
    const val RemoteAudioStateChanged = "RemoteAudioStateChanged"
    const val LocalPublishFallbackToAudioOnly = "LocalPublishFallbackToAudioOnly"
    const val RemoteSubscribeFallbackToAudioOnly = "RemoteSubscribeFallbackToAudioOnly"
    const val RtcStats = "RtcStats"
    const val NetworkQuality = "NetworkQuality"
    const val RemoteVideoStats = "RemoteVideoStats"
    const val RemoteAudioStats = "RemoteAudioStats"
    const val RtmpStreamingStateChanged = "RtmpStreamingStateChanged"
    const val TranscodingUpdated = "TranscodingUpdated"
    const val StreamInjectedStatus = "StreamInjectedStatus"
    const val StreamMessage = "StreamMessage"
    const val StreamMessageError = "StreamMessageError"
    const val ChannelMediaRelayStateChanged = "ChannelMediaRelayStateChanged"
    const val ChannelMediaRelayEvent = "ChannelMediaRelayEvent"
    const val MetadataReceived = "MetadataReceived"
    const val AudioPublishStateChanged = "AudioPublishStateChanged"
    const val VideoPublishStateChanged = "VideoPublishStateChanged"
    const val AudioSubscribeStateChanged = "AudioSubscribeStateChanged"
    const val VideoSubscribeStateChanged = "VideoSubscribeStateChanged"
    const val RtmpStreamingEvent = "RtmpStreamingEvent"
    const val UserSuperResolutionEnabled = "UserSuperResolutionEnabled"

    fun toMap(): Map<String, String> {
      return hashMapOf(
        "Warning" to Warning,
        "Error" to Error,
        "JoinChannelSuccess" to JoinChannelSuccess,
        "RejoinChannelSuccess" to RejoinChannelSuccess,
        "LeaveChannel" to LeaveChannel,
        "ClientRoleChanged" to ClientRoleChanged,
        "UserJoined" to UserJoined,
        "UserOffline" to UserOffline,
        "ConnectionStateChanged" to ConnectionStateChanged,
        "ConnectionLost" to ConnectionLost,
        "TokenPrivilegeWillExpire" to TokenPrivilegeWillExpire,
        "RequestToken" to RequestToken,
        "ActiveSpeaker" to ActiveSpeaker,
        "VideoSizeChanged" to VideoSizeChanged,
        "RemoteVideoStateChanged" to RemoteVideoStateChanged,
        "RemoteAudioStateChanged" to RemoteAudioStateChanged,
        "LocalPublishFallbackToAudioOnly" to LocalPublishFallbackToAudioOnly,
        "RemoteSubscribeFallbackToAudioOnly" to RemoteSubscribeFallbackToAudioOnly,
        "RtcStats" to RtcStats,
        "NetworkQuality" to NetworkQuality,
        "RemoteVideoStats" to RemoteVideoStats,
        "RemoteAudioStats" to RemoteAudioStats,
        "RtmpStreamingStateChanged" to RtmpStreamingStateChanged,
        "TranscodingUpdated" to TranscodingUpdated,
        "StreamInjectedStatus" to StreamInjectedStatus,
        "StreamMessage" to StreamMessage,
        "StreamMessageError" to StreamMessageError,
        "ChannelMediaRelayStateChanged" to ChannelMediaRelayStateChanged,
        "ChannelMediaRelayEvent" to ChannelMediaRelayEvent,
        "MetadataReceived" to MetadataReceived,
        "AudioPublishStateChanged" to AudioPublishStateChanged,
        "VideoPublishStateChanged" to VideoPublishStateChanged,
        "AudioSubscribeStateChanged" to AudioSubscribeStateChanged,
        "VideoSubscribeStateChanged" to VideoSubscribeStateChanged,
        "RtmpStreamingEvent" to RtmpStreamingEvent,
        "UserSuperResolutionEnabled" to UserSuperResolutionEnabled
      )
    }
  }
}

class RtcChannelEventHandler(
  private val emitter: (methodName: String, data: Map<String, Any?>?) -> Unit
) : IRtcEngineEventHandlerEx() {
  companion object {
    const val PREFIX = "io.agora.rtc."
  }

  private fun callback(methodName: String, channel: RtcConnection?, vararg data: Any?) {
    channel?.let {
      emitter(
        methodName, hashMapOf(
//          "channelId" to it.channelId(),
          "data" to data.toList()
        )
      )
    }
  }

  override fun onWarning(warn: Int) {
    super.onWarning(warn)
  }

  override fun onError(err: Int) {
    super.onError(err)
  }

  override fun onApiCallExecuted(error: Int, api: String?, result: String?) {
    super.onApiCallExecuted(error, api, result)
  }

  override fun onCameraReady() {
    super.onCameraReady()
  }

  override fun onCameraFocusAreaChanged(rect: Rect?) {
    super.onCameraFocusAreaChanged(rect)
  }

  override fun onCameraExposureAreaChanged(rect: Rect?) {
    super.onCameraExposureAreaChanged(rect)
  }

  override fun onFacePositionChanged(
    imageWidth: Int,
    imageHeight: Int,
    faceRectArr: Array<out AgoraFacePositionInfo>?
  ) {
    super.onFacePositionChanged(imageWidth, imageHeight, faceRectArr)
  }

  override fun onVideoStopped() {
    super.onVideoStopped()
  }

  override fun onLeaveChannel(stats: RtcStats?) {
    super.onLeaveChannel(stats)
  }

  override fun onRtcStats(stats: RtcStats?) {
    super.onRtcStats(stats)
  }

  override fun onAudioVolumeIndication(speakers: Array<out AudioVolumeInfo>?, totalVolume: Int) {
    super.onAudioVolumeIndication(speakers, totalVolume)
  }

  override fun onLastmileQuality(quality: Int) {
    super.onLastmileQuality(quality)
  }

  override fun onLastmileProbeResult(result: LastmileProbeResult?) {
    super.onLastmileProbeResult(result)
  }

  override fun onLocalVideoStat(sentBitrate: Int, sentFrameRate: Int) {
    super.onLocalVideoStat(sentBitrate, sentFrameRate)
  }

  override fun onRemoteVideoStats(stats: RemoteVideoStats?) {
    super.onRemoteVideoStats(stats)
  }

  override fun onRemoteAudioStats(stats: RemoteAudioStats?) {
    super.onRemoteAudioStats(stats)
  }

  override fun onLocalVideoStats(stats: LocalVideoStats?) {
    super.onLocalVideoStats(stats)
  }

  override fun onLocalAudioStats(stats: LocalAudioStats?) {
    super.onLocalAudioStats(stats)
  }

  override fun onFirstLocalVideoFrame(width: Int, height: Int, elapsed: Int) {
    super.onFirstLocalVideoFrame(width, height, elapsed)
  }

  override fun onConnectionLost() {
    super.onConnectionLost()
  }

  override fun onConnectionInterrupted() {
    super.onConnectionInterrupted()
  }

  override fun onConnectionStateChanged(state: Int, reason: Int) {
    super.onConnectionStateChanged(state, reason)
  }

  override fun onNetworkTypeChanged(type: Int) {
    super.onNetworkTypeChanged(type)
  }

  override fun onConnectionBanned() {
    super.onConnectionBanned()
  }

  override fun onRefreshRecordingServiceStatus(status: Int) {
    super.onRefreshRecordingServiceStatus(status)
  }

  override fun onMediaEngineLoadSuccess() {
    super.onMediaEngineLoadSuccess()
  }

  override fun onMediaEngineStartCallSuccess() {
    super.onMediaEngineStartCallSuccess()
  }

  override fun onAudioMixingFinished() {
    super.onAudioMixingFinished()
  }

  override fun onRequestToken() {
    super.onRequestToken()
  }

  override fun onAudioRouteChanged(routing: Int) {
    super.onAudioRouteChanged(routing)
  }

  override fun onAudioMixingStateChanged(state: Int, errorCode: Int) {
    super.onAudioMixingStateChanged(state, errorCode)
  }

  override fun onFirstLocalAudioFramePublished(elapsed: Int) {
    super.onFirstLocalAudioFramePublished(elapsed)
  }

  override fun onAudioEffectFinished(soundId: Int) {
    super.onAudioEffectFinished(soundId)
  }

  override fun onClientRoleChanged(oldRole: Int, newRole: Int) {
    super.onClientRoleChanged(oldRole, newRole)
  }

  override fun onRtmpStreamingStateChanged(
    url: String?,
    state: RTMP_STREAM_PUBLISH_STATE?,
    errCode: RTMP_STREAM_PUBLISH_ERROR?
  ) {
    super.onRtmpStreamingStateChanged(url, state, errCode)
  }

  override fun onStreamPublished(url: String?, error: Int) {
    super.onStreamPublished(url, error)
  }

  override fun onStreamUnpublished(url: String?) {
    super.onStreamUnpublished(url)
  }

  override fun onTranscodingUpdated() {
    super.onTranscodingUpdated()
  }

  override fun onTokenPrivilegeWillExpire(token: String?) {
    super.onTokenPrivilegeWillExpire(token)
  }

  override fun onLocalPublishFallbackToAudioOnly(isFallbackOrRecover: Boolean) {
    super.onLocalPublishFallbackToAudioOnly(isFallbackOrRecover)
  }

  override fun onChannelMediaRelayStateChanged(state: Int, code: Int) {
    super.onChannelMediaRelayStateChanged(state, code)
  }

  override fun onChannelMediaRelayEvent(code: Int) {
    super.onChannelMediaRelayEvent(code)
  }

  override fun onIntraRequestReceived() {
    super.onIntraRequestReceived()
  }

  override fun onUplinkNetworkInfoUpdated(info: UplinkNetworkInfo?) {
    super.onUplinkNetworkInfoUpdated(info)
  }

  override fun onDownlinkNetworkInfoUpdated(info: DownlinkNetworkInfo?) {
    super.onDownlinkNetworkInfoUpdated(info)
  }

  override fun onEncryptionError(errorType: ENCRYPTION_ERROR_TYPE?) {
    super.onEncryptionError(errorType)
  }

  override fun onPermissionError(permission: PERMISSION?) {
    super.onPermissionError(permission)
  }

  override fun onLocalUserRegistered(uid: Int, userAccount: String?) {
    super.onLocalUserRegistered(uid, userAccount)
  }

  override fun onUserInfoUpdated(uid: Int, userInfo: UserInfo?) {
    super.onUserInfoUpdated(uid, userInfo)
  }

  override fun onFirstLocalVideoFramePublished(elapsed: Int) {
    super.onFirstLocalVideoFramePublished(elapsed)
  }

  override fun onAudioSubscribeStateChanged(
    channel: String?,
    uid: Int,
    oldState: STREAM_SUBSCRIBE_STATE?,
    newState: STREAM_SUBSCRIBE_STATE?,
    elapseSinceLastState: Int
  ) {
    super.onAudioSubscribeStateChanged(channel, uid, oldState, newState, elapseSinceLastState)
  }

  override fun onVideoSubscribeStateChanged(
    channel: String?,
    uid: Int,
    oldState: STREAM_SUBSCRIBE_STATE?,
    newState: STREAM_SUBSCRIBE_STATE?,
    elapseSinceLastState: Int
  ) {
    super.onVideoSubscribeStateChanged(channel, uid, oldState, newState, elapseSinceLastState)
  }

  override fun onAudioPublishStateChanged(
    channel: String?,
    oldState: STREAM_PUBLISH_STATE?,
    newState: STREAM_PUBLISH_STATE?,
    elapseSinceLastState: Int
  ) {
    super.onAudioPublishStateChanged(channel, oldState, newState, elapseSinceLastState)
  }

  override fun onVideoPublishStateChanged(
    channel: String?,
    oldState: STREAM_PUBLISH_STATE?,
    newState: STREAM_PUBLISH_STATE?,
    elapseSinceLastState: Int
  ) {
    super.onVideoPublishStateChanged(channel, oldState, newState, elapseSinceLastState)
  }

  override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
    super.onJoinChannelSuccess(channel, uid, elapsed)
  }

  override fun onRejoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
    super.onRejoinChannelSuccess(channel, uid, elapsed)
  }

  override fun onUserJoined(uid: Int, elapsed: Int) {
    super.onUserJoined(uid, elapsed)
  }

  override fun onUserOffline(uid: Int, reason: Int) {
    super.onUserOffline(uid, reason)
  }

  override fun onActiveSpeaker(uid: Int) {
    super.onActiveSpeaker(uid)
  }

  override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
    super.onFirstRemoteVideoDecoded(uid, width, height, elapsed)
  }

  override fun onFirstRemoteVideoFrame(uid: Int, width: Int, height: Int, elapsed: Int) {
    super.onFirstRemoteVideoFrame(uid, width, height, elapsed)
  }

  override fun onUserMuteVideo(uid: Int, muted: Boolean) {
    super.onUserMuteVideo(uid, muted)
  }

  override fun onUserEnableVideo(uid: Int, enabled: Boolean) {
    super.onUserEnableVideo(uid, enabled)
  }

  override fun onUserEnableLocalVideo(uid: Int, enabled: Boolean) {
    super.onUserEnableLocalVideo(uid, enabled)
  }

  override fun onVideoSizeChanged(uid: Int, width: Int, height: Int, rotation: Int) {
    super.onVideoSizeChanged(uid, width, height, rotation)
  }

  override fun onRemoteAudioStateChanged(
    uid: Int,
    state: REMOTE_AUDIO_STATE?,
    reason: REMOTE_AUDIO_STATE_REASON?,
    elapsed: Int
  ) {
    super.onRemoteAudioStateChanged(uid, state, reason, elapsed)
  }

  override fun onRemoteVideoStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {
    super.onRemoteVideoStateChanged(uid, state, reason, elapsed)
  }

  override fun onRemoteSubscribeFallbackToAudioOnly(uid: Int, isFallbackOrRecover: Boolean) {
    super.onRemoteSubscribeFallbackToAudioOnly(uid, isFallbackOrRecover)
  }

  override fun onAudioQuality(uid: Int, quality: Int, delay: Short, lost: Short) {
    super.onAudioQuality(uid, quality, delay, lost)
  }

  override fun onNetworkQuality(uid: Int, txQuality: Int, rxQuality: Int) {
    super.onNetworkQuality(uid, txQuality, rxQuality)
  }

  override fun onRemoteVideoStat(
    uid: Int,
    delay: Int,
    receivedBitrate: Int,
    receivedFrameRate: Int
  ) {
    super.onRemoteVideoStat(uid, delay, receivedBitrate, receivedFrameRate)
  }

  override fun onRemoteAudioTransportStats(uid: Int, delay: Int, lost: Int, rxKBitRate: Int) {
    super.onRemoteAudioTransportStats(uid, delay, lost, rxKBitRate)
  }

  override fun onRemoteVideoTransportStats(uid: Int, delay: Int, lost: Int, rxKBitRate: Int) {
    super.onRemoteVideoTransportStats(uid, delay, lost, rxKBitRate)
  }

  override fun onLocalAudioStateChanged(
    state: LOCAL_AUDIO_STREAM_STATE?,
    error: LOCAL_AUDIO_STREAM_ERROR?
  ) {
    super.onLocalAudioStateChanged(state, error)
  }

  override fun onLocalVideoStateChanged(state: Int, error: Int) {
    super.onLocalVideoStateChanged(state, error)
  }

  override fun onStreamInjectedStatus(url: String?, uid: Int, status: Int) {
    super.onStreamInjectedStatus(url, uid, status)
  }

  override fun onStreamMessage(uid: Int, streamId: Int, data: ByteArray?) {
    super.onStreamMessage(uid, streamId, data)
  }

  override fun onStreamMessageError(uid: Int, streamId: Int, error: Int, missed: Int, cached: Int) {
    super.onStreamMessageError(uid, streamId, error, missed, cached)
  }

  override fun onAudioTransportQuality(p0: Int, p1: Int, p2: Short, p3: Short) {
    TODO("Not yet implemented")
  }

  override fun onVideoTransportQuality(p0: Int, p1: Int, p2: Short, p3: Short) {
    TODO("Not yet implemented")
  }

  override fun onRecap(p0: ByteArray?) {
    TODO("Not yet implemented")
  }

  //  override fun onWarning(warn: Int) {
//    callback(RtcChannelEvents.Warning, rtcChannel, warn)
//  }
//
//  override fun onError(err: Int) {
//    callback(RtcChannelEvents.Error, rtcChannel, err)
//  }
//
//  override fun onJoinChannelSuccess(uid: Int, elapsed: Int) {
//    callback(
//      RtcChannelEvents.JoinChannelSuccess,
//      rtcChannel,
//      rtcChannel?.channelId(),
//      uid.toUInt().toLong(),
//      elapsed
//    )
//  }
//
//  override fun onRejoinChannelSuccess(rtcChannel: RtcConnection?, uid: Int, elapsed: Int) {
//    callback(
//      RtcChannelEvents.RejoinChannelSuccess,
//      rtcChannel,
//      rtcChannel?.channelId(),
//      uid.toUInt().toLong(),
//      elapsed
//    )
//  }
//
//  override fun onLeaveChannel(rtcChannel: RtcConnection?, stats: IRtcEngineEventHandler.RtcStats?) {
//    callback(RtcChannelEvents.LeaveChannel, rtcChannel, stats?.toMap())
//  }
//
//  override fun onClientRoleChanged(
//    rtcChannel: RtcConnection?,
//    oldRole: Int,
//    newRole: Int
//  ) {
//    callback(RtcChannelEvents.ClientRoleChanged, rtcChannel, oldRole, newRole)
//  }
//
//  override fun onUserJoined(rtcChannel: RtcConnection?, uid: Int, elapsed: Int) {
//    callback(RtcChannelEvents.UserJoined, rtcChannel, uid.toUInt().toLong(), elapsed)
//  }
//
//  override fun onUserOffline(
//    rtcChannel: RtcConnection?,
//    uid: Int,
//    reason: Int
//  ) {
//    callback(RtcChannelEvents.UserOffline, rtcChannel, uid.toUInt().toLong(), reason)
//  }
//
//  override fun onConnectionStateChanged(
//    rtcChannel: RtcConnection?,
//    state: Int,
//    reason: Int
//  ) {
//    callback(RtcChannelEvents.ConnectionStateChanged, rtcChannel, state, reason)
//  }
//
//  override fun onConnectionLost(rtcChannel: RtcConnection?) {
//    callback(RtcChannelEvents.ConnectionLost, rtcChannel)
//  }
//
//  override fun onTokenPrivilegeWillExpire(rtcChannel: RtcConnection?, token: String?) {
//    callback(RtcChannelEvents.TokenPrivilegeWillExpire, rtcChannel, token)
//  }
//
//  override fun onRequestToken(rtcChannel: RtcConnection?) {
//    callback(RtcChannelEvents.RequestToken, rtcChannel)
//  }
//
//  override fun onActiveSpeaker(rtcChannel: RtcConnection?, uid: Int) {
//    callback(RtcChannelEvents.ActiveSpeaker, rtcChannel, uid.toUInt().toLong())
//  }
//
//  override fun onVideoSizeChanged(
//    rtcChannel: RtcConnection?,
//    uid: Int,
//    width: Int,
//    height: Int,
//    @IntRange(from = 0, to = 360) rotation: Int
//  ) {
//    callback(
//      RtcChannelEvents.VideoSizeChanged,
//      rtcChannel,
//      uid.toUInt().toLong(),
//      width,
//      height,
//      rotation
//    )
//  }
//
//  override fun onRemoteVideoStateChanged(
//    rtcChannel: RtcConnection?,
//    uid: Int,
//    state: Int,
//    reason: Int,
//    elapsed: Int
//  ) {
//    callback(
//      RtcChannelEvents.RemoteVideoStateChanged,
//      rtcChannel,
//      uid.toUInt().toLong(),
//      state,
//      reason,
//      elapsed
//    )
//  }
//
//  override fun onRemoteAudioStateChanged(
//    rtcChannel: RtcConnection?,
//    uid: Int,
//    state: Int,
//    reason: Int,
//    elapsed: Int
//  ) {
//    callback(
//      RtcChannelEvents.RemoteAudioStateChanged,
//      rtcChannel,
//      uid.toUInt().toLong(),
//      state,
//      reason,
//      elapsed
//    )
//  }
//
//  override fun onLocalPublishFallbackToAudioOnly(
//    rtcChannel: RtcConnection?,
//    isFallbackOrRecover: Boolean
//  ) {
//    callback(RtcChannelEvents.LocalPublishFallbackToAudioOnly, rtcChannel, isFallbackOrRecover)
//  }
//
//  override fun onRemoteSubscribeFallbackToAudioOnly(
//    rtcChannel: RtcConnection?,
//    uid: Int,
//    isFallbackOrRecover: Boolean
//  ) {
//    callback(
//      RtcChannelEvents.RemoteSubscribeFallbackToAudioOnly,
//      rtcChannel,
//      uid.toUInt().toLong(),
//      isFallbackOrRecover
//    )
//  }
//
//  override fun onRtcStats(rtcChannel: RtcConnection?, stats: IRtcEngineEventHandler.RtcStats?) {
//    callback(RtcChannelEvents.RtcStats, rtcChannel, stats?.toMap())
//  }
//
//  override fun onNetworkQuality(
//    rtcChannel: RtcConnection?,
//    uid: Int,
//    txQuality: Int,
//    rxQuality: Int
//  ) {
//    callback(
//      RtcChannelEvents.NetworkQuality,
//      rtcChannel,
//      uid.toUInt().toLong(),
//      txQuality,
//      rxQuality
//    )
//  }
//
//  override fun onRemoteVideoStats(
//    rtcChannel: RtcConnection?,
//    stats: IRtcEngineEventHandler.RemoteVideoStats?
//  ) {
//    callback(RtcChannelEvents.RemoteVideoStats, rtcChannel, stats?.toMap())
//  }
//
//  override fun onRemoteAudioStats(
//    rtcChannel: RtcConnection?,
//    stats: IRtcEngineEventHandler.RemoteAudioStats?
//  ) {
//    callback(RtcChannelEvents.RemoteAudioStats, rtcChannel, stats?.toMap())
//  }
//
//  override fun onRtmpStreamingStateChanged(
//    rtcChannel: RtcConnection?,
//    url: String?,
//    state: Int,
//    errCode: Int
//  ) {
//    callback(RtcChannelEvents.RtmpStreamingStateChanged, rtcChannel, url, state, errCode)
//  }
//
//  override fun onTranscodingUpdated(rtcChannel: RtcConnection?) {
//    callback(RtcChannelEvents.TranscodingUpdated, rtcChannel)
//  }
//
//  override fun onStreamInjectedStatus(
//    rtcChannel: RtcConnection?,
//    url: String?,
//    uid: Int,
//    status: Int
//  ) {
//    callback(RtcChannelEvents.StreamInjectedStatus, rtcChannel, url, uid.toUInt().toLong(), status)
//  }
//
//  override fun onStreamMessage(
//    rtcChannel: RtcConnection?,
//    uid: Int,
//    streamId: Int,
//    data: ByteArray?
//  ) {
//    callback(
//      RtcChannelEvents.StreamMessage,
//      rtcChannel,
//      uid.toUInt().toLong(),
//      streamId,
//      data?.let { String(it, Charsets.UTF_8) })
//  }
//
//  override fun onStreamMessageError(
//    rtcChannel: RtcConnection?,
//    uid: Int,
//    streamId: Int,
//    error: Int,
//    missed: Int,
//    cached: Int
//  ) {
//    callback(
//      RtcChannelEvents.StreamMessageError,
//      rtcChannel,
//      uid.toUInt().toLong(),
//      streamId,
//      error,
//      missed,
//      cached
//    )
//  }
//
//  override fun onChannelMediaRelayStateChanged(
//    rtcChannel: RtcConnection?,
//    state: Int,
//    code: Int
//  ) {
//    callback(RtcChannelEvents.ChannelMediaRelayStateChanged, rtcChannel, state, code)
//  }
//
//  override fun onChannelMediaRelayEvent(
//    rtcChannel: RtcConnection?,
//    code: Int
//  ) {
//    callback(RtcChannelEvents.ChannelMediaRelayEvent, rtcChannel, code)
//  }
//
//  override fun onAudioPublishStateChanged(
//    rtcChannel: RtcConnection?,
//    oldState: Int,
//    newState: Int,
//    elapseSinceLastState: Int
//  ) {
//    callback(
//      RtcChannelEvents.AudioPublishStateChanged,
//      rtcChannel,
//      rtcChannel?.channelId(),
//      oldState,
//      newState,
//      elapseSinceLastState
//    )
//  }
//
//  override fun onVideoPublishStateChanged(
//    rtcChannel: RtcConnection?,
//    oldState: Int,
//    newState: Int,
//    elapseSinceLastState: Int
//  ) {
//    callback(
//      RtcChannelEvents.VideoPublishStateChanged,
//      rtcChannel,
//      rtcChannel?.channelId(),
//      oldState,
//      newState,
//      elapseSinceLastState
//    )
//  }
//
//  override fun onAudioSubscribeStateChanged(
//    rtcChannel: RtcConnection?,
//    uid: Int,
//    oldState: Int,
//    newState: Int,
//    elapseSinceLastState: Int
//  ) {
//    callback(
//      RtcChannelEvents.AudioSubscribeStateChanged,
//      rtcChannel,
//      rtcChannel?.channelId(),
//      uid.toUInt().toLong(),
//      oldState,
//      newState,
//      elapseSinceLastState
//    )
//  }
//
//  override fun onVideoSubscribeStateChanged(
//    rtcChannel: RtcConnection?,
//    uid: Int,
//    oldState: Int,
//    newState: Int,
//    elapseSinceLastState: Int
//  ) {
//    callback(
//      RtcChannelEvents.VideoSubscribeStateChanged,
//      rtcChannel,
//      rtcChannel?.channelId(),
//      uid.toUInt().toLong(),
//      oldState,
//      newState,
//      elapseSinceLastState
//    )
//  }
//
//  override fun onRtmpStreamingEvent(
//    rtcChannel: RtcConnection?,
//    url: String?,
//    errCode: Int
//  ) {
//    callback(RtcChannelEvents.RtmpStreamingEvent, rtcChannel, url, errCode)
//  }
//
//  override fun onUserSuperResolutionEnabled(
//    rtcChannel: RtcConnection?,
//    uid: Int,
//    enabled: Boolean,
//    reason: Int
//  ) {
//    callback(
//      RtcChannelEvents.UserSuperResolutionEnabled,
//      rtcChannel,
//      uid.toUInt().toLong(),
//      enabled,
//      reason
//    )
//  }
}
