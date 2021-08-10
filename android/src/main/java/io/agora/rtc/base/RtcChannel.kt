package io.agora.rtc.base

import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.RtcConnection
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineEx
import java.util.*

class IRtcChannel {
  interface RtcChannelInterface : RtcAudioInterface, RtcVideoInterface, RtcVoicePositionInterface,
    RtcPublishStreamInterface, RtcMediaRelayInterface, RtcDualStreamInterface,
    RtcFallbackInterface, RtcMediaMetadataInterface, RtcEncryptionInterface,
    RtcInjectStreamInterface, RtcStreamMessageInterface {
    fun create(params: Map<String, *>, callback: Callback)

    fun destroy(params: Map<String, *>, callback: Callback)

    fun setClientRole(params: Map<String, *>, callback: Callback)

    fun joinChannel(params: Map<String, *>, callback: Callback)

    fun joinChannelWithUserAccount(params: Map<String, *>, callback: Callback)

    fun leaveChannel(params: Map<String, *>, callback: Callback)

    fun renewToken(params: Map<String, *>, callback: Callback)

    fun getConnectionState(params: Map<String, *>, callback: Callback)

    @Deprecated("")
    fun publish(params: Map<String, *>, callback: Callback)

    @Deprecated("")
    fun unpublish(params: Map<String, *>, callback: Callback)

    fun getCallId(params: Map<String, *>, callback: Callback)
  }

  interface RtcAudioInterface {
    fun adjustUserPlaybackSignalVolume(params: Map<String, *>, callback: Callback)

    fun muteLocalAudioStream(params: Map<String, *>, callback: Callback)

    fun muteRemoteAudioStream(params: Map<String, *>, callback: Callback)

    fun muteAllRemoteAudioStreams(params: Map<String, *>, callback: Callback)

    @Deprecated("")
    fun setDefaultMuteAllRemoteAudioStreams(params: Map<String, *>, callback: Callback)
  }

  interface RtcVideoInterface {
    fun muteLocalVideoStream(params: Map<String, *>, callback: Callback)

    fun muteRemoteVideoStream(params: Map<String, *>, callback: Callback)

    fun muteAllRemoteVideoStreams(params: Map<String, *>, callback: Callback)

    @Deprecated("")
    fun setDefaultMuteAllRemoteVideoStreams(params: Map<String, *>, callback: Callback)

    fun enableRemoteSuperResolution(params: Map<String, *>, callback: Callback)
  }

  interface RtcVoicePositionInterface {
    fun setRemoteVoicePosition(params: Map<String, *>, callback: Callback)
  }

  interface RtcPublishStreamInterface {
    fun setLiveTranscoding(params: Map<String, *>, callback: Callback)

    fun addPublishStreamUrl(params: Map<String, *>, callback: Callback)

    fun removePublishStreamUrl(params: Map<String, *>, callback: Callback)
  }

  interface RtcMediaRelayInterface {
    fun startChannelMediaRelay(params: Map<String, *>, callback: Callback)

    fun updateChannelMediaRelay(params: Map<String, *>, callback: Callback)

    fun stopChannelMediaRelay(params: Map<String, *>, callback: Callback)
  }

  interface RtcDualStreamInterface {
    fun setRemoteVideoStreamType(params: Map<String, *>, callback: Callback)

    fun setRemoteDefaultVideoStreamType(params: Map<String, *>, callback: Callback)
  }

  interface RtcFallbackInterface {
    fun setRemoteUserPriority(params: Map<String, *>, callback: Callback)
  }

  interface RtcMediaMetadataInterface {
    fun registerMediaMetadataObserver(params: Map<String, *>, callback: Callback)

    fun unregisterMediaMetadataObserver(params: Map<String, *>, callback: Callback)

    fun setMaxMetadataSize(params: Map<String, *>, callback: Callback)

    fun sendMetadata(params: Map<String, *>, callback: Callback)
  }

  interface RtcEncryptionInterface {
    @Deprecated("")
    fun setEncryptionSecret(params: Map<String, *>, callback: Callback)

    @Deprecated("")
    fun setEncryptionMode(params: Map<String, *>, callback: Callback)

