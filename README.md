## 介绍

IM即时通讯系统是一种基于互联网的即时通讯软件，可以实现用户之间的即时消息传递、音视频通话、在线状态等功能。IM系统通常由客户端和服务端两部分组成。

## 软件架构

在软件架构方面，IM系统通常采用C/S（Client/Server）模式，即客户端和服务端分离的模式，客户端和服务端之间通过网络通信来实现数据交互。IM系统中的客户端一般包括PC端、移动端（Android、iOS等）和Web端，服务端则可以是自建服务器或第三方云服务。

## 代码模块

    im-parent ------------ 系统依赖管理
        codec ------------ 自定义通讯协议模块
        common ----------- 公共模块
        message-store ---- 消息持久化模块
        service ---------- IM核心业务模块，可以根据具体业务继续拆分子服务
        tcp -------------- 网络事件处理模块，根据不同事件分发消息到IM服务进行处理

## 功能特点

在功能特点方面，IM系统具有以下几个特点：

1. 即时通讯：用户可以通过IM系统实现即时消息的传递，与其他在线用户进行交流。
2. 多种消息类型：IM系统支持多种消息类型，包括文字、图片、语音、文件等。
3. 音视频通话：IM系统通常支持音视频通话功能，用户可以通过IM系统进行语音或视频通话。
4. 在线状态：IM系统可以实时显示用户的在线状态，例如在线、离线、忙碌等状态。
5. 群组聊天：IM系统可以支持群组聊天，多个用户可以在同一个群组中进行聊天和讨论。
6. 消息推送：IM系统可以将新消息实时推送给用户，无需用户手动刷新。

IM即时通讯系统具有快速、高效、实时等特点，被广泛应用于社交、企业通讯、在线客服等领域。
