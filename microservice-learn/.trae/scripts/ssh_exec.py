#!/usr/bin/env python3
"""通用 SSH 执行工具：凭据从环境变量读取，脚本本身不含任何硬编码凭据。
用法:
  $env:SSHHOST='1.2.3.4'; $env:SSHUSER='u'; $env:SSHPASS='p'
  python ssh_exec.py "command1" "command2" ...
"""
import os, sys, paramiko

host = os.environ.get("SSHHOST")
user = os.environ.get("SSHUSER")
pwd  = os.environ.get("SSHPASS")
if not (host and user and pwd):
    sys.exit("[err] env SSHHOST/SSHUSER/SSHPASS not set")

cmds = sys.argv[1:]
if not cmds:
    sys.exit("[err] no command given")

cli = paramiko.SSHClient()
cli.set_missing_host_key_policy(paramiko.AutoAddPolicy())
try:
    cli.connect(host, port=22, username=user, password=pwd, timeout=15,
                allow_agent=False, look_for_keys=False)
    for c in cmds:
        print(f"\n$ {c}")
        stdin, stdout, stderr = cli.exec_command(c, get_pty=True, timeout=120)
        out = stdout.read().decode("utf-8", "ignore")
        err = stderr.read().decode("utf-8", "ignore")
        if out.strip(): print(out.rstrip())
        if err.strip(): print("[stderr]", err.rstrip())
finally:
    cli.close()
