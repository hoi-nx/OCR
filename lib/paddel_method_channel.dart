import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'paddel_platform_interface.dart';

/// An implementation of [PaddelPlatform] that uses method channels.
class MethodChannelPaddel extends PaddelPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('paddel');

  @override
  Future<String?> getPlatformVersion(String arg1,Uint8List imageData) async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion', {'arg1': arg1,'imageData': imageData});
    return version;
  }

  @override
  Future<String?> runingModel(Uint8List imageData) async {
    final version = await methodChannel.invokeMethod<String>('runingModel',{'imageData': imageData});
    return version;
  }
}
