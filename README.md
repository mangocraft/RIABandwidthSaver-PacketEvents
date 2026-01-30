# RIABandwidthSaver-PE

## 概述

RIABandwidthSaver-PE 是一个基于 PacketEvents 的节流插件，由 Mangocraft Code Team 开发基于 Ghost-chu 旧的 RIABandwidthSaver 插件（https://github.com/Ghost-chu/RIABandwidthSaver）进行修改，旨在在玩家处于 AFK 状态期间避免发送不必要的数据包和区块，以缓解服务器的带宽和流量资源消耗的问题。

与原 RIABandwidthSaver 插件对比

* 将前置插件由ProtocolLib改为了 PacketEvents ，更稳定、更兼容
* 优化了过滤算法，更准确、更高效
* 增加了对Folia服务器的支持

* **你需要安装 PacketEvents 才能使用此插件**
* 提供的数据信息未压缩之前的流量，实际情况下，您的服务器很可能配置了网络数据包压缩，这种情况下统计流量和实际流量会有较大出入。

## 功能

* 降低处于 AFK 状态玩家的客户端视野距离，不影响服务器的 Tick 视野距离（模拟距离），以便减少 AFK 玩家的流量消耗（越小的视距=越少的数据包=越小的流量消耗）
* 基于视角检测的 AFK 机制：通过检测玩家视角移动来判断是否进入 AFK 模式
  * 默认视角检测 AFK 阈值为 300 秒（5 分钟）
  * 当玩家长时间没有明显视角移动时，进入节能模式
  * 支持通过权限 `riabandwidthsaver.bypass` 绕过 AFK 检测
* 抑制或减少数据包发送：
  * （取消）动画数据包
  * （取消）方块破坏动画数据包
  * （取消）实体声音数据包
  * （取消）音效数据包
  * （取消）粒子效果数据包
  * （取消）爆炸动画数据包
  * （取消）世界时间同步数据包
  * （取消）实体头部旋转数据包
  * （取消）受伤动画数据包
  * （取消）伤害事件数据包
  * （取消）实体查看数据包
  * （减少）实体移动数据包（2% 通过率）
  * （减少）实体移动和视角数据包（2% 通过率）
  * （减少）实体位置同步数据包（2% 通过率）
  * （减少）实体速度数据包（2% 通过率）
  * （减少）实体元数据数据包（5% 通过率）
  * （减少）实体头部朝向数据包（20% 通过率）
  * （减少）相对实体移动数据包（2% 通过率）
  * （减少）相对实体移动视角数据包（2% 通过率）
  * （减少）经验球生成数据包（2% 通过率）
  * （取消）方块动作数据包（完全不过滤）
  * （取消）光照更新数据包
  * （取消）视角看向数据包
  * （取消）TAB列表头部和尾部文本更新数据包
  * （取消）世界事件数据包
  * （取消）物品、投掷物、实体捡起动画数据包
  * （取消）自定义声音数据包
  * （取消）实体药水效果更新数据包
  * （取消）收集物品数据包
  * （取消）地图数据包
  * （取消）属性更新数据包
  * （取消）玩家信息更新数据包
* 自动攻击和 AFK 池支持：智能检测自动化行为，允许使用自动攻击和 AFK 池的玩家顺利进入 AFK 状态
* 传送命令退出 AFK：当玩家使用 `/tpaccept`、`/tpa`、`/tpahere`、`/spawn`、`/warp`、`/back`、`/home`、`/res tp`、`/huskhomes:back`、`/huskhomes:tpaccept` 等传送命令时，自动退出 AFK 模式
* 受到攻击退出 AFK：当 AFK 玩家受到攻击时，自动退出 AFK 模式
* 流量统计（节省与消耗）
* 调试模式：可开启调试模式查看过滤详情


## 命令

```
/riabandwidthsaver - 查看流量节省统计数据
/riabandwidthsaver unfiltered - 查看流量消耗统计数据
/riabandwidthsaver reload - 重载配置文件并重新注册数据包监听器
```

## 权限

```
riabandwidthsaver.bypass - 允许玩家绕过 AFK 检测
riabandwidthsaver.admin - 允许访问管理员命令（默认为 OP）
```

## 配置文件

```yaml
# 计算所有数据包（即启用 /riabandwidthsaver unfiltered 的统计信息）
calcAllPackets: true
# 是否修改玩家视野距离
modifyPlayerViewDistance: false
# 视角检测AFK阈值（秒），默认为 300 秒（5分钟）
afkPerspectiveThresholdSeconds: 300
# 调试模式开关，开启后会在控制台输出过滤信息
debug: false
message:
  playerEcoEnable: '§a🍃 ECO 节能模式已启用，限制数据传输，可能会看着卡顿，实际正常，不会影响机器运行'
  playerEcoDisable: '§8🍃 ECO 节能模式已停用，数据传输将恢复正常'
```

## 版本

当前版本: 2.2

## 兼容性

* Folia 服务器支持
* PacketEvents v2.0+
* Minecraft 1.21+ (API 版本)

## 安装

1. 下载最新的 `RIABandwidthSaver-PE-x.x.jar` 文件
2. 将其放入服务器的 `plugins` 目录
3. 确保已安装 PacketEvents 插件
4. 启动服务器
5. 根据需要修改配置文件
6. 重启服务器使配置生效