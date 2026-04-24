<template>
  <div id="globalHeader">
    <div class="header-card">
      <div class="brand" @click="goHome">
        <div class="brand-mark">
          <img class="logo" src="../assets/logo.png" alt="猪小问" />
        </div>
        <div>
          <div class="title">猪小问</div>
          <div class="subtitle">智能问答与测评平台</div>
        </div>
      </div>

      <div class="nav-list">
        <button
          v-for="item in visibleRoutes"
          :key="item.path"
          class="nav-item"
          :class="{ active: isRouteActive(item.path) }"
          type="button"
          @click="doMenuClick(item.path)"
        >
          {{ getRouteLabel(item.path, item.name) }}
        </button>
      </div>

      <div class="user-area">
        <template v-if="loginUserStore.loginUser.id">
          <button class="user-chip" type="button" @click="goProfile">
            <a-avatar
              :size="30"
              :image-url="loginUserStore.loginUser.userAvatar"
            >
              {{ getUserInitial(loginUserStore.loginUser.userName) }}
            </a-avatar>
            <div class="user-meta">
              <span class="user-name">
                {{ loginUserStore.loginUser.userName ?? "已登录用户" }}
              </span>
              <span class="user-role">欢迎回来</span>
            </div>
          </button>
          <a-button class="ghost-button" @click="handleLogout">
            退出登录
          </a-button>
        </template>
        <template v-else>
          <a-space>
            <a-button class="ghost-button" @click="router.push('/user/login')">
              登录
            </a-button>
            <a-button type="primary" @click="router.push('/user/register')">
              免费注册
            </a-button>
          </a-space>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { routes } from "@/router/routes";
import { useRoute, useRouter } from "vue-router";
import { computed } from "vue";
import { useLoginUserStore } from "@/store/userStore";
import checkAccess from "@/access/CheckAccess";
import { Message } from "@arco-design/web-vue";
import ACCESS_ENUM from "@/access/accessEnum";

const router = useRouter();
const route = useRoute();
const loginUserStore = useLoginUserStore();

const routeLabelMap: Record<string, string> = {
  "/": "发现应用",
  "/profile": "个人中心",
  "/add/app": "创建应用",
  "/admin/user": "用户管理",
  "/admin/app": "应用管理",
  "/admin/question": "题目管理",
  "/admin/scoring_result": "评分管理",
  "/admin/answer": "答题管理",
};

const visibleRoutes = computed(() => {
  return routes.filter((item) => {
    if (item.meta?.hideInMenu) return false;
    if (!checkAccess(loginUserStore.loginUser, item.meta?.access as string)) {
      return false;
    }
    return true;
  });
});

const doMenuClick = (key: string) => {
  router.push({
    path: key,
  });
};

const goHome = () => {
  router.push("/");
};

const goProfile = () => {
  router.push("/profile");
};

const isRouteActive = (path?: string) => {
  if (!path) {
    return false;
  }
  if (path === "/") {
    return route.path === "/";
  }
  return route.path.startsWith(path);
};

const getRouteLabel = (path?: string, name?: string) => {
  if (!path) {
    return name ?? "";
  }
  return routeLabelMap[path] ?? String(name ?? "");
};

const getUserInitial = (userName?: string) => {
  return userName?.slice(0, 1) ?? "鱼";
};

const handleLogout = () => {
  loginUserStore.setLoginUser({
    userRole: ACCESS_ENUM.NOT_LOGIN,
  });
  Message.success("已退出登录");
  router.push("/user/login");
};
</script>

<style scoped>
#globalHeader {
  width: 100%;
}

.header-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  padding: 18px 22px;
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.88);
  background: linear-gradient(
    135deg,
    rgba(255, 255, 255, 0.84),
    rgba(244, 245, 247, 0.92)
  );
  backdrop-filter: blur(18px);
  box-shadow: var(--shadow-lg);
}

.brand {
  display: flex;
  align-items: center;
  gap: 14px;
  cursor: pointer;
  flex-shrink: 0;
}

.brand-mark {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 52px;
  height: 52px;
  border-radius: 18px;
  background: linear-gradient(
    135deg,
    var(--brand-100),
    rgba(255, 255, 255, 0.95)
  );
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.8);
}

.logo {
  width: 34px;
  height: 34px;
}

.title {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
}

.subtitle {
  margin-top: 2px;
  font-size: 12px;
  color: var(--text-secondary);
}

.nav-list {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  flex: 1;
  flex-wrap: wrap;
}

.nav-item {
  border: none;
  background: transparent;
  padding: 10px 16px;
  border-radius: 999px;
  color: var(--text-secondary);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.nav-item:hover {
  color: var(--brand-700);
  background: rgba(139, 148, 158, 0.12);
}

.nav-item.active {
  color: white;
  background: linear-gradient(135deg, var(--brand-500), var(--brand-700));
  box-shadow: 0 10px 20px rgba(79, 87, 97, 0.14);
}

.user-area {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.user-chip {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 10px 8px 8px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(59, 65, 72, 0.08);
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.user-chip:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.user-meta {
  display: flex;
  flex-direction: column;
  line-height: 1.1;
}

.user-name {
  font-size: 13px;
  font-weight: 700;
}

.user-role {
  font-size: 12px;
  color: var(--text-secondary);
}

.ghost-button {
  border-radius: 999px;
  border-color: rgba(79, 87, 97, 0.14);
  background: rgba(255, 255, 255, 0.74);
  color: var(--brand-700);
}

@media (max-width: 1024px) {
  .header-card {
    flex-direction: column;
    align-items: stretch;
  }

  .nav-list {
    justify-content: flex-start;
  }

  .user-area {
    justify-content: space-between;
    flex-wrap: wrap;
  }
}

@media (max-width: 640px) {
  .header-card {
    padding: 16px;
    border-radius: 20px;
  }

  .brand {
    width: 100%;
  }

  .nav-list {
    gap: 8px;
  }

  .nav-item {
    padding: 9px 14px;
    font-size: 13px;
  }

  .user-area {
    width: 100%;
    justify-content: flex-start;
  }
}
</style>
