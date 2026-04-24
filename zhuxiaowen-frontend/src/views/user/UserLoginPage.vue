<template>
  <div id="userLoginPage">
    <div class="page-header">
      <h2>欢迎登录</h2>
      <p>继续管理你的应用、题目与评分结果。</p>
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
      <a-form-item class="submit-item">
        <a-button type="primary" html-type="submit" long size="large">
          登录
        </a-button>
      </a-form-item>
      <a-form-item>
        <div class="form-footer">
          <span>还没有账号？</span>
          <a class="link-text" href="/user/register">立即注册</a>
        </div>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from "vue";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import { useLoginUserStore } from "@/store/userStore";
import { userLoginUsingPost } from "@/api/userController";

const form = reactive({
  userAccount: "",
  userPassword: "",
} as API.UserLoginRequest);

const router = useRouter();
const loginUserStore = useLoginUserStore();

const handleSubmit = async () => {
  const res = await userLoginUsingPost(form);
  if (res.data.code === 0) {
    await loginUserStore.fecthLoginUser();
    message.success("登录成功");
    router.push({
      path: "/",
      replace: true,
    });
  } else {
    message.error("登录失败：" + res.data.message);
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
