<script setup>
import { onMounted, reactive, ref } from "vue";

import {
  auditCadreAffair,
  createCadreAffair,
  deleteCadreAffair,
  fetchCadreAffairDetail,
  fetchCadreAffairPage,
  updateCadreAffair,
} from "@/services/villageAffair.api";
import { showFlash } from "@/shared/ui/flash";
import { formatDate, textExcerpt } from "@/shared/utils/format";

const rows = ref([]);
const loading = ref(false);
const current = ref(1);
const pages = ref(1);
const size = 10;

const filters = reactive({
  status: "",
  affairType: "",
  title: "",
});

const STATUS_OPTIONS = [
  { label: "全部状态", value: "" },
  { label: "草稿", value: 0 },
  { label: "待审核", value: 1 },
  { label: "已发布", value: 2 },
  { label: "已下架", value: 3 },
];

const AFFAIR_TYPES = [
  { label: "财务公开", value: "FINANCE" },
  { label: "项目公示", value: "PROJECT" },
  { label: "政策事项", value: "POLICY" },
  { label: "其他事项", value: "OTHER" },
];

const showForm = ref(false);
const saving = ref(false);
const formError = ref("");
const editId = ref(null);
const form = reactive({
  affairType: "PROJECT",
  title: "",
  summary: "",
  content: "",
  amount: "",
  attachments: "",
});

const showDetail = ref(false);
const detailLoading = ref(false);
const detailData = ref(null);

const showAudit = ref(false);
const auditing = ref(false);
const auditId = ref(null);
const auditForm = reactive({
  status: 2,
  auditRemark: "",
});

function statusClass(value) {
  if (value === 0) return "sv-tag--draft";
  if (value === 1) return "sv-tag--pending";
  if (value === 2) return "sv-tag--approved";
  return "sv-tag--closed";
}

function statusLabel(value) {
  return (
    {
      0: "草稿",
      1: "待审核",
      2: "已发布",
      3: "已下架",
    }[value] || value
  );
}

function affairTypeLabel(value) {
  return (
    {
      FINANCE: "财务公开",
      PROJECT: "项目公示",
      POLICY: "政策事项",
      OTHER: "其他事项",
    }[value] || value || "-"
  );
}

function resetForm() {
  form.affairType = "PROJECT";
  form.title = "";
  form.summary = "";
  form.content = "";
  form.amount = "";
  form.attachments = "";
}

function sanitizePayload() {
  return {
    affairType: form.affairType,
    title: form.title.trim(),
    summary: form.summary.trim() || undefined,
    content: form.content.trim() || undefined,
    amount: form.amount === "" ? undefined : Number(form.amount),
    attachments: form.attachments.trim() || undefined,
  };
}

