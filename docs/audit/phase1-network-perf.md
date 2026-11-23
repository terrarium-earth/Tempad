# Tempad 大修 · 第一阶段审计：网络发包与服务端性能

- 基线：`upstream/1.21.x` @ `e3816ad`（3.0.4，NeoForge 21.1.140 / MC 1.21.1）
- 范围：服务端 tick 热路径、C2S/S2C 包、BE/实体同步、位置查询
- 方法：静态代码审计（未做 spark 采样，严重度基于代码路径推断，标注“需实测”的条目上线前应用 spark 验证）

严重度：**P0** 可被普通玩家/恶意客户端触发明显卡服或越权；**P1** 正常游玩下持续产生的大量发包或 tick 开销；**P2** 低频或局部问题。

---

## 总览

| # | 级别 | 类别 | 问题 | 位置 |
|---|---|---|---|---|
| 1 | P0 | 性能 | 打开 Tempad 会同步加载**全服所有锚点**所在区块（跨维度） | `AnchorPointsHandler.kt:34-53` |
| 2 | P0 | 安全/性能 | 客户端可传任意 `BlockPos` 作为 Workstation 上下文：远程加载/生成任意区块、远程改别人的工作台 | `WorkstationContext.kt`，所有带 `ContextHolder` 的 C2S 包 |
| 3 | P0 | 安全 | 客户端可传任意物品栏槽位作为 Inventory 上下文，多数包不校验物品类型 | `InventoryContext.kt`，`SaveSettingsPacket` / `SyncPortalSettingsPacket` / `BackTrackLocation` / `UpdateTempadLockPacket` |
| 4 | P0 | 性能 | 打开类 C2S 包无频率限制，每个包都触发 #1 的全量扫描 → 放大型 DoS | `OpenTempadPacket` / `OpenAppPacket` / `RedirectAppPacket` / `UseMacroPacket` / `OpenTimedoorPacket` |
| 5 | P1 | 发包 | 时间门 `closingTime` 是 SynchedEntityData 且每 tick `--` → 每扇门每 tick 一个实体数据包 | `TimedoorEntity.kt:296` |
| 6 | P1 | 发包 | 未启动的 Metronome 每 tick `sendBlockUpdated`，**永久**持续 | `MetronomeBe.kt:64-68` |
| 7 | P1 | 发包/tick | Metronome 每 tick 向 6 个邻居 + 8 个槽位 `move()`，`insert(0, Execute)` 仍触发邻居 BE 全量同步 | `MetronomeBe.kt:79-97`，`BlockChrononContent.kt`，`WorkstationChrononHandler.kt`，`ChrononHandler.kt:move` |
| 8 | P1 | 发包 | Workstation 下载升级期间每 tick 发送含完整物品栏 NBT 的 BE 包 | `WorkstationBE.kt:127-129` |
| 9 | P1 | 性能 | `PlayerHandler.locations` 为 O(在线人数 × 背包+饰品) 且每次打开都算 | `PlayerHandler.kt:30-40` |
| 10 | P1 | 性能 | 卡包位置每张 `IndirectLocation` 卡再次走完整 provider（叠加 #1、#9） | `WalletLocationHandler.kt`，`LocationGetter.kt:IndirectLocation.get` |
| 11 | P2 | 发包 | BE `getUpdateTag` 直接 `saveAdditional` 全量（含物品栏/位置卡），配合 #6-#8 放大流量 | `MetronomeBe.kt:128`，`RudimentaryTempadBE.kt:49`，`AbstractMarkerBe.kt:57`，`WorkstationBE.kt:88` |
| 12 | P2 | 稳定性 | 未知 ContextType id 解码直接 `first{}` 抛异常；ConfigSync 遇到未知 id 不消费字节导致流错位 | `SyncableContext.kt:29`，`ConfigSyncedCache.kt:44` |
| 13 | P2 | 稳定性 | 配置监听器里 `Tempad.server!!`，客户端/未开服时改配置会 NPE | `ConfigSyncedCache.kt:81` |
| 14 | P2 | 正确性 | `animationOffset` 在 StartTracking 时写入共享的同步字段 → 每个新观察者都会重置所有人的动画并广播 | `Tempad.kt:274-280` |
| 15 | P2 | 杂项 | `PlayerTickEvent` 监听在客户端也跑 `travelHistory.logLocation` | `Tempad.kt:239` |
| 16 | P2 | 安全 | 所有 Tempad 菜单 `stillValid = true`，物品被移走后菜单仍可继续发操作包 | `AbstractTempadMenu.kt:29`，`WalletMenu.kt:49`，`MetronomeMenu.kt:64` |
| 17 | P2 | 杂项 | 时间门有实体进入时每 tick `addRegionTicket(PORTAL, r=3)` | `TimedoorEntity.kt:303-307` |

