# Simple Memo - Android 客户端

基于 **Kotlin + Jetpack Compose** 构建的 Android 客户端，支持 **Android 11+ (API 30+)**。

## 技术栈

- **语言**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **网络**: Retrofit2 + OkHttp + Gson
- **架构**: MVVM (ViewModel + Repository)
- **图片**: Coil
- **主题**: 支持深色/浅色模式

## 项目结构

```
android-client/
├── app/src/main/java/hong/com/simplememo/
│   ├── SimpleMemoApp.kt              # Application
│   ├── MainActivity.kt               # 主 Activity + 导航
│   ├── data/
│   │   ├── api/ApiClient.kt          # Retrofit 配置
│   │   ├── api/ApiService.kt         # API 接口定义
│   │   ├── model/Models.kt           # 数据类
│   │   └── repository/
│   │       ├── AuthRepository.kt     # 认证仓储
│   │       └── MemoRepository.kt     # 备忘仓储
│   └── ui/
│       ├── theme/Theme.kt            # 主题（Apple 风格）
│       ├── login/LoginScreen.kt      # 登录页
│       ├── memo/                     # 备忘页面
│       └── settings/                 # 设置页
└── app/src/main/res/
    ├── values/                       # 浅色主题资源
    └── values-night/                 # 深色主题资源
```

## 开发环境

- **Android Studio**: Hedgehog (2023.1.1) 或更新
- **JDK**: 17
- **Gradle**: 8.5
- **AGP**: 8.2.0
- **Kotlin**: 1.9.22
- **Compile SDK**: 34
- **Min SDK**: 30 (Android 11)
- **Target SDK**: 34

## 构建与打包 APK

### 方法一：Android Studio

1. 用 Android Studio 打开 `android-client/` 目录
2. 等待 Gradle 同步完成
3. 菜单栏 → Build → Build Bundle(s) / APK(s) → Build APK(s)
4. APK 文件生成在 `app/build/outputs/apk/debug/`

### 方法二：命令行

```bash
# 进入 android-client 目录
cd android-client

# 生成调试 APK
./gradlew assembleDebug

# 生成发布 APK（需配置签名）
./gradlew assembleRelease

# APK 位置：
# app/build/outputs/apk/debug/app-debug.apk
# app/build/outputs/apk/release/app-release.apk
```

### 发布签名 APK

1. 生成密钥库：`keytool -genkey -v -keystore release.keystore -alias simplememo -keyalg RSA -keysize 2048 -validity 10000`
2. 在 `app/` 目录下创建 `keystore.properties`：

```
storeFile=release.keystore
storePassword=your_password
keyAlias=simplememo
keyPassword=your_password
```

3. 运行 `./gradlew assembleRelease`

---

> 初次修改：2026-06-22 - Android 客户端初始搭建
