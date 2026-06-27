const { contextBridge, ipcRenderer } = require('electron');

contextBridge.exposeInMainWorld('api', {
  db: {
    getConfig: () => ipcRenderer.invoke('db:getConfig'),
    saveConfig: (config) => ipcRenderer.invoke('db:saveConfig', config),
    testConnection: (config) => ipcRenderer.invoke('db:testConnection', config),
    init: () => ipcRenderer.invoke('db:init')
  },
  memo: {
    list: () => ipcRenderer.invoke('memo:list'),
    create: (data) => ipcRenderer.invoke('memo:create', data),
    update: (id, fields) => ipcRenderer.invoke('memo:update', id, fields),
    delete: (id) => ipcRenderer.invoke('memo:delete', id)
  },
  reminder: {
    list: (memoId) => ipcRenderer.invoke('reminder:list', memoId),
    create: (data) => ipcRenderer.invoke('reminder:create', data),
    delete: (id) => ipcRenderer.invoke('reminder:delete', id)
  },
  openSettings: () => ipcRenderer.invoke('window:openSettings')
});
