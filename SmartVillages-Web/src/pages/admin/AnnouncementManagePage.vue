<script setup>
import { onMounted, ref } from "vue";

import {
  getCadreAnnouncementPage,
  createCadreAnnouncement,
  updateCadreAnnouncement,
  deleteCadreAnnouncement,
} from "@/services/announcement.api";
import { formatDate } from "@/shared/utils/format";

const rows = ref([]);
const loading = ref(false);
const current = ref(1);
const pages = ref(1);
const size = 10;

const showForm = ref(false);
const editId = ref(null);
const saving = ref(false);
const form = ref({ title: "", content: "", type: 1 });
const formError = ref("");

const typeMap = { 1: "村务", 2: "政策", 3: "民生", 4: "应急" };

async function load(page = 1) {
  loading.value = true;
  try {
    const data = await getCadreAnnouncementPage({ current: page, size });
    rows.value = data.records ?? [];
    current.value = Number(data.current) || page;
    pages.value = Number(data.pages) || 1;
  } catch {
    rows.value = [];
  } finally {
    loading.value = false;
  }
}

function openCreate() {
  editId.value = null;
  form.value = { title: "", content: "", type: 1 };
  formError.value = "";
  showForm.value = true;
}

function openEdit(r) {
  editId.value = r.id;
  form.value = { title: r.title, content: r.content, type: r.type };
  formError.value = "";
  showForm.value = true;
}

function closeForm() {
  showForm.value = false;
  editId.value = null;
}

async function submitForm() {
  formError.value = "";
  if (!form.value.title.trim()) { formError.value = "请输入标题"; return; }
  if (!form.value.content.trim()) { formError.value = "请输入内容"; return; }
  saving.value = true;
  try {
    if (editId.value) {
      await updateCadreAnnouncement(editId.value, form.value);
    } else {
      await createCadreAnnouncement(form.value);
    }
    closeForm();
    await load(current.value);
  } catch (e) {
    formError.value = e?.message || "保存失败";
  } finally {
    saving.value = false;
  }
}

async function remove(r) {
  if (!confirm(`确认删除公告「${r.title}」？`)) return;
  try {
    await deleteCadreAnnouncement(r.id);
    load(current.value);
  } catch { alert("删除失败"); }
}

onMounted(() => load());
</script>

<template>
  <div>
    <div class="sv-manager-head">
      <div>
        <h2>公告管理</h2>
        <p>发布和管理村务通知公告，提交后需管理员审核。</p>
      </div>
      <button class="sv-btn sv-btn--primary" @click="openCreate">发布公告</button>
    </div>

    <div v-if="loading" class="sv-loading-inline"><span class="sv-spinner" /> 加载中...</div>

    <div v-else class="sv-card sv-table-card">
      <div class="sv-table-wrap">
        <table class="sv-table">
          <thead>
            <tr>
              <th class="col-num">ID</th>
              <th>类型</th>
              <th>标题</th>
              <th>状态</th>
              <th>发布时间</th>
              <th class="col-actions">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="r in rows" :key="r.id">
              <td class="col-num">{{ r.id }}</td>
              <td><span class="sv-tag sv-tag--draft">{{ typeMap[r.type] ?? "公告" }}</span></td>
              <td class="title-cell">{{ r.title }}</td>
              <td>
                <span class="sv-tag" :class="r.status === 0 ? 'sv-tag--pending' : r.status === 1 ? 'sv-tag--approved' : r.status === 2 ? 'sv-tag--rejected' : 'sv-tag--closed'">
                  {{ { 0: "待审核", 1: "已通过", 2: "已拒绝", 3: "已下架" }[r.status] ?? r.status }}
                </span>
              </td>
              <td>{{ formatDate(r.publishTime || r.createTime) }}</td>
              <td class="col-actions">
                <button v-if="r.status !== 1" class="sv-btn sv-btn--ghost sv-btn--sm" @click="openEdit(r)">编辑</button>
                <button class="sv-btn sv-btn--ghost sv-btn--sm" style="color: var(--danger)" @click="remove(r)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-if="!rows.length" class="sv-empty">暂无公告，点击“发布公告”创建</div>
    </div>

    <div class="sv-pager" v-if="pages > 1">
      <button :disabled="current <= 1" @click="load(current - 1)">上一页</button>
      <span class="sv-pager-current">{{ current }}</span>
      <span style="color:var(--text-placeholder)">/ {{ pages }}</span>
      <button :disabled="current >= pages" @click="load(current + 1)">下一页</button>
    </div>

    <!-- 发布/编辑弹窗 -->
    <div v-if="showForm" class="sv-modal-mask" @click.self="closeForm">
      <div class="sv-modal sv-modal--lg">
        <div class="sv-modal-header">
          <h3>{{ editId ? '编辑公告' : '发布公告' }}</h3>
          <button class="sv-modal-close" @click="closeForm">✕</button>
        </div>
        <div class="sv-modal-body">
          <div class="sv-form-group">
            <label class="sv-form-label required">标题</label>
            <input v-model="form.title" class="sv-input" placeholder="请输入公告标题" />
          </div>
          <div class="sv-form-group">
            <label class="sv-form-label">类型</label>
            <select v-model.number="form.type" class="sv-select">
              <option v-for="(label, val) in typeMap" :key="val" :value="Number(val)">{{ label }}</option>
            </select>
          </div>
          <div class="sv-form-group" style="align-items: flex-start">
            <label class="sv-form-label required" style="margin-top: 6px">内容</label>
            <textarea v-model="form.content" class="sv-textarea" rows="6" placeholder="请输入公告正文内容"></textarea>
          </div>
          <p v-if="formError" style="color:var(--danger);font-size:13px;text-align:center;margin:0">{{ formError }}</p>
        </div>
        <div class="sv-modal-footer">
          <button class="sv-btn sv-btn--secondary" @click="closeForm">取消</button>
          <button class="sv-btn sv-btn--primary" :disabled="saving" @click="submitForm">
            {{ saving ? '保存中...' : editId ? '保存修改' : '提交审核' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.title-cell { max-width: 280px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
</style>
