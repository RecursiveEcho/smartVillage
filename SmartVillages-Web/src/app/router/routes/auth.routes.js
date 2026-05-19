export const authRoutes = [
  {
    path: "/login",
    name: "Login",
    component: () => import("@/pages/auth/loginPage.vue"),
    meta: {
      guestOnly: true,
      title: "登录",
    },
  },
]
