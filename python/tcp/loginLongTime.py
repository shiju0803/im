#  Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
import json
import socket
import struct
import threading
import uuid


# 多端登录测试脚本
def task(s):
    print("task开始")
    while True:
        # 接收command并且解析
        command = struct.unpack('>I', s.recv(4))[0]
        print(command)
        # 接收包大小并且解析
        num = struct.unpack('>I', s.recv(4))[0]
        if command == 0x232a:
            print("收到下线通知，退出登录")
            s.close()


s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect(("127.0.0.1", 9000))

threading.Thread(target=task, args=(s,)).start()

# 基础数据
imei = str(uuid.uuid1())
command = 0x2328
version = 2
# WEB(1, "WEB"),IOS(2, "IOS"),ANDROID(3, "ANDROID"),WINDOWS(4, "WINDOWS"),MAC(5, "MAC")
clientType = 5
print(clientType)
messageType = 0x0
appId = 10000
userId = "im00001"

# 数据转换为bytes
commandByte = command.to_bytes(4, "big")
versionByte = version.to_bytes(4, "big")
messageTypeByte = messageType.to_bytes(4, "big")
clientTypeByte = clientType.to_bytes(4, "big")
appIdByte = appId.to_bytes(4, "big")
imeiByte = bytes(imei, "utf-8")
imeiLen = len(imeiByte)
imeiLenByte = imeiLen.to_bytes(4, "big")
data = {"userId": userId}
jsonData = json.dumps(data)
body = bytes(jsonData, 'utf-8')
bodyLen = len(body)
bodyLenByte = bodyLen.to_bytes(4, "big")

s.sendall(
    commandByte + versionByte + clientTypeByte + messageTypeByte + appIdByte + imeiLenByte + bodyLenByte + imeiByte + body)

while True:
    i = 1 + 1
