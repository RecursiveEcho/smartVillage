<script setup>
import { onMounted, reactive, ref } from "vue";

import {
  createVillageParty,
  deleteVillageParty,
  fetchVillagePartyDetail,
  fetchVillagePartyPage,
  updateVillageParty,
} from "@/services/villageParty.api";
import { showFlash } from "@/shared/ui/flash";
import { formatDate } from "@/shared/utils/format";

const rows = ref([]);
const loading = ref(false);
const current = ref(1);
const pages = ref(1);
const size = 10;

const filters = reactive({
  orgName: "",
  orgType: "",
  secretaryName: "",
});

const ORG_TYPES = ["党支部", "党总支"];

const showForm = ref(false);
const saving = ref(false);
const formError = ref("");
const editId = ref(null);
const form = reactive({
  orgName: "",
  orgType: "",
  secretaryName: "",
  memberCount: "",
  contactPhone: "",
  remark: "",
});

const showDetail = ref(false);
const detailLoading = ref(false);
const detailData = ref(null);

function resetForm() {
  form.orgName = "";
  form.orgType = "";
  form.secretaryName = "";
  form.memberCount = "";
  form.contactPhone = "";
  form.remark = "";
}

function sanitizePayload() {
  return {
    orgName: form.orgName.trim(),
    orgType: form.orgType.trim() || undefined,
    secretaryName: form.secretaryName.trim() || undefined,
    memberCount: form.memberCount === "" ? undefined : Number(form.memberCount),
    contactPhone: form.contactPhone.trim() || undefined,
    remark: form.remark.trim() || undefined,
  };
}

async function load(page = 1) {
  loading.value = true;
  try {
    const data = await fetchVillagePartyPage({
      current: page,
      size,
      orgName: filters.orgName.trim() || undefined,
      orgType: filters.orgType.trim() || undefined,
      secretaryName: filters.secretaryName.trim() || undefined,
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
  filters.orgName = "";
  filters.orgType = "";
  filters.secretaryName = "";
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
    const detail = await fetchVillagePartyDetail(row.id);
    form.orgName = detail.orgName || "";
    form.orgType = detail.orgType || "";
    form.secretaryName = detail.secretaryName || "";
    form.memberCount = detail.memberCount ?? "";
    form.contactPhone = detail.contactPhone || "";
    form.remark = detail.remark || "";
    showForm.value = true;
  } catch {
    showFlash("加载党建组织详情失败");
  }
}

function closeForm() {
  showForm.value = false;
  editId.value = null;
}

async function submitForm() {
  formError.value = "";
  if (!form.orgName.trim()) {
    formError.value = "请输入党组织名称";
    return;
  }

  saving.value = true;
  try {
    const nextPage = editId.value ? current.value : 1;
    const payload = sanitizePayload();
    if (editId.value) {
      await updateVillageParty(editId.value, payload);
      showFlash("党建组织信息更新成功");
    } else {
      await createVillageParty(payload);
      showFlash("党建组织信息创建成功");
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
    detailData.value = await fetchVillagePartyDetail(row.id);
  } catch {
    showFlash("加载党建组织详情失败");
    showDetail.value = false;
  } finally {
    detailLoading.value = false;
  }
}

async function removeRow(row) {
  if (!confirm(`确认删除党组织「${row.orgName}」？`)) return;
  try {
    await deleteVillageParty(row.id);
    showFlash("党建组织信息已删除");
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
        <h2>党建组织信息</h2>
        <p>维护党组织名称、组织类型、书记、党员人数和联系方式，用于基层组织治理。</p>
      </div>
      <button class="sv-btn sv-btn--primary" @click="openCreate">新增组织</button>
    </div>

    <div class="sv-card filter-card">
      <div class="sv-filter-bar">
        <input v-model="filters.orgName" class="sv-input filter-input" placeholder="党组织名称" />
        <select v-model="filters.orgType" class="sv-select filter-select">
          <option value="">全部类型</option>
          <option v-for="item in ORG_TYPES" :key="item" :value="item">{{ item }}</option>
        </select>
        <input v-model="filters.secretaryName" class="sv-input filter-input" placeholder="书记姓名" />
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
              <th>党组织名称</th>
              <th>组织类型</th>
              <th>书记姓名</th>
              <th>党员人数</th>
              <th>创建时间</th>
              <th class="col-actions">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in rows" :key="row.id">
              <td class="col-num">{{ row.id }}</td>
              <td><strong>{{ row.orgName }}</strong></td>
              <td>{{ row.orgType || "-" }}</td>
              <td>{{ row.secretaryName || "-" }}</td>
              <td>{{ row.memberCount ?? "-" }}</td>
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
      <div v-if="!rows.length" class="sv-empty">暂无党建组织信息</div>
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
          <h3>{{ editId ? "编辑党建组织信息" : "新增党建组织信息" }}</h3>
          <button class="sv-modal-close" @click="closeForm">✕</button>
        </div>
        <div class="sv-modal-body">
          <div class="form-grid">
            <div class="sv-form-group">
              <label class="sv-form-label required">党组织名称</label>
              <input v-model="form.orgName" class="sv-input" placeholder="请输入党组织名称" />
            </div>
            <div class="sv-form-group">
              <label class="sv-form-label">组织类型</label>
              <select v-model="form.orgType" class="sv-select">
                <option value="">未填写</option>
                <option v-for="item in ORG_TYPES" :key="item" :value="item">{{ item }}</option>
              </select>
            </div>
            <div class="sv-form-group">
              <label class="sv-form-label">书记姓名</label>
              <input v-model="form.secretaryName" class="sv-input" placeholder="请输入书记姓名" />
            </div>
            <div class="sv-form-group">
              <label class="sv-form-label">党员人数</label>
              <input v-model="form.memberCount" type="number" min="0" class="sv-input" placeholder="请输入人数" />
            </div>
            <div class="sv-form-group form-grid-full">
              <label class="sv-form-label">联系电话</label>
              <input v-model="form.contactPhone" class="sv-input" placeholder="请输入联系电话" />
            </div>
          </div>
          <div class="sv-form-group form-group-top">
            <label class="sv-form-label">备注</label>
            <textarea v-model="form.remark" class="sv-textarea" rows="3" placeholder="可补充组织说明"></textarea>
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
          <h3>党建组织详情</h3>
          <button class="sv-modal-close" @click="showDetail = false">✕</button>
        </div>
        <div class="sv-modal-body">
          <div v-if="detailLoading" class="sv-loading-inline"><span class="sv-spinner" /> 加载中...</div>
          <div v-else-if="detailData" class="detail-grid">
            <div class="detail-item"><span>党组织名称</span><strong>{{ detailData.orgName || "-" }}</strong></div>
            <div class="detail-item"><span>组织类型</span><strong>{{ detailData.orgType || "-" }}</strong></div>
            <div class="detail-item"><span>书记姓名</span><strong>{{ detailData.secretaryName || "-" }}</strong></div>
            <div class="detail-item"><span>党员人数</span><strong>{{ detailData.memberCount ?? "-" }}</strong></div>
            <div class="detail-item detail-item--full"><span>联系电话</span><strong>{{ detailData.contactPhone || "-" }}</strong></div>
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
  min-width: 140px;
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
