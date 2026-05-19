import { createRouter, createWebHistory } from "vue-router"

import { adminRoutes } from "@/app/router/routes/admin.routes"
import { announcementRoutes } from "@/app/router/routes/announcement.routes"
import { authRoutes } from "@/app/router/routes/auth.routes"
import { featureRoutes } from "@/app/router/routes/feature.routes"
import { interactionRoutes } from "@/app/router/routes/interaction.routes"
import { managementRoutes } from "@/app/router/routes/management.routes"
import { getSavedUserRole, isAuthenticated } from "@/shared/auth/guards"

const publicRoutes = [
  {
    path: "/",
    component: () => import("@/app/layouts/PublicLayout.vue"),
    children: [
      {
        path: "",
        name: "Home",
        component: () => import("@/pages/public/HomePage.vue"),
        meta: {
          title: "首页",
        },
      },
      ...announcementRoutes,
      ...featureRoutes,
      ...interactionRoutes,
    ],
  },
]

const routes = [
  ...authRoutes,
  ...publicRoutes,
  ...adminRoutes,
  ...managementRoutes,
  {
    path: "/:pathMatch(.*)*",
    redirect: "/",
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to) => {
  if (to.meta?.guestOnly && isAuthenticated()) {
    return getDefaultRouteByRole(getSavedUserRole())
  }

  if (to.meta?.requiresAuth && !isAuthenticated()) {
    return {
      path: "/login",
      query: {
        redirect: to.fullPath,
      },
    }
  }

  if (to.meta?.roles?.length) {
    const role = getSavedUserRole()

    if (!role || !to.meta.roles.includes(role)) {
      return getDefaultRouteByRole(role)
    }
  }

  return true
})

function getDefaultRouteByRole(role) {
  if (role === "ADMIN") {
    return "/admin"
  }

  if (role === "CADRE") {
    return "/cadre"
  }

  return "/"
}

export default router
