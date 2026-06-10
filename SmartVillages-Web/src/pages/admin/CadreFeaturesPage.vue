<script setup>
import { onMounted, ref } from "vue";

import { fetchCadreFeaturePage, fetchCadreFeatureDetail, updateCadreFeature } from "@/services/feature.api";
import { formatDate } from "@/shared/utils/format";

const rows = ref([]);
const loading = ref(false);
const current = ref(1);
const pages = ref(1);
const size = 10;

const showDetail = ref(false);
const showEdit = ref(false);
const detailData = ref(null);
const editForm = ref({ title: "", content: "", type: "" });
const editId = ref(null);
const editError = ref("");
const saving = ref(false);

async function load(page = 1) {
  loading.value = true;
  try {
    const data = await fetchCadreFeaturePage({ current: page, size });
    rows.value = data.records ?? [];
    current.value = Number(data.current) || page;
    pages.value = Number(data.pages) || 1;
  } catch {
    rows.value = [];
  } finally {
    loading.value = false;
  }
}

async function viewDetail(r) {
  try {
    detailData.value = await fetchCadreFeatureDetail(r.id);
    showDetail.value = true;
  } catch { alert("加载详情失败"); }
}

function openEdit(r) {
  editId.value = r.id;
  editForm.value = { title: r.title, content: r.content || "", type: r.type || "" };
  editError.value = "";
  showEdit.value = true;
}

async function submitEdit() {
  editError.value = "";
  if (!editForm.value.title.trim()) { editError.value = "请输入标题"; return; }
  saving.value = true;
  try {
    await updateCadreFeature(editId.value, editForm.value);
    showEdit.value = false;
    load(current.value);
  } catch (e) {
    editError.value = e?.message || "保存失败";
  } finally {
    saving.value = false;
  }
}

onMounted(() => load());
</script>

<template>
  <div>
    <div class="sv-manager-head">
      <div>
      <h2>风采管理</h2>
        <p>发布和管理乡村建设风貌展示，保持图片和正文质量。</p>
      </div>
    </div>

    <div v-if="loading" class="sv-loading-inline"><span class="sv-spinner" /> 加载中...</div>

    <div v-else class="sv-card sv-table-card">
      <div class="sv-table-wrap">
        <table class="sv-table">
          <thead>
            <tr>
              <th class="col-num">ID</th>
              <th>标题</th>
              <th>状态</th>
              <th>创建时间</th>
              <th class="col-actions">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="r in rows" :key="r.id">
              <td class="col-num">{{ r.id }}</td>
              <td class="title-cell">{{ r.title }}</td>
              <td>
                <span class="sv-tag" :class="r.status === 1 ? 'sv-tag--approved' : 'sv-tag--pending'">
                  {{ r.status === 1 ? '已发布' : '待审核' }}
                </span>
              </td>
              <td>{{ formatDate(r.createTime) }}</td>
              <td class="col-actions">
                <button class="sv-btn sv-btn--ghost sv-btn--sm" @click="viewDetail(r)">查看</button>
                <button class="sv-btn sv-btn--ghost sv-btn--sm" @click="openEdit(r)">编辑</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-if="!rows.length" class="sv-empty">暂无风采数据</div>
    </div>

    <div class="sv-pager" v-if="pages > 1">
      <button :disabled="current <= 1" @click="load(current - 1)">上一页</button>
      <span class="sv-pager-current">{{ current }}</span>
      <span style="color:var(--text-placeholder)">/ {{ pages }}</span>
      <button :disabled="current >= pages" @click="load(current + 1)">下一页</button>
    </div>

    <!-- 查看详情弹窗 -->
    <div v-if="showDetail" class="sv-modal-mask" @click.self="showDetail = false">
      <div class="sv-modal sv-modal--lg">
        <div class="sv-modal-header">
          <h3>风采详情</h3>
          <button class="sv-modal-close" @click="showDetail = false">✕</button>
        </div>
        <div class="sv-modal-body">
          <template v-if="detailData">
            <div class="sv-modal-article">
              <h4>{{ detailData.title }}</h4>
              <p class="sv-modal-article__body">{{ detailData.content || '暂无内容' }}</p>
            </div>
          </template>
        </div>
      </div>
    </div>

    <!-- 编辑弹窗 -->
    <div v-if="showEdit" class="sv-modal-mask" @click.self="showEdit = false">
      <div class="sv-modal sv-modal--lg">
        <div class="sv-modal-header">
          <h3>编辑风采</h3>
          <button class="sv-modal-close" @click="showEdit = false">✕</button>
        </div>
        <div class="sv-modal-body">
          <div class="sv-form-group">
            <label class="sv-form-label required">标题</label>
            <input v-model="editForm.title" class="sv-input" placeholder="请输入标题" />
          </div>
          <div class="sv-form-group" style="align-items:flex-start;">
            <label class="sv-form-label" style="margin-top:6px;">内容</label>
            <textarea v-model="editForm.content" class="sv-textarea" rows="6" placeholder="请输入内容"></textarea>
          </div>
          <p v-if="editError" style="color:var(--danger);font-size:13px;text-align:center;margin:0">{{ editError }}</p>
        </div>
        <div class="sv-modal-footer">
          <button class="sv-btn sv-btn--secondary" @click="showEdit = false">取消</button>
          <button class="sv-btn sv-btn--primary" :disabled="saving" @click="submitEdit">
            {{ saving ? '保存中...' : '保存修改' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.title-cell { max-width: 280px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
</style>
