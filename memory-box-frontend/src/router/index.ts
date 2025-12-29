import { createRouter, createWebHistory } from 'vue-router'
import LandingPage from '@/LandingPage.vue'
import SignIn from '@/SignIn.vue'
import SignUp from '@/SignUp.vue'
import ResetPassword from '@/ResetPassword.vue'
import LearnMore from '@/LearnMore.vue'
import MemoriesPage from '@/MemoriesPage.vue'


const routes = [
  { path: '/', component: LandingPage },
  { path: '/sign-in', component: SignIn },
  { path: '/sign-up', component: SignUp },
  { path: '/reset-password', component: ResetPassword },
  { path: '/learn-more', component: LearnMore },
  { path: '/memories', component: MemoriesPage },
  { path: '/:pathMatch(.*)*', redirect: '/sign-in' } // fallback
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

export default router
