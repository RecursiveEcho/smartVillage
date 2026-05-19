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
        meta: {
          title: "管理员首页",
        },
      },
      {
        path: "users",
        name: "AdminUsers",
        component: () => import("@/pages/admin/ManagementPage.vue"),
        meta: {
          title: "用户管理",
        },
      },
      {
        path: "announcements",
        name: "AdminAnnouncements",
        component: () => import("@/pages/admin/AnnouncementManagePage.vue"),
        meta: {
          title: "公告管理",
        },
      },
      {
        path: "media",
        name: "AdminMedia",
        component: () => import("@/pages/admin/MediaManagePage.vue"),
        meta: {
          title: "媒体管理",
        },
      },
    ],
  },
]
