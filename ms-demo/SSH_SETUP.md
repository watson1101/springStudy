# SSH 免密登录配置指南

部署脚本通过 SSH 连接到远程服务器（`hong@localhost`）上传 JAR 并重启服务。
配置免密登录后，执行 `mvn package -P deploy` 时不再需要手动输入密码。

## 方式一：密钥认证（推荐）

### 第 1 步：在 Windows 上生成 SSH 密钥

打开 PowerShell 或 CMD，执行：

```bash
ssh-keygen -t rsa -b 4096 -f "%USERPROFILE%/.ssh/id_rsa_msdemo"
```

一路回车即可。这会在 `C:\Users\你的用户名\.ssh\` 下生成两个文件：
- `id_rsa_msdemo` — 私钥（保密，不要泄露）
- `id_rsa_msdemo.pub` — 公钥（需要上传到服务器）

### 第 2 步：将公钥上传到服务器

**方法 A — 使用 ssh-copy-id（推荐，如果服务器已启动 SSH）：**

```bash
type "%USERPROFILE%\.ssh\id_rsa_msdemo.pub" | ssh hong@localhost "mkdir -p ~/.ssh && cat >> ~/.ssh/authorized_keys"
```

会提示输入密码（`123456`），输入一次即可。

**方法 B — 手动复制（如果方法 A 不行）：**

1. 在 Windows 上查看公钥内容：
   ```bash
   type "%USERPROFILE%\.ssh\id_rsa_msdemo.pub"
   ```
   复制输出的全部内容。

2. 登录到服务器（WSL 终端）：
   ```bash
   ssh hong@localhost
   ```

3. 在 WSL 中执行：
   ```bash
   mkdir -p ~/.ssh
   chmod 700 ~/.ssh
   echo "粘贴你刚才复制的公钥内容" >> ~/.ssh/authorized_keys
   chmod 600 ~/.ssh/authorized_keys
   ```

### 第 3 步：配置 SSH 使用该密钥

编辑 `C:\Users\你的用户名\.ssh\config`（如果不存在则新建），添加：

```
Host msdemo
    HostName localhost
    User hong
    IdentityFile ~/.ssh/id_rsa_msdemo
```

### 第 4 步：验证

```bash
ssh msdemo "echo 免密登录成功"
```

不需要输入密码即为成功。

---

## 方式二：密码认证（临时方案）

如果暂未配置免密，也可以使用密码登录，但每次运行 `mvn deploy` 时都需要输入密码。

### 安装 sshpass（自动输入密码）

在 WSL 中安装 `sshpass`：

```bash
sudo apt install sshpass
```

然后修改 `deploy.bat`，将开头的配置改为：

```batch
set "SSH_PASS=123456"
```

并将所有 `ssh` 命令改为 `sshpass -p %SSH_PASS% ssh`，将 `scp` 改为 `sshpass -p %SSH_PASS% scp`。

> ⚠️ 密码明文写在脚本中有安全风险，仅建议临时测试使用。

---

## 远程服务器要求

服务器需要满足以下条件：

1. **SSH 服务已启动**（WSL Ubuntu 默认安装但可能未启动）：
   ```bash
   sudo service ssh start
   ```

2. **Java 已安装**：
   ```bash
   java -version
   ```

3. **部署目录存在**（脚本会自动创建）：
   ```bash
   mkdir -p /home/hong/apps/msdemo
   ```

4. **如需开机自启**，可将 SSH 服务设为开机启动：
   ```bash
   sudo systemctl enable ssh
   ```
   （WSL 需要 systemd 支持，或通过 Windows 任务计划程序实现）