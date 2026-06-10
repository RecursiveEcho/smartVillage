<script setup>
import { onMounted, ref } from "vue";

import { updateUserStatus, deleteAdminUser, createCadreUser, getAdminUserPage } from "@/services/admin.api";
import { formatDate } from "@/shared/utils/format";

const rows = ref([]);
const loading = ref(false);
const current = ref(1);
const pages = ref(1);
const size = 10;

const showCreate = ref(false);
const createForm = ref({ username: "", password: "", role: "CADRE" });
const createLoading = ref(false);
const createError = ref("");

async function load(page = 1) {
  loading.value = true;
  try {
    const data = await getAdminUserPage({ current: page, size });
    rows.value = data.records ?? [];
    current.value = Number(data.current) || page;
    pages.value = Number(data.pages) || 1;
  } catch {
    rows.value = [];
  } finally {
    loading.value = false;
  }
}

async function toggleStatus(row) {
  const next = row.status === 1 ? 0 : 1;
  const label = next === 0 ? "禁用" : "启用";
  if (!confirm(`确认${label}用户「${row.username}」？`)) return;
  try {
    await updateUserStatus(row.id, next);
    await load(current.value);
  } catch (e) {
    alert(e?.message || "操作失败");
  }
}

async function removeUser(row) {
  if (!confirm(`确认删除用户「${row.username}」？此操作不可恢复。`)) return;
  try {
    await deleteAdminUser(row.id);
    await load(current.value);
  } catch (e) {
    alert(e?.message || "删除失败");
  }
}

async function handleCreate() {
  createError.value = "";
  if (!createForm.value.username.trim() || !createForm.value.password.trim()) {
    createError.value = "请填写用户名和密码";
    return;
  }
  createLoading.value = true;
  try {
    await createCadreUser(createForm.value);
    showCreate.value = false;
    createForm.value = { username: "", password: "", role: "CADRE" };
    await load(1);
  } catch (e) {
    createError.value = e?.message || "创建失败";
  } finally {
    createLoading.value = false;
  }
}

const roleMap = { ADMIN: "管理员", CADRE: "村干部", VILLAGER: "村民" };
function roleLabel(r) { return roleMap[r] ?? r; }

onMounted(() => load());
</script>

<template>
  <div>
    <div class="sv-manager-head">
      <div>
        <h2>用户管理</h2>
        <p>管理系统用户及村干部账号，控制账号状态和角色权限。</p>
      </div>
      <button class="sv-btn sv-btn--primary" @click="showCreate = true">新增村干部</button>
    </div>

    <div v-if="loading" class="sv-loading-inline"><span class="sv-spinner" /> 加载中...</div>

    <div v-else class="sv-card sv-table-card">
      <div class="sv-table-wrap">
        <table class="sv-table">
          <thead>
            <tr>
              <th class="col-num">ID</th>
              <th>用户名</th>
              <th>角色</th>
              <th>状态</th>
              <th>创建时间</th>
              <th class="col-actions">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="r in rows" :key="r.id">
              <td class="col-num">{{ r.id }}</td>
              <td><strong>{{ r.username }}</strong></td>
              <td><span class="sv-tag" :class="r.role === 'ADMIN' ? 'sv-tag--approved' : r.role === 'CADRE' ? 'sv-tag--processing' : 'sv-tag--draft'">{{ roleLabel(r.role) }}</span></td>
              <td>
                <span class="sv-tag" :class="r.status === 1 ? 'sv-tag--approved' : 'sv-tag--rejected'">
                  {{ r.status === 1 ? "正常" : "已禁用" }}
                </span>
              </td>
              <td>{{ formatDate(r.createTime) }}</td>
              <td class="col-actions">
                <button class="sv-btn sv-btn--ghost sv-btn--sm" @click="toggleStatus(r)">
                  {{ r.status === 1 ? "禁用" : "启用" }}
                </button>
                <button v-if="r.role !== 'ADMIN'" class="sv-btn sv-btn--ghost sv-btn--sm" style="color: var(--danger)" @click="removeUser(r)">
                  删除
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-if="!rows.length" class="sv-empty">暂无用户数据</div>
    </div>

    <div class="sv-pager" v-if="pages > 1">
      <button :disabled="current <= 1" @click="load(current - 1)">上一页</button>
      <span class="sv-pager-current">{{ current }}</span>
      <span style="color: var(--text-placeholder)">/ {{ pages }}</span>
      <button :disabled="current >= pages" @click="load(current + 1)">下一页</button>
    </div>

    <!-- 新增村干部弹窗 -->
    <div v-if="showCreate" class="sv-modal-mask" @click.self="showCreate = false">
      <div class="sv-modal">
        <div class="sv-modal-header">
          <h3>新增村干部账号</h3>
          <button class="sv-modal-close" @click="showCreate = false">✕</button>
        </div>
        <div class="sv-modal-body">
          <div class="sv-form-group">
            <label class="sv-form-label required">用户名</label>
            <input v-model="createForm.username" class="sv-input" placeholder="请输入用户名" />
          </div>
          <div class="sv-form-group">
            <label class="sv-form-label required">密码</label>
            <input v-model="createForm.password" type="password" class="sv-input" placeholder="请输入密码" />
          </div>
          <p v-if="createError" class="sv-form-error" style="margin-left: 0; text-align: center">{{ createError }}</p>
        </div>
        <div class="sv-modal-footer">
          <button class="sv-btn sv-btn--secondary" @click="showCreate = false">取消</button>
          <button class="sv-btn sv-btn--primary" :disabled="createLoading" @click="handleCreate">
            {{ createLoading ? "创建中..." : "确认创建" }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
