const { api } = window;

console.log('=== app.js loaded ===');
console.log('window.api:', window.api);

// ============================================
// Setup UI event listeners
// ============================================
document.addEventListener('DOMContentLoaded', () => {
  console.log('[DOM] Content loaded');

  // Settings button - MOST IMPORTANT
  const btnSettings = document.getElementById('btnSettings');
  console.log('[DOM] btnSettings element:', btnSettings);
  if (btnSettings) {
    btnSettings.addEventListener('click', async (e) => {
      console.log('[CLICK] Settings button clicked!');
      alert('设置按钮被点击了！');

      try {
        console.log('[CLICK] Calling api.openSettings()...');
        const result = await api.openSettings();
        console.log('[CLICK] Settings result:', result);
        alert('设置窗口已打开');
      } catch (err) {
        console.error('[CLICK] Settings error:', err);
        alert('打开设置失败: ' + err.message);
      }
    });
    console.log('[DOM] Settings button listener attached ✓');
  } else {
    console.error('[DOM] ERROR: Settings button not found!');
  }

  // Refresh button
  const btnRefresh = document.getElementById('btnRefresh');
  if (btnRefresh) {
    btnRefresh.addEventListener('click', () => loadMemos());
    console.log('[DOM] Refresh button listener attached ✓');
  }

  // Add button
  const btnAdd = document.getElementById('btnAdd');
  const inputTitle = document.getElementById('inputTitle');
  if (btnAdd && inputTitle) {
    async function addMemo() {
      const title = inputTitle.value.trim();
      if (!title) return;
      const priority = document.getElementById('inputPriority').value;
      inputTitle.value = '';
      await api.memo.create({ title, content: '', priority });
      await loadMemos();
    }
    btnAdd.addEventListener('click', addMemo);
    inputTitle.addEventListener('keydown', (e) => {
      if (e.key === 'Enter') addMemo();
    });
    console.log('[DOM] Add memo listeners attached ✓');
  }

  console.log('[DOM] All event listeners attached');
});

console.log('=== app.js script execution complete ===');

// ============================================
// Helper functions
// ============================================
function escHtml(str) {
  return String(str).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;').replace(/'/g,'&#39;');
}

function formatTime(dt) {
  if (!dt) return '';
  const d = new Date(dt);
  const pad = n => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
}

// ============================================
// Memo operations
// ============================================
async function loadMemos() {
  try {
    console.log('[DATA] Loading memos...');
    const memos = await api.memo.list();
    const list = document.getElementById('memoList');
    const count = document.getElementById('memoCount');
    if (!list || !count) return;

    count.textContent = `${memos.length} 条备忘`;

    if (memos.length === 0) {
      list.innerHTML = '<div class="empty-state">暂无备忘，输入标题快速添加</div>';
      return;
    }

    list.innerHTML = '';
    for (const m of memos) {
      const li = document.createElement('li');
      li.className = 'memo-item' + (m.is_done ? ' done' : '');
      li.dataset.id = m.id;

      const time = formatTime(m.created_at);
      const reminders = await api.reminder.list(m.id);
      let reminderHtml = '';
      if (reminders.length > 0) {
        reminderHtml = '<div class="memo-reminders">' + reminders.map(r => {
          const repeatLabel = { none: '单次', daily: '每天', weekly: '每周', monthly: '每月' }[r.repeat_type] || '单次';
          return `<div class="memo-reminder-item">
            <span>⏰ ${formatTime(r.remind_at)} <span class="repeat-tag">${repeatLabel}</span></span>
            <button onclick="window.deleteReminder(${r.id})" title="删除提醒">✕</button>
          </div>`;
        }).join('') + '</div>';
      }

      li.innerHTML = `
        <div class="memo-item-header">
          <span class="memo-title">${escHtml(m.title)}</span>
          <span class="memo-priority ${m.priority}">${{low:'低',medium:'中',high:'高'}[m.priority]}</span>
          <div class="memo-actions">
            <button class="btn-done" onclick="window.toggleDone(${m.id}, ${m.is_done})">${m.is_done ? '恢复' : '完成'}</button>
            <button class="btn-delete" onclick="window.deleteMemo(${m.id})">删除</button>
          </div>
        </div>
        ${m.content ? `<div class="memo-content">${escHtml(m.content)}</div>` : ''}
        <div class="memo-time">创建于 ${time}</div>
        ${reminderHtml}
      `;
      list.appendChild(li);
    }
    console.log('[DATA] Memos loaded:', memos.length);
  } catch (err) {
    console.error('[DATA] Error loading memos:', err);
  }
}

// Global functions for inline onclick
window.deleteMemo = async (id) => {
  if (confirm('确定删除这条备忘？')) {
    await api.memo.delete(id);
    await loadMemos();
  }
};

window.toggleDone = async (id, isDone) => {
  await api.memo.update(id, { is_done: isDone ? 0 : 1 });
  await loadMemos();
};

window.deleteReminder = async (id) => {
  await api.reminder.delete(id);
  await loadMemos();
};

// ============================================
// Initialize app
// ============================================
(async () => {
  try {
    console.log('[INIT] Initializing database...');
    const initRes = await api.db.init();
    console.log('[INIT] DB init result:', initRes);
    if (!initRes.success) {
      console.warn('[INIT] DB init failed (may need configuration):', initRes.error);
    }
    console.log('[INIT] Loading initial memos...');
    await loadMemos();
    console.log('[INIT] App ready');
  } catch (err) {
    console.error('[INIT] Init error:', err);
  }
})();
