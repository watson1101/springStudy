const cron = require('node-cron');
const { Notification } = require('electron');
const db = require('./db');

let task = null;

function getNextRemindAt(reminder) {
  const d = new Date(reminder.remind_at);
  const interval = reminder.repeat_interval || 1;
  switch (reminder.repeat_type) {
    case 'daily':
      d.setDate(d.getDate() + interval);
      return d;
    case 'weekly':
      d.setDate(d.getDate() + 7 * interval);
      return d;
    case 'monthly':
      d.setMonth(d.getMonth() + interval);
      return d;
    default:
      return null;
  }
}

function formatDateTime(dt) {
  const d = new Date(dt);
  const pad = n => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
}

async function checkReminders(userDataPath, mainWindow) {
  try {
    const due = await db.getDueReminders(userDataPath);
    for (const r of due) {
      const next = getNextRemindAt(r);

      const notification = new Notification({
        title: `提醒: ${r.memo_title}`,
        body: r.memo_content || '时间到了！',
        silent: false
      });
      notification.show();
      notification.on('click', () => {
        if (mainWindow) {
          mainWindow.show();
          mainWindow.focus();
        }
      });

      await db.markTriggered(userDataPath, r.id, next);
    }
  } catch (err) {
    console.error('Reminder check failed:', err.message);
  }
}

function start(userDataPath, mainWindow) {
  if (task) task.stop();
  task = cron.schedule('* * * * *', () => {
    checkReminders(userDataPath, mainWindow);
  });
}

function stop() {
  if (task) {
    task.stop();
    task = null;
  }
}

module.exports = { start, stop };
