
import 'dart:typed_data';

import 'paddel_platform_interface.dart';

class Paddel {
  Future<String?> getPlatformVersion(String arg1,Uint8List imageData) {
    return PaddelPlatform.instance.getPlatformVersion(arg1,imageData);
  }

  Future<String?> runingModel(Uint8List imageData) {
    return PaddelPlatform.instance.runingModel(imageData);
  }
}
