import 'package:integration_test/integration_test.dart';
import 'package:integration_test_app/src/fake_iris_rtc_engine.dart';
import 'dart:io';
import 'package:agora_rtc_engine/rtc_engine.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test_app/main.dart' as app;
import 'package:integration_test_app/src/fake_iris_rtc_engine.dart';
import 'package:agora_rtc_engine/media_recorder.dart';

import 'agora_rtc_engine_event_handler_smoke_test.generated.dart'
    as rtc_engine_event;

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  rtc_engine_event.rtcEngineEventHandlerSomkeTestCases();

  testWidgets(
    'onWarning',
    (WidgetTester tester) async {
      app.main();
      await tester.pumpAndSettle();

      FakeIrisRtcEngine fakeIrisEngine = FakeIrisRtcEngine();
      await fakeIrisEngine.initialize(getIrisRtcEngineIntPtrOnly: true);
      String engineAppId = const String.fromEnvironment('TEST_APP_ID',
          defaultValue: '<YOUR_APP_ID>');
      RtcEngine rtcEngine = await RtcEngine.createWithContext(RtcEngineContext(
        engineAppId,
        areaCode: [AreaCode.NA, AreaCode.GLOB],
      ));

      MediaRecorder.getMediaRecorder(rtcEngine,
          callback: MediaRecorderObserver(
            onRecorderStateChanged: (state, error) {},
          ));

      fakeIrisEngine.fireRtcEngineEvent('onRecorderStateChanged');
// Wait for the `EventChannel` event be sent from Android/iOS side
      await tester.pump(const Duration(milliseconds: 500));
      // expect(warningCalled, isTrue);

      rtcEngine.destroy();
    },
  );

    

}
