## Quleme（去了么）

帮助机长/DJ 起飞/搓碟的健康记录 Android App。  
当前版本：`1.0.7`

## 功能特点

- 隐私安全：数据默认本地存储，支持应用锁（生物识别/系统认证）。
- 快捷记录：一键记录每日状态，支持撤销与补录。
- 数据统计：提供周视图与月度热力图，直观展示频率趋势。
- 健康建议：根据年龄和频率提供个性化健康提醒。
- 双主题入口：支持 Boy/Girl 两套文案与启动入口切换。
- 数据备份：支持本地备份恢复与 WebDAV 同步备份。

## 技术栈

- 语言：Kotlin（JDK 17）
- UI：Jetpack Compose + Material 3
- 架构：MVVM + Repository + Hilt（依赖注入）
- 本地存储：Room
- 导航：Navigation Compose
- 序列化：Gson

## 项目信息

- Gradle 插件：AGP `9.2.0`
- Kotlin：`2.3.21`
- compileSdk / targetSdk：`36`
- minSdk：`26`
- 应用包名：`com.quleme`
- 模块：单模块 `:app`

## 项目结构

```text
quleme
├─ app
│  └─ src/main/java/com/luleme
│     ├─ data            # 数据层：Room / WebDAV / Repository 实现
│     ├─ domain          # 领域层：模型与仓库接口
│     ├─ di              # Hilt 依赖注入模块
│     └─ ui              # Compose UI、导航、页面与主题
├─ gradle/libs.versions.toml
└─ VERSION               # 应用版本号来源
```

## 本地运行

### 1) 环境要求

- Android Studio（建议最新稳定版）
- JDK 17
- Android SDK 36

### 2) 构建 Debug 包

在项目根目录执行：

```bash
./gradlew assembleDebug
```

Windows（CMD）可用：

```bat
gradlew.bat assembleDebug
```

### 3) 安装到设备（可选）

```bash
./gradlew installDebug
```

## 发布签名（Release）

项目支持通过环境变量注入签名信息：

- `SIGNING_KEY_STORE_PATH`
- `SIGNING_STORE_PASSWORD`
- `SIGNING_KEY_ALIAS`
- `SIGNING_KEY_PASSWORD`

未提供时将跳过 release 签名配置绑定。

## 预览图

<div style="display:inline-block">
<img src=".github/demo/1.jpg" alt="demo1" width="200">
<img src=".github/demo/2.jpg" alt="demo2" width="200">
<img src=".github/demo/3.jpg" alt="demo3" width="200">
</div>

## License

本项目采用 [LICENSE](./LICENSE) 中声明的许可协议。

## Star 趋势

[![Star 趋势](https://starchart.cc/sky22333/luleme.svg?variant=adaptive)](https://starchart.cc/sky22333/luleme)