    fun enableEncryption(params: Map<String, *>, callback: Callback)
  }

  interface RtcInjectStreamInterface {
    fun addInjectStreamUrl(params: Map<String, *>, callback: Callback)

    fun removeInjectStreamUrl(params: Map<String, *>, callback: Callback)
  }

  interface RtcStreamMessageInterface {
    fun createDataStream(params: Map<String, *>, callback: Callback)

    fun sendStreamMessage(params: Map<String, *>, callback: Callback)
  }
}

class RtcChannelManager(
  private val emit: (methodName: String, data: Map<String, Any?>?) -> Unit
) : IRtcChannel.RtcChannelInterface {
  private val rtcChannelMap = Collections.synchronizedMap(mutableMapOf<String, RtcConnection>())
  private val channelMediaOptionsMap =
    Collections.synchronizedMap(mutableMapOf<String, ChannelMediaOptions>())
  private val mediaObserverMap = Collections.synchronizedMap(mutableMapOf<String, MediaObserver>())

  fun release() {
//    rtcChannelMap.forEach { it.value.destroy() }
    rtcChannelMap.clear()
    mediaObserverMap.clear()
  }

  operator fun get(channelId: String): RtcConnection? {
    return rtcChannelMap[channelId]
  }

  override fun create(params: Map<String, *>, callback: Callback) {
    callback.resolve(params["engine"] as RtcEngine) {
      val channelId = params["channelId"] as String
      rtcChannelMap[channelId] = RtcConnection()
      channelMediaOptionsMap[channelId] = ChannelMediaOptions()
      Unit
    }
  }

  override fun destroy(params: Map<String, *>, callback: Callback) {
    rtcChannelMap.remove(params["channelId"] as String)
//    callback.code(rtcChannelMap.remove(params["channelId"] as String)?.destroy())
  }

  override fun setClientRole(params: Map<String, *>, callback: Callback) {
    val channelId = params["channelId"] as String
    val options = channelMediaOptionsMap[channelId]
    val role = (params["role"] as Number).toInt()
    options?.clientRoleType = role
    (params["engine"] as RtcEngineEx).updateChannelMediaOptionsEx(
      options,
      this[channelId]
    )
//    val role = (params["role"] as Number).toInt()
//    (params["options"] as? Map<*, *>)?.let {
//      callback.code(
//        this[params["channelId"] as String]?.setClientRole(
//          role,
//          mapToClientRoleOptions(it)
//        )
//      )
//      return@setClientRole
//    }
//    callback.code(this[params["channelId"] as String]?.setClientRole(role))
  }

  override fun joinChannel(params: Map<String, *>, callback: Callback) {
    val channelId = params["channelId"] as String
    val options = channelMediaOptionsMap[channelId]
    mapToChannelMediaOptions(params["options"] as Map<*, *>, options)
    callback.code(
      (params["engine"] as RtcEngineEx).joinChannelEx(
        params["token"] as? String,
        channelId,
        (params["optionalUid"] as Number).toNativeUInt(),
        options,
        RtcChannelEventHandler { methodName, data ->
          emit(
            methodName,
            data
          )
        },
        this[channelId]
      )
    )
  }

  override fun joinChannelWithUserAccount(params: Map<String, *>, callback: Callback) {
    val channelId = params["channelId"] as String
    val options = channelMediaOptionsMap[channelId]
    mapToChannelMediaOptions(params["options"] as Map<*, *>, options)
    callback.code(
      (params["engine"] as RtcEngineEx).joinChannelWithUserAccountEx(
        params["token"] as? String,
        channelId,
        params["userAccount"] as String,
        options,
        RtcChannelEventHandler { methodName, data ->
          emit(
            methodName,
            data
          )
        },
        this[params["channelId"] as String]
      )
    )
  }

  override fun leaveChannel(params: Map<String, *>, callback: Callback) {
    callback.code((params["engine"] as RtcEngineEx).leaveChannel())
  }

  override fun renewToken(params: Map<String, *>, callback: Callback) {
//    callback.code((params["engine"] as RtcEngineEx).renewTokenEx(params["token"] as String))
  }

  override fun getConnectionState(params: Map<String, *>, callback: Callback) {
    callback.resolve(this[params["channelId"] as String]) {
      RtcConnection.CONNECTION_STATE_TYPE.getValue(
        (params["engine"] as RtcEngineEx).getConnectionStateEx(
          it
        )
      )
    }
  }

  override fun publish(params: Map<String, *>, callback: Callback) {
//    callback.code(this[params["channelId"] as String]?.publish())
  }

  override fun unpublish(params: Map<String, *>, callback: Callback) {
//    callback.code(this[params["channelId"] as String]?.unpublish())
  }

  override fun getCallId(params: Map<String, *>, callback: Callback) {
//    callback.resolve(this[params["channelId"] as String]) { it.id }
  }

  override fun adjustUserPlaybackSignalVolume(params: Map<String, *>, callback: Callback) {
//    callback.code(
//      this[params["channelId"] as String]?.adjustUserPlaybackSignalVolume(
//        (params["uid"] as Number).toNativeUInt(),
//        (params["volume"] as Number).toInt()
//      )
//    )
  }

  override fun muteLocalAudioStream(params: Map<String, *>, callback: Callback) {
    val muted = params["muted"] as Boolean
    val channelId = params["channelId"] as String
    val options = channelMediaOptionsMap[channelId]
    options?.publishCustomAudioTrack = muted
    options?.publishCustomAudioTrackEnableAec = muted
    options?.publishAudioTrack = muted
    callback.code(
      (params["engine"] as RtcEngineEx).updateChannelMediaOptionsEx(
        options,
        this[channelId]
      )
    )
//    callback.code(
//      this[params["channelId"] as String]?.muteLocalAudioStream(
//        params["muted"] as Boolean
//      )
//    )
  }

  override fun muteRemoteAudioStream(params: Map<String, *>, callback: Callback) {
    callback.code(
      (params["engine"] as RtcEngineEx).muteRemoteAudioStreamEx(
        (params["uid"] as Number).toNativeUInt(),
        params["muted"] as Boolean,
        this[params["channelId"] as String]
      )
    )
  }

  override fun muteAllRemoteAudioStreams(params: Map<String, *>, callback: Callback) {
//    callback.code(this[params["channelId"] as String]?.muteAllRemoteAudioStreams(params["muted"] as Boolean))
  }

  override fun setDefaultMuteAllRemoteAudioStreams(params: Map<String, *>, callback: Callback) {
    val muted = params["muted"] as Boolean
    val channelId = params["channelId"] as String
    val options = channelMediaOptionsMap[channelId]
    options?.autoSubscribeAudio = muted
    callback.code(
      (params["engine"] as RtcEngineEx).updateChannelMediaOptionsEx(
        options,
        this[channelId]
      )
    )
//    callback.code(this[params["channelId"] as String]?.setDefaultMuteAllRemoteAudioStreams(params["muted"] as Boolean))
  }

  override fun muteLocalVideoStream(params: Map<String, *>, callback: Callback) {
    val muted = params["muted"] as Boolean
    val channelId = params["channelId"] as String
    val options = channelMediaOptionsMap[channelId]
    options?.publishCameraTrack = muted
    options?.publishScreenTrack = muted
    options?.publishCustomVideoTrack = muted
    options?.publishEncodedVideoTrack = muted
    callback.code(
      (params["engine"] as RtcEngineEx).updateChannelMediaOptionsEx(
        options,
        this[channelId]
      )
    )
//    callback.code(
//      this[params["channelId"] as String]?.muteLocalVideoStream(
//        params["muted"] as Boolean
//      )
//    )
  }

  override fun muteRemoteVideoStream(params: Map<String, *>, callback: Callback) {
    callback.code(
      (params["engine"] as RtcEngineEx).muteRemoteVideoStreamEx(
        (params["uid"] as Number).toNativeUInt(),
        params["muted"] as Boolean,
        this[params["channelId"] as String]
      )
    )
  }

  override fun muteAllRemoteVideoStreams(params: Map<String, *>, callback: Callback) {
//    callback.code(this[params["channelId"] as String]?.muteAllRemoteVideoStreams(params["muted"] as Boolean))
  }

  override fun setDefaultMuteAllRemoteVideoStreams(params: Map<String, *>, callback: Callback) {
    val muted = params["muted"] as Boolean
    val channelId = params["channelId"] as String
    val options = channelMediaOptionsMap[channelId]
    options?.autoSubscribeVideo = muted
    callback.code(
      (params["engine"] as RtcEngineEx).updateChannelMediaOptionsEx(
        options,
        this[channelId]
      )
    )
//    callback.code(this[params["channelId"] as String]?.setDefaultMuteAllRemoteVideoStreams(params["muted"] as Boolean))
  }

  override fun enableRemoteSuperResolution(params: Map<String, *>, callback: Callback) {
//    callback.code(
//      this[params["channelId"] as String]?.enableRemoteSuperResolution(
//        (params["uid"] as Number).toNativeUInt(),
//        params["enable"] as Boolean
//      )
//    )
  }

  override fun setRemoteVoicePosition(params: Map<String, *>, callback: Callback) {
    callback.code(
      (params["engine"] as RtcEngineEx).setRemoteVoicePositionEx(
        (params["uid"] as Number).toNativeUInt(),
        (params["pan"] as Number).toDouble(),
        (params["gain"] as Number).toDouble(),
        this[params["channelId"] as String]
      )
    )
  }

  override fun setLiveTranscoding(params: Map<String, *>, callback: Callback) {
//    callback.code(
//      this[params["channelId"] as String]?.setLiveTranscoding(
//        mapToLiveTranscoding(
//          params["transcoding"] as Map<*, *>
//        )
//      )
//    )
  }

  override fun addPublishStreamUrl(params: Map<String, *>, callback: Callback) {
//    callback.code(
//      this[params["channelId"] as String]?.addPublishStreamUrl(
//        params["url"] as String,
//        params["transcodingEnabled"] as Boolean
//      )
//    )
  }

  override fun removePublishStreamUrl(params: Map<String, *>, callback: Callback) {
//    callback.code(this[params["channelId"] as String]?.removePublishStreamUrl(params["url"] as String))
  }

  override fun startChannelMediaRelay(params: Map<String, *>, callback: Callback) {
//    callback.code(
//      this[params["channelId"] as String]?.startChannelMediaRelay(
//        mapToChannelMediaRelayConfiguration(params["channelMediaRelayConfiguration"] as Map<*, *>)
//      )
//    )
  }

  override fun updateChannelMediaRelay(params: Map<String, *>, callback: Callback) {
//    callback.code(
//      this[params["channelId"] as String]?.updateChannelMediaRelay(
//        mapToChannelMediaRelayConfiguration(params["channelMediaRelayConfiguration"] as Map<*, *>)
//      )
//    )
  }

  override fun stopChannelMediaRelay(params: Map<String, *>, callback: Callback) {
//    callback.code(this[params["channelId"] as String]?.stopChannelMediaRelay())
  }

  override fun setRemoteVideoStreamType(params: Map<String, *>, callback: Callback) {
//    callback.code(
//      this[params["channelId"] as String]?.setRemoteVideoStreamType(
//        (params["uid"] as Number).toNativeUInt(),
//        (params["streamType"] as Number).toInt()
//      )
//    )
  }

  override fun setRemoteDefaultVideoStreamType(params: Map<String, *>, callback: Callback) {
    val streamType = (params["streamType"] as Number).toInt()
    val channelId = params["channelId"] as String
    val options = channelMediaOptionsMap[channelId]
    options?.defaultVideoStreamType = streamType
    callback.code(
      (params["engine"] as RtcEngineEx).updateChannelMediaOptionsEx(
        options,
        this[channelId]
      )
    )
//    callback.code(this[params["channelId"] as String]?.setRemoteDefaultVideoStreamType((params["streamType"] as Number).toInt()))
  }

  override fun setRemoteUserPriority(params: Map<String, *>, callback: Callback) {
//    callback.code(
//      this[params["channelId"] as String]?.setRemoteUserPriority(
//        (params["uid"] as Number).toNativeUInt(),
//        (params["userPriority"] as Number).toInt()
//      )
//    )
  }

  override fun registerMediaMetadataObserver(params: Map<String, *>, callback: Callback) {
//    val channelId = params["channelId"] as String
//    val mediaObserver = MediaObserver { data ->
//      emit(
//        RtcChannelEvents.MetadataReceived,
//        data?.toMutableMap()?.apply { put("channelId", channelId) })
//    }
//    callback.code(
//      this[channelId]?.registerMediaMetadataObserver(
//        mediaObserver,
//        IMetadataObserver.VIDEO_METADATA
//      )
//    ) {
//      mediaObserverMap[channelId] = mediaObserver
//      Unit
//    }
  }

  override fun unregisterMediaMetadataObserver(params: Map<String, *>, callback: Callback) {
//    val channelId = params["channelId"] as String
//    callback.code(
//      this[channelId]?.registerMediaMetadataObserver(
//        null,
//        IMetadataObserver.VIDEO_METADATA
//      )
//    ) {
//      mediaObserverMap.remove(channelId)
//      Unit
//    }
  }

  override fun setMaxMetadataSize(params: Map<String, *>, callback: Callback) {
//    callback.resolve(mediaObserverMap[params["channelId"] as String]) {
//      it.maxMetadataSize = (params["size"] as Number).toInt()
//      Unit
//    }
  }

  override fun sendMetadata(params: Map<String, *>, callback: Callback) {
//    callback.resolve(mediaObserverMap[params["channelId"] as String]) {
//      it.addMetadata(params["metadata"] as String)
//      Unit
//    }
  }

  override fun setEncryptionSecret(params: Map<String, *>, callback: Callback) {
//    callback.code(this[params["channelId"] as String]?.setEncryptionSecret(params["secret"] as String))
  }

  override fun setEncryptionMode(params: Map<String, *>, callback: Callback) {
//    callback.code(
//      this[params["channelId"] as String]?.setEncryptionMode(
//        when ((params["encryptionMode"] as Number).toInt()) {
//          EncryptionConfig.EncryptionMode.AES_128_XTS.value -> "aes-128-xts"
//          EncryptionConfig.EncryptionMode.AES_128_ECB.value -> "aes-128-ecb"
//          EncryptionConfig.EncryptionMode.AES_256_XTS.value -> "aes-256-xts"
//          else -> ""
//        }
//      )
//    )
  }

  override fun enableEncryption(params: Map<String, *>, callback: Callback) {
//    callback.code(
//      this[params["channelId"] as String]?.enableEncryption(
//        params["enabled"] as Boolean,
//        mapToEncryptionConfig(params["config"] as Map<*, *>)
//      )
//    )
  }

  override fun addInjectStreamUrl(params: Map<String, *>, callback: Callback) {
//    callback.code(
//      this[params["channelId"] as String]?.addInjectStreamUrl(
//        params["url"] as String,
//        mapToLiveInjectStreamConfig(params["config"] as Map<*, *>)
//      )
//    )
  }

  override fun removeInjectStreamUrl(params: Map<String, *>, callback: Callback) {
//    callback.code(this[params["channelId"] as String]?.removeInjectStreamUrl(params["url"] as String))
  }

  override fun createDataStream(params: Map<String, *>, callback: Callback) {
    val channel = this[params["channelId"] as String]
    (params["config"] as? Map<*, *>)?.let { config ->
      callback.code(
        (params["engine"] as RtcEngineEx).createDataStreamEx(
          mapToDataStreamConfig(
            config
          ),
          channel
        )
      ) { it }
      return@createDataStream
    }
    callback.code(
      (params["engine"] as RtcEngineEx).createDataStreamEx(
        params["reliable"] as Boolean,
        params["ordered"] as Boolean,
        channel
      )
    ) { it }
  }

  override fun sendStreamMessage(params: Map<String, *>, callback: Callback) {
    callback.code(
      (params["engine"] as RtcEngineEx).sendStreamMessageEx(
        (params["streamId"] as Number).toInt(),
        (params["message"] as String).toByteArray(),
        this[params["channelId"] as String]
      )
    )
  }
}
