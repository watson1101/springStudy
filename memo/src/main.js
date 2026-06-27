const { app, BrowserWindow, ipcMain, Notification } = require('electron');
const path = require('path');
const db = require('./db');
const scheduler = require('./scheduler');

let mainWindow;
let userDataPath;

function createWindow() {
  mainWindow = new BrowserWindow({
    width: 900,
    height: 700,
    minWidth: 700,
    minHeight: 500,
    webPreferences: {
      preload: path.join(__dirname, 'preload.js'),
      contextIsolation: true,
      nodeIntegration: false
    },
    title: 'MemoApp - 备忘录',
    show: false
  });

  mainWindow.loadFile(path.join(__dirname, 'renderer', 'index.html'));
  mainWindow.once('ready-to-show', () => {
    mainWindow.show();
    mainWindow.webContents.openDevTools(); // Debug mode
  });
}

app.whenReady().then(async () => {
  userDataPath = app.getPath('userData');
  createWindow();
  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) createWindow();
  });
});

app.on('window-all-closed', () => {
  scheduler.stop();
  if (process.platform !== 'darwin') app.quit();
});

// --- IPC Handlers ---

// DB Config
ipcMain.handle('db:getConfig', () => {
  return db.loadConfig(userDataPath);
});

ipcMain.handle('db:saveConfig', async (_event, config) => {
  db.saveConfig(config, userDataPath);
  db.resetPool();
  return { success: true };
});

ipcMain.handle('db:testConnection', async (_event, config) => {
  try {
    await db.testConnection(config);
    return { success: true };
  } catch (err) {
    return { success: false, error: err.message };
  }
});

ipcMain.handle('db:init', async () => {
  try {
    await db.initTables(userDataPath);
    scheduler.start(userDataPath, mainWindow);
    return { success: true };
  } catch (err) {
    return { success: false, error: err.message };
  }
});

// Memos
ipcMain.handle('memo:list', async () => {
  return await db.listMemos(userDataPath);
});

ipcMain.handle('memo:create', async (_event, data) => {
  return await db.createMemo(userDataPath, data);
});

ipcMain.handle('memo:update', async (_event, id, fields) => {
  await db.updateMemo(userDataPath, id, fields);
});

ipcMain.handle('memo:delete', async (_event, id) => {
  await db.deleteMemo(userDataPath, id);
});

// Reminders
ipcMain.handle('reminder:list', async (_event, memoId) => {
  return await db.listReminders(userDataPath, memoId);
});

ipcMain.handle('reminder:create', async (_event, data) => {
  return await db.createReminder(userDataPath, data);
});

ipcMain.handle('reminder:delete', async (_event, id) => {
  await db.deleteReminder(userDataPath, id);
});

// Window control
ipcMain.handle('window:openSettings', async () => {
  console.log('[Main] Opening settings window...');
  try {
    const settingsWin = new BrowserWindow({
      width: 550,
      height: 480,
      webPreferences: {
        preload: path.join(__dirname, 'preload.js'),
        contextIsolation: true,
        nodeIntegration: false
      },
      title: '设置 - MemoApp',
      resizable: false,
      alwaysOnTop: true
    });

    settingsWin.loadFile(path.join(__dirname, 'renderer', 'settings.html'));
    console.log('[Main] Settings window created successfully');
    return { success: true };
  } catch (err) {
    console.error('[Main] Error creating settings window:', err);
    return { success: false, error: err.message };
  }
});
