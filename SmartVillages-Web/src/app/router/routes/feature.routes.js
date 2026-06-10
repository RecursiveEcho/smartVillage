export const featureRoutes = [
  {
    path: "features",
    name: "Features",
    component: () => import("@/pages/public/FeatureListPage.vue"),
    meta: { title: "乡村风采" },
  },
  {
    path: "features/:id",
    name: "FeatureDetail",
    component: () => import("@/pages/public/FeatureDetailPage.vue"),
    meta: { title: "风采详情" },
  },
  {
    path: "affairs",
    name: "Affairs",
    component: () => import("@/pages/public/AffairListPage.vue"),
    meta: { title: "村务公开" },
  },
  {
    path: "affairs/:id",
    name: "AffairDetail",
    component: () => import("@/pages/public/AffairDetailPage.vue"),
    meta: { title: "村务详情" },
  },
];
