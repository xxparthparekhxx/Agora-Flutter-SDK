package io.agora.rtc.base

import android.content.Context
import android.view.TextureView
import android.widget.FrameLayout
import io.agora.rtc2.RtcConnection
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineEx
import io.agora.rtc2.video.VideoCanvas
import java.lang.ref.WeakReference

class RtcTextureView(
  context: Context
) : FrameLayout(context) {
  private var texture: TextureView
  private var canvas: VideoCanvas
  private var channel: WeakReference<RtcConnection>? = null

  init {
    try {
      texture = RtcEngine.CreateTextureView(context)
    } catch (e: UnsatisfiedLinkError) {
      throw RuntimeException("Please init RtcEngine first!")
    }
    canvas = VideoCanvas(texture)
    addView(texture)
  }

  fun setData(engine: RtcEngineEx, channel: RtcConnection?, uid: Number) {
    this.channel = if (channel != null) WeakReference(channel) else null
    canvas.uid = uid.toNativeUInt()
    setupVideoCanvas(engine)
  }

  fun resetVideoCanvas(engine: RtcEngine) {
    val canvas =
      VideoCanvas(null, canvas.renderMode, canvas.mirrorMode, canvas.uid)
    if (canvas.uid == 0) {
      engine.setupLocalVideo(canvas)
    } else {
      engine.setupRemoteVideo(canvas)
    }
  }

  private fun setupVideoCanvas(engine: RtcEngineEx) {
    removeAllViews()
    texture = RtcEngine.CreateTextureView(context.applicationContext)
    addView(texture)
    texture.layout(0, 0, width, height)
    canvas.view = texture
    if (canvas.uid == 0) {
      engine.setupLocalVideo(canvas)
    } else {
      engine.setupRemoteVideo(canvas)
    }
  }

  fun setRenderMode(engine: RtcEngineEx, renderMode: Int) {
    canvas.renderMode = renderMode
    setupRenderMode(engine)
  }

  fun setMirrorMode(engine: RtcEngineEx, mirrorMode: Int) {
    canvas.mirrorMode = mirrorMode
    setupRenderMode(engine)
  }

  private fun setupRenderMode(engine: RtcEngineEx) {
    if (canvas.uid == 0) {
      engine.setLocalRenderMode(canvas.renderMode, canvas.mirrorMode)
    } else {
      channel?.get()?.let {
        engine.setRemoteRenderModeEx(canvas.uid, canvas.renderMode, canvas.mirrorMode, it)
        return@setupRenderMode
      }
      engine.setRemoteRenderMode(canvas.uid, canvas.renderMode, canvas.mirrorMode)
    }
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val width: Int = MeasureSpec.getSize(widthMeasureSpec)
    val height: Int = MeasureSpec.getSize(heightMeasureSpec)
    texture.layout(0, 0, width, height)
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
  }
}