---

## 详细说明

### #1 P0 — 锚点查询同步加载全服区块

```kotlin
// AnchorPointsHandler.kt
fun getPostions(accessor) = anchors.mapValues { getBlockEntity(it.value) }.filterValues { it != null && it.canAccess(accessor) } ...
fun getBlockEntity(pos: GlobalPos) = Tempad.server?.get(pos.dimension())?.getBlockEntity(pos.pos)
```

- `anchors` 是**全服共享**的一张表（存在主世界 attachment 上），不是按玩家分的。
- 服务端主线程上的 `Level.getBlockEntity` 会走 `getChunkAt` → 区块不在内存就**同步加载**（必要时生成）。
- 触发点：每次打开传送 App（`TeleportApp.createContent`）、每次 `OpenTimedoorPacket` / `WriteToCardPacket` / `DeleteLocationPacket`（拿 handler 时会算 `locations`）、每次卡包解析指向锚点的 `IndirectLocation`、每次投影器/工作台开门。
- 后果：服务器上锚点越多，任何玩家按一次 Tempad 键就会卡一下，分散在各维度的锚点更严重。我判断这是“开 Tempad 卡服”的主因，**需 spark 实测确认**。

**修复方向**：锚点表改为存元数据快照（名字、颜色、落点、访问规则、所有者），BE 变化时写回快照；查询时**只读快照、不碰区块**。确实要校验方块时用 `level.isLoaded(pos)` / `getChunkSource().getChunkNow()` 判断，未加载就按快照处理。另外可以按 owner/访问规则建索引，避免每次 O(全服锚点数)。

### #2 P0 — WorkstationContext 信任客户端坐标

`ContextHolder` 由客户端完整序列化上来，`WorkstationContext(player, data: BlockPos)` 对 `data` 没有任何距离、维度、权限校验：

- `player.level().getBlockEntity(任意坐标)` 会同步加载/生成该区块。改包客户端可以借此让服务器生成任意远处的区块（卡服 + 污染存档）。
- 能对任意坐标的工作台改设置、改锁（`SyncPortalSettingsPacket`、`UpdateTempadLockPacket`、`SaveSettingsPacket`），扣它的能量开门。
- setter 每次都会 `sendBlockUpdated(UPDATE_ALL)`。

**修复方向**：统一一个 `ContextHolder.resolveFor(player): SyncableContext?`，做以下校验：
1. `level.isLoaded(pos)`，并且 `player.canInteractWithBlock(pos, 4.0)`（或者当前打开的菜单就绑定了这个 pos）；
2. 目标物品必须是预期物品（Tempad / 卡包）；
3. 校验不通过就丢弃该包。
所有 C2S 包都改走这个入口。

### #3 P0 — InventoryContext 信任客户端槽位

- 槽位 `data: Int` 不做范围检查，越界会在网络处理线程抛异常。
- `SaveSettingsPacket` 会对该槽位的**任意物品**写入 `defaultApp` / `defaultMacro` 组件。`SyncPortalSettingsPacket`、`UpdateTempadLockPacket`、`BackTrackLocation` 也一样，都不检查物品是不是 Tempad。
- **修复方向**：同 #2，在统一校验入口里完成范围和物品类型检查。

### #4 P0 — 打开类包无节流

