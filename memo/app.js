// app.js
App({
  globalData: {
    userInfo: null,
    // 大模型配置 - 用户需要配置自己的API密钥
    aiConfig: {
      provider: 'kimi', // kimi, deepseek, glm
      apiKey: '',
      apiUrl: {
        kimi: 'https://api.moonshot.cn/v1/chat/completions',
        deepseek: 'https://api.deepseek.com/v1/chat/completions',
        glm: 'https://open.bigmodel.cn/api/paas/v4/chat/completions'
      }
    },
    // 备忘数据存储key
    storageKey: 'memo_list',
    // 提醒订阅模板ID - 需要在微信公众平台配置
    templateId: ''
  },

  onLaunch() {
    // 展示本地存储能力
    const memos = this.getStoredMemos();
    console.log('当前备忘数量:', memos.length);

    // 检查并触发过期提醒
    this.checkExpiredReminders();
  },

  // 获取存储的备忘列表
  getStoredMemos() {
    try {
      const value = wx.getStorageSync(this.globalData.storageKey);
      return value || [];
    } catch (e) {
      console.error('获取存储失败:', e);
      return [];
    }
  },

  // 保存备忘列表
  saveMemos(memos) {
    try {
      wx.setStorageSync(this.globalData.storageKey, memos);
      return true;
    } catch (e) {
      console.error('保存失败:', e);
      wx.showToast({
        title: '保存失败',
        icon: 'none'
      });
      return false;
    }
  },

  // 检查过期提醒
  checkExpiredReminders() {
    const memos = this.getStoredMemos();
    const now = new Date().getTime();
    let hasExpired = false;

    memos.forEach((memo, index) => {
      if (memo.remindTime && memo.remindTime <= now && !memo.notified) {
        hasExpired = true;
        // 这里应该发送订阅消息，但由于需要用户授权，在详情页处理
        console.log('备忘过期:', memo.title);
      }
    });
  }
})
