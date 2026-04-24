<template>
  <div id="homePage">
    <section class="hero-section">
      <div class="hero-copy">
        <h1>发现更有质感的答题应用与 AI 测评体验</h1>
        <p>
          从兴趣测试到智能评分，把问答流程做得更轻盈、更清晰，也更适合长期沉淀内容。
        </p>
        <div class="hero-search">
          <a-input-search
            v-model="keyword"
            placeholder="搜索应用名称，比如 MBTI、职业测评、知识问答"
            button-text="搜索"
            size="large"
            search-button
            allow-clear
            @search="onSearch"
          />
        </div>
        <div class="hero-meta">
          <div class="meta-card">
            <span class="meta-label">已收录应用</span>
            <strong>{{ total }}</strong>
          </div>
          <div class="meta-card">
            <span class="meta-label">当前分页</span>
            <strong>{{ searchParams.current }}/{{ totalPages }}</strong>
          </div>
        </div>
      </div>
      <div class="hero-panel">
        <div class="panel-glow"></div>
        <div class="panel-card">
          <div class="panel-title">核心功能导览</div>
          <ul class="panel-list">
            <li>创建问答应用并设置封面与描述</li>
            <li>配置题目、评分结果和答题逻辑</li>
            <li>查看个人答题记录与应用沉淀</li>
          </ul>
          <div class="panel-footer">
            从创建到回看结果，核心流程都能快速进入。
          </div>
        </div>
      </div>
    </section>

    <section class="content-section">
      <div class="section-header">
        <div>
          <h2>热门应用</h2>
          <p>
            {{
              keyword
                ? `与“${keyword}”相关的结果`
                : "为你展示已通过审核的问答应用"
            }}
          </p>
        </div>
        <a-button v-if="keyword" class="clear-btn" @click="clearSearch">
          清空搜索
        </a-button>
      </div>

      <a-list
        class="app-list"
        :grid-props="{ gutter: [20, 20], xs: 24, sm: 12, md: 12, lg: 8, xl: 6 }"
        :bordered="false"
        :data="dataList"
        :loading="loading"
        :pagination-props="{
          pageSize: searchParams.pageSize,
          current: searchParams.current,
          total,
          hideOnSinglePage: total <= Number(searchParams.pageSize || 12),
        }"
        @page-change="onPageChange"
      >
        <template #item="{ item }">
          <AppCard :app="item" />
        </template>
        <template #empty>
          <a-empty description="暂时没有找到符合条件的应用" />
        </template>
      </a-list>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watchEffect } from "vue";
import AppCard from "@/components/AppCard.vue";
import { listAppVoByPageUsingPost } from "@/api/appController";
import message from "@arco-design/web-vue/es/message";
import { REVIEW_STATUS_ENUM } from "@/constant/app";

const initSearchParams = {
  current: 1,
  pageSize: 12,
  sortOrder: "descend",
  sortField: "createTime",
};

const searchParams = ref<API.AppQueryRequest>({
  ...initSearchParams,
});
const dataList = ref<API.AppVO[]>([]);
const total = ref<number>(0);
const keyword = ref("");
const loading = ref(false);

const totalPages = computed(() => {
  const pageSize = Number(searchParams.value.pageSize || 12);
  const count = Math.ceil(total.value / pageSize);
  return count || 1;
});

const loadData = async () => {
  loading.value = true;
  const params = {
    reviewStatus: REVIEW_STATUS_ENUM.PASS,
    ...searchParams.value,
  };
  const res = await listAppVoByPageUsingPost(params);
  if (res.data.code === 0) {
    dataList.value = res.data.data?.records || [];
    total.value = res.data.data?.total || 0;
  } else {
    message.error("获取数据失败：" + res.data.message);
  }
  loading.value = false;
};

const onPageChange = (page: number) => {
  searchParams.value = {
    ...searchParams.value,
    current: page,
  };
};

const onSearch = (value: string) => {
  searchParams.value = {
    ...searchParams.value,
    current: 1,
    appName: value?.trim() || undefined,
  };
};

const clearSearch = () => {
  keyword.value = "";
  onSearch("");
};

watchEffect(() => {
  loadData();
});
</script>

<style scoped>
#homePage {
  display: flex;
  flex-direction: column;
  gap: 26px;
}

.hero-section {
  display: grid;
  grid-template-columns: minmax(0, 1.35fr) minmax(280px, 0.9fr);
  gap: 24px;
  align-items: stretch;
}

.hero-copy,
.hero-panel,
.content-section {
  border: 1px solid rgba(255, 255, 255, 0.9);
  background: linear-gradient(
    145deg,
    rgba(255, 255, 255, 0.92),
    rgba(244, 245, 247, 0.88)
  );
  backdrop-filter: blur(18px);
  box-shadow: var(--shadow-lg);
}

.hero-copy {
  padding: 34px;
  border-radius: 32px;
}

.hero-copy h1 {
  margin: 0 0 14px;
  font-size: clamp(32px, 4vw, 52px);
  line-height: 1.08;
}

.hero-copy p {
  max-width: 680px;
  margin: 0;
  color: var(--text-secondary);
  font-size: 16px;
  line-height: 1.8;
}

.hero-search {
  margin-top: 28px;
  max-width: 640px;
}

.hero-meta {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
  margin-top: 28px;
}

.meta-card {
  padding: 18px 20px;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.72);
  border: 1px solid var(--line-soft);
}

.meta-label {
  display: block;
  margin-bottom: 10px;
  font-size: 13px;
  color: var(--text-secondary);
}

.meta-card strong {
  font-size: 20px;
}

.hero-panel {
  position: relative;
  overflow: hidden;
  border-radius: 32px;
  padding: 22px;
  min-height: 100%;
}

.panel-glow {
  position: absolute;
  inset: auto -15% -20% auto;
  width: 240px;
  height: 240px;
  border-radius: 999px;
  background: radial-gradient(
    circle,
    rgba(139, 148, 158, 0.24),
    transparent 65%
  );
}

.panel-card {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 100%;
  min-height: 280px;
  padding: 18px;
  border-radius: 26px;
  background: linear-gradient(
    180deg,
    rgba(79, 87, 97, 0.92),
    rgba(35, 39, 45, 0.96)
  );
  color: white;
}

.panel-title {
  font-size: 22px;
  font-weight: 700;
}

.panel-list {
  margin: 22px 0;
  padding-left: 20px;
  line-height: 2;
  color: rgba(255, 255, 255, 0.86);
}

.panel-footer {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
}

.content-section {
  padding: 28px;
  border-radius: 32px;
}

.section-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 24px;
}

.section-header h2 {
  margin: 0 0 6px;
  font-size: 28px;
}

.section-header p {
  margin: 0;
  color: var(--text-secondary);
}

.clear-btn {
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.72);
  border-color: rgba(79, 87, 97, 0.14);
  color: var(--brand-700);
}

.app-list :deep(.arco-list-content) {
  overflow: visible;
}

@media (max-width: 960px) {
  .hero-section {
    grid-template-columns: 1fr;
  }

  .hero-meta {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .hero-copy,
  .hero-panel,
  .content-section {
    border-radius: 24px;
  }

  .hero-copy {
    padding: 24px 20px;
  }

  .content-section {
    padding: 20px 16px;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
