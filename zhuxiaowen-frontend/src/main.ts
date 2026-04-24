import { createApp } from "vue";
import App from "./App.vue";
import ArcoVue from "@arco-design/web-vue";
import "@arco-design/web-vue/dist/arco.css";
import "./styles/theme.css";
import { createPinia } from "pinia";
import router from "./router";
import "@/access";

const pinia = createPinia();

createApp(App).use(router).use(pinia).use(ArcoVue).mount("#app");
