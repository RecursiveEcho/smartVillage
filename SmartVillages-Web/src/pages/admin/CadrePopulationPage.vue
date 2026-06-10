<script setup>
import { onMounted, reactive, ref } from "vue";

import {
  createVillagePopulation,
  deleteVillagePopulation,
  fetchVillagePopulationDetail,
  fetchVillagePopulationPage,
  updateVillagePopulation,
} from "@/services/villagePopulation.api";
import { showFlash } from "@/shared/ui/flash";
import { formatDate } from "@/shared/utils/format";

const rows = ref([]);
const loading = ref(false);
const current = ref(1);
const pages = ref(1);
const size = 10;

const filters = reactive({
  householdNo: "",
  fullName: "",
  gender: "",
  relationToHead: "",
});

const GENDER_OPTIONS = [
  { label: "全部性别", value: "" },
  { label: "未知", value: 0 },
  { label: "男", value: 1 },
  { label: "女", value: 2 },
];

const showForm = ref(false);
const saving = ref(false);
const formError = ref("");
const editId = ref(null);
const form = reactive({
  householdNo: "",
  fullName: "",
  gender: "",
  birthDate: "",
  idCardLast4: "",
  relationToHead: "",
  address: "",
  remark: "",
});

const showDetail = ref(false);
const detailLoading = ref(false);
const detailData = ref(null);

function genderLabel(value) {
  return value === 1 ? "男" : value === 2 ? "女" : "未知";
}

function resetForm() {
  form.householdNo = "";
  form.fullName = "";
  form.gender = "";
  form.birthDate = "";
  form.idCardLast4 = "";
  form.relationToHead = "";
  form.address = "";
  form.remark = "";
}

function sanitizePayload() {
  return {
    householdNo: form.householdNo.trim() || undefined,
    fullName: form.fullName.trim(),
    gender: form.gender === "" ? undefined : Number(form.gender),
    birthDate: form.birthDate || undefined,
    idCardLast4: form.idCardLast4.trim() || undefined,
    relationToHead: form.relationToHead.trim() || undefined,
    address: form.address.trim() || undefined,
    remark: form.remark.trim() || undefined,
  };
}

