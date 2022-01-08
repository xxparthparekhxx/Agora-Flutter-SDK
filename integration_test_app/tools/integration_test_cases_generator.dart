import 'dart:typed_data';

import 'package:analyzer/dart/analysis/analysis_context.dart';
import 'package:analyzer/dart/ast/ast.dart';
import 'package:analyzer/dart/element/type.dart';
import 'package:analyzer/dart/element/type_visitor.dart';
import 'package:path/path.dart' as path;
import 'package:analyzer/dart/analysis/analysis_context_collection.dart'
    show AnalysisContextCollection;
import 'dart:convert';
import 'dart:io';
import 'package:file/file.dart' as file;

import 'package:analyzer/dart/analysis/results.dart' show ParsedUnitResult;
import 'package:analyzer/dart/analysis/session.dart' show AnalysisSession;
import 'package:analyzer/dart/ast/ast.dart' as dart_ast;
import 'package:analyzer/dart/ast/syntactic_entity.dart'
    as dart_ast_syntactic_entity;
import 'package:analyzer/dart/ast/visitor.dart' as dart_ast_visitor;
import 'package:analyzer/error/error.dart' show AnalysisError;
import 'package:file/local.dart';

import 'package:code_vistor/code_vistor.dart';

enum GeneratorConfigPlatform {
  Android,
  iOS,
  macOS,
  Windows,
  Linux,
}

extension GeneratorConfigPlatformExt on GeneratorConfigPlatform {
  String toPlatformExpression() {
    switch (this) {
      case GeneratorConfigPlatform.Android:
        return 'Platform.isAndroid';
      case GeneratorConfigPlatform.iOS:
        return 'Platform.isIOS';
      case GeneratorConfigPlatform.macOS:
        return 'Platform.isMacOS';
      case GeneratorConfigPlatform.Windows:
        return 'Platform.isWindows';
      case GeneratorConfigPlatform.Linux:
        return 'Platform.isLinux';
    }
  }
}

class GeneratorConfig {
  const GeneratorConfig({
    required this.name,
    this.donotGenerate = false,
    this.supportedPlatforms = const [
      GeneratorConfigPlatform.Android,
      GeneratorConfigPlatform.iOS,
      GeneratorConfigPlatform.macOS,
      GeneratorConfigPlatform.Windows,
      GeneratorConfigPlatform.Linux,
    ],
    this.shouldMockResult = false,
    this.shouldMockReturnCode = false,
  });
  final String name;
  final bool donotGenerate;
  final List<GeneratorConfigPlatform> supportedPlatforms;
  final bool shouldMockReturnCode;
  final bool shouldMockResult;
}

const List<GeneratorConfigPlatform> desktopPlatforms = [
  GeneratorConfigPlatform.macOS,
  GeneratorConfigPlatform.Windows,
  GeneratorConfigPlatform.Linux,
];

const List<GeneratorConfigPlatform> mobilePlatforms = [
  GeneratorConfigPlatform.Android,
  GeneratorConfigPlatform.iOS,
];

IOSink? _openSink(String? output) {
  if (output == null) {
    return null;
  }
  IOSink sink;
  File file;
  if (output == 'stdout') {
    sink = stdout;
  } else {
    file = File(output);
    sink = file.openWrite();
  }
  return sink;
}

abstract class Generator {
  void generate(StringSink sink, ParseResult parseResult);

  IOSink? shouldGenerate(ParseResult parseResult);
}

abstract class DefaultGenerator implements Generator {
  const DefaultGenerator();

  GeneratorConfig? _getConfig(
      List<GeneratorConfig> configs, String methodName) {
    for (final config in configs) {
      if (config.name == methodName) {
        return config;
      }
    }
    return null;
  }

  String _concatParamName(String? prefix, String name) {
    if (prefix == null) return name;
    return '$prefix${name[0].toUpperCase()}${name.substring(1)}';
  }

