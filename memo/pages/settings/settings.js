// settings.js
const app = getApp();
const aiUtil = require('../../utils/ai.js');

Page({
  data: {
    providers: [
      { label: 'Kimi (月之暗面)', value: 'kimi' },
      { label: 'DeepSeek', value: 'deepseek' },
      { label: '智谱GLM', value: 'glm' }
    ],
    providerIndex: 0,
    apiKey: '',
    showApiKey: false,
    templateId: '',
    testing: false
  },

  onLoad() {
    this.loadSettings();
  },

  // 加载已保存的设置
  loadSettings() {
    const aiConfig = aiUtil.getAIConfig();
    const templateId = app.globalData.templateId || wx.getStorageSync('template_id') || '';

    // 找到对应的提供商索引
    const providerIndex = this.data.providers.findIndex(
      p => p.value === aiConfig.provider
    );

    this.setData({
      providerIndex: providerIndex >= 0 ? providerIndex : 0,
      apiKey: aiConfig.apiKey || '',
      templateId: templateId
    });
  },

  // 提供商变更
  onProviderChange(e) {
    this.setData({
      providerIndex: parseInt(e.detail.value)
    });
  },

  // API密钥输入
  onApiKeyInput(e) {
    this.setData({
      apiKey: e.detail.value
    });
  },

  // 切换API密钥显示/隐藏
  toggleApiKey() {
    this.setData({
      showApiKey: !this.data.showApiKey
    });
  },

  // 模板ID输入
  onTemplateIdInput(e) {
    this.setData({
      templateId: e.detail.value
    });
  },

  // 测试连接
  async testConnection() {
    const { apiKey, providerIndex, providers } = this.data;

    if (!apiKey) {
      wx.showToast({
        title: '请先输入API密钥',
        icon: 'none'
      });
      return;
    }

    this.setData({ testing: true });

    try {
      const result = await aiUtil.testConnection(
        providers[providerIndex].value,
        apiKey
      );

      if (result.success) {
        wx.showToast({
          title: '连接成功',
          icon: 'success'
        });
      } else {
        wx.showModal({
          title: '连接失败',
          content: result.error || '请检查API密钥是否正确',
          showCancel: false
        });
      }
    } catch (error) {
      wx.showModal({
        title: '连接失败',
        content: error.message || '网络错误，请稍后重试',
        showCancel: false
      });
    } finally {
      this.setData({ testing: false });
    }
  },

  // 保存设置
  saveSettings() {
    const { apiKey, providerIndex, providers, templateId } = this.data;

    if (apiKey) {
      // 保存AI配置
      aiUtil.setAIConfig(providers[providerIndex].value, apiKey);
      wx.setStorageSync('ai_config', app.globalData.aiConfig);
    }

    if (templateId) {
      // 保存模板ID
      app.globalData.templateId = templateId;
      wx.setStorageSync('template_id', templateId);
    }

    wx.showToast({
      title: '保存成功',
      icon: 'success'
    });

    setTimeout(() => {
      wx.navigateBack();
    }, 1000);
  }
});
