<template>
  <div id="userCenterPage">
    <section class="profile-hero">
      <div class="hero-main">
        <div class="hero-user">
          <a-avatar :size="76" :image-url="loginUser.userAvatar">
            {{ getInitial(loginUser.userName) }}
          </a-avatar>
          <div>
            <h1>{{ loginUser.userName || text.defaultUserName }}</h1>
            <p>{{ loginUser.userProfile || text.defaultUserProfile }}</p>
          </div>
        </div>

        <div class="hero-actions">
          <a-button
            type="primary"
            size="large"
            @click="router.push('/add/app')"
          >
            {{ text.createApp }}
          </a-button>
          <a-button size="large" @click="router.push('/answer/my')">
            {{ text.viewAnswers }}
          </a-button>
        </div>
      </div>

      <div class="stats-grid">
        <div class="stat-card">
          <span class="stat-label">{{ text.myApps }}</span>
          <strong>{{ appTotal }}</strong>
        </div>
        <div class="stat-card">
          <span class="stat-label">{{ text.myAnswers }}</span>
          <strong>{{ answerTotal }}</strong>
        </div>
        <div class="stat-card">
          <span class="stat-label">{{ text.accountRole }}</span>
          <strong>{{ roleText }}</strong>
        </div>
      </div>
    </section>

    <section class="center-grid">
      <a-card class="panel-card" :title="text.profileTitle">
        <a-form
          layout="vertical"
          :model="profileForm"
          @submit="handleProfileSubmit"
        >
          <a-form-item field="userName" :label="text.nickNameLabel">
            <a-input
              v-model="profileForm.userName"
              :placeholder="text.nickNamePlaceholder"
            />
          </a-form-item>
          <a-form-item field="userProfile" :label="text.profileLabel">
            <a-textarea
              v-model="profileForm.userProfile"
              :placeholder="text.profilePlaceholder"
              :max-length="120"
              allow-clear
            />
          </a-form-item>
          <a-form-item field="userAvatar" :label="text.avatarLabel">
            <a-input
              v-model="profileForm.userAvatar"
              :placeholder="text.avatarPlaceholder"
              allow-clear
            />
          </a-form-item>
          <a-form-item>
            <a-space>
              <a-button
                type="primary"
                html-type="submit"
                :loading="savingProfile"
              >
                {{ text.saveProfile }}
              </a-button>
              <a-button @click="fillProfileForm">
                {{ text.resetProfile }}
              </a-button>
            </a-space>
          </a-form-item>
        </a-form>
      </a-card>

      <a-card class="panel-card" :title="text.quickEntryTitle">
        <div class="quick-actions">
          <button
            class="quick-card"
            type="button"
            @click="router.push('/add/app')"
          >
            <strong>{{ text.createApp }}</strong>
            <span>{{ text.createAppDesc }}</span>
          </button>
          <button
            class="quick-card"
            type="button"
            @click="router.push('/answer/my')"
          >
            <strong>{{ text.myAnswers }}</strong>
            <span>{{ text.myAnswersDesc }}</span>
          </button>
          <button class="quick-card" type="button" @click="router.push('/')">
            <strong>{{ text.backHome }}</strong>
            <span>{{ text.backHomeDesc }}</span>
          </button>
        </div>
      </a-card>
    </section>

    <section class="list-grid">
      <a-card class="panel-card" :title="text.myApps">
        <div v-if="myApps.length" class="item-list">
          <button
            v-for="item in myApps"
            :key="item.id"
            class="list-item"
            type="button"
            @click="openApp(item.id)"
          >
            <div>
              <strong>{{ item.appName || text.defaultAppName }}</strong>
              <p>{{ item.appDesc || text.defaultAppDesc }}</p>
            </div>
            <span class="item-meta">
              {{ formatTime(item.createTime, "MM-DD") }}
            </span>
          </button>
        </div>
        <a-empty v-else :description="text.emptyApps" />
      </a-card>

      <a-card class="panel-card" :title="text.recentAnswers">
        <div v-if="myAnswers.length" class="item-list">
          <button
            v-for="item in myAnswers"
            :key="item.id"
            class="list-item"
            type="button"
            @click="openAnswer(item.id)"
          >
            <div>
              <strong>{{ item.resultName || text.defaultResultName }}</strong>
              <p>{{ item.resultDesc || text.defaultResultDesc }}</p>
            </div>
            <span class="item-meta">
              {{ formatTime(item.createTime, "MM-DD") }}
            </span>
          </button>
        </div>
        <a-empty v-else :description="text.emptyAnswers" />
      </a-card>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watchEffect } from "vue";