  String _getParamType(Parameter parameter) {
    if (parameter.typeArguments.isEmpty) {
      return parameter.type;
    }

    return '${parameter.type}<${parameter.typeArguments.join(', ')}>';
  }

  void _createConstructorInitializerForMethodParameter(
    ParseResult parseResult,
    Parameter? rootParameter,
    Parameter parameter,
    StringBuffer initializerBuilder,
  ) {
    final bool isClass = parseResult.classMap.containsKey(parameter.type);
    final bool isEnum = parseResult.enumMap.containsKey(parameter.type);

    if (isEnum) {
      final enumz = parseResult.enumMap[parameter.type]!;

      initializerBuilder.writeln(
          'const ${_getParamType(parameter)} ${_concatParamName(rootParameter?.name, parameter.name)} = ${enumz.enumConstants[0].name};');

      return;
    }

    final parameterClass = parseResult.classMap[parameter.type]!;
    final initBlockParameterListBuilder = StringBuffer();
    final initBlockBuilder = StringBuffer();
    initBlockBuilder.write(parameterClass.name);
    initBlockBuilder.write('(');

    for (final cp in parameterClass.constructors[0].parameters) {
      final adjustedParamName = _concatParamName(parameter.name, cp.name);
      if (cp.isNamed) {
        if (cp.isPrimitiveType) {
          initBlockParameterListBuilder.writeln(
              'const ${_getParamType(cp)} $adjustedParamName = ${cp.primitiveDefualtValue()};');
          initBlockBuilder.write('${cp.name}: $adjustedParamName,');
        } else {
          _createConstructorInitializerForMethodParameter(
              parseResult, parameter, cp, initializerBuilder);
          initBlockBuilder.write('${cp.name}: $adjustedParamName,');
        }
      } else {
        if (cp.isPrimitiveType) {
          initBlockParameterListBuilder.writeln(
              'const ${_getParamType(cp)} $adjustedParamName = ${cp.primitiveDefualtValue()};');
          initBlockBuilder.write('$adjustedParamName,');
        } else {
          _createConstructorInitializerForMethodParameter(
              parseResult, parameter, cp, initializerBuilder);
          initBlockBuilder.write('$adjustedParamName,');
        }
      }
    }

    initBlockBuilder.write(')');

    initializerBuilder.write(initBlockParameterListBuilder.toString());
    initializerBuilder.writeln(
        'final ${_getParamType(parameter)} ${_concatParamName(rootParameter?.name, parameter.name)} = ${initBlockBuilder.toString()};');
  }

