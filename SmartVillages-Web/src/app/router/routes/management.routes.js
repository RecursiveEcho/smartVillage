export const managementRoutes = [
  {
    path: "/cadre",
    component: () => import("@/app/layouts/CadreLayout.vue"),
    meta: {
      requiresAuth: true,
      roles: ["CADRE"],
    },
    children: [
      {
        path: "",
        name: "CadreDashboard",
        component: () => import("@/pages/admin/CadreDashboardPage.vue"),
        meta: { title: "干部首页" },
      },
      {
        path: "announcements",
        name: "CadreAnnouncements",
        component: () => import("@/pages/admin/AnnouncementManagePage.vue"),
        meta: { title: "公告管理" },
      },
      {
        path: "affairs",
        name: "CadreAffairs",
        component: () => import("@/pages/admin/CadreAffairsPage.vue"),
        meta: { title: "公示事项" },
      },
      {
        path: "population",
        name: "CadrePopulation",
        component: () => import("@/pages/admin/CadrePopulationPage.vue"),
        meta: { title: "人口台账" },
      },
      {
        path: "house-land",
        name: "CadreHouseLand",
        component: () => import("@/pages/admin/CadreHouseLandPage.vue"),
        meta: { title: "房屋土地台账" },
      },
      {
        path: "party",
        name: "CadreParty",
        component: () => import("@/pages/admin/CadrePartyPage.vue"),
        meta: { title: "党建组织信息" },
      },
      {
        path: "features",
        name: "CadreFeatures",
        component: () => import("@/pages/admin/CadreFeaturesPage.vue"),
        meta: { title: "风采管理" },
      },
      {
        path: "interactions",
        name: "CadreInteractions",
        component: () => import("@/pages/admin/CadreMessagesPage.vue"),
        meta: { title: "留言处理" },
      },
      {
        path: "tickets",
        name: "CadreTickets",
        component: () => import("@/pages/admin/CadreTicketsPage.vue"),
        meta: { title: "工单管理" },
      },
      {
        path: "media",
        name: "CadreMedia",
        component: () => import("@/pages/admin/MediaManagePage.vue"),
        meta: { title: "媒体管理" },
      },
    ],
  },
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
        name: "VillageHome",
        component: () => import("@/pages/village/VillageHomePage.vue"),
        meta: { title: "村民首页" },
      },
      {
        path: "tickets",
        name: "VillageTickets",
        component: () => import("@/pages/village/VillageTicketsPage.vue"),
        meta: { title: "我的工单" },
      },
      {
        path: "tickets/new",
        name: "VillageTicketNew",
        component: () => import("@/pages/village/VillageTicketNewPage.vue"),
        meta: { title: "提交工单" },
      },
      {
        path: "tickets/:id",
        name: "VillageTicketDetail",
        component: () => import("@/pages/village/VillageTicketDetailPage.vue"),
        meta: { title: "工单详情" },
      },
      {
        path: "messages",
        name: "VillageMessages",
        component: () => import("@/pages/village/VillageMessagesPage.vue"),
        meta: { title: "我的留言" },
      },
    ],
  },
];
