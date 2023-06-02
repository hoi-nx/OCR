import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:paddel/paddel.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  final _paddelPlugin = Paddel();
  bool isLoading = false;

  void _handleButtonClick() {
    setState(() {
      isLoading = true; // Show loading indicator
    });

    initPlatformState();
    // Simulate an asynchronous operation

  }
  @override
  void initState() {
    super.initState();
    // initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    Uint8List imageData = await _getImageBytes();
    try {
      platformVersion =
          await _paddelPlugin.getPlatformVersion("Demo testing",imageData) ?? 'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
      isLoading = false;
    });
  }

  Future<Uint8List> _getAssetBytes(String assetName) async {
    ByteData byteData = await rootBundle.load(assetName);
    return byteData.buffer.asUint8List();
  }

  Future<Uint8List> _getImageBytes() async {
    Uint8List assetBytes = await _getAssetBytes('assets/images/det_0.jpg');
    return assetBytes;
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: Text('Button Image'),
        ),
        body: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                TextButton(
                  style: TextButton.styleFrom(
                    primary: Colors.blue,
                  ),
                  onPressed: _handleButtonClick ,
                  child: Text('Load Model'),
                )
                ,
                TextButton(
                  style: TextButton.styleFrom(
                    primary: Colors.blue,
                  ),
                  onPressed: () { },
                  child: Text('Nhận dạng ảnh'),
                )
                ,
                TextButton(
                  style: TextButton.styleFrom(
                    primary: Colors.blue,
                  ),
                  onPressed: () { },
                  child: Text('May anh'),
                )
                ,
                TextButton(
                  style: TextButton.styleFrom(
                    primary: Colors.blue,
                  ),
                  onPressed: () { },
                  child: Text('Chon anh'),
                )
                ,
              ],
            ),
            SizedBox(height: 16),
            if (isLoading)
              CircularProgressIndicator(),
            Text('Running on: $_platformVersion\n'),
            Image.asset(
              'assets/images/det_0.jpg',
              width: 500,
              height: 500,
            ),
            SizedBox(height: 16), // Khoảng cách giữa ảnh và text
            // Text('Ket qua: '),
          ],
        ),
      ),
    );
  }
}

