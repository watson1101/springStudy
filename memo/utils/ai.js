// utils/ai.js - 大模型API调用工具

const app = getApp();

/**
 * 调用大模型分析文本
 * @param {String} text - 待分析的文本
 * @returns {Object} 分析结果
 */
async function analyzeText(text) {
  if (!text || text.trim() === '') {
    return {
      title: '',
      remindTime: null,
      remindTimeStr: '',
      category: '其他'
    };
  }

  const config = app.globalData.aiConfig;

  // 检查是否配置了API密钥
  if (!config.apiKey) {
    console.warn('未配置AI密钥，使用本地规则解析');
    return localParse(text);
  }

  try {
    // 构建提示词
    const prompt = buildPrompt(text);

    // 调用API
    const result = await callAI(prompt, config);

    // 解析结果
    return parseAIResult(result, text);

  } catch (error) {
    console.error('AI调用失败:', error);
    // 失败时使用本地解析
    return localParse(text);
  }
}

/**
 * 构建AI提示词
 */
function buildPrompt(text) {
  return `请分析以下备忘内容，提取关键信息并以JSON格式返回。

分析要求：
1. 提取简短标题（不超过15字）
2. 识别是否有提醒时间，包括：
   - 明确时间（如：明天下午3点、下周二上午10点）
   - 相对时间（如：2小时后、30分钟后）
   - 日期（如：5月20日、下周三）
3. 如果没有时间相关内容，remindTime设为null
4. 识别分类（工作/生活/学习/其他）

当前时间：${new Date().toLocaleString('zh-CN')}

备忘内容：${text}

请直接返回JSON，格式如下：
{
  "title": "标题",
  "remindTime": "2024-05-20 15:00:00 或 null",
  "remindTimeStr": "明天下午3点 或 ''",
  "category": "工作/生活/学习/其他"
}`;
}

/**
 * 调用大模型API
 */
async function callAI(prompt, config) {
  const apiUrl = config.apiUrl[config.provider] || config.apiUrl.kimi;

  const headers = {
    'Content-Type': 'application/json'
  };

  // 根据不同提供商设置认证头
  switch (config.provider) {
    case 'kimi':
      headers['Authorization'] = `Bearer ${config.apiKey}`;
      break;
    case 'deepseek':
      headers['Authorization'] = `Bearer ${config.apiKey}`;
      break;
    case 'glm':
      headers['Authorization'] = `Bearer ${config.apiKey}`;
      break;
  }

  const requestBody = {
    model: getModelName(config.provider),
    messages: [
      {
        role: 'user',
        content: prompt
      }
    ],
    temperature: 0.3,
    max_tokens: 500
  };

  const response = await wx.request({
    url: apiUrl,
    method: 'POST',
    header: headers,
    data: requestBody,
    timeout: 30000
  });

  if (response.statusCode === 200) {
    const data = response.data;
    // 根据不同提供商解析响应
    return parseResponse(data, config.provider);
  } else {
    throw new Error(`API调用失败: ${response.statusCode}`);
  }
}

/**
 * 获取模型名称
 */
function getModelName(provider) {
  const models = {
    kimi: 'moonshot-v1-8k',
    deepseek: 'deepseek-chat',
    glm: 'glm-4-flash'
  };
  return models[provider] || models.kimi;
}

/**
 * 解析API响应
 */
function parseResponse(data, provider) {
  try {
    let content = '';

    switch (provider) {
      case 'kimi':
      case 'deepseek':
        content = data.choices[0].message.content;
        break;
      case 'glm':
        content = data.choices[0].message.content;
        break;
      default:
        content = data.choices[0].message.content;
    }

    // 提取JSON内容
    const jsonMatch = content.match(/\{[\s\S]*\}/);
    if (jsonMatch) {
      return JSON.parse(jsonMatch[0]);
    }
    throw new Error('无法解析AI响应');
  } catch (error) {
    console.error('解析响应失败:', error);
    throw error;
  }
}

/**
 * 解析AI结果
 */
function parseAIResult(aiResult, originalText) {
  const result = {
    title: aiResult.title || extractTitle(originalText),
    remindTime: null,
    remindTimeStr: aiResult.remindTimeStr || '',
    category: aiResult.category || '其他'
  };

  // 解析时间
  if (aiResult.remindTime) {
    const remindDate = new Date(aiResult.remindTime);
    if (!isNaN(remindDate.getTime())) {
      result.remindTime = remindDate.getTime();
    }
  }

  return result;
}

/**
 * 本地规则解析（AI失败时的降级方案）
 */
function localParse(text) {
  const result = {
    title: extractTitle(text),
    remindTime: null,
    remindTimeStr: '',
    category: '其他'
  };

  // 简单时间匹配
  const timePatterns = [
    /(\d+)分钟[后之]/,
    /(\d+)小时[后之]/,
    /明天/,
    /后天/,
    /下周一|下周二|下周三|下周四|下周五|下周六|下周日/,
    /(\d{1,2})月(\d{1,2})日/,
    /(\d{1,2})[:：](\d{2})/
  ];

  for (const pattern of timePatterns) {
    const match = text.match(pattern);
    if (match) {
      result.remindTime = parseTimeFromText(text, match);
      result.remindTimeStr = match[0];
      break;
    }
  }

  return result;
}

/**
 * 从文本提取标题
 */
function extractTitle(text) {
  if (!text) return '无标题';

  // 尝试提取第一句话
  const firstSentence = text.split(/[。！？\n]/)[0];
  if (firstSentence && firstSentence.length <= 20) {
    return firstSentence;
  }

  // 取前15个字符
  return text.substring(0, 15) + '...';
}

/**
 * 从文本解析时间
 */
function parseTimeFromText(text, match) {
  const now = new Date();

  // 分钟
  if (text.match(/(\d+)分钟/)) {
    const minutes = parseInt(match[1]);
    return now.getTime() + minutes * 60 * 1000;
  }

  // 小时
  if (text.match(/(\d+)小时/)) {
    const hours = parseInt(match[1]);
    return now.getTime() + hours * 60 * 60 * 1000;
  }

  // 明天
  if (text.includes('明天')) {
    const tomorrow = new Date(now);
    tomorrow.setDate(tomorrow.getDate() + 1);
    tomorrow.setHours(9, 0, 0, 0);
    return tomorrow.getTime();
  }

  // 具体时间
  const timeMatch = text.match(/(\d{1,2})[:：](\d{2})/);
  if (timeMatch) {
    const today = new Date();
    today.setHours(parseInt(timeMatch[1]), parseInt(timeMatch[2]), 0, 0);
    if (today.getTime() > now.getTime()) {
      return today.getTime();
    }
  }

  return null;
}

/**
 * 设置AI配置
 */
function setAIConfig(provider, apiKey) {
  app.globalData.aiConfig.provider = provider;
  app.globalData.aiConfig.apiKey = apiKey;
  wx.setStorageSync('ai_config', app.globalData.aiConfig);
}

/**
 * 获取AI配置
 */
function getAIConfig() {
  const saved = wx.getStorageSync('ai_config');
  if (saved) {
    app.globalData.aiConfig = saved;
  }
  return app.globalData.aiConfig;
}

/**
 * 测试API连接
 */
async function testConnection(provider, apiKey) {
  const testConfig = {
    provider: provider,
    apiKey: apiKey,
    apiUrl: app.globalData.aiConfig.apiUrl
  };

  try {
    const result = await callAI('你好', testConfig);
    return { success: true, result };
  } catch (error) {
    return { success: false, error: error.message };
  }
}

module.exports = {
  analyzeText,
  setAIConfig,
  getAIConfig,
  testConnection,
  localParse
};
