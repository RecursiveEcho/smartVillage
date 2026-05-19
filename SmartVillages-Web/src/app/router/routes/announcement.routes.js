export const announcementRoutes = [
  {
    path: "announcements",
    name: "Announcements",
    component: () => import("@/pages/public/Announcement.vue"),
    meta: {
      title: "公告列表",
    },
  },
  {
    path: "announcements/:id",
    name: "AnnouncementDetail",
    component: () => import("@/pages/public/Announcement.vue"),
    meta: {
      title: "公告详情",
    },
  },
]
