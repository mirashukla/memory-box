import { createRouter, createWebHistory } from 'vue-router'
import LandingPage from '@/LandingPage.vue'
import SignIn from '@/SignIn.vue'
import SignUp from '@/SignUp.vue'
import ResetPassword from '@/ResetPassword.vue'
import LearnMore from '@/LearnMore.vue'
import MemoriesPage from '@/MemoriesPage.vue'
import { useAuthStore } from '@/stores/authStore'



const routes = [
  { path: '/', component: LandingPage },
  { path: '/sign-in', component: SignIn },
  { path: '/sign-up', component: SignUp },
  { path: '/reset-password', component: ResetPassword },
  { path: '/learn-more', component: LearnMore },
  { path: '/memories', component: MemoriesPage, meta: { requiresAuth: true } },
  { path: '/:pathMatch(.*)*', redirect: '/sign-in' } // fallback
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

// Global route guard
router.beforeEach((to, from, next) => {
  const auth = useAuthStore()

  // Protect routes with requiresAuth
  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    next('/sign-in')
  }
  // Prevent logged-in users from visiting auth pages
  else if ((to.path === '/sign-in' || to.path === '/sign-up') && auth.isAuthenticated) {
    next('/memories')
  } else {
    next()
  }
})

export default router