客户端按键用的是 `while (consumeClick())`，正常情况下频率有限。但服务端完全不限速：改包客户端每秒发几千个 `OpenTempadPacket`，每个包都会执行 #1、#9、#10，并且构造、发送一个包含全部位置的菜单包。
**修复方向**：每个玩家按包类型做冷却（比如 open 类 5 tick，timedoor 类复用物品 cooldown）。同时缓存位置列表，按 (player, upgrades hash) 缓存几秒，有写操作时失效。

### #5 P1 — closingTime 每 tick 同步

`closingTime` 用 `EntityDataSerializers.INT` 同步，`tick()` 里每 tick 执行 `closingTime--` → entityData 变脏 → 每 tick 给所有追踪者发一个 `ClientboundSetEntityDataPacket`。双向门是 2 扇，常驻门（工作台 / 投影器）一直开着就会一直发。
**修复方向**：改为同步一个**绝对的关闭时刻**（gameTime），或者只在“续期 / 进入关闭动画”时同步，客户端用 `level.gameTime` 自己算剩余时间。普通字段 `closingTime` 只保留在服务端。

### #6 P1 — 未启动的 Metronome 永久刷包

```kotlin
} else {            // bootChronons < jumpStartAmount
    bootTime = 100
    level?.sendBlockUpdated(...)   // 每 tick
    this.setChanged()              // 每 tick
}
```

放下但没有充能启动的 Metronome，每 tick 都发 BE 数据包，并且把区块标脏，会一直持续下去。启动倒计时的 100 tick 里也是每 tick 发。
**修复方向**：只在状态变化时同步（bootChronons 变化、启动完成），倒计时让客户端自己推算，或者每 20 tick 同步一次。

### #7 P1 — Metronome 能量传输的空操作也触发同步

`move(from, to, n)` 无论 `extracted` 是不是 0，都会执行 `to.insert(extracted, Execute)` 和 `from.extract(inserted, Execute)`。而 `BlockChrononContent` 和 `WorkstationChrononHandler` 的 Execute 分支**无条件** `setChanged()` + `sendBlockUpdated(UPDATE_ALL)`。
结果是：Metronome 贴着投影器或工作台时，即使双方都满了或都空了，每 tick 也会给邻居发一个全量 BE 包（工作台还带完整物品栏）。
另外每个 Metronome 每 tick 还要做 6 次 `getBlockEntity` 加 capability 查询，以及 8 次物品 capability 构造。
**修复方向**：
- `move()` 在 `extracted == 0` 时直接 return；
- handler 在实际变化量为 0 时不做 setChanged / sync；
- 邻居 capability 用 `BlockCapabilityCache` 缓存；
- 传输改为每 N tick 批量执行一次。

### #8 P1 — Workstation 下载期间每 tick 全量同步

`downloadTime--` 每 tick 执行 `sendBlockUpdated`，而 `getUpdateTag` 里带了 `inventory.serializeNBT`。
**修复方向**：客户端根据 `maxDownloadTime` 和开始时间自己插值进度；服务端只在开始和结束时同步。

### #9 P1 — PlayerHandler 扫描全体在线玩家

装了 playerKey 升级后，每次打开都要对每个在线玩家执行 `ContextRegistry.locate`（遍历整个背包加饰品槽），再调用 `canAccess`（可能走 FTB Teams / Allies 查询）。人多的服务器上这是 O(P²)：所有人都在开 Tempad 时，每人每次的成本都是 O(P)。
**修复方向**：每个玩家身上的“广播器 / 屏蔽器”状态每秒缓存一次，或在物品栏变化时更新。

### #10 P1 — 卡包间接位置重复走全量 provider

`IndirectLocation.get()` 每次都会 `TempadLocations[provider](...)` 重新构造 handler，并取完整的 `locations` 再按 id 取一个。卡包最多 18 张卡，那就是 18 次全量 #1 或 #9。
**修复方向**：`LocationHandler` 加一个 `get(id)` 的直查实现，锚点和玩家都能 O(1) 定位，不要先算全量再过滤。

### #11 P2 — BE 更新标签全量

