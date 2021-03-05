import 'package:flutter/material.dart';
import 'package:flutter_card_serial/card_lifecycle_mixin.dart';


import 'test_page.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> with CardLifecycleMixin<MyApp>  {

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: TestPage(),
    );
  }
}
