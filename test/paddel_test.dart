import 'package:flutter_test/flutter_test.dart';
import 'package:paddel/paddel.dart';
import 'package:paddel/paddel_platform_interface.dart';
import 'package:paddel/paddel_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockPaddelPlatform
    with MockPlatformInterfaceMixin
    implements PaddelPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final PaddelPlatform initialPlatform = PaddelPlatform.instance;

  test('$MethodChannelPaddel is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelPaddel>());
  });

  test('getPlatformVersion', () async {
    Paddel paddelPlugin = Paddel();
    MockPaddelPlatform fakePlatform = MockPaddelPlatform();
    PaddelPlatform.instance = fakePlatform;

    expect(await paddelPlugin.getPlatformVersion(), '42');
  });
}
