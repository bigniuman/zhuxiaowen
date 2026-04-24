<template>
  <a-card class="appCard" hoverable @click="doCardClick">
    <div class="cover">
      <img
        v-if="app.appIcon"
        class="cover-image"
        :alt="app.appName"
        :src="app.appIcon"
      />
      <div v-else class="cover-fallback">
        <span>{{ getAppInitial(app.appName) }}</span>
      </div>
      <div class="cover-overlay"></div>
    </div>

    <div class="card-body">
      <div class="card-tags">
        <a-tag size="small" color="gray">
          {{ getAppTypeLabel(app.appType) }}
        </a-tag>
        <a-tag size="small" color="gray">
          {{ getScoringStrategyLabel(app.scoringStrategy) }}
        </a-tag>
      </div>

      <h3 class="card-title">{{ app.appName || "未命名应用" }}</h3>
      <p class="card-desc">{{ app.appDesc || "这个应用还没有补充简介。" }}</p>

      <div class="card-footer">
        <div class="author">
          <a-avatar :size="28" :image-url="app.user?.userAvatar">
            {{ getAppInitial(app.user?.userName) }}
          </a-avatar>
          <span>{{ app.user?.userName ?? "匿名作者" }}</span>
        </div>

        <button class="share-btn" type="button" @click.stop="doShare">
          <IconShareInternal />
        </button>
      </div>
    </div>
  </a-card>
</template>

<script setup lang="ts">
import { IconShareInternal } from "@arco-design/web-vue/es/icon";
import { Message } from "@arco-design/web-vue";
import { APP_SCORING_STRATEGY_MAP, APP_TYPE_MAP } from "@/constant/app";
import { defineProps, withDefaults } from "vue";
import { useRouter } from "vue-router";

interface Props {
  app: API.AppVO;
}

const props = withDefaults(defineProps<Props>(), {
  app: () => {
    return {};
  },
});

const router = useRouter();

const doCardClick = () => {
  router.push(`/app/detail/${props.app.id}`);
};

const shareLink = `${window.location.protocol}//${window.location.host}/app/detail/${props.app.id}`;

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

const getAppInitial = (text?: string) => {
  return text?.slice(0, 1) ?? "鱼";
};

const doShare = async () => {
  try {
    await navigator.clipboard.writeText(shareLink);
    Message.success("应用链接已复制");
  } catch (error) {
    Message.info(`请手动复制链接：${shareLink}`);
  }
};
</script>

<style scoped>
.appCard {
  cursor: pointer;
  overflow: hidden;
  margin: 0;
  border: 1px solid rgba(255, 255, 255, 0.82);
  background: linear-gradient(
    180deg,
    rgba(255, 255, 255, 0.92),
    rgba(244, 245, 247, 0.9)
  );
  transition: transform 0.22s ease, box-shadow 0.22s ease;
}

.appCard:hover {
  transform: translateY(-6px);
  box-shadow: 0 22px 40px rgba(35, 39, 45, 0.1);
}

.cover {
  position: relative;
  height: 200px;
  margin: -20px -20px 0;
  overflow: hidden;
}

.cover-image,
.cover-fallback {
  width: 100%;
  height: 100%;
}

.cover-image {
  object-fit: cover;
}

.cover-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--brand-500), var(--brand-800));
  color: white;
  font-size: 56px;
  font-weight: 700;
}

.cover-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(0, 0, 0, 0.04), rgba(0, 0, 0, 0.22));
}

.card-body {
  padding-top: 18px;
}

.card-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 14px;
}

.card-title {
  margin: 0 0 10px;
  font-size: 20px;
  line-height: 1.3;
  color: var(--text-primary);
}

.card-desc {
  margin: 0;
  min-height: 48px;
  color: var(--text-secondary);
  line-height: 1.7;
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 22px;
}

.author {
  display: flex;
  align-items: center;
  gap: 10px;
  color: var(--text-primary);
  font-weight: 600;
}

.share-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border: none;
  border-radius: 999px;
  background: rgba(139, 148, 158, 0.12);
  color: var(--brand-700);
  cursor: pointer;
  transition: all 0.2s ease;
}

.share-btn:hover {
  background: rgba(139, 148, 158, 0.2);
  transform: scale(1.04);
}
</style>
