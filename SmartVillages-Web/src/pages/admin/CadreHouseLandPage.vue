<script setup>
import { onMounted, reactive, ref } from "vue";

import {
  createVillageHouseLand,
  deleteVillageHouseLand,
  fetchVillageHouseLandDetail,
  fetchVillageHouseLandPage,
  updateVillageHouseLand,
} from "@/services/villageHouseLand.api";
import { showFlash } from "@/shared/ui/flash";
import { formatDate } from "@/shared/utils/format";

const rows = ref([]);
const loading = ref(false);
const current = ref(1);
const pages = ref(1);
const size = 10;

const filters = reactive({
  bizType: "",
  ownerName: "",
  location: "",
});

const BIZ_TYPE_OPTIONS = [
  { label: "全部类型", value: "" },
  { label: "房屋", value: "HOUSE" },
  { label: "土地", value: "LAND" },
];

const showForm = ref(false);
const saving = ref(false);
const formError = ref("");
const editId = ref(null);
const form = reactive({
  bizType: "HOUSE",
  parcelCode: "",
  location: "",
  areaMu: "",
  ownerName: "",
  certNo: "",
  remark: "",
});

const showDetail = ref(false);
const detailLoading = ref(false);
const detailData = ref(null);

function bizTypeLabel(value) {
  return value === "LAND" ? "土地" : "房屋";
}

function resetForm() {
  form.bizType = "HOUSE";
  form.parcelCode = "";
  form.location = "";
  form.areaMu = "";
  form.ownerName = "";
  form.certNo = "";
  form.remark = "";
}

function sanitizePayload() {
  return {
    bizType: form.bizType,
    parcelCode: form.parcelCode.trim() || undefined,
    location: form.location.trim() || undefined,
    areaMu: form.areaMu === "" ? undefined : Number(form.areaMu),
    ownerName: form.ownerName.trim() || undefined,
    certNo: form.certNo.trim() || undefined,
    remark: form.remark.trim() || undefined,
  };
}

