export const managementRoutes = [
  {
    path: "/cadre",
    component: () => import("@/app/layouts/AdminLayout.vue"),
    meta: {
      requiresAuth: true,
      roles: ["CADRE"],
    },
    children: [
      {
        path: "",
        name: "CadreDashboard",
        component: () => import("@/pages/admin/DashboardPage.vue"),
        meta: {
          title: "干部首页",
        },
      },
      {
        path: "announcements",
        name: "CadreAnnouncements",
        component: () => import("@/pages/admin/AnnouncementManagePage.vue"),
        meta: {
          title: "公告管理",
        },
      },
      {
        path: "interactions",
        name: "CadreInteractions",
        component: () => import("@/pages/admin/ManagementPage.vue"),
        meta: {
          title: "留言处理",
        },
      },
      {
        path: "media",
        name: "CadreMedia",
        component: () => import("@/pages/admin/MediaManagePage.vue"),
        meta: {
          title: "媒体管理",
        },
      },
    ],
  },
]