  String generateWithTemplate({
    required ParseResult parseResult,
    required Clazz clazz,
    required String testCaseTemplate,
    required String testCasesContentTemplate,
    required String methodInvokeObjectName,
    required List<GeneratorConfig> configs,
    List<GeneratorConfigPlatform>? supportedPlatformsOverride,
  }) {
//     const testWidgetTemplate = '''
// testWidgets('{{TEST_CASE_NAME}}', (WidgetTester tester) async {
//     app.main();
//     await tester.pumpAndSettle();

//     String engineAppId = const String.fromEnvironment('TEST_APP_ID',
//       defaultValue: '<YOUR_APP_ID>');

//     RtcEngine rtcEngine = await RtcEngine.create(engineAppId);

//     final screenShareHelper = await rtcEngine.getScreenShareHelper();

//     {{TEST_CASE_BODY}}

//     await screenShareHelper.destroy();
//     await rtcEngine.destroy();
//   },
//   skip: {{TEST_CASE_SKIP}},
// );
// ''';

    final testCases = <String>[];
    for (final method in clazz.methods) {
      final methodName = method.name;

      final config = _getConfig(configs, methodName);
      if (config?.donotGenerate == true) continue;
      if (methodName.startsWith('_')) continue;
      if (methodName.startsWith('create')) continue;

      StringBuffer pb = StringBuffer();

//       if (!method.returnType.isVoid()) {
// //         final mockCallApiResultBlock = '''
// // fakeIrisEngine.mockCallApiResult(
// //   ${method.body.callApiInvoke.apiType},
// //   ${method.body.callApiInvoke.params},
// //   '1',
// // );
// // ''';

//         // stdout.writeln(
//         //     'method.returnType: ${method.returnType.typeArguments[0]}');
//         final typeArgument = method.returnType.typeArguments[0];
//         if (parseResult.enumMap.containsKey(typeArgument)) {
//           final enumz = parseResult.enumMap[typeArgument]!;
//           final jsonValue =
//               enumz.enumConstants[0].annotations[0].arguments[0].value;
//           final mockCallApiReturnCodeBlock = '''
// fakeIrisEngine.mockCallApiReturnCode(
//   ${method.body.callApiInvoke.apiType},
//   ${method.body.callApiInvoke.params},
//   $jsonValue,
// );
// ''';

//           pb.writeln(mockCallApiReturnCodeBlock);
//         }
//       }

      for (final parameter in method.parameters) {
        if (parameter.isPrimitiveType) {
          pb.writeln(
              'const ${_getParamType(parameter)} ${parameter.name} = ${parameter.primitiveDefualtValue()};');
        } else {
          _createConstructorInitializerForMethodParameter(
              parseResult, null, parameter, pb);
        }
      }

      StringBuffer methodCallBuilder = StringBuffer();
      // methodCallBuilder.write('await screenShareHelper.$methodName(');
      methodCallBuilder.write('await $methodInvokeObjectName.$methodName(');
      for (final parameter in method.parameters) {
        if (parameter.isNamed) {
          methodCallBuilder.write('${parameter.name}:${parameter.name},');
        } else {
          methodCallBuilder.write('${parameter.name}, ');
        }
      }
      methodCallBuilder.write(');');

      pb.writeln(methodCallBuilder.toString());

//       final expectBlock = '''
// fakeIrisEngine.expectCalledApi(
//   ${method.body.callApiInvoke.apiType},
//   ${method.body.callApiInvoke.params},
// );
// ''';
//       pb.writeln(expectBlock);

      String skipExpression = 'false';

      if (supportedPlatformsOverride != null) {
        // skipExpression =
        //     '!(${desktopPlatforms.map((e) => e.toPlatformExpression()).join(' || ')})';
        skipExpression =
            '!(${supportedPlatformsOverride.map((e) => e.toPlatformExpression()).join(' || ')})';
      } else {
        if (config != null &&
            config.supportedPlatforms.length <
                GeneratorConfigPlatform.values.length) {
          skipExpression =
              '!(${config.supportedPlatforms.map((e) => e.toPlatformExpression()).join(' || ')})';
        }
      }

      String testCase =
          testCaseTemplate.replaceAll('{{TEST_CASE_NAME}}', methodName);
      testCase = testCase.replaceAll('{{TEST_CASE_BODY}}', pb.toString());
      testCase = testCase.replaceAll('{{TEST_CASE_SKIP}}', skipExpression);
      testCases.add(testCase);
    }

//     final output = '''
// import 'dart:io';

// import 'package:agora_rtc_engine/rtc_engine.dart';
// import 'package:flutter_test/flutter_test.dart';
// import 'package:integration_test_app/main.dart' as app;

// void rtcEngineSubProcessSmokeTestCases() {
//   ${testCases.join('\n')}
//   {{TEST_CASES_CONTENT}}
// }
// ''';

    final output = testCasesContentTemplate.replaceAll(
      '{{TEST_CASES_CONTENT}}',
      testCases.join('\n'),
    );

    return output;
  }
}

class RtcEngineEventHandlerSomkeTestGenerator implements Generator {
  const RtcEngineEventHandlerSomkeTestGenerator();

