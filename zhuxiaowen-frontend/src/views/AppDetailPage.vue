<template>
  <div id="appDetailPage">
    <a-card class="detail-card">
      <div class="detail-grid">
        <div class="content-wrapper">
          <div class="tag-row">
            <a-tag color="gray">{{ getAppTypeLabel(data.appType) }}</a-tag>
            <a-tag color="gray">
              {{ getScoringStrategyLabel(data.scoringStrategy) }}
            </a-tag>
          </div>
          <h1>{{ data.appName || "未命名应用" }}</h1>
          <p class="summary">
            {{ data.appDesc || "这个应用还没有补充详细描述。" }}
          </p>

          <div class="meta-panel">
            <div class="meta-item">
              <span class="meta-label">作者</span>
              <div class="author-box">
                <a-avatar :size="30" :image-url="data.user?.userAvatar">
                  {{ getInitial(data.user?.userName) }}
                </a-avatar>
                <span>{{ data.user?.userName ?? "匿名作者" }}</span>
              </div>
            </div>
            <div class="meta-item">
              <span class="meta-label">创建时间</span>
              <strong>{{ formatTime(data.createTime) }}</strong>
            </div>
          </div>

          <div class="action-row">
            <a-button type="primary" size="large" :href="`/answer/do/${id}`">
              开始答题
            </a-button>
            <a-button size="large" @click="doShare">复制分享链接</a-button>
            <a-button v-if="isMy" size="large" :href="`/add/question/${id}`">
              设置题目
            </a-button>
            <a-button
              v-if="isMy"
              size="large"
              :href="`/add/scoring_result/${id}`"
            >
              设置评分
            </a-button>
            <a-button v-if="isMy" size="large" :href="`/add/app/${id}`">
              编辑应用
            </a-button>
          </div>
        </div>

        <div class="visual-panel">
          <div class="visual-frame">
            <a-image
              v-if="data.appIcon"
              width="100%"
              height="100%"
              fit="cover"
              :src="data.appIcon"
            />
            <div v-else class="visual-fallback">
              {{ getInitial(data.appName) }}
            </div>
          </div>
        </div>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { computed, defineProps, ref, watchEffect, withDefaults } from "vue";
import { getAppVoByIdUsingGet } from "@/api/appController";
import message from "@arco-design/web-vue/es/message";
import { Message } from "@arco-design/web-vue";
import { dayjs } from "@arco-design/web-vue/es/_utils/date";
import { useLoginUserStore } from "@/store/userStore";
import { APP_SCORING_STRATEGY_MAP, APP_TYPE_MAP } from "@/constant/app";

interface Props {
  id: string;
}

const props = withDefaults(defineProps<Props>(), {
  id: () => {
    return "";
  },
});

const data = ref<API.AppVO>({});
const loginUserStore = useLoginUserStore();
const loginUserId = loginUserStore.loginUser?.id;

const isMy = computed(() => {
  return loginUserId && loginUserId === data.value.userId;
});

const loadData = async () => {
  if (!props.id) {
    return;
  }
  const res = await getAppVoByIdUsingGet({
    id: props.id as any,
  });
  if (res.data.code === 0) {
    data.value = res.data.data as any;
  } else {
    message.error("获取数据失败：" + res.data.message);
  }
};

watchEffect(() => {
  loadData();
});

const shareLink = `${window.location.protocol}//${window.location.host}/app/detail/${props.id}`;

const getAppTypeLabel = (appType?: number) => {
  return APP_TYPE_MAP[appType as keyof typeof APP_TYPE_MAP] ?? "问答应用";
};

const getScoringStrategyLabel = (strategy?: number) => {
  return (
    APP_SCORING_STRATEGY_MAP[
      strategy as keyof typeof APP_SCORING_STRATEGY_MAP
    ] ?? "综合评分"
  );
};

const getInitial = (text?: string) => {
  return text?.slice(0, 1) ?? "鱼";
};

const formatTime = (time?: string) => {
  if (!time) {
    return "暂无时间";
  }
  return dayjs(time).format("YYYY-MM-DD HH:mm:ss");
};

const doShare = async () => {
  try {
    await navigator.clipboard.writeText(shareLink);
    Message.success("链接已复制");
  } catch (error) {
    Message.info(`请手动复制链接：${shareLink}`);
  }
};
</script>

<style scoped>
#appDetailPage {
  padding: 4px 0;
}

.detail-card {
  overflow: hidden;
  border-radius: 32px;
  border: 1px solid rgba(255, 255, 255, 0.88);
  background: linear-gradient(
    145deg,
    rgba(255, 255, 255, 0.94),
    rgba(244, 245, 247, 0.9)
  );
}

.detail-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.15fr) minmax(300px, 0.85fr);
  gap: 32px;
  align-items: center;
}

.content-wrapper h1 {
  margin: 14px 0 14px;
  font-size: clamp(30px, 4vw, 48px);
  line-height: 1.08;
}

.tag-row {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.summary {
  margin: 0;
  color: var(--text-secondary);
  font-size: 16px;
  line-height: 1.9;
}

.meta-panel {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
  margin: 28px 0;
}

.meta-item {
  padding: 18px 20px;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.74);
  border: 1px solid var(--line-soft);
}

.meta-label {
  display: block;
  margin-bottom: 10px;
  font-size: 13px;
  color: var(--text-secondary);
}

.author-box {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
}

.action-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.visual-panel {
  display: flex;
  justify-content: center;
}

.visual-frame {
  width: min(100%, 420px);
  aspect-ratio: 1 / 1;
  border-radius: 30px;
  overflow: hidden;
  background: linear-gradient(135deg, var(--brand-500), var(--brand-800));
  box-shadow: 0 22px 44px rgba(35, 39, 45, 0.1);
}

.visual-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  color: white;
  font-size: 96px;
  font-weight: 700;
}

@media (max-width: 960px) {
  .detail-grid {
    grid-template-columns: 1fr;
  }

  .meta-panel {
    grid-template-columns: 1fr;
  }
}
</style>
