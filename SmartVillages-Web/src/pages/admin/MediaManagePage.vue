<script setup>
import { computed, onMounted, reactive, ref } from "vue";

import {
  auditMedia,
  deleteMedia,
  fetchAuditedMediaPage,
  fetchMediaPage,
  fetchPendingMediaPage,
  updateMediaStatus,
  uploadMedia,
} from "@/services/media.api";
import { formatDate } from "@/shared/utils/format";

const STATUS_MAP = { 0: "待审核", 1: "已通过", 2: "已拒绝", 3: "已下架" };
const FILETYPE_MAP = { image: "图片", video: "视频", document: "文档" };
const CATEGORY_MAP = { banner: "轮播图", announcement: "公告配图", feature: "风采配图", other: "其他" };

function statusClass(s) {
  if (s === 0) return "sv-tag--pending";
  if (s === 1) return "sv-tag--approved";
  if (s === 2) return "sv-tag--rejected";
  return "sv-tag--closed";
}

const tab = ref("mine");
const rows = ref([]);
const loading = ref(false);
const current = ref(1);
const pages = ref(1);
const size = 10;

const uploadForm = reactive({
  file: null,
  fileName: "",
  fileType: "image",
  category: "feature",
});
const uploading = ref(false);
const uploadError = ref("");
const fileInput = ref(null);

const dateColumnLabel = computed(() => {
  if (tab.value === "audited") return "审核时间";
  return "上传时间";
});

const selectedFileMeta = computed(() => {
  if (!uploadForm.file) return "支持图片、视频、PDF、Word 和 Excel 文件";
  const size = uploadForm.file.size || 0;
  const mb = size / 1024 / 1024;
  const readableSize = mb >= 1 ? `${mb.toFixed(1)} MB` : `${Math.max(1, Math.round(size / 1024))} KB`;
  return `${FILETYPE_MAP[uploadForm.fileType] ?? uploadForm.fileType} · ${readableSize}`;
});

function switchTab(t) {
  tab.value = t;
  current.value = 1;
  load(1);
}

async function load(page = 1) {
  loading.value = true;
  try {
    const params = { current: page, size };
    let data;
    if (tab.value === "pending") {
      data = await fetchPendingMediaPage(params);
    } else if (tab.value === "audited") {
      data = await fetchAuditedMediaPage(params);
    } else {
      data = await fetchMediaPage(params);
    }
    rows.value = data.records ?? [];
    current.value = Number(data.current) || page;
    pages.value = Number(data.pages) || 1;
  } catch {
    rows.value = [];
  } finally {
    loading.value = false;
  }
}

function handleFileChange(event) {
  const file = event.target.files?.[0] ?? null;
  uploadForm.file = file;
  uploadForm.fileName = file?.name ?? "";

  if (file?.type?.startsWith("video/")) {
    uploadForm.fileType = "video";
  } else if (file?.type?.startsWith("image/")) {
    uploadForm.fileType = "image";
  }
}

async function submitUpload() {
  uploadError.value = "";
  if (!uploadForm.file) {
    uploadError.value = "请选择要上传的文件";
    return;
  }

  const formData = new FormData();
  formData.append("file", uploadForm.file);
  formData.append("fileType", uploadForm.fileType);
  formData.append("category", uploadForm.category);

  uploading.value = true;
  try {
    await uploadMedia(formData);
    uploadForm.file = null;
    uploadForm.fileName = "";
    if (fileInput.value) {
      fileInput.value.value = "";
    }
    tab.value = "mine";
    await load(1);
  } catch (e) {
    uploadError.value = e?.message || "上传失败，请稍后重试";
  } finally {
    uploading.value = false;
  }
}

async function approve(r) {
  if (!confirm(`确认通过「${r.fileName}」？`)) return;
  try {
    await auditMedia(r.id, 1);
    load(current.value);
  } catch {
    alert("操作失败");
  }
}

