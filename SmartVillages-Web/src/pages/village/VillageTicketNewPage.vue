<script setup>
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";

import { createServiceTicket } from "@/services/village.api";

const router = useRouter();
const form = reactive({ title: "", content: "" });
const saving = ref(false);
const error = ref("");

async function submit() {
  error.value = "";
  if (!form.title.trim()) { error.value = "请输入工单标题"; return; }
  if (!form.content.trim()) { error.value = "请输入工单内容"; return; }
  saving.value = true;
  try {
    const result = await createServiceTicket(form);
    router.replace(`/village/tickets/${result.id}`);
  } catch (e) {
    error.value = e?.message || "提交失败";
  } finally {
    saving.value = false;
  }
}
</script>

<template>
  <div>
    <div class="sv-manager-head">
      <div>
        <h2>提交工单</h2>
        <p>描述您的诉求，村干部将跟进处理。</p>
      </div>
    </div>

    <div class="sv-card ticket-form-card">
      <div class="sv-form-group">
        <label class="sv-form-label required">标题</label>
        <input v-model="form.title" class="sv-input" placeholder="简要描述您的诉求" />
      </div>
      <div class="sv-form-group form-group-top">
        <label class="sv-form-label required form-label-top">内容</label>
        <textarea v-model="form.content" class="sv-textarea" rows="6" placeholder="请详细描述您的问题或诉求"></textarea>
      </div>
      <p v-if="error" class="form-error">{{ error }}</p>
      <div class="form-actions">
        <button class="sv-btn sv-btn--secondary" @click="router.back()">取消</button>
        <button class="sv-btn sv-btn--primary" :disabled="saving" @click="submit">
          {{ saving ? '提交中...' : '提交工单' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.ticket-form-card {
  max-width: 680px;
}

.form-group-top {
  align-items: flex-start;
}

.form-label-top {
  margin-top: 6px;
}

.form-error {
  margin: 0 0 12px;
  color: var(--danger);
  font-size: 13px;
  text-align: center;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
