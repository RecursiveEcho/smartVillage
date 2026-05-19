import { createRouter, createWebHistory } from "vue-router"

import { adminRoutes } from "@/app/router/routes/admin.routes"
import { announcementRoutes } from "@/app/router/routes/announcement.routes"
import { authRoutes } from "@/app/router/routes/auth.routes"
import { featureRoutes } from "@/app/router/routes/feature.routes"
import { interactionRoutes } from "@/app/router/routes/interaction.routes"
import { managementRoutes } from "@/app/router/routes/management.routes"
import { getCurrentUser } from "@/services/auth.api"
import { getSavedUserRole, hasRequiredRole, isAuthenticated, normalizeRole } from "@/shared/auth/guards"
import { removeSavedUser, removeToken, setSavedUser } from "@/shared/auth/token"

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

router.beforeEach(async (to) => {
  if (to.meta?.guestOnly && isAuthenticated()) {
    const role = await resolveCurrentRole()
    return getDefaultRouteByRole(role)
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
    const role = await resolveCurrentRole()

    if (!role) {
      return {
        path: "/login",
        query: {
          redirect: to.fullPath,
        },
      }
    }

    if (!hasRequiredRole(role, to.meta.roles)) {
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

async function resolveCurrentRole() {
  const savedRole = getSavedUserRole()

  if (savedRole) {
    return savedRole
  }

  try {
    const user = await getCurrentUser()
    setSavedUser(user)
    return normalizeRole(user?.role)
  } catch {
    removeToken()
    removeSavedUser()
    return ""
  }
}

export default router
