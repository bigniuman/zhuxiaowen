<template>
  <div id="userRegisterPage">
    <div class="page-header">
      <h2>创建账号</h2>
      <p>注册后即可开始搭建你的问答应用。</p>
    </div>
    <a-form
      layout="vertical"
      class="auth-form"
      :model="form"
      @submit="handleSubmit"
    >
      <a-form-item field="userAccount" label="账号">
        <a-input
          v-model="form.userAccount"
          placeholder="请输入账号"
          size="large"
        />
      </a-form-item>
      <a-form-item field="userPassword" tooltip="密码不少于 8 位" label="密码">
        <a-input-password
          v-model="form.userPassword"
          placeholder="请输入密码"
          size="large"
        />
      </a-form-item>
      <a-form-item
        field="checkPassword"
        tooltip="密码不少于 8 位"
        label="确认密码"
      >
        <a-input-password
          v-model="form.checkPassword"
          placeholder="请再次输入密码"
          size="large"
        />
      </a-form-item>
      <a-form-item class="submit-item">
        <a-button type="primary" html-type="submit" long size="large">
          注册
        </a-button>
      </a-form-item>
      <a-form-item>
        <div class="form-footer">
          <span>已经有账号？</span>
          <a class="link-text" href="/user/login">返回登录</a>
        </div>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from "vue";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import { userRegisterUsingPost } from "@/api/userController";

const form = reactive({
  userAccount: "",
  userPassword: "",
  checkPassword: "",
} as API.UserRegisterRequest);

const router = useRouter();

const handleSubmit = async () => {
  const res = await userRegisterUsingPost(form);
  if (res.data.code === 0) {
    message.success("注册成功");
    router.push({
      path: "/user/login",
      replace: true,
    });
  } else {
    message.error("注册失败：" + res.data.message);
  }
};
</script>

<style scoped>
.page-header {
  margin-bottom: 28px;
  text-align: center;
}

.page-header h2 {
  margin: 0 0 10px;
  font-size: 30px;
  color: var(--text-primary);
}

.page-header p {
  margin: 0;
  color: var(--text-secondary);
}

.auth-form {
  max-width: 100%;
}

.submit-item {
  margin-top: 8px;
  margin-bottom: 10px;
}

.form-footer {
  display: flex;
  justify-content: center;
  gap: 6px;
  width: 100%;
  color: var(--text-secondary);
}

.link-text {
  color: var(--brand-700);
  font-weight: 600;
  text-decoration: none;
}
</style>
