const { contextBridge, ipcRenderer } = require('electron')

contextBridge.exposeInMainWorld('electronAPI', {
  getAppVersion: () => ipcRenderer.invoke('get-app-version'),
  selectImage: () => ipcRenderer.invoke('select-image'),
  platform: process.platform
})
