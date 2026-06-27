const mysql = require('mysql2/promise');
const path = require('path');
const fs = require('fs');

let pool = null;
let configPath = '';

function getConfigPath(userDataPath) {
  configPath = path.join(userDataPath, 'config.json');
  return configPath;
}

function loadConfig(userDataPath) {
  const cfgPath = getConfigPath(userDataPath);
  if (fs.existsSync(cfgPath)) {
    return JSON.parse(fs.readFileSync(cfgPath, 'utf-8'));
  }
  return {
    host: 'localhost',
    port: 3306,
    user: 'root',
    password: '123456',
    database: 'memo_db'
  };
}

function saveConfig(config, userDataPath) {
  const cfgPath = getConfigPath(userDataPath);
  fs.writeFileSync(cfgPath, JSON.stringify(config, null, 2), 'utf-8');
}

async function getPool(userDataPath) {
  if (pool) return pool;
  const config = loadConfig(userDataPath);
  pool = mysql.createPool({
    host: config.host,
    port: config.port,
    user: config.user,
    password: config.password,
    database: config.database,
    waitForConnections: true,
    connectionLimit: 5,
    queueLimit: 0
  });
  return pool;
}

function resetPool() {
  if (pool) {
    pool.end().catch(() => {});
    pool = null;
  }
}

async function testConnection(config) {
  const conn = await mysql.createConnection({
    host: config.host,
    port: config.port,
    user: config.user,
    password: config.password
  });
  await conn.execute(`CREATE DATABASE IF NOT EXISTS \`${config.database}\``);
  await conn.end();
  return true;
}

async function initTables(userDataPath) {
  const p = await getPool(userDataPath);
  await p.execute(`
    CREATE TABLE IF NOT EXISTS memos (
      id INT AUTO_INCREMENT PRIMARY KEY,
      title VARCHAR(255) NOT NULL,
      content TEXT,
      priority ENUM('low','medium','high') DEFAULT 'medium',
      created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
      updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
      is_done TINYINT(1) DEFAULT 0
    )
  `);
  await p.execute(`
    CREATE TABLE IF NOT EXISTS reminders (
      id INT AUTO_INCREMENT PRIMARY KEY,
      memo_id INT,
      remind_at DATETIME NOT NULL,
      repeat_type ENUM('none','daily','weekly','monthly') DEFAULT 'none',
      repeat_interval INT DEFAULT 1,
      is_active TINYINT(1) DEFAULT 1,
      last_triggered DATETIME,
      FOREIGN KEY (memo_id) REFERENCES memos(id) ON DELETE CASCADE
    )
  `);
}

// Memo CRUD
async function listMemos(userDataPath) {
  const p = await getPool(userDataPath);
  const [rows] = await p.execute('SELECT * FROM memos ORDER BY created_at DESC');
  return rows;
}

async function createMemo(userDataPath, { title, content, priority }) {
  const p = await getPool(userDataPath);
  const [result] = await p.execute(
    'INSERT INTO memos (title, content, priority) VALUES (?, ?, ?)',
    [title, content || '', priority || 'medium']
  );
  return { id: result.insertId, title, content, priority };
}

async function updateMemo(userDataPath, id, fields) {
  const p = await getPool(userDataPath);
  const sets = [];
  const vals = [];
  if (fields.title !== undefined) { sets.push('title = ?'); vals.push(fields.title); }
  if (fields.content !== undefined) { sets.push('content = ?'); vals.push(fields.content); }
  if (fields.priority !== undefined) { sets.push('priority = ?'); vals.push(fields.priority); }
  if (fields.is_done !== undefined) { sets.push('is_done = ?'); vals.push(fields.is_done); }
  vals.push(id);
  await p.execute(`UPDATE memos SET ${sets.join(', ')} WHERE id = ?`, vals);
}

async function deleteMemo(userDataPath, id) {
  const p = await getPool(userDataPath);
  await p.execute('DELETE FROM memos WHERE id = ?', [id]);
}

// Reminder CRUD
async function listReminders(userDataPath, memoId) {
  const p = await getPool(userDataPath);
  let rows;
  if (memoId) {
    [rows] = await p.execute(
      'SELECT r.*, m.title as memo_title FROM reminders r JOIN memos m ON r.memo_id = m.id WHERE r.memo_id = ? ORDER BY r.remind_at',
      [memoId]
    );
  } else {
    [rows] = await p.execute(
      'SELECT r.*, m.title as memo_title FROM reminders r JOIN memos m ON r.memo_id = m.id WHERE r.is_active = 1 ORDER BY r.remind_at'
    );
  }
  return rows;
}

async function createReminder(userDataPath, { memo_id, remind_at, repeat_type, repeat_interval }) {
  const p = await getPool(userDataPath);
  const [result] = await p.execute(
    'INSERT INTO reminders (memo_id, remind_at, repeat_type, repeat_interval) VALUES (?, ?, ?, ?)',
    [memo_id, remind_at, repeat_type || 'none', repeat_interval || 1]
  );
  return { id: result.insertId };
}

async function deleteReminder(userDataPath, id) {
  const p = await getPool(userDataPath);
  await p.execute('DELETE FROM reminders WHERE id = ?', [id]);
}

async function getDueReminders(userDataPath) {
  const p = await getPool(userDataPath);
  const [rows] = await p.execute(
    `SELECT r.*, m.title as memo_title, m.content as memo_content
     FROM reminders r JOIN memos m ON r.memo_id = m.id
     WHERE r.is_active = 1 AND r.remind_at <= NOW()`
  );
  return rows;
}

async function markTriggered(userDataPath, reminderId, nextRemindAt) {
  const p = await getPool(userDataPath);
  if (nextRemindAt) {
    await p.execute(
      'UPDATE reminders SET last_triggered = NOW(), remind_at = ? WHERE id = ?',
      [nextRemindAt, reminderId]
    );
  } else {
    await p.execute(
      'UPDATE reminders SET last_triggered = NOW(), is_active = 0 WHERE id = ?',
      [reminderId]
    );
  }
}

module.exports = {
  loadConfig, saveConfig, testConnection, getPool, resetPool,
  initTables, listMemos, createMemo, updateMemo, deleteMemo,
  listReminders, createReminder, deleteReminder,
  getDueReminders, markTriggered
};