import { useRouter } from "vue-router";
import { Message } from "@arco-design/web-vue";
import { dayjs } from "@arco-design/web-vue/es/_utils/date";
import { useLoginUserStore } from "@/store/userStore";
import { updateMyUserUsingPost } from "@/api/userController";
import { listMyAppVoByPageUsingPost } from "@/api/appController";
import { listMyUserAnswerVoByPageUsingPost } from "@/api/userAnswerController";
import ACCESS_ENUM from "@/access/accessEnum";

const text = {
  defaultUserName: "\u672a\u547d\u540d\u7528\u6237",
  defaultUserProfile:
    "\u5728\u8fd9\u91cc\u7ba1\u7406\u4f60\u7684\u8d44\u6599\u3001\u5e94\u7528\u548c\u7b54\u9898\u8bb0\u5f55\u3002",
  createApp: "\u521b\u5efa\u5e94\u7528",
  viewAnswers: "\u67e5\u770b\u7b54\u9898\u8bb0\u5f55",
  myApps: "\u6211\u7684\u5e94\u7528",
  myAnswers: "\u6211\u7684\u7b54\u9898",
  recentAnswers: "\u6700\u8fd1\u7b54\u9898",
  accountRole: "\u8d26\u53f7\u89d2\u8272",
  profileTitle: "\u4e2a\u4eba\u8d44\u6599",
  nickNameLabel: "\u6635\u79f0",
  nickNamePlaceholder: "\u8bf7\u8f93\u5165\u6635\u79f0",
  profileLabel: "\u4e2a\u4eba\u7b80\u4ecb",
  profilePlaceholder:
    "\u4ecb\u7ecd\u4e00\u4e0b\u4f60\u81ea\u5df1\u6216\u4f60\u7684\u5e94\u7528\u65b9\u5411",
  avatarLabel: "\u5934\u50cf\u94fe\u63a5",
  avatarPlaceholder:
    "\u8f93\u5165\u56fe\u7247\u94fe\u63a5\u540e\u53ef\u66f4\u65b0\u5934\u50cf",
  saveProfile: "\u4fdd\u5b58\u8d44\u6599",
  resetProfile: "\u91cd\u7f6e",
  quickEntryTitle: "\u5feb\u6377\u5165\u53e3",
  createAppDesc:
    "\u53d1\u5e03\u65b0\u7684\u95ee\u7b54\u6216\u6d4b\u8bc4\u5185\u5bb9",
  myAnswersDesc:
    "\u67e5\u770b\u6700\u8fd1\u7684\u6d4b\u8bd5\u7ed3\u679c\u548c\u8bb0\u5f55",
  backHome: "\u8fd4\u56de\u9996\u9875",
  backHomeDesc:
    "\u7ee7\u7eed\u6d4f\u89c8\u5df2\u4e0a\u7ebf\u7684\u5e94\u7528\u5185\u5bb9",
  defaultAppName: "\u672a\u547d\u540d\u5e94\u7528",
  defaultAppDesc:
    "\u8fd9\u4e2a\u5e94\u7528\u8fd8\u6ca1\u6709\u8865\u5145\u7b80\u4ecb\u3002",
  emptyApps: "\u4f60\u8fd8\u6ca1\u6709\u521b\u5efa\u5e94\u7528",
  defaultResultName: "\u672a\u547d\u540d\u7ed3\u679c",
  defaultResultDesc:
    "\u8fd9\u6761\u7b54\u9898\u8bb0\u5f55\u8fd8\u6ca1\u6709\u8be6\u7ec6\u63cf\u8ff0\u3002",
  emptyAnswers: "\u4f60\u8fd8\u6ca1\u6709\u7b54\u9898\u8bb0\u5f55",
  admin: "\u7ba1\u7406\u5458",
  user: "\u666e\u901a\u7528\u6237",
  updateSuccess: "\u4e2a\u4eba\u8d44\u6599\u5df2\u66f4\u65b0",
  updateFailPrefix: "\u66f4\u65b0\u5931\u8d25\uff1a",
  loadAppsFailPrefix: "\u83b7\u53d6\u6211\u7684\u5e94\u7528\u5931\u8d25\uff1a",
  loadAnswersFailPrefix:
    "\u83b7\u53d6\u7b54\u9898\u8bb0\u5f55\u5931\u8d25\uff1a",
};

const router = useRouter();
const loginUserStore = useLoginUserStore();
const savingProfile = ref(false);
const appTotal = ref(0);
const answerTotal = ref(0);
const myApps = ref<API.AppVO[]>([]);
const myAnswers = ref<API.UserAnswerVO[]>([]);