几个 BE 的 `getUpdateTag` 都是 `saveAdditional` 全量。Metronome 带 9 槽物品栏，投影器带位置卡数据。它本身不算问题，但和 #6~#8 的高频同步叠加，会把流量放大。
**修复方向**：只放客户端渲染需要的字段。

### #12 / #13 P2 — 解码与配置同步健壮性

- `ContextRegistry.registry.keys.first { it.id == id }`：收到未知 id 会抛 `NoSuchElementException`，连接被踢。应该改成返回 null 然后丢包。
- `ConfigSyncType.decode`：遇到未知 id 时跳过但不读取对应 payload，后面的字段会全部错位。应该给每个 entry 写长度前缀，或者直接断言 id 都已知。
- 配置监听器里的 `Tempad.server!!`：单人或客户端修改配置时会 NPE。应该改成 `server?.let`。

### #14 P2 — animationOffset 逻辑错误

每有一个新玩家开始追踪某扇门，就把**共享的**同步字段改成当前 tickCount，并广播给所有追踪者，结果是其他人看到的动画会跳。
**修复方向**：改成客户端本地字段，在实体加入客户端世界时（`onAddedToLevel`，client 侧）记录。

### #15 ~ #17 P2

- #15：监听器开头加 `if (event.entity.level().isClientSide) return`。
- #16：`stillValid` 改为校验上下文物品仍然存在（Workstation 还要校验距离）。这一项和 #2、#3 一起修。
- #17：用一个字段记录上次加 ticket 的 gameTime，每 20 tick 续一次就够了（PORTAL ticket 寿命 300 tick）。

---

## 已排除 / 无需处理

- **Level attachment 存档丢失**：NeoForge 21.1 的 `LevelAttachmentsSavedData.isDirty()` 恒为 `true`，所以原地修改 `playerPoints` / `anchorPoints` / `metronomeEnergy` 不会丢数据。代价是每次自动保存都会全量序列化，体量不大，暂不处理。
- **快捷键连发**：客户端是 `consumeClick`，正常玩家不会刷屏。风险只来自改包客户端（见 #4）。
- **客户端渲染**：TimedoorRenderer、各 BE renderer 没有发现每帧的世界查询，不在本阶段范围内。
- **上游分支**：`upstream/fix-and-clean-tmedoor` 相对 1.21.x 没有新提交；`1.21.x-cloche` 只是构建迁移。上游目前没有在修这些问题。

---

## 修复记录

### 1. 统一 C2S 上下文校验（分支 `audit/phase1-network-perf`）

覆盖 #2 #3 #16（Tempad、Metronome 菜单），以及 #12 的前半部分。

- `ContextRegistry.register(type, provider, validator)` 新增了 `ContextValidator` 参数，默认值是全部放行，兼容附属 mod。新增 `ContextHolder.resolve(player, expectedItem)`：先跑校验器，再检查物品类型，任一不通过就返回 null。原来的 `getCtx` 保留，但只能用于服务端自己构造的上下文。
- 内置的几个校验器：
  - Inventory：槽位必须在范围内。
  - Workstation：先检查 `isLoaded`（保证不会加载区块），再检查 `canInteractWithBlock(pos, 6.0)`，确认该位置确实是 WorkstationBE，并执行**锁定规则**。
  - Curios：槽位标识和下标都必须存在。
  - Carried：直接放行，物品类型由调用方检查。
