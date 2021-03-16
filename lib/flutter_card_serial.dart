import 'dart:async';

import 'package:flutter/services.dart';

import 'card_listener_mixin.dart';

class FlutterCardPlugin {
  static const String channelName = 'flutter_card_serial';
  static const String eventChannelName = 'flutter_card_serial/read';
  static EventChannel _scannerPlugin;
  static StreamSubscription _subscription;
  static List<CardListenerMixin> listeners = [];
  static const MethodChannel _channel = const MethodChannel(channelName);

  static void init() {
    if (_scannerPlugin == null)
      _scannerPlugin = const EventChannel(eventChannelName);
    _subscription = _scannerPlugin
        .receiveBroadcastStream()
        .listen(_onEvent, onError: _onError);
  }

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future connect() async {
    return await _channel.invokeMethod('connect');
  }

  static Future disconnect() async {
    await _channel.invokeMethod('disconnect');
  }

  static Future<bool> openAuto() async {
    return await _channel.invokeMethod('openAuto');
  }

  static Future<bool> closeAuto() async {
    return await _channel.invokeMethod('closeAuto');
  }

  static void registerListener(CardListenerMixin listener) {
    if (!listeners.contains(listener)) listeners.add(listener);
  }

  static void unRegisterListener(CardListenerMixin listener) {
    if (listeners.contains(listener)) listeners.remove(listener);
  }

  static void dispose() {
    listeners.clear();
    assert(_subscription != null);
    _subscription.cancel();
  }

  static void _onEvent(Object code) {
    listeners.forEach((listener) => listener.checkRouteAndFireEvent(code));
  }

  static void _onError(Object error) {
    listeners.forEach((listener) => listener.onError(error));
  }
}