  @override
  void generate(StringSink sink, ParseResult parseResult) {
    final clazz = parseResult.classMap['RtcEngineEventHandler'];
    stdout.writeln('clazz: $clazz');
    if (clazz == null) return;

    // final Map<String, List<String>> classFieldsMap = parseResult.classFieldsMap;
    // final Map<String, String> fieldsTypeMap = parseResult.fieldsTypeMap;

    final fields = clazz.fields;

    final Map<String, List<String>> genericTypeAliasParametersMap =
        parseResult.genericTypeAliasParametersMap;

    // final callbackImpl = <String>[];
    // final fieldList = classFieldsMap[''] ?? [];

    final testCases = <String>[];

    const testCaseTemplate = '''
testWidgets('{{TEST_CASE_NAME}}', (WidgetTester tester) async {
  app.main();
  await tester.pumpAndSettle();

  FakeIrisRtcEngine fakeIrisEngine = FakeIrisRtcEngine();
  await fakeIrisEngine.initialize();
  final rtcEngine = await RtcEngine.create('123');
  {{TEST_CASE_BODY}}

  // Wait for the `EventChannel` event be sent from Android/iOS side
  await tester.pump(const Duration(milliseconds: 500));

  rtcEngine.destroy();
  fakeIrisEngine.dispose();
});
    
    ''';

    for (final field in fields) {
      final fieldType = field.type.type;
      final paramsOfFieldType = genericTypeAliasParametersMap[fieldType]
          ?.map((e) => e.split(' ')[1])
          .toList();
      final paramsOfFieldTypeList = paramsOfFieldType?.join(',');

      final eventName =
          'on${field.name.substring(0, 1).toUpperCase()}${field.name.substring(1)}';

      final t = '''
rtcEngine.setEventHandler(RtcEngineEventHandler(
  ${field.name}: ($paramsOfFieldTypeList) {},
));
  
fakeIrisEngine.fireRtcEngineEvent('$eventName');
      ''';

      String testCase =
          testCaseTemplate.replaceAll('{{TEST_CASE_NAME}}', eventName);
      testCase = testCase.replaceAll('{{TEST_CASE_BODY}}', t);

      testCases.add(testCase);
    }

    const testCasesContentTemplate = '''
import 'package:agora_rtc_engine/rtc_engine.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test_app/main.dart' as app;
import 'package:integration_test_app/src/fake_iris_rtc_engine.dart';

void rtcEngineEventHandlerSomkeTestCases() {
  {{TEST_CASES_CONTENT}}
}
''';

    final output = testCasesContentTemplate.replaceAll(
        '{{TEST_CASES_CONTENT}}', testCases.join(""));

    sink.writeln(output);
  }

  @override
  IOSink? shouldGenerate(ParseResult parseResult) {
    if (parseResult.classMap.containsKey('RtcEngineEventHandler')) {
      return _openSink(path.join(
          fileSystem.currentDirectory.absolute.path,
          'integration_test_app',
          'integration_test',
          'agora_rtc_engine_event_handler_smoke_test.generated.dart'));
    }

    return null;
  }
}

class RtcChannelEventHandlerSomkeTestGenerator implements Generator {
  const RtcChannelEventHandlerSomkeTestGenerator();

  static const Map<String, String> _functionNameToEventNameMap = {
    'warning': 'ChannelWarning',
    'error': 'ChannelError',
  };