- 所有带上下文的 C2S 包都改成走 `resolveTempad` 或 `resolve`，失败就静默丢包：BackTrack、CreateLocation、DeleteLocation、OpenTimedoor、RedirectApp、SaveSettings、SyncPortalSettings、UpdateTempadLock、WriteToCard。
- 审计时漏掉、这次一并修掉的问题：
  - **工作台锁定可被绕过**：锁定只在右键打开时检查，直接发包可以操作别人锁住的工作台。现在锁定规则放进了校验器。
  - **标记方块包会加载区块**：UpdateAnchor、UpdateChronomark、UpdateMetronome 原来先 `getBlockEntity` 再 `mayInteract`，伪造一个远处坐标就能让服务器加载区块。现在先检查已加载和距离。
  - **`allowLocationSaving` 只在客户端生效**：CreateLocation 现在会在服务端检查这个配置。
  - **名字长度不受限**：位置名和标记名在服务端截断到 64 字符。
  - **偏移量可以是 NaN**：SyncPortalSettings 现在拒绝 NaN 和 Infinity。
  - **未知 id 导致崩溃**：DeleteLocation 遇到未知 provider 会 `!!` NPE，SyncPortalSettings 遇到工作台没有 owner 时也会 `!!` NPE，两处都已修。
  - **RedirectApp 信任客户端标志**：`isStationary` 不再取客户端发来的值，改为由校验后的上下文推导。
  - **SaveSettings 不校验 id**：现在会检查 app 和 macro 的 id 是否已注册；写回时改用 `ctx.modify`，工作台才会正确标脏。
- 菜单：`AbstractTempadMenu.stillValid` 改为重新校验上下文（Tempad 不在了、离开工作台、工作台被锁都会关闭菜单）。`MetronomeMenu.stillValid` 增加了距离检查。WalletMenu 还没改。
- 未知 ContextType 现在抛 `DecoderException`，消息更清楚。行为仍然是断开该客户端，对畸形包来说这是合理的。
- 构建修复：
  - `maven.octo-studios.com` 域名已经失效，Curios 改从 `maven.theillusivec4.top` 获取，编译版本升到 `9.2.0+1.21.1`（该仓库最低的 1.21.1 版本）。
  - cursemaven 返回的 POM Gradle 解析不了，改为只取 jar。
- 验证：`./gradlew compileKotlin` 通过。**还没有在游戏里实测**。

### 2. 锚点快照化（覆盖 #1、#10 的锚点部分）

- `AnchorPointsData` 原来只存 `UUID → GlobalPos`，现在存 `UUID → AnchorSnapshot`。快照包含：位置、落点 `NamedGlobalVec3`（名字、坐标、维度、朝向、颜色）、owner 和访问规则 `AnchorAccessRule`。
- **查询只读快照，不再调用 `getBlockEntity`，也就不会加载区块**：
  - `getPositions` 遍历快照，权限检查只用快照里的 owner 和规则。规则里的 `PlayerAccess` 用目标维度的 `ServerLevel` 对象来计算，这个调用不触发加载。
  - 新增 `getPosition(id)` 做 O(1) 单点查询。`AnchorPointsHandler.get` 和 `getSerializable` 改用它，卡包里的 `IndirectLocation` 也不再先算全量列表。
- **刷新时机**：
  - `AbstractMarkerBe.setChanged()` 会把快照写回表里。所有编辑包都会调用 `setChanged`。
  - 染色、首次认领 owner、物品放置（`SpatialAnchorItem`）这三处原来不调 `setChanged`，属性改了但不保证存档。现在都补上了。
  - `onLoad` 时按需自愈：如果表里同一个 id 登记在别的位置（比如结构工具复制出来的方块），不会去抢这个 id。
- **自清理**：只对已加载区块检查，如果对应位置的方块已经不在了，就删掉这条快照。未加载的区块不做检查。
- **旧存档迁移**：
  - 旧格式通过 `Codec.withAlternative` 兼容读取，读进来时快照里没有 `location`。
  - `ServerStartedEvent` 时做一次性迁移：逐个加载这些锚点的区块并刷新快照，完成后在日志里打印数量和耗时。迁移只跑这一次，之后不会再因为锚点加载区块。
  - 迁移前还没刷新的条目，查询时会被跳过。
- **顺带修复的问题**：
  - **放置带 id 的锚点物品会留下孤儿条目**：`onPlace` 先用随机 id 登记，之后物品自带的 id 才写进方块。结果表里留下一条用随机 id 的孤儿记录，位置卡引用的 id 也查不到。现在刷新快照时会清掉同一位置上的其他 id，迁移时也会把这类条目重新挂到方块自己的 id 下。
  - **状态变化会误注销**：`onRemove` 在同一方块只改状态时也会触发，原来会把锚点从表里注销。现在只有方块真正变成别的方块时才注销。
