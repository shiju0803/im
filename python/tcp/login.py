#  Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
import json
import socket
import uuid

# 自定义私有协议解码器测试脚本

imei = "A1ABFA32-1C85-D6D8-5F3a-29Fc8ED7f14B"

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect(("127.0.0.1", 9000))

# 基础数据
command = 0x2328
print(command)
version = 1
clientType = 1
messageType = 0x0
appId = 10000
name = "ShiJu"
userId = "29"

# 数据转换为bytes
commandByte = command.to_bytes(4, "big")
versionByte = version.to_bytes(4, "big")
clientTypeByte = clientType.to_bytes(4, "big")
messageTypeByte = messageType.to_bytes(4, "big")
appIdByte = appId.to_bytes(4, "big")
imeiByte = bytes(imei, "utf-8")
imeiLen = len(imeiByte)
imeiLenByte = imeiLen.to_bytes(4, "big")
data = {"userId": userId, "name": name, "appId": appId, "clientType": clientType, "imei": imei}
# data = {"userId": userId}
jsonData = json.dumps(data)
body = bytes(jsonData, 'utf-8')
bodyLen = len(body)
bodyLenByte = bodyLen.to_bytes(4, "big")

s.sendall(commandByte + versionByte + clientTypeByte + messageTypeByte + appIdByte + imeiLenByte + bodyLenByte + imeiByte + body)
# for x in range(100):
#     s.sendall(commandByte + versionByte + clientTypeByte + messageTypeByte + appIdByte + imeiLenByte + bodyLenByte + imeiByte + body)