async function reject(r) {
  if (!confirm(`确认拒绝「${r.fileName}」？`)) return;
  try {
    await auditMedia(r.id, 2);
    load(current.value);
  } catch {
    alert("操作失败");
  }
}

async function toggleOffline(r) {
  const next = r.status === 3 ? 1 : 3;
  try {
    await updateMediaStatus(r.id, next);
    load(current.value);
  } catch {
    alert("操作失败");
  }
}

async function removeMedia(r) {
  if (!confirm(`确认删除「${r.fileName}」？`)) return;
  try {
    await deleteMedia(r.id);
    load(current.value);
  } catch {
    alert("删除失败");
  }
}

onMounted(() => load(1));
</script>

<template>
  <div class="media-page">
    <div class="sv-manager-head">
      <div>
        <h2>媒体管理</h2>
        <p>上传图片、视频或文档，并处理待审核和已审核媒体。</p>
      </div>
      <span class="sv-tag sv-tag--processing">上传后进入审核流程</span>
    </div>

    <section class="upload-panel">
      <div class="upload-copy">
        <p>上传媒体</p>
        <h3>图片、视频或文档</h3>
        <span>适用于乡村风采、公告配图和门户轮播等内容。</span>
      </div>

      <form class="upload-form" @submit.prevent="submitUpload">
        <label class="file-picker">
          <input ref="fileInput" type="file" accept="image/*,video/*,.pdf,.doc,.docx,.xls,.xlsx" @change="handleFileChange" />
          <strong>{{ uploadForm.fileName || "选择文件" }}</strong>
          <small>{{ selectedFileMeta }}</small>
        </label>

        <select v-model="uploadForm.fileType" class="sv-select">
          <option value="image">图片</option>
          <option value="video">视频</option>
          <option value="document">文档</option>
        </select>

        <select v-model="uploadForm.category" class="sv-select">
          <option value="feature">风采配图</option>
          <option value="announcement">公告配图</option>
          <option value="banner">轮播图</option>
          <option value="other">其他</option>
        </select>

        <button class="sv-btn sv-btn--primary" :disabled="uploading">
          {{ uploading ? "上传中..." : "上传媒体" }}
        </button>
      </form>

      <p v-if="uploadError" class="upload-error">{{ uploadError }}</p>
    </section>

    <div class="sv-filter-bar">
      <div class="tabs">
        <button :class="{ active: tab === 'mine' }" @click="switchTab('mine')">我的上传</button>
        <button :class="{ active: tab === 'pending' }" @click="switchTab('pending')">待审核</button>
        <button :class="{ active: tab === 'audited' }" @click="switchTab('audited')">已审核</button>
      </div>
    </div>

    <div v-if="loading" class="sv-loading-inline"><span class="sv-spinner" /> 加载中...</div>

    <div v-else class="sv-card sv-table-card">
      <div class="sv-table-wrap">
        <table class="sv-table">
          <thead>
            <tr>
              <th class="col-num">ID</th>
              <th>文件名</th>
              <th>类型</th>
              <th>分类</th>
              <th>状态</th>
              <th>{{ dateColumnLabel }}</th>
              <th class="col-actions">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="r in rows" :key="r.id">
              <td class="col-num">{{ r.id }}</td>
              <td class="file-cell" :title="r.fileUrl">{{ r.fileName }}</td>
              <td>{{ FILETYPE_MAP[r.fileType] ?? r.fileType }}</td>
              <td>{{ CATEGORY_MAP[r.category] ?? r.category }}</td>
              <td><span class="sv-tag" :class="statusClass(r.status)">{{ STATUS_MAP[r.status] ?? r.status }}</span></td>
              <td>{{ formatDate(tab === 'audited' ? r.auditTime : r.createTime) }}</td>
              <td class="col-actions">
                <template v-if="tab === 'pending'">
                  <button class="sv-btn sv-btn--primary sv-btn--sm" @click="approve(r)">通过</button>
                  <button class="sv-btn sv-btn--danger sv-btn--sm" @click="reject(r)">拒绝</button>
                </template>
                <template v-else>
                  <button v-if="r.status === 1" class="sv-btn sv-btn--ghost sv-btn--sm" @click="toggleOffline(r)">下架</button>
                  <button v-else-if="r.status === 3" class="sv-btn sv-btn--ghost sv-btn--sm" @click="toggleOffline(r)">启用</button>
                  <button v-if="tab === 'mine'" class="sv-btn sv-btn--danger sv-btn--sm" @click="removeMedia(r)">删除</button>
                </template>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-if="!rows.length" class="sv-empty">
        {{ tab === 'mine' ? '暂无上传文件' : tab === 'pending' ? '暂无待审核文件' : '暂无已审核文件' }}
      </div>
    </div>

    <div class="sv-pager" v-if="pages > 1">
      <button :disabled="current <= 1" @click="load(current - 1)">上一页</button>
      <span class="sv-pager-current">{{ current }}</span>
      <span class="sv-pager-total">/ {{ pages }}</span>
      <button :disabled="current >= pages" @click="load(current + 1)">下一页</button>
    </div>
  </div>
