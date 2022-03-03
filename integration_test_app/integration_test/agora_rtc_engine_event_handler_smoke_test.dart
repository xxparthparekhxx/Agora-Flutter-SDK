import 'package:integration_test/integration_test.dart';

import 'agora_rtc_engine_event_handler_smoke_test.generated.dart'
    as rtc_engine_event;

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  testWidgets(
    'onWarning',
    (WidgetTester tester) async {
      app.main();
      await tester.pumpAndSettle();

      FakeIrisRtcEngine fakeIrisEngine = FakeIrisRtcEngine();
      await fakeIrisEngine.initialize();
      final rtcEngine = await RtcEngine.create('123');

      MediaRecorder.getMediaRecorder(rtcEngine,
          callback: MediaRecorderObserver(
            onRecorderStateChanged: (state, error) {},
          ));

      fakeIrisEngine.fireRtcEngineEvent('onRecorderStateChanged');
// Wait for the `EventChannel` event be sent from Android/iOS side
      await tester.pump(const Duration(milliseconds: 500));
      // expect(warningCalled, isTrue);

      rtcEngine.destroy();
      fakeIrisEngine.dispose();
    },
  );
}
