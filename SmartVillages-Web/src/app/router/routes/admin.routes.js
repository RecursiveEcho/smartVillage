export const adminRoutes = [
  {
    path: "/admin",
    component: () => import("@/app/layouts/AdminLayout.vue"),
    meta: {
      requiresAuth: true,
      roles: ["ADMIN"],
    },
    children: [
      {
        path: "",
        name: "AdminDashboard",
        component: () => import("@/pages/admin/DashboardPage.vue"),
        meta: { title: "管理员首页" },
      },
      {
        path: "users",
        name: "AdminUsers",
        component: () => import("@/pages/admin/AdminUsersPage.vue"),
        meta: { title: "用户管理" },
      },
    ],
  },
];