</template>

<style scoped>
.media-page {
  display: grid;
  gap: 18px;
}

.upload-panel {
  position: relative;
  display: grid;
  grid-template-columns: minmax(220px, 0.45fr) minmax(0, 1fr);
  gap: 18px;
  padding: 22px;
  border-radius: 8px;
  border: 1px solid var(--border-color);
  background:
    linear-gradient(135deg, rgba(233, 241, 235, 0.9), rgba(255, 255, 255, 0.96)),
    #fff;
  box-shadow: var(--shadow-card);
}

.upload-copy p {
  margin: 0 0 2px;
  color: var(--text-placeholder);
  font-size: 12px;
}

.upload-copy h3 {
  margin: 0 0 6px;
  font-size: 20px;
}

.upload-copy span {
  color: var(--text-secondary);
  font-size: 13px;
}

.upload-form {
  display: grid;
  grid-template-columns: minmax(220px, 1fr) 130px 150px auto;
  align-items: center;
  gap: 10px;
}

.file-picker {
  min-height: 58px;
  display: grid;
  align-content: center;
  gap: 2px;
  min-width: 0;
  padding: 9px 12px;
  border: 1px dashed var(--border-strong);
  border-radius: var(--radius-control);
  background: rgba(255, 255, 255, 0.72);
  color: var(--text-secondary);
  cursor: pointer;
}

.file-picker input {
  display: none;
}

.file-picker strong,
.file-picker small {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-picker strong {
  color: var(--text-primary);
  font-size: 13px;
}

.file-picker small {
  color: var(--text-placeholder);
  font-size: 12px;
}

.upload-error {
  grid-column: 2;
  margin: -8px 0 0;
  color: var(--danger);
  font-size: 13px;
}

.tabs {
  display: flex;
  gap: 4px;
  padding: 4px;
  border: 1px solid var(--border-color);
  border-radius: 999px;
  background: #fff;
}

.tabs button {
  min-height: 34px;
  padding: 0 16px;
  border: none;
  border-radius: 999px;
  background: transparent;
  cursor: pointer;
  font-size: 14px;
  font-weight: 700;
  color: var(--text-secondary);
}

.tabs button.active {
  color: var(--forest-900);
  background: var(--field-100);
}

.file-cell {
  max-width: 240px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

@media (max-width: 1060px) {
  .upload-panel,
  .upload-form {
    grid-template-columns: 1fr;
  }

  .upload-error {
    grid-column: 1;
  }
}

@media (max-width: 620px) {
  .tabs {
    width: 100%;
    overflow-x: auto;
  }

  .tabs button {
    flex: 1;
    white-space: nowrap;
  }
}
</style>
