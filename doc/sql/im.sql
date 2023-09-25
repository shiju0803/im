/*
 Navicat Premium Data Transfer

 Source Server         : 106.52.219.98_3306
 Source Server Type    : MySQL
 Source Server Version : 50736
 Source Host           : 106.52.219.98:3306
 Source Schema         : im

 Target Server Type    : MySQL
 Target Server Version : 50736
 File Encoding         : 65001

 Date: 25/09/2023 22:13:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for app_user
-- ----------------------------
DROP TABLE IF EXISTS `app_user`;
CREATE TABLE `app_user`  (
  `user_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户id',
  `user_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名称',
  `password` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `mobile` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `register_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '注册方式 1-手机号 2-邮箱',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'IM-应用用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for im_conversation_set
-- ----------------------------
DROP TABLE IF EXISTS `im_conversation_set`;
CREATE TABLE `im_conversation_set`  (
  `conversation_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '会话id',
  `app_id` int(10) NOT NULL COMMENT '应用id',
  `conversation_type` int(10) NULL DEFAULT NULL COMMENT '0 单聊 1群聊 2机器人 3公众号',
  `from_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发起方id',
  `to_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '接收方id',
  `is_mute` int(10) NULL DEFAULT 0 COMMENT '是否免打扰 0正常 1免打扰',
  `is_top` int(10) NULL DEFAULT 0 COMMENT '是否置顶 0未置顶 1置顶',
  `sequence` bigint(20) NULL DEFAULT NULL COMMENT '会话序列',
  `read_sequence` bigint(20) NULL DEFAULT NULL COMMENT '已读序列',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`app_id`, `conversation_id`) USING BTREE,
  INDEX `idx_fid`(`from_id`) USING BTREE,
  INDEX `idx_tid`(`to_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'IM-会话记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for im_friendship
-- ----------------------------
DROP TABLE IF EXISTS `im_friendship`;
CREATE TABLE `im_friendship`  (
  `app_id` int(20) NOT NULL COMMENT '应用id',
  `from_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户id',
  `to_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '好友id',
  `black` int(10) UNSIGNED NOT NULL DEFAULT 1 COMMENT '1正常 2拉黑',
  `remark` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `status` int(10) NULL DEFAULT 1 COMMENT '状态 1正常 2删除',
  `friend_sequence` bigint(20) NULL DEFAULT NULL COMMENT '好友序列',
  `black_sequence` bigint(20) NULL DEFAULT NULL COMMENT '黑名单序列',
  `add_source` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '来源',
  `extra` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '拓展参数',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`app_id`, `from_id`, `to_id`, `black`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'IM-好友关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for im_friendship_group
-- ----------------------------
DROP TABLE IF EXISTS `im_friendship_group`;
CREATE TABLE `im_friendship_group`  (
  `group_id` int(50) NOT NULL AUTO_INCREMENT COMMENT '好友分组id',
  `app_id` int(20) NULL DEFAULT NULL COMMENT '应用id',
  `from_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户id',
  `group_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '好友分组名称',
  `sequence` bigint(20) NULL DEFAULT NULL COMMENT '序列',
  `del_flag` int(10) UNSIGNED NULL DEFAULT 0 COMMENT '删除标志 0正常 1删除',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`group_id`) USING BTREE,
  UNIQUE INDEX `UNIQUE`(`app_id`, `from_id`, `group_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'IM-好友分组表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for im_friendship_group_member
-- ----------------------------
DROP TABLE IF EXISTS `im_friendship_group_member`;
CREATE TABLE `im_friendship_group_member`  (
  `group_id` bigint(20) NOT NULL COMMENT '好友分组id',
  `to_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '好友id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`group_id`) USING BTREE,
  INDEX `idx_tid`(`to_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'IM-好友分组成员表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for im_friendship_request
-- ----------------------------
DROP TABLE IF EXISTS `im_friendship_request`;
CREATE TABLE `im_friendship_request`  (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `app_id` int(20) NULL DEFAULT NULL COMMENT '应用id',
  `from_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户id',
  `to_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '好友id',
  `remark` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `read_status` int(10) NULL DEFAULT 0 COMMENT '是否已读 0未读 1已读',
  `add_source` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '好友来源',
  `add_wording` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '好友验证信息',
  `approve_status` int(10) NULL DEFAULT NULL COMMENT '审批状态 1同意 2拒绝',
  `sequence` bigint(20) NULL DEFAULT NULL COMMENT '序列',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_aid`(`app_id`) USING BTREE,
  INDEX `idx_fid`(`from_id`) USING BTREE,
  INDEX `idx_tid`(`to_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'IM-好友申请表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for im_group
-- ----------------------------
DROP TABLE IF EXISTS `im_group`;
CREATE TABLE `im_group`  (
  `app_id` int(20) NOT NULL COMMENT '应用id',
  `group_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '群id',
  `owner_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '群主\r\n',
  `group_type` int(10) NULL DEFAULT NULL COMMENT '群类型 1私有群（类似微信） 2公开群(类似qq）',
  `group_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '群名称',
  `mute` int(10) NULL DEFAULT 0 COMMENT '是否全员禁言，0 不禁言；1 全员禁言',
  `apply_join_type` int(10) NULL DEFAULT NULL COMMENT '申请加群选项：\r\n 0 表示禁止任何人申请加入\r\n 1 表示需要群主或管理员审批\r\n 2 表示允许无需审批自由加入群组',
  `photo` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '群头像',
  `max_member_count` int(20) NULL DEFAULT NULL COMMENT '最大成员数',
  `introduction` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '群简介',
  `notification` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '群公告',
  `private_chat` int(10) NULL DEFAULT 0 COMMENT '是否禁止私聊 0 允许群成员发起私聊 1 不允许群成员发起私聊',
  `status` int(5) NOT NULL DEFAULT 0 COMMENT '群状态 0正常 1解散',
  `sequence` bigint(20) NULL DEFAULT NULL COMMENT '群序列',
  `extra` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '拓展参数',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`app_id`, `group_id`) USING BTREE,
  INDEX `idx_oid`(`owner_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'IM-群组表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for im_group_member
-- ----------------------------
DROP TABLE IF EXISTS `im_group_member`;
CREATE TABLE `im_group_member`  (
  `group_member_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '群成员id',
  `group_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '群id',
  `app_id` int(10) NULL DEFAULT NULL COMMENT '应用id',
  `member_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '成员id\r\n',
  `role` int(10) NULL DEFAULT 0 COMMENT '群成员类型，0 普通成员, 1 管理员, 2 群主， 3 禁言，4 已经移除的成员',
  `speak_date` bigint(100) NULL DEFAULT NULL COMMENT '禁言到期时间',
  `mute` int(10) NULL DEFAULT 0 COMMENT '是否全员禁言，0 不禁言；1 全员禁言',
  `alias` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '群昵称',
  `join_time` datetime NULL DEFAULT NULL COMMENT '加入时间',
  `leave_time` datetime NULL DEFAULT NULL COMMENT '离开时间',
  `join_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '加入类型',
  `extra` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '拓展参数',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`group_member_id`) USING BTREE,
  INDEX `idx_gid`(`group_id`) USING BTREE,
  INDEX `idx_aid`(`app_id`) USING BTREE,
  INDEX `idx_mid`(`member_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'IM-群组成员表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for im_group_message_history
-- ----------------------------
DROP TABLE IF EXISTS `im_group_message_history`;
CREATE TABLE `im_group_message_history`  (
  `app_id` int(20) NOT NULL COMMENT '应用id',
  `group_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '群id',
  `message_key` bigint(50) NOT NULL COMMENT '消息体id',
  `from_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户id',
  `sequence` bigint(20) NULL DEFAULT NULL COMMENT '序列',
  `message_random` int(20) NULL DEFAULT NULL COMMENT '消息随机码',
  `message_time` bigint(20) NULL DEFAULT NULL COMMENT '消息时间',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`app_id`, `group_id`, `message_key`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'IM-群组历史消息' ROW_FORMAT = DYNAMIC;

/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

-- ----------------------------
-- Table structure for im_message_body
-- ----------------------------
DROP TABLE IF EXISTS `im_message_body`;
CREATE TABLE `im_message_body`  (
  `app_id` int(10) NOT NULL COMMENT '应用id',
  `message_key` bigint(50) NOT NULL COMMENT '消息键',
  `message_body` varchar(5000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '消息体',
  `security_key` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密钥',
  `message_time` bigint(20) NULL DEFAULT NULL COMMENT '消息时间',
  `extra` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '拓展参数',
  `del_flag` int(10) NULL DEFAULT 0 COMMENT '删除标识 0未删除 1已删除',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`message_key`) USING BTREE,
  INDEX `idx_aid`(`app_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'IM-消息体' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for im_message_history
-- ----------------------------
DROP TABLE IF EXISTS `im_message_history`;
CREATE TABLE `im_message_history`  (
  `app_id` int(20) NOT NULL COMMENT '应用id',
  `owner_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '消息拥有者id',
  `message_key` bigint(50) NOT NULL COMMENT '消息键',
  `from_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户id',
  `to_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '好友id',
  `sequence` bigint(20) NULL DEFAULT NULL COMMENT '序列',
  `message_random` int(20) NULL DEFAULT NULL COMMENT '消息随机码',
  `message_time` bigint(20) NULL DEFAULT NULL COMMENT '消息时间',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`app_id`, `owner_id`, `message_key`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'IM-历史消息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for im_user_data
-- ----------------------------
DROP TABLE IF EXISTS `im_user_data`;
CREATE TABLE `im_user_data`  (
  `user_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户id',
  `app_id` int(11) NOT NULL COMMENT '应用id',
  `user_type` int(10) NOT NULL DEFAULT 1 COMMENT '用户类型 1普通用户 2客服 3机器人',
  `nick_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `photo` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像',
  `user_sex` int(10) NULL DEFAULT NULL COMMENT '性别',
  `birth_day` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '生日',
  `location` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '地址',
  `self_signature` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '个性签名',
  `friend_allow_type` int(10) NOT NULL DEFAULT 1 COMMENT '加好友验证类型（Friend_AllowType） 1无需验证 2需要验证',
  `forbidden_flag` int(10) NOT NULL DEFAULT 0 COMMENT '禁用标识  0未禁用 1禁用',
  `disable_add_friend` int(10) NOT NULL DEFAULT 0 COMMENT '管理员禁止用户添加加好友：0 未禁用 1 已禁用',
  `silent_flag` int(10) NOT NULL DEFAULT 0 COMMENT '禁言标识 0未禁言 1禁言',
  `del_flag` int(20) NOT NULL DEFAULT 0 COMMENT '删除标识 0未删除 1已删除',
  `extra` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '拓展参数',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`app_id`, `user_id`, `user_type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'IM-用户信息表' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
