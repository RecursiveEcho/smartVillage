export const villageRoutes = [
  {
    path: "/village",
    component: () => import("@/app/layouts/VillageLayout.vue"),
    meta: {
      requiresAuth: true,
      roles: ["VILLAGER"],
    },
    children: [
      {
        path: "",
        name: "VillageDashboard",
        component: () => import("@/pages/village/DashboardPage.vue"),
        meta: {
          title: "村民首页",
        },
      },
      {
        path: "services",
        name: "VillageServices",
        component: () => import("@/pages/village/ServicePage.vue"),
        meta: {
          title: "民生服务",
        },
      },
    ],
  },
]
