// detail.js
const app = getApp();

Page({
  data: {
    memoId: '',
    memo: {},
    hasRemind: false,
    timeRange: [],
    timeRangeKey: 'label',
    timeSelectorValue: [0, 0],
    selectedTimeStr: '',
    tempTitle: '',
    tempContent: ''
  },

  onLoad(options) {
    const memoId = options.id;
    this.setData({ memoId });
    this.loadMemo(memoId);
    this.initTimeRange();
  },

  // 加载备忘详情
  loadMemo(id) {
    const memos = app.getStoredMemos();
    const memo = memos.find(m => m.id === id);

    if (memo) {
      this.setData({
        memo: memo,
        tempTitle: memo.title || '',
        tempContent: memo.content || '',
        hasRemind: !!memo.remindTime,
        selectedTimeStr: memo.remindTimeStr || ''
      });
    } else {
      wx.showToast({
        title: '备忘不存在',
        icon: 'none'
      });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    }
  },

  // 初始化时间选择器数据
  initTimeRange() {
    const dates = [];
    const times = [];
    const now = new Date();

    // 生成未来7天的日期
    for (let i = 0; i < 7; i++) {
      const date = new Date(now);
      date.setDate(date.getDate() + i);
      const label = i === 0 ? '今天' : i === 1 ? '明天' : `${date.getMonth() + 1}月${date.getDate()}日`;
      dates.push({
        label: label,
        value: date.toISOString()
      });
    }

    // 生成时间点（每半小时）
    for (let h = 0; h < 24; h++) {
      for (let m = 0; m < 60; m += 30) {
        const timeLabel = `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}`;
        times.push({
          label: timeLabel,
          hour: h,
          minute: m
        });
      }
    }

    this.setData({
      timeRange: [dates, times]
    });
  },

  // 标题变更
  onTitleChange(e) {
    this.setData({
      tempTitle: e.detail.value
    });
  },

  // 内容变更
  onContentChange(e) {
    this.setData({
      tempContent: e.detail.value
    });
  },

  // 提醒开关
  onRemindSwitch(e) {
    this.setData({
      hasRemind: e.detail.value
    });
  },

  // 时间选择
  onTimeChange(e) {
    const value = e.detail.value;
    const dates = this.data.timeRange[0];
    const times = this.data.timeRange[1];

    const selectedDate = new Date(dates[value[0]].value);
    const selectedTime = times[value[1]];

    selectedDate.setHours(selectedTime.hour, selectedTime.minute, 0, 0);

    const timeStr = `${dates[value[0]].label} ${selectedTime.label}`;

    this.setData({
      timeSelectorValue: value,
      selectedTimeStr: timeStr,
      'memo.remindTime': selectedDate.getTime(),
      'memo.remindTimeStr': timeStr
    });
  },

  // 快速选择时间
  quickSelect(e) {
    const now = new Date();
    let remindTime;

    if (e.currentTarget.dataset.type === 'tomorrow') {
      remindTime = new Date(now);
      remindTime.setDate(remindTime.getDate() + 1);
      remindTime.setHours(9, 0, 0, 0);
      this.setData({
        selectedTimeStr: '明天 09:00'
      });
    } else {
      const minutes = parseInt(e.currentTarget.dataset.minutes);
      remindTime = new Date(now.getTime() + minutes * 60 * 1000);
      this.setData({
        selectedTimeStr: `${minutes}分钟后`
      });
    }

    this.setData({
      'memo.remindTime': remindTime.getTime(),
      'memo.remindTimeStr': this.formatDateTime(remindTime)
    });
  },

  // 格式化日期时间
  formatDateTime(date) {
    const month = date.getMonth() + 1;
    const day = date.getDate();
    const hour = String(date.getHours()).padStart(2, '0');
    const minute = String(date.getMinutes()).padStart(2, '0');
    return `${month}月${day}日 ${hour}:${minute}`;
  },

  // 预览图片
  previewImage(e) {
    const url = e.currentTarget.dataset.url;
    wx.previewImage({
      current: url,
      urls: this.data.memo.images
    });
  },

  // 保存备忘
  saveMemo() {
    const { memo, tempTitle, tempContent, hasRemind } = this.data;

    // 更新备忘
    memo.title = tempTitle || memo.title;
    memo.content = tempContent || memo.content;

    if (!hasRemind) {
      memo.remindTime = null;
      memo.remindTimeStr = '';
    }

    // 保存到存储
    const memos = app.getStoredMemos();
    const index = memos.findIndex(m => m.id === memo.id);
    if (index !== -1) {
      memos[index] = memo;
      app.saveMemos(memos);
    }

    wx.showToast({
      title: '保存成功',
      icon: 'success'
    });

    setTimeout(() => {
      wx.navigateBack();
    }, 1000);
  },

  // 删除备忘
  deleteMemo() {
    wx.showModal({
      title: '确认删除',
      content: '确定要删除这条备忘吗？',
      success: (res) => {
        if (res.confirm) {
          const memos = app.getStoredMemos();
          const filtered = memos.filter(m => m.id !== this.data.memoId);
          app.saveMemos(filtered);

          wx.showToast({
            title: '已删除',
            icon: 'success'
          });

          setTimeout(() => {
            wx.navigateBack();
          }, 1000);
        }
      }
    });
  },

  // 订阅消息
  subscribeMessage() {
    const templateId = app.globalData.templateId;

    if (!templateId) {
      wx.showModal({
        title: '提示',
        content: '请先在设置中配置订阅消息模板ID',
        showCancel: false
      });
      return;
    }

    wx.requestSubscribeMessage({
      tmplIds: [templateId],
      success: (res) => {
        if (res[templateId] === 'accept') {
          const memos = app.getStoredMemos();
          const memo = memos.find(m => m.id === this.data.memoId);
          if (memo) {
            memo.subscribed = true;
            app.saveMemos(memos);
            this.setData({ 'memo.subscribed': true });
          }

          wx.showToast({
            title: '订阅成功',
            icon: 'success'
          });
        }
      },
      fail: (err) => {
        console.error('订阅失败:', err);
        wx.showToast({
          title: '订阅失败',
          icon: 'none'
        });
      }
    });
  }
});
