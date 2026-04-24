<template>
  <div id="userLayout">
    <div class="auth-shell">
      <section class="brand-panel">
        <div class="brand-content">
          <div class="logo-badge">
            <img class="logo" src="../assets/logo.png" alt="猪小问" />
          </div>
          <div class="eyebrow">猪小问</div>
          <h1 class="title">把问答流程做得更清爽，也更专业。</h1>
          <p class="description">
            登录后你可以创建测评应用、配置题目、沉淀评分结果，并用更好的视觉体验承接整个答题过程。
          </p>
        </div>
      </section>

      <section class="form-panel">
        <div class="form-card">
          <router-view />
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts"></script>

<style scoped>
#userLayout {
  min-height: 100vh;
  padding: 24px;
}

.auth-shell {
  display: grid;
  grid-template-columns: minmax(0, 1.05fr) minmax(420px, 0.95fr);
  min-height: calc(100vh - 48px);
  border-radius: 34px;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.9);
  box-shadow: var(--shadow-lg);
}

.brand-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
  background: radial-gradient(
      circle at top right,
      rgba(210, 214, 219, 0.2),
      transparent 28%
    ),
    linear-gradient(160deg, rgba(79, 87, 97, 0.94), rgba(35, 39, 45, 0.98));
  color: white;
}

.brand-content {
  max-width: 520px;
}

.logo-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 78px;
  height: 78px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(12px);
  margin-bottom: 24px;
}

.logo {
  width: 46px;
  height: 46px;
}

.eyebrow {
  margin-bottom: 12px;
  font-size: 14px;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.74);
}

.title {
  margin: 0;
  font-size: clamp(32px, 3.5vw, 54px);
  line-height: 1.08;
}

.description {
  margin: 22px 0 0;
  font-size: 16px;
  line-height: 1.9;
  color: rgba(255, 255, 255, 0.78);
}

.form-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 36px;
  background: linear-gradient(
    180deg,
    rgba(255, 255, 255, 0.82),
    rgba(244, 245, 247, 0.94)
  );
}

.form-card {
  width: min(100%, 520px);
  padding: 34px 30px;
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(255, 255, 255, 0.96);
  box-shadow: 0 20px 50px rgba(35, 39, 45, 0.12);
}

@media (max-width: 980px) {
  #userLayout {
    padding: 12px;
  }

  .auth-shell {
    grid-template-columns: 1fr;
    min-height: auto;
  }

  .brand-panel,
  .form-panel {
    padding: 24px 20px;
  }

  .form-card {
    padding: 28px 20px;
  }
}
</style>