  @override
  void generate(StringSink sink, ParseResult parseResult) {
    final clazz = parseResult.classMap['RtcChannelEventHandler'];

    if (clazz == null) return;

    final fields = clazz.fields;

    final Map<String, List<String>> genericTypeAliasParametersMap =
        parseResult.genericTypeAliasParametersMap;

    final testCases = <String>[];

    const testCaseTemplate = '''
testWidgets('{{TEST_CASE_NAME}}', (WidgetTester tester) async {
  app.main();
  await tester.pumpAndSettle();

  FakeIrisRtcEngine fakeIrisEngine = FakeIrisRtcEngine();
  await fakeIrisEngine.initialize();
  final rtcEngine = await RtcEngine.create('123');
  final rtcChannel = await RtcChannel.create('testapi');
  {{TEST_CASE_BODY}}

  // Wait for the `EventChannel` event be sent from Android/iOS side
  await tester.pump(const Duration(milliseconds: 500));

  await rtcChannel.destroy();
  await rtcEngine.destroy();
  fakeIrisEngine.dispose();
});
    ''';

    for (final field in fields) {
      final fieldType = field.type.type;
      final paramsOfFieldType = genericTypeAliasParametersMap[fieldType]
          ?.map((e) => e.split(' ')[1])
          .toList();
      final paramsOfFieldTypeList = paramsOfFieldType?.join(',');

      final baseEventName =
          _functionNameToEventNameMap[field.name] ?? field.name;
      final eventName =
          'on${baseEventName.substring(0, 1).toUpperCase()}${baseEventName.substring(1)}';

      final t = '''
rtcChannel.setEventHandler(RtcChannelEventHandler(
  ${field.name}: ($paramsOfFieldTypeList) {},
));
  
fakeIrisEngine.fireRtcChannelEvent('$eventName');
      ''';

      String testCase =
          testCaseTemplate.replaceAll('{{TEST_CASE_NAME}}', eventName);
      testCase = testCase.replaceAll('{{TEST_CASE_BODY}}', t);

      testCases.add(testCase);
    }

    const testCasesContentTemplate = '''
import 'package:agora_rtc_engine/rtc_channel.dart';
import 'package:agora_rtc_engine/rtc_engine.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test_app/main.dart' as app;
import 'package:integration_test_app/src/fake_iris_rtc_engine.dart';

void rtcChannelEventHandlerSomkeTestCases() {
  {{TEST_CASES_CONTENT}}
}
''';

    final output = testCasesContentTemplate.replaceAll(
        '{{TEST_CASES_CONTENT}}', testCases.join(""));

    sink.writeln(output);
  }

  @override
  IOSink? shouldGenerate(ParseResult parseResult) {
    if (parseResult.classMap.containsKey('RtcChannelEventHandler')) {
      return _openSink(path.join(
          fileSystem.currentDirectory.absolute.path,
          'integration_test_app',
          'integration_test',
          'agora_rtc_channel_event_handler_smoke_test.generated.dart'));
    }

    return null;
  }
}

class RtcEngineSubProcessSmokeTestGenerator extends DefaultGenerator {
  const RtcEngineSubProcessSmokeTestGenerator();

