import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

const API_TARGET = process.env.VITE_DEV_PROXY_TARGET || 'http://127.0.0.1:8090'

/** 仅在 Docker/NFS 等无法可靠使用原生监听的环境设为 1，默认关闭以降低本机 CPU 占用 */
const useWatchPolling = process.env.VITE_USE_POLLING === '1'

/**
 * 开发代理与 Vue Router 共用同一路径前缀时（如 /announcements、/admin），
 * 整页刷新会带 Accept: text/html，若仍走代理会得到 JSON。文档请求回落到 SPA。
 */
function apiProxy() {
  return {
    target: API_TARGET,
    changeOrigin: true,
    bypass(req) {
      const accept = req.headers.accept || ''
      if (accept.includes('text/html')) {
        return '/index.html'
      }
    },
  }
}

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    // 开发工具会增加启动与运行时开销；不需要时可设 VITE_ENABLE_VUE_DEVTOOLS=0
    ...(process.env.VITE_ENABLE_VUE_DEVTOOLS === '0' ? [] : [vueDevTools()]),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    host: true,
    watch: useWatchPolling
      ? {
          usePolling: true,
          interval: 300,
        }
      : undefined,
    proxy: {
      '/api': apiProxy(),
      '/auth': apiProxy(),
      '/admin': apiProxy(),
      '/cadre': apiProxy(),
      '/villager': apiProxy(),
      '/interactions': apiProxy(),
      '/media': apiProxy(),
      '/affairs': apiProxy(),
      '/announcements': apiProxy(),
      '/features': apiProxy(),
      '/guest': apiProxy(),
      '/public': apiProxy(),
      '/v3': apiProxy(),
    },
  },
})
