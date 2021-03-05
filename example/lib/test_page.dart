import 'package:flutter/material.dart';
import 'package:flutter_card_serial/card_listener_mixin.dart';
import 'package:flutter_card_serial/flutter_card_serial.dart';

class TestPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => TestPageState();
}

class TestPageState extends State<TestPage> with CardListenerMixin<TestPage> {

  int _mode = 0;

  @override
  void initState() {
    super.initState();
  }

  void _switch1() async {
    FlutterCardPlugin.connect().catchError((error) {
      print(error.toString());
    });
  }

  void _switch() async {
    if(_mode == 0) {
      FlutterCardPlugin.openAuto().then((value) {
        if(value) {
          setState(() {
            _mode = 1;
          });
        }
      });
    } else {
      FlutterCardPlugin.closeAuto().then((value) {
        if(value) {
          setState(() {
            _mode = 0;
          });
        }
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Plugin example app'),
      ),
      body: Column(
        children: <Widget>[
          Text('当前扫描模式: $_mode'),
          RaisedButton(
            onPressed: _switch,
            child: const Text('switch', style: TextStyle(fontSize: 20)),
          ),
          RaisedButton(
            onPressed: _switch1,
            child: const Text('switch1', style: TextStyle(fontSize: 20)),
          ),
        ],
      ),
    );
  }

  @override
  void onEvent(Object event) {
    print("ChannelPage: $event");
  }

  @override
  void onError(Object error) {
  }
}