const loginUser = computed(() => loginUserStore.loginUser);
const roleText = computed(() => {
  if (loginUser.value.userRole === ACCESS_ENUM.ADMIN) {
    return text.admin;
  }
  return text.user;
});

const profileForm = reactive<API.UserUpdateMyRequest>({
  userAvatar: "",
  userName: "",
  userProfile: "",
});

const fillProfileForm = () => {
  profileForm.userAvatar = loginUser.value.userAvatar || "";
  profileForm.userName = loginUser.value.userName || "";
  profileForm.userProfile = loginUser.value.userProfile || "";
};

const loadCenterData = async () => {
  const [appRes, answerRes] = await Promise.all([
    listMyAppVoByPageUsingPost({
      current: 1,
      pageSize: 4,
      sortField: "createTime",
      sortOrder: "descend",
    }),
    listMyUserAnswerVoByPageUsingPost({
      current: 1,
      pageSize: 4,
      sortField: "createTime",
      sortOrder: "descend",
    }),
  ]);

  if (appRes.data.code === 0) {
    myApps.value = appRes.data.data?.records || [];
    appTotal.value = appRes.data.data?.total || 0;
  } else {
    Message.error(text.loadAppsFailPrefix + appRes.data.message);
  }

  if (answerRes.data.code === 0) {
    myAnswers.value = answerRes.data.data?.records || [];
    answerTotal.value = answerRes.data.data?.total || 0;
  } else {
    Message.error(text.loadAnswersFailPrefix + answerRes.data.message);
  }
};

const handleProfileSubmit = async () => {
  savingProfile.value = true;
  const res = await updateMyUserUsingPost({
    userAvatar: profileForm.userAvatar,
    userName: profileForm.userName,
    userProfile: profileForm.userProfile,
  });
  if (res.data.code === 0) {
    await loginUserStore.fecthLoginUser();
    fillProfileForm();
    Message.success(text.updateSuccess);
  } else {
    Message.error(text.updateFailPrefix + res.data.message);
  }
  savingProfile.value = false;
};

const getInitial = (textValue?: string) => {
  return textValue?.slice(0, 1) ?? "\u9c7c";
};

const formatTime = (time?: string, format = "YYYY-MM-DD") => {
  if (!time) {
    return "--";
  }
  return dayjs(time).format(format);
};

const openApp = (id?: number) => {
  if (!id) {
    return;
  }
  router.push(`/app/detail/${id}`);
};

const openAnswer = (id?: number) => {
  if (!id) {
    return;
  }
  router.push(`/answer/result/${id}`);
};

watchEffect(() => {
  fillProfileForm();
  loadCenterData();
});
</script>

<style scoped>
#userCenterPage {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.profile-hero,
.panel-card {
  border: 1px solid rgba(255, 255, 255, 0.88);
  background: linear-gradient(
    145deg,
    rgba(255, 255, 255, 0.92),
    rgba(244, 245, 247, 0.9)
  );
  box-shadow: var(--shadow-lg);
}

.profile-hero {
  padding: 28px;
  border-radius: 30px;
}

.hero-main {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.hero-user {
  display: flex;
  align-items: center;
  gap: 18px;
}

.hero-user h1 {
  margin: 0 0 8px;
  font-size: clamp(30px, 4vw, 44px);
}

.hero-user p {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.7;
}

.hero-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  margin-top: 24px;
}

.stat-card {
  padding: 18px 20px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.72);
  border: 1px solid var(--line-soft);
}

.stat-label {
  display: block;
  margin-bottom: 10px;
  color: var(--text-secondary);
  font-size: 13px;
}

.stat-card strong {
  font-size: 24px;
}

.center-grid,
.list-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 24px;
}

.quick-actions {
  display: grid;
  gap: 12px;
}

.quick-card,
.list-item {
  width: 100%;
  border: 1px solid var(--line-soft);
  background: rgba(255, 255, 255, 0.76);
  border-radius: 20px;
  padding: 18px;
  text-align: left;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.quick-card:hover,
.list-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.quick-card strong,
.list-item strong {
  display: block;
  margin-bottom: 8px;
  font-size: 16px;
}

.quick-card span,
.list-item p {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.6;
}

.item-list {
  display: grid;
  gap: 12px;
}

.list-item {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.item-meta {
  color: var(--text-secondary);
  font-size: 13px;
  white-space: nowrap;
}

@media (max-width: 980px) {
  .hero-main,
  .center-grid,
  .list-grid,
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .hero-main {
    flex-direction: column;
    align-items: flex-start;
  }

  .hero-user {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
