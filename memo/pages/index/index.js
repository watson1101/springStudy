// index.js
const app = getApp();
const aiUtil = require('../../utils/ai.js');

Page({
  data: {
    inputText: '',
    images: [],
    memos: [],
    analyzing: false,
    showAiTip: false,
    aiTipText: '',
    currentTime: new Date().getTime()
  },

  onLoad() {
    this.loadMemos();
    // 每分钟检查一次提醒
    this.remindTimer = setInterval(() => {
      this.setData({
        currentTime: new Date().getTime()
      });
      this.checkReminders();
    }, 60000);
  },

  onShow() {
    this.loadMemos();
  },

  onUnload() {
    if (this.remindTimer) {
      clearInterval(this.remindTimer);
    }
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.loadMemos();
    this.setData({
      currentTime: new Date().getTime()
    });
    this.checkReminders();
    wx.stopPullDownRefresh();
  },

  // 加载备忘列表
  loadMemos() {
    const memos = app.getStoredMemos();
    // 按创建时间倒序排列
    memos.sort((a, b) => b.createTime - a.createTime);
    this.setData({ memos });
  },

  // 输入框内容变化
  onInputChange(e) {
    this.setData({
      inputText: e.detail.value
    });
  },

  // 选择图片
  chooseImage() {
    wx.chooseMedia({
      count: 3,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const tempFiles = res.tempFiles.map(file => file.tempFilePath);
        this.setData({
          images: [...this.data.images, ...tempFiles]
        });
      }
    });
  },

  // 删除图片
  removeImage(e) {
    const index = e.currentTarget.dataset.index;
    const images = this.data.images;
    images.splice(index, 1);
    this.setData({ images });
  },

  // 保存备忘
  async saveMemo() {
    const { inputText, images } = this.data;

    if (!inputText && images.length === 0) {
      wx.showToast({
        title: '请输入内容或添加图片',
        icon: 'none'
      });
      return;
    }

    // 调用AI分析文本
    this.setData({ analyzing: true });

    try {
      const aiResult = await aiUtil.analyzeText(inputText);

      const memo = {
        id: Date.now().toString(),
        content: inputText,
        title: aiResult.title || this.extractTitle(inputText),
        images: images,
        remindTime: aiResult.remindTime || null,
        remindTimeStr: aiResult.remindTimeStr || '',
        createTime: new Date().getTime(),
        createTimeStr: this.formatTime(new Date()),
        notified: false,
        aiAnalysis: aiResult
      };

      // 保存到存储
      const memos = app.getStoredMemos();
      memos.push(memo);
      app.saveMemos(memos);

      // 清空输入
      this.setData({
        inputText: '',
        images: [],
        analyzing: false
      });

      wx.showToast({
        title: '保存成功',
        icon: 'success'
      });

      // 重新加载列表
      this.loadMemos();

      // 如果有提醒时间，请求订阅消息权限
      if (memo.remindTime) {
        this.requestSubscribe(memo);
      }

    } catch (error) {
      console.error('AI分析失败:', error);
      // 即使AI失败也保存
      const memo = {
        id: Date.now().toString(),
        content: inputText,
        title: this.extractTitle(inputText),
        images: images,
        remindTime: null,
        remindTimeStr: '',
        createTime: new Date().getTime(),
        createTimeStr: this.formatTime(new Date()),
        notified: false
      };

      const memos = app.getStoredMemos();
      memos.push(memo);
      app.saveMemos(memos);

      this.setData({
        inputText: '',
        images: [],
        analyzing: false
      });

      this.loadMemos();
      wx.showToast({
        title: '保存成功',
        icon: 'success'
      });
    }
  },

  // 提取标题
  extractTitle(text) {
    if (!text) return '无标题';
    // 取前20个字符作为标题
    return text.length > 20 ? text.substring(0, 20) + '...' : text;
  },

  // 格式化时间
  formatTime(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hour = String(date.getHours()).padStart(2, '0');
    const minute = String(date.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day} ${hour}:${minute}`;
  },

  // 检查提醒
  checkReminders() {
    const memos = this.data.memos;
    const now = new Date().getTime();
    let needUpdate = false;

    memos.forEach(memo => {
      if (memo.remindTime && memo.remindTime <= now && !memo.notified) {
        // 发送本地通知
        this.sendLocalNotification(memo);
        memo.notified = true;
        needUpdate = true;
      }
    });

    if (needUpdate) {
      app.saveMemos(memos);
      this.loadMemos();
    }
  },

  // 发送本地通知
  sendLocalNotification(memo) {
    // 使用小程序通知API
    wx.showModal({
      title: '🔔 提醒',
      content: `${memo.title}\n${memo.content}`,
      showCancel: false,
      confirmText: '我知道了'
    });
  },

  // 请求订阅消息权限
  requestSubscribe(memo) {
    const templateId = app.globalData.templateId;
    if (!templateId) {
      console.log('未配置订阅消息模板ID');
      return;
    }

    wx.requestSubscribeMessage({
      tmplIds: [templateId],
      success: (res) => {
        console.log('订阅成功:', res);
        if (res[templateId] === 'accept') {
          // 用户同意订阅，保存订阅状态
          memo.subscribed = true;
          app.saveMemos(this.data.memos);
        }
      }
    });
  },

  // 跳转详情页
  goToDetail(e) {
    const memo = e.currentTarget.dataset.memo;
    wx.navigateTo({
      url: `/pages/detail/detail?id=${memo.id}`
    });
  },

  // 聚焦输入框
  focusInput() {
    // 可以在这里添加一些交互效果
  }
});
