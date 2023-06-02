import 'dart:typed_data';

import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'paddel_method_channel.dart';

abstract class PaddelPlatform extends PlatformInterface {
  /// Constructs a PaddelPlatform.
  PaddelPlatform() : super(token: _token);

  static final Object _token = Object();

  static PaddelPlatform _instance = MethodChannelPaddel();

  /// The default instance of [PaddelPlatform] to use.
  ///
  /// Defaults to [MethodChannelPaddel].
  static PaddelPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [PaddelPlatform] when
  /// they register themselves.
  static set instance(PaddelPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion(String arg1,Uint8List imageData) {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
  Future<String?> runingModel(Uint8List imageData) {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
