import 'package:flutter/material.dart';

import 'flutter_card_serial.dart';

mixin CardListenerMixin<T extends StatefulWidget> on State<T> {
  void onEvent(Object code);

  void onError(Object error);

  void checkRouteAndFireEvent(Object code) {
    if (!ModalRoute.of(context).isCurrent) return;
    this.onEvent(code);
  }

  void registerPdaListener() {
    FlutterCardPlugin.registerListener(this);
  }

  void unRegisterPdaListener() {
    FlutterCardPlugin.unRegisterListener(this);
  }

  @override
  void initState() {
    super.initState();
    this.registerPdaListener();
  }

  @override
  void dispose() {
    super.dispose();
    this.unRegisterPdaListener();
  }
}