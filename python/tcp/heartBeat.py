#  Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
import json
import socket
import uuid
import threading
import time

# 心跳检测测试脚本

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect(("127.0.0.1", 9000))


def doPing(s):
    # 基础数据
    command = 0x270f
    version = 1
    clientType = 4
    messageType = 0x0
    appId = 10000
    userId = "im00001"
    imei = str(uuid.uuid1())

    # 数据转换为bytes
    commandByte = command.to_bytes(4, "big")
    versionByte = version.to_bytes(4, "big")
    clientTypeByte = clientType.to_bytes(4, "big")
    messageTypeByte = messageType.to_bytes(4, "big")
    appIdByte = appId.to_bytes(4, "big")
    imeiByte = bytes(imei, "utf-8")
    imeiLen = len(imeiByte)
    imeiLenByte = imeiLen.to_bytes(4, "big")
    data = {}
    jsonData = json.dumps(data)
    body = bytes(jsonData, 'utf-8')
    bodyLen = len(body)
    bodyLenByte = bodyLen.to_bytes(4, "big")

    s.sendall(
        commandByte + versionByte + clientTypeByte + messageTypeByte + appIdByte + imeiLenByte + bodyLenByte + imeiByte + body)


def ping(s):
    while True:
        time.sleep(10)
        doPing(s)


def pong(s):
    while True:
        print('接收到服务器数据：', s.recv(1024).decode('gbk'))


# 创建一个线程专门接收服务端数据并且打印
threading.Thread(target=ping, args=(s,)).start()
threading.Thread(target=pong, args=(s,)).start()