async function load(page = 1) {
  loading.value = true;
  try {
    const data = await fetchVillageHouseLandPage({
      current: page,
      size,
      bizType: filters.bizType || undefined,
      ownerName: filters.ownerName.trim() || undefined,
      location: filters.location.trim() || undefined,
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
  filters.bizType = "";
  filters.ownerName = "";
  filters.location = "";
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
    const detail = await fetchVillageHouseLandDetail(row.id);
    form.bizType = detail.bizType || "HOUSE";
    form.parcelCode = detail.parcelCode || "";
    form.location = detail.location || "";
    form.areaMu = detail.areaMu ?? "";
    form.ownerName = detail.ownerName || "";
    form.certNo = detail.certNo || "";
    form.remark = detail.remark || "";
    showForm.value = true;
  } catch {
    showFlash("加载房屋土地台账详情失败");
  }
}

function closeForm() {
  showForm.value = false;
  editId.value = null;
}

async function submitForm() {
  formError.value = "";
  if (!form.bizType) {
    formError.value = "请选择类型";
    return;
  }

  saving.value = true;
  try {
    const nextPage = editId.value ? current.value : 1;
    const payload = sanitizePayload();
    if (editId.value) {
      await updateVillageHouseLand(editId.value, payload);
      showFlash("房屋土地台账更新成功");
    } else {
      await createVillageHouseLand(payload);
      showFlash("房屋土地台账创建成功");
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
    detailData.value = await fetchVillageHouseLandDetail(row.id);
  } catch {
    showFlash("加载房屋土地台账详情失败");
    showDetail.value = false;
  } finally {
    detailLoading.value = false;
  }
}

async function removeRow(row) {
  if (!confirm(`确认删除${bizTypeLabel(row.bizType)}台账「${row.parcelCode || row.ownerName || row.id}」？`)) return;
  try {
    await deleteVillageHouseLand(row.id);
    showFlash("房屋土地台账已删除");
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
        <h2>房屋土地台账</h2>
        <p>围绕房屋、地块、面积、权利人和权证信息，支撑基层地房核查与资产管理。</p>
      </div>
      <button class="sv-btn sv-btn--primary" @click="openCreate">新增台账</button>
    </div>

    <div class="sv-card filter-card">
      <div class="sv-filter-bar">
        <select v-model="filters.bizType" class="sv-select filter-select">
          <option v-for="option in BIZ_TYPE_OPTIONS" :key="option.value" :value="option.value">{{ option.label }}</option>
        </select>
        <input v-model="filters.ownerName" class="sv-input filter-input" placeholder="权利人/户主" />
        <input v-model="filters.location" class="sv-input filter-input" placeholder="坐落" />
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
              <th>类型</th>
              <th>编号</th>
              <th>坐落</th>
              <th>面积(亩)</th>
              <th>权利人/户主</th>
              <th>创建时间</th>
              <th class="col-actions">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in rows" :key="row.id">
              <td class="col-num">{{ row.id }}</td>
              <td><span class="sv-tag" :class="row.bizType === 'LAND' ? 'sv-tag--processing' : 'sv-tag--draft'">{{ bizTypeLabel(row.bizType) }}</span></td>
              <td>{{ row.parcelCode || "-" }}</td>
              <td class="cell-ellipsis" :title="row.location">{{ row.location || "-" }}</td>
              <td>{{ row.areaMu ?? "-" }}</td>
              <td>{{ row.ownerName || "-" }}</td>
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
      <div v-if="!rows.length" class="sv-empty">暂无房屋土地台账数据</div>
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
          <h3>{{ editId ? "编辑房屋土地台账" : "新增房屋土地台账" }}</h3>
          <button class="sv-modal-close" @click="closeForm">✕</button>
        </div>
        <div class="sv-modal-body">
          <div class="form-grid">
            <div class="sv-form-group">
              <label class="sv-form-label required">类型</label>
              <select v-model="form.bizType" class="sv-select">
                <option value="HOUSE">房屋</option>
                <option value="LAND">土地</option>
              </select>
            </div>
            <div class="sv-form-group">
              <label class="sv-form-label">编号</label>
              <input v-model="form.parcelCode" class="sv-input" placeholder="地块/房屋编号" />
            </div>
            <div class="sv-form-group">
              <label class="sv-form-label">面积(亩)</label>
              <input v-model="form.areaMu" type="number" min="0" step="0.01" class="sv-input" placeholder="如：12.50" />
            </div>
            <div class="sv-form-group">
              <label class="sv-form-label">权利人/户主</label>
              <input v-model="form.ownerName" class="sv-input" placeholder="请输入权利人或户主" />
            </div>
            <div class="sv-form-group form-grid-full">
              <label class="sv-form-label">坐落</label>
              <input v-model="form.location" class="sv-input" placeholder="请输入坐落位置" />
            </div>
            <div class="sv-form-group form-grid-full">
              <label class="sv-form-label">权证号</label>
              <input v-model="form.certNo" class="sv-input" placeholder="请输入权证号" />
            </div>
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
          <h3>房屋土地台账详情</h3>
          <button class="sv-modal-close" @click="showDetail = false">✕</button>
        </div>
        <div class="sv-modal-body">
          <div v-if="detailLoading" class="sv-loading-inline"><span class="sv-spinner" /> 加载中...</div>
          <div v-else-if="detailData" class="detail-grid">
            <div class="detail-item"><span>类型</span><strong>{{ bizTypeLabel(detailData.bizType) }}</strong></div>
            <div class="detail-item"><span>编号</span><strong>{{ detailData.parcelCode || "-" }}</strong></div>
            <div class="detail-item"><span>坐落</span><strong>{{ detailData.location || "-" }}</strong></div>
            <div class="detail-item"><span>面积(亩)</span><strong>{{ detailData.areaMu ?? "-" }}</strong></div>
            <div class="detail-item"><span>权利人/户主</span><strong>{{ detailData.ownerName || "-" }}</strong></div>
            <div class="detail-item"><span>权证号</span><strong>{{ detailData.certNo || "-" }}</strong></div>
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
  width: 220px;
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

.form-grid-full {
  grid-column: 1 / -1;
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
