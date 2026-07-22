import { createApp } from "vue";

import { setUnauthorizedHandler } from "@/shared/api/interceptors";
import { invalidateUserCache } from "@/shared/auth/session";

import App from "./App.vue";
import router from "./router";

setUnauthorizedHandler(() => {
  invalidateUserCache();

  if (router.currentRoute.value.name === "login") return;

  void router.replace({
    name: "login",
    query: { redirect: router.currentRoute.value.fullPath },
  });
});

createApp(App).use(router).mount("#app");
