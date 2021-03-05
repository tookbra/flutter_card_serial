import 'package:flutter/material.dart';
import 'package:flutter_card_serial/flutter_card_serial.dart';

mixin CardLifecycleMixin<T extends StatefulWidget> on State<T> {
  void initCardLifecycle() {
    FlutterCardPlugin.init();
  }

  void disposeCardLifecycle() {
    FlutterCardPlugin.dispose();
  }

  @override
  void initState() {
    this.initCardLifecycle();
    super.initState();
  }

  @override
  void dispose() {
    this.disposeCardLifecycle();
    super.dispose();
  }
}