async function load(page = 1) {
  loading.value = true;
  try {
    const data = await fetchVillagePopulationPage({
      current: page,
      size,
      householdNo: filters.householdNo.trim() || undefined,
      fullName: filters.fullName.trim() || undefined,
      gender: filters.gender === "" ? undefined : Number(filters.gender),
      relationToHead: filters.relationToHead.trim() || undefined,
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
  filters.householdNo = "";
  filters.fullName = "";
  filters.gender = "";
  filters.relationToHead = "";
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
  saving.value = false;
  editId.value = row.id;
  try {
    const detail = await fetchVillagePopulationDetail(row.id);
    form.householdNo = detail.householdNo || "";
    form.fullName = detail.fullName || "";
    form.gender = detail.gender ?? "";
    form.birthDate = detail.birthDate || "";
    form.idCardLast4 = detail.idCardLast4 || "";
    form.relationToHead = detail.relationToHead || "";
    form.address = detail.address || "";
    form.remark = detail.remark || "";
    showForm.value = true;
  } catch {
    showFlash("加载人口台账详情失败");
  }
}

function closeForm() {
  showForm.value = false;
  editId.value = null;
}

async function submitForm() {
  formError.value = "";
  if (!form.fullName.trim()) {
    formError.value = "请输入姓名";
    return;
  }

  saving.value = true;
  try {
    const nextPage = editId.value ? current.value : 1;
    const payload = sanitizePayload();
    if (editId.value) {
      await updateVillagePopulation(editId.value, payload);
      showFlash("人口台账更新成功");
    } else {
      await createVillagePopulation(payload);
      showFlash("人口台账创建成功");
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
    detailData.value = await fetchVillagePopulationDetail(row.id);
  } catch {
    showFlash("加载人口台账详情失败");
    showDetail.value = false;
  } finally {
    detailLoading.value = false;
  }
}

async function removeRow(row) {
  if (!confirm(`确认删除人口台账「${row.fullName}」？`)) return;
  try {
    await deleteVillagePopulation(row.id);
    showFlash("人口台账已删除");
    load(current.value);
  } catch (error) {
    showFlash(error?.message || "删除失败");
  }
}

onMounted(() => load());
</script>

<template>
  <div>
    <div class="sv-manager-head">
      <div>
        <h2>人口台账</h2>
        <p>管理户号、成员关系、住址和基础人口信息，形成基层治理底册。</p>
      </div>
      <button class="sv-btn sv-btn--primary" @click="openCreate">新增台账</button>
    </div>

    <div class="sv-card filter-card">
      <div class="sv-filter-bar">
        <input v-model="filters.householdNo" class="sv-input filter-input" placeholder="户号" />
        <input v-model="filters.fullName" class="sv-input filter-input" placeholder="姓名" />
        <select v-model="filters.gender" class="sv-select filter-select">
          <option v-for="option in GENDER_OPTIONS" :key="String(option.value)" :value="option.value">
            {{ option.label }}
          </option>
        </select>
        <input v-model="filters.relationToHead" class="sv-input filter-input" placeholder="与户主关系" />
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
              <th>户号</th>
              <th>姓名</th>
              <th>性别</th>
              <th>地址</th>
              <th>创建时间</th>
              <th class="col-actions">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in rows" :key="row.id">
              <td class="col-num">{{ row.id }}</td>
              <td>{{ row.householdNo || "-" }}</td>
              <td><strong>{{ row.fullName }}</strong></td>
              <td>{{ genderLabel(row.gender) }}</td>
              <td class="cell-ellipsis" :title="row.address">{{ row.address || "-" }}</td>
              <td>{{ formatDate(row.createTime) }}</td>
              <td class="col-actions">
                <button class="sv-btn sv-btn--ghost sv-btn--sm" @click="viewDetail(row)">查看</button>
                <button class="sv-btn sv-btn--ghost sv-btn--sm" @click="openEdit(row)">编辑</button>
                <button class="sv-btn sv-btn--ghost sv-btn--sm danger-text" @click="removeRow(row)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-if="!rows.length" class="sv-empty">暂无人口台账数据</div>
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
          <h3>{{ editId ? "编辑人口台账" : "新增人口台账" }}</h3>
          <button class="sv-modal-close" @click="closeForm">✕</button>
        </div>
        <div class="sv-modal-body">
          <div class="form-grid">
            <div class="sv-form-group">
              <label class="sv-form-label">户号</label>
              <input v-model="form.householdNo" class="sv-input" placeholder="如：QSC-001" />
            </div>
            <div class="sv-form-group">
              <label class="sv-form-label required">姓名</label>
              <input v-model="form.fullName" class="sv-input" placeholder="请输入姓名" />
            </div>
            <div class="sv-form-group">
              <label class="sv-form-label">性别</label>
              <select v-model="form.gender" class="sv-select">
                <option value="">未填写</option>
                <option :value="0">未知</option>
                <option :value="1">男</option>
                <option :value="2">女</option>
              </select>
            </div>
            <div class="sv-form-group">
              <label class="sv-form-label">出生日期</label>
              <input v-model="form.birthDate" type="date" class="sv-input" />
            </div>
            <div class="sv-form-group">
              <label class="sv-form-label">证件后四位</label>
              <input v-model="form.idCardLast4" class="sv-input" maxlength="4" placeholder="如：1024" />
            </div>
            <div class="sv-form-group">
              <label class="sv-form-label">与户主关系</label>
              <input v-model="form.relationToHead" class="sv-input" placeholder="如：户主/配偶/子女" />
            </div>
          </div>
          <div class="sv-form-group form-group-top">
            <label class="sv-form-label">地址</label>
            <textarea v-model="form.address" class="sv-textarea" rows="3" placeholder="请输入住址"></textarea>
          </div>
          <div class="sv-form-group form-group-top">
            <label class="sv-form-label">备注</label>
            <textarea v-model="form.remark" class="sv-textarea" rows="3" placeholder="可补充台账说明"></textarea>
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
          <h3>人口台账详情</h3>
          <button class="sv-modal-close" @click="showDetail = false">✕</button>
        </div>
        <div class="sv-modal-body">
          <div v-if="detailLoading" class="sv-loading-inline"><span class="sv-spinner" /> 加载中...</div>
          <div v-else-if="detailData" class="detail-grid">
            <div class="detail-item"><span>户号</span><strong>{{ detailData.householdNo || "-" }}</strong></div>
            <div class="detail-item"><span>姓名</span><strong>{{ detailData.fullName || "-" }}</strong></div>
            <div class="detail-item"><span>性别</span><strong>{{ genderLabel(detailData.gender) }}</strong></div>
            <div class="detail-item"><span>出生日期</span><strong>{{ formatDate(detailData.birthDate) || "-" }}</strong></div>
            <div class="detail-item"><span>证件后四位</span><strong>{{ detailData.idCardLast4 || "-" }}</strong></div>
            <div class="detail-item"><span>与户主关系</span><strong>{{ detailData.relationToHead || "-" }}</strong></div>
            <div class="detail-item detail-item--full"><span>地址</span><strong>{{ detailData.address || "-" }}</strong></div>
            <div class="detail-item detail-item--full"><span>备注</span><strong>{{ detailData.remark || "-" }}</strong></div>
          </div>
        </div>
        <div class="sv-modal-footer">
          <button class="sv-btn sv-btn--secondary" @click="showDetail = false">关闭</button>
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
  width: 180px;
}

.filter-select {
  min-width: 120px;
}

.cell-ellipsis {
  max-width: 240px;
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

.form-group-top {
  align-items: flex-start;
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