- 验证：`./gradlew compileKotlin` 通过，**还没有进游戏实测**。重点要测：
  1. 用旧存档开服后，看迁移日志，确认锚点列表完整；
  2. 改名、改颜色、改访问权限后，Tempad 列表要立刻更新；
  3. 拆掉锚点再放回去，之前写好的位置卡要仍然有效；
  4. 用 spark 对比打开 Tempad 时的 tick 耗时，看看改动前后差多少。

### 3. C2S 节流 + 位置查询缓存（覆盖 #4、#9，以及 #10 的玩家部分）

- **新增 `PacketThrottle`**：按「玩家 × 桶」计冷却，冷却期内的包直接丢弃。改包客户端每秒发几千个包，现在每个桶每秒最多处理 4~5 个。正常玩家碰不到这个上限。

  | 桶 | 冷却 | 涉及的包 |
  |---|---|---|
  | `OPEN_MENU` | 5 tick | OpenTempad、OpenApp、RedirectApp、OpenWallet，共用一个桶，来回交替发也绕不过去 |
  | `MACRO` | 5 tick | UseMacro |
  | `OPEN_TIMEDOOR` | 5 tick | OpenTimedoor。原有的 40 tick 物品冷却只在开门成功后才生效，开门失败的路径以前不受限 |
  | `WRITE_CARD` | 5 tick | WriteToCard |
  | `EDIT_LOCATION` | 4 tick | CreateLocation、DeleteLocation |
  | `BACKTRACK` | 5 tick | BackTrackLocation |

- **不做节流的包**：设置类、锁定、标记编辑这几种包在校验之后只写几个值，开销很小。丢掉它们反而会让用户最后一次修改不生效。
- **新增 `BroadcastAccessCache`**：缓存每个在线玩家身上带的 `PlayerAccess` 设备（广播器、屏蔽器等），每 20 tick 重算一次。
  - 以前每次打开 Tempad，都要对每个在线玩家遍历整个背包和饰品槽，全服加起来是 O(P²)。
  - 玩家的位置仍然实时读取，只有「带了哪些设备」这一项最多滞后 1 秒。
- **`PlayerHandler.get(id)` 改为直查目标玩家**，不再先构建所有在线玩家的列表。这样卡包里的玩家卡、开门、写卡都变成 O(1)。
- `PlayerHandler.getSerializable` 现在要求目标玩家对当前玩家可见，否则返回 null。以前可以给任意在线玩家写位置卡，不过实际传送时还是会校验权限。
- 玩家下线时清理该玩家的节流记录和设备缓存。
- **为什么不缓存整份位置列表**：锚点快照化之后，再加上上面这两处优化，构建位置列表已经没有昂贵的步骤了。缓存整份列表反而会带来过期问题，比如刚新建的位置在列表里看不到。所以这次没有做整表缓存。
- 验证：`./gradlew compileKotlin` 通过，**还没有进游戏实测**。重点测两点：
  1. 正常连按快捷键、来回切换 App，不应该出现「按了没反应」的情况；
  2. 玩家 A 拿起或放下广播器后，B 打开 Tempad 时 A 应该在 1 秒内出现或消失。

## 建议的修复顺序（第一阶段）

1. **统一 C2S 上下文校验**（#2 #3 #16 #12）：把安全漏洞和区块加载 DoS 一次堵上，改动集中在 `api/context` 和各包的 handler 入口。
2. **锚点快照化**（#1 #10）：收益最大，查询路径上不再碰区块。
3. **C2S 节流 + 位置列表缓存**（#4 #9）。
4. **同步瘦身**（#5 #6 #7 #8 #11）：只在状态变化时同步，`move()` 零量直接返回，closingTime 改为绝对时间。
5. P2 杂项。

每一步都应该在测试服上用 **spark profiler**（tick 耗时）和 **网络包统计**（比如 `/spark profiler --only-ticks-over 50`，配合 packet logger 类的 mod）做修复前后对比。
