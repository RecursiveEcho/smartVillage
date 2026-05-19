export const interactionRoutes = [
  {
    path: "interactions",
    name: "Interactions",
    component: () => import("@/pages/public/InteractionPage.vue"),
    meta: {
      title: "村民互动",
    },
  },
  {
    path: "my/interactions",
    name: "MyInteractions",
    component: () => import("@/pages/public/InteractionPage.vue"),
    meta: {
      requiresAuth: true,
      roles: ["VILLAGER"],
      title: "我的留言",
    },
  },
]