  static const List<GeneratorConfig> configs = [
    GeneratorConfig(name: 'getScreenShareHelper', donotGenerate: true),
    GeneratorConfig(name: 'instance', donotGenerate: true),
    GeneratorConfig(name: 'initialize', donotGenerate: true),
    GeneratorConfig(name: 'getSdkVersion', donotGenerate: true),
    GeneratorConfig(name: 'getErrorDescription', donotGenerate: true),

    // TODO(littlegnal): This should be a getter proerpty.
    GeneratorConfig(name: 'methodChannel', donotGenerate: true),
    GeneratorConfig(name: 'setEventHandler', donotGenerate: true),
    GeneratorConfig(name: 'sendMetadata', donotGenerate: true),
    GeneratorConfig(name: 'sendStreamMessage', donotGenerate: true),
    // TODO(littlegnal): Re-enable it later
    GeneratorConfig(name: 'setLiveTranscoding', donotGenerate: true),
    // TODO(littlegnal): Re-enable it later
    GeneratorConfig(name: 'enableVirtualBackground', donotGenerate: true),
    GeneratorConfig(name: 'deviceManager', donotGenerate: true),
    GeneratorConfig(name: 'destroy', donotGenerate: true),
    GeneratorConfig(
      name: 'enableLoopbackRecording',
      supportedPlatforms: desktopPlatforms,
    ),
    // TODO(littlegnal): Re-enable it later
    GeneratorConfig(name: 'setVideoEncoderConfiguration', donotGenerate: true),
    GeneratorConfig(name: 'getUserInfoByUid', donotGenerate: true),
    GeneratorConfig(name: 'getUserInfoByUserAccount', donotGenerate: true),
    GeneratorConfig(name: 'getConnectionState', donotGenerate: true),
    // Only run on valid appId.
    GeneratorConfig(name: 'getCameraMaxZoomFactor', donotGenerate: true),
    GeneratorConfig(
        name: 'isCameraAutoFocusFaceModeSupported', donotGenerate: true),
    GeneratorConfig(
        name: 'isCameraExposurePositionSupported', donotGenerate: true),
    GeneratorConfig(name: 'isCameraFocusSupported', donotGenerate: true),
    GeneratorConfig(name: 'isCameraZoomSupported', donotGenerate: true),
    GeneratorConfig(
        name: 'setCameraAutoFocusFaceModeEnabled', donotGenerate: true),
    GeneratorConfig(name: 'setCameraExposurePosition', donotGenerate: true),
    GeneratorConfig(
        name: 'setCameraFocusPositionInPreview', donotGenerate: true),
    GeneratorConfig(name: 'setCameraZoomFactor', donotGenerate: true),
    GeneratorConfig(name: 'startRhythmPlayer', donotGenerate: true),
    GeneratorConfig(name: 'stopRhythmPlayer', donotGenerate: true),
    GeneratorConfig(name: 'configRhythmPlayer', donotGenerate: true),
    GeneratorConfig(name: 'getNativeHandle', donotGenerate: true),

// TODO(littlegnal): Re-enable it later
    GeneratorConfig(name: 'takeSnapshot', donotGenerate: true),

    // Destop only
    GeneratorConfig(
      name: 'setAudioSessionOperationRestriction',
      supportedPlatforms: desktopPlatforms,
    ),
    GeneratorConfig(
      name: 'setScreenCaptureContentHint',
      supportedPlatforms: desktopPlatforms,
    ),
    GeneratorConfig(
      name: 'startScreenCaptureByDisplayId',
      supportedPlatforms: desktopPlatforms,
    ),
    GeneratorConfig(
      name: 'startScreenCaptureByScreenRect',
      supportedPlatforms: desktopPlatforms,
    ),
    GeneratorConfig(
      name: 'startScreenCaptureByWindowId',
      supportedPlatforms: desktopPlatforms,
    ),
    GeneratorConfig(
      name: 'stopScreenCapture',
      supportedPlatforms: desktopPlatforms,
    ),
    GeneratorConfig(
      name: 'updateScreenCaptureParameters',
      supportedPlatforms: desktopPlatforms,
    ),
    GeneratorConfig(
      name: 'updateScreenCaptureRegion',
      supportedPlatforms: desktopPlatforms,
    ),
    GeneratorConfig(
      name: 'startScreenCapture',
      supportedPlatforms: desktopPlatforms,
    ),
  ];

  @override
  void generate(StringSink sink, ParseResult parseResult) {
    final clazz = parseResult.classMap['RtcEngine'];
    if (clazz == null) return;

    const testCaseTemplate = '''
testWidgets('{{TEST_CASE_NAME}}', (WidgetTester tester) async {
    app.main();
    await tester.pumpAndSettle();

    String engineAppId = const String.fromEnvironment('TEST_APP_ID',
      defaultValue: '<YOUR_APP_ID>');

    RtcEngine rtcEngine = await RtcEngine.createWithContext(RtcEngineContext(
    engineAppId,
    areaCode: [AreaCode.NA, AreaCode.GLOB],
  ));

    final screenShareHelper = await rtcEngine.getScreenShareHelper();

    {{TEST_CASE_BODY}}

    await screenShareHelper.destroy();
    await rtcEngine.destroy();
  },
  skip: {{TEST_CASE_SKIP}},
);
''';

    const testCasesContentTemplate = '''
import 'dart:io';

import 'package:agora_rtc_engine/rtc_engine.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test_app/main.dart' as app;

void rtcEngineSubProcessSmokeTestCases() {
  {{TEST_CASES_CONTENT}}
}
''';

    final output = generateWithTemplate(
      parseResult: parseResult,
      clazz: clazz,
      testCaseTemplate: testCaseTemplate,
      testCasesContentTemplate: testCasesContentTemplate,
      methodInvokeObjectName: 'screenShareHelper',
      configs: configs,
      supportedPlatformsOverride: desktopPlatforms,
    );

    sink.writeln(output);
  }