async function load(page = 1) {
  loading.value = true;
  try {
    const data = await fetchCadreAffairPage({
      current: page,
      size,
      status: filters.status === "" ? undefined : Number(filters.status),
      affairType: filters.affairType || undefined,
      title: filters.title.trim() || undefined,
    });
    rows.value = data.records ?? [];
    current.value = Number(data.current) || page;
    pages.value = Number(data.pages) || 1;
  } catch {
    rows.value = [];
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  load(1);
}

function handleReset() {
  filters.status = "";
  filters.affairType = "";
  filters.title = "";
  load(1);
}

function openCreate() {
  editId.value = null;
  formError.value = "";
  resetForm();
  showForm.value = true;
}

async function openEdit(row) {
  formError.value = "";
  editId.value = row.id;
  try {
    const detail = await fetchCadreAffairDetail(row.id);
    form.affairType = detail.affairType || "PROJECT";
    form.title = detail.title || "";
    form.summary = detail.summary || "";
    form.content = detail.content || "";
    form.amount = detail.amount ?? "";
    form.attachments = detail.attachments || "";
    showForm.value = true;
  } catch {
    showFlash("加载公示事项详情失败");
  }
}

function closeForm() {
  showForm.value = false;
  editId.value = null;
}

async function submitForm() {
  formError.value = "";
  if (!form.title.trim()) {
    formError.value = "请输入标题";
    return;
  }

  saving.value = true;
  try {
    const nextPage = editId.value ? current.value : 1;
    const payload = sanitizePayload();
    if (editId.value) {
      await updateCadreAffair(editId.value, payload);
      showFlash("公示事项更新成功");
    } else {
      await createCadreAffair(payload);
      showFlash("公示事项创建成功");
    }
    closeForm();
    load(nextPage);
  } catch (error) {
    formError.value = error?.message || "保存失败";
  } finally {
    saving.value = false;
  }
}

async function viewDetail(row) {
  detailLoading.value = true;
  detailData.value = null;
  showDetail.value = true;
  try {
    detailData.value = await fetchCadreAffairDetail(row.id);
  } catch {
    showFlash("加载公示事项详情失败");
    showDetail.value = false;
  } finally {
    detailLoading.value = false;
  }
}

async function removeRow(row) {
  if (!confirm(`确认删除公示事项「${row.title}」？`)) return;
  try {
    await deleteCadreAffair(row.id);
    showFlash("公示事项已删除");
    load(current.value);
  } catch (error) {
    showFlash(error?.message || "删除失败");
  }
}

function openAudit(row, status = 2) {
  auditId.value = row.id;
  auditForm.status = status;
  auditForm.auditRemark = "";
  showAudit.value = true;
}

async function submitAudit() {
  if (!auditId.value) return;
  auditing.value = true;
  try {
    await auditCadreAffair(auditId.value, {
      status: Number(auditForm.status),
      auditRemark: auditForm.auditRemark.trim() || undefined,
    });
    showFlash("公示事项状态已更新");
    showAudit.value = false;
    load(current.value);
  } catch (error) {
    showFlash(error?.message || "状态更新失败");
  } finally {
    auditing.value = false;
  }
}

onMounted(() => load());
</script>

<template>
  <div>
    <div class="sv-manager-head">
      <div>
        <h2>公示事项</h2>
        <p>维护村务公开、公示决议、项目结果和政策事项，形成可追溯的公开记录。</p>
      </div>
      <button class="sv-btn sv-btn--primary" @click="openCreate">新增公示事项</button>
    </div>

    <div class="sv-card filter-card">
      <div class="sv-filter-bar">
        <select v-model="filters.status" class="sv-select filter-select">
          <option v-for="item in STATUS_OPTIONS" :key="String(item.value)" :value="item.value">{{ item.label }}</option>
        </select>
        <select v-model="filters.affairType" class="sv-select filter-select">
          <option value="">全部事项类型</option>
          <option v-for="item in AFFAIR_TYPES" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
        <input v-model="filters.title" class="sv-input filter-input" placeholder="按标题筛选" />
        <button class="sv-btn sv-btn--secondary sv-btn--sm" @click="handleSearch">筛选</button>
        <button class="sv-btn sv-btn--ghost sv-btn--sm" @click="handleReset">重置</button>
      </div>
    </div>

    <div v-if="loading" class="sv-loading-inline"><span class="sv-spinner" /> 加载中...</div>

    <div v-else class="sv-card sv-table-card">
      <div class="sv-table-wrap">
        <table class="sv-table">
          <thead>
            <tr>
              <th class="col-num">ID</th>
              <th>事项类型</th>
              <th>标题</th>
              <th>摘要</th>
              <th>状态</th>
              <th>发布时间</th>
              <th class="col-actions">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in rows" :key="row.id">
              <td class="col-num">{{ row.id }}</td>
              <td>{{ affairTypeLabel(row.affairType) }}</td>
              <td class="title-cell" :title="row.title">{{ row.title }}</td>
              <td class="summary-cell" :title="row.summary">{{ textExcerpt(row.summary, 36) || "-" }}</td>
              <td><span class="sv-tag" :class="statusClass(row.status)">{{ statusLabel(row.status) }}</span></td>
              <td>{{ formatDate(row.publishTime || row.createTime) }}</td>
              <td class="col-actions">
                <button class="sv-btn sv-btn--ghost sv-btn--sm" @click="viewDetail(row)">查看</button>
                <button class="sv-btn sv-btn--ghost sv-btn--sm" @click="openEdit(row)">编辑</button>
                <button v-if="row.status !== 2" class="sv-btn sv-btn--ghost sv-btn--sm" @click="openAudit(row, 2)">发布</button>
                <button v-if="row.status === 2" class="sv-btn sv-btn--ghost sv-btn--sm" @click="openAudit(row, 3)">下架</button>
                <button class="sv-btn sv-btn--ghost sv-btn--sm danger-text" @click="removeRow(row)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-if="!rows.length" class="sv-empty">暂无公示事项数据</div>
    </div>

    <div class="sv-pager" v-if="pages > 1">
      <button :disabled="current <= 1" @click="load(current - 1)">上一页</button>
      <span class="sv-pager-current">{{ current }}</span>
      <span class="sv-pager-total">/ {{ pages }}</span>
      <button :disabled="current >= pages" @click="load(current + 1)">下一页</button>
    </div>

    <div v-if="showForm" class="sv-modal-mask" @click.self="closeForm">
      <div class="sv-modal sv-modal--lg">
        <div class="sv-modal-header">
          <h3>{{ editId ? "编辑公示事项" : "新增公示事项" }}</h3>
          <button class="sv-modal-close" @click="closeForm">✕</button>
        </div>
        <div class="sv-modal-body">
          <div class="form-grid">
            <div class="sv-form-group">
              <label class="sv-form-label required">事项类型</label>
              <select v-model="form.affairType" class="sv-select">
                <option v-for="item in AFFAIR_TYPES" :key="item.value" :value="item.value">{{ item.label }}</option>
              </select>
            </div>
            <div class="sv-form-group">
              <label class="sv-form-label">金额</label>
              <input v-model="form.amount" type="number" min="0" step="0.01" class="sv-input" placeholder="财务公开可填写金额" />
            </div>
            <div class="sv-form-group form-grid-full">
              <label class="sv-form-label required">标题</label>
              <input v-model="form.title" class="sv-input" placeholder="请输入标题" />
            </div>
            <div class="sv-form-group form-grid-full">
              <label class="sv-form-label">摘要</label>
              <textarea v-model="form.summary" class="sv-textarea" rows="3" placeholder="请输入摘要"></textarea>
            </div>
            <div class="sv-form-group form-grid-full">
              <label class="sv-form-label">附件</label>
              <input v-model="form.attachments" class="sv-input" placeholder='可填 JSON 数组字符串，如 ["https://..."]' />
            </div>
          </div>
          <div class="sv-form-group form-group-top">
            <label class="sv-form-label">正文</label>
            <textarea v-model="form.content" class="sv-textarea textarea-lg" rows="8" placeholder="请输入正文，支持 HTML 字符串"></textarea>
          </div>
          <p v-if="formError" class="modal-error">{{ formError }}</p>
        </div>
        <div class="sv-modal-footer">
          <button class="sv-btn sv-btn--secondary" @click="closeForm">取消</button>
          <button class="sv-btn sv-btn--primary" :disabled="saving" @click="submitForm">
            {{ saving ? "保存中..." : editId ? "保存修改" : "确认新增" }}
          </button>
        </div>
      </div>
    </div>

    <div v-if="showDetail" class="sv-modal-mask" @click.self="showDetail = false">
      <div class="sv-modal sv-modal--lg">
        <div class="sv-modal-header">
          <h3>公示事项详情</h3>
          <button class="sv-modal-close" @click="showDetail = false">✕</button>
        </div>
        <div class="sv-modal-body">
          <div v-if="detailLoading" class="sv-loading-inline"><span class="sv-spinner" /> 加载中...</div>
          <div v-else-if="detailData">
            <div class="detail-grid">
              <div class="detail-item"><span>事项类型</span><strong>{{ affairTypeLabel(detailData.affairType) }}</strong></div>
              <div class="detail-item"><span>状态</span><strong>{{ statusLabel(detailData.status) }}</strong></div>
              <div class="detail-item detail-item--full"><span>标题</span><strong>{{ detailData.title || "-" }}</strong></div>
              <div class="detail-item detail-item--full"><span>摘要</span><strong>{{ detailData.summary || "-" }}</strong></div>
              <div class="detail-item"><span>金额</span><strong>{{ detailData.amount ?? "-" }}</strong></div>
              <div class="detail-item"><span>发布时间</span><strong>{{ formatDate(detailData.publishTime) || "-" }}</strong></div>
              <div class="detail-item detail-item--full"><span>附件</span><strong>{{ detailData.attachments || "-" }}</strong></div>
            </div>
            <div class="article-view" v-html="detailData.content || '<p>暂无正文</p>'" />
          </div>
        </div>
        <div class="sv-modal-footer">
          <button class="sv-btn sv-btn--secondary" @click="showDetail = false">关闭</button>
        </div>
      </div>
    </div>

    <div v-if="showAudit" class="sv-modal-mask" @click.self="showAudit = false">
      <div class="sv-modal">
        <div class="sv-modal-header">
          <h3>更新公示状态</h3>
          <button class="sv-modal-close" @click="showAudit = false">✕</button>
        </div>
        <div class="sv-modal-body">
          <div class="sv-form-group">
            <label class="sv-form-label">目标状态</label>
            <select v-model="auditForm.status" class="sv-select">
              <option :value="2">发布</option>
              <option :value="1">打回待审核</option>
              <option :value="3">下架</option>
            </select>
          </div>
          <div class="sv-form-group form-group-top">
            <label class="sv-form-label">审核备注</label>
            <textarea v-model="auditForm.auditRemark" class="sv-textarea" rows="4" placeholder="可填写审核说明或驳回原因"></textarea>
          </div>
        </div>
        <div class="sv-modal-footer">
          <button class="sv-btn sv-btn--secondary" @click="showAudit = false">取消</button>
          <button class="sv-btn sv-btn--primary" :disabled="auditing" @click="submitAudit">
            {{ auditing ? "提交中..." : "确认提交" }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.filter-card {
  margin-bottom: 16px;
}

.filter-input {
  width: 220px;
}

.filter-select {
  min-width: 140px;
}

.title-cell,
.summary-cell {
  max-width: 220px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.danger-text {
  color: var(--danger);
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 14px;
}

.form-grid-full {
  grid-column: 1 / -1;
}

.form-group-top {
  align-items: flex-start;
}

.textarea-lg {
  min-height: 180px;
}

.modal-error {
  margin: 0;
  color: var(--danger);
  font-size: 13px;
  text-align: center;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.detail-item {
  display: grid;
  gap: 6px;
  padding: 14px;
  border-radius: 8px;
  background: var(--field-50);
  border: 1px solid var(--border-color);
}

.detail-item span {
  color: var(--text-placeholder);
  font-size: 12px;
}

.detail-item strong {
  color: var(--text-primary);
  font-size: 14px;
  line-height: 1.7;
  word-break: break-word;
}

.detail-item--full {
  grid-column: 1 / -1;
}

.article-view {
  padding: 18px;
  border-radius: 8px;
  border: 1px solid var(--border-color);
  background: #fff;
  line-height: 1.8;
}

.article-view :deep(p:first-child) {
  margin-top: 0;
}

@media (max-width: 900px) {
  .form-grid,
  .detail-grid {
    grid-template-columns: 1fr;
  }

  .filter-input,
  .filter-select {
    width: 100%;
  }
}
</style>
