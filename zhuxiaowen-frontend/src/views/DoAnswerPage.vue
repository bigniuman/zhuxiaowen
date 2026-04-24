<template>
  <div id="doAnswerPage">
    <a-card class="answer-card">
      <div class="answer-header">
        <h1>{{ app.appName }}</h1>
        <p>{{ app.appDesc }}</p>
      </div>

      <template v-if="questionContent.length">
        <h2 class="question-title">
          {{ current }}. {{ currentQuestion?.title }}
        </h2>

        <a-radio-group
          v-model="currentAnswer"
          direction="vertical"
          :options="questionOptions"
          @change="doRadioChange"
        />

        <div class="action-row">
          <a-space size="large">
            <a-button v-if="current > 1" circle @click="current -= 1">
              上一题
            </a-button>

            <a-button
              v-if="current < questionContent.length"
              type="primary"
              circle
              :disabled="!currentAnswer"
              @click="current += 1"
            >
              下一题
            </a-button>

            <a-button
              v-else
              type="primary"
              circle
              :loading="submitting"
              :disabled="!currentAnswer"
              @click="doSubmit"
            >
              {{ submitting ? "评分中" : "查看结果" }}
            </a-button>
          </a-space>
        </div>
      </template>

      <a-empty v-else description="暂无可用题目" />
    </a-card>
  </div>
</template>

<script setup lang="ts">
import {
  computed,
  defineProps,
  reactive,
  ref,
  watchEffect,
  withDefaults,
} from "vue";
import { useRouter } from "vue-router";
import message from "@arco-design/web-vue/es/message";
import API from "@/api";
import { getAppVoByIdUsingGet } from "@/api/appController";
import { listQuestionVoByPageUsingPost } from "@/api/questionController";
import { addUserAnswerUsingPost } from "@/api/userAnswerController";

interface Props {
  appId: string;
}

const props = withDefaults(defineProps<Props>(), {
  appId: "",
});

const router = useRouter();

const app = ref<API.AppVO>({});
const questionContent = ref<API.QuestionContentDTO[]>([]);
const current = ref(1);
const currentQuestion = ref<API.QuestionContentDTO>({});
const currentAnswer = ref<string>();
const answerList = reactive<string[]>([]);
const submitting = ref(false);

const questionOptions = computed(() => {
  return (
    currentQuestion.value?.options?.map((option) => ({
      label: `${option.key}. ${option.value}`,
      value: option.key,
    })) ?? []
  );
});

const loadData = async () => {
  if (!props.appId) {
    return;
  }

  let res = await getAppVoByIdUsingGet({
    id: props.appId as any,
  });

  if (res.data.code === 0) {
    app.value = res.data.data as API.AppVO;
  } else {
    message.error(`获取应用失败：${res.data.message}`);
  }

  res = await listQuestionVoByPageUsingPost({
    appId: props.appId as any,
    current: 1,
    pageSize: 1,
    sortField: "createTime",
    sortOrder: "descend",
  });

  if (res.data.code === 0 && res.data.data?.records?.length) {
    questionContent.value = res.data.data.records[0].questionContent ?? [];
  } else if (res.data.code !== 0) {
    message.error(`获取题目失败：${res.data.message}`);
  }
};

watchEffect(() => {
  loadData();
});

watchEffect(() => {
  currentQuestion.value = questionContent.value[current.value - 1] ?? {};
  currentAnswer.value = answerList[current.value - 1];
});

const doRadioChange = (value: string | number | boolean) => {
  answerList[current.value - 1] = String(value);
};

const doSubmit = async () => {
  if (!props.appId || answerList.length === 0) {
    return;
  }

  submitting.value = true;

  try {
    const res = await addUserAnswerUsingPost({
      appId: props.appId as any,
      choices: [...answerList],
    });

    if (res.data.code === 0 && res.data.data) {
      router.push(`/answer/result/${res.data.data}`);
      return;
    }

    message.error(`提交答案失败：${res.data.message}`);
  } finally {
    submitting.value = false;
  }
};
</script>

<style scoped>
#doAnswerPage {
  max-width: 880px;
  margin: 0 auto;
}

.answer-card {
  border-radius: 24px;
}

.answer-header {
  margin-bottom: 24px;
}

.answer-header h1 {
  margin: 0 0 10px;
  font-size: 30px;
}

.answer-header p {
  margin: 0;
  color: var(--text-secondary);
}

.question-title {
  margin-bottom: 20px;
  font-size: 22px;
  color: var(--text-primary);
}

.action-row {
  margin-top: 28px;
}
</style>