  @override
  IOSink? shouldGenerate(ParseResult parseResult) {
    if (parseResult.classMap.containsKey('RtcEngine')) {
      return _openSink(path.join(
          fileSystem.currentDirectory.absolute.path,
          'integration_test_app',
          'integration_test',
          'agora_rtc_engine_subprocess_api_smoke_test.generated.dart'));
    }

    return null;
  }
}

class RtcDeviceManagerSmokeTestGenerator extends DefaultGenerator {
  const RtcDeviceManagerSmokeTestGenerator();

  static const List<GeneratorConfig> configs = [
    GeneratorConfig(name: 'getVideoDevice', donotGenerate: true),
  ];

  @override
  void generate(StringSink sink, ParseResult parseResult) {
    final clazz = parseResult.classMap['RtcDeviceManager'];
    if (clazz == null) return;

    const testCaseTemplate = '''
testWidgets('{{TEST_CASE_NAME}}', (WidgetTester tester) async {
    app.main();
    await tester.pumpAndSettle();

    String engineAppId = const String.fromEnvironment('TEST_APP_ID',
        defaultValue: '<YOUR_APP_ID>');

    RtcEngine rtcEngine = await RtcEngine.create(engineAppId);
    final deviceManager = rtcEngine.deviceManager;

    try {
      {{TEST_CASE_BODY}}
    } catch (e) {
      if (e is! PlatformException) {
        rethrow;
      }
      expect(e.code != '-7', isTrue);
    }

    await rtcEngine.destroy();
  },
  skip: {{TEST_CASE_SKIP}},
);
''';

    const testCasesContentTemplate = '''
import 'dart:io';

import 'package:flutter/services.dart';
import 'package:agora_rtc_engine/rtc_engine.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test_app/main.dart' as app;

void rtcDeviceManagerSmokeTestCases() {
  {{TEST_CASES_CONTENT}}
}
''';

    final output = generateWithTemplate(
      parseResult: parseResult,
      clazz: clazz,
      testCaseTemplate: testCaseTemplate,
      testCasesContentTemplate: testCasesContentTemplate,
      methodInvokeObjectName: 'deviceManager',
      configs: configs,
      supportedPlatformsOverride: desktopPlatforms,
    );

    sink.writeln(output);
  }

  @override
  IOSink? shouldGenerate(ParseResult parseResult) {
    if (parseResult.classMap.containsKey('RtcDeviceManager')) {
      return _openSink(path.join(
          fileSystem.currentDirectory.absolute.path,
          'integration_test_app',
          'integration_test',
          'agora_rtc_device_manager_api_smoke_test.generated.dart'));
    }

    return null;
  }
}

final List<Generator> generators = [
  const RtcEngineSubProcessSmokeTestGenerator(),
  const RtcEngineEventHandlerSomkeTestGenerator(),
  const RtcDeviceManagerSmokeTestGenerator(),
  const RtcChannelEventHandlerSomkeTestGenerator(),
];

const file.FileSystem fileSystem = LocalFileSystem();

void main(List<String> args) {
  final srcDir = path.join(
    fileSystem.currentDirectory.absolute.path,
    'lib',
    'src',
  );
  final List<String> includedPaths = <String>[
    path.join(srcDir, 'rtc_engine.dart'),
    path.join(srcDir, 'enums.dart'),
    path.join(srcDir, 'classes.dart'),
    path.join(srcDir, 'rtc_device_manager.dart'),
    path.join(srcDir, 'events.dart'),
  ];

  CodeVistor codeVistor = CodeVistor(includedPaths: includedPaths);
  final parseResult = codeVistor.visit();

  for (final generator in generators) {
    final sink = generator.shouldGenerate(parseResult);
    if (sink != null) {
      generator.generate(sink, parseResult);
      sink.flush();
    }
  }
}
