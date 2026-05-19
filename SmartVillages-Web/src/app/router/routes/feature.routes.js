export const featureRoutes = [
  {
    path: "features",
    name: "Features",
    component: () => import("@/pages/public/FeaturePage.vue"),
    meta: {
      title: "特色服务",
    },
  },
]
