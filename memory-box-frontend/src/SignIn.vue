<template>
  <div class="auth-page">
    <div class="auth-card">
      <!-- Logo -->
      <router-link to="/">
        <img src="@/images/MemoryBox.png" class="auth-logo" alt="Memory Box Logo" />
      </router-link>


      <!-- Heading -->
      <h2>Welcome Back</h2>
      <p class="subtitle">Sign in to your Memory Box account ✨</p>

      <!-- Sign In Form -->
      <form class="auth-form" @submit.prevent="handleSignIn">
        <input type="email" v-model="email" placeholder="Email" required />
            <div class="password-field">
              <input :type="showPassword ? 'text' : 'password'" v-model="password" placeholder="Password" required />
              <button type="button" class="toggle-btn" @click="showPassword = !showPassword">
                {{ showPassword ? 'Hide' : 'Show' }}
              </button>
            </div>

        <button type="submit" class="primary-btn auth-btn">Sign In</button>
      </form>

      <!-- Links -->
      <router-link to="/reset-password" class="text-link">Forgot your password?</router-link>

      <div class="divider"><span>or</span></div>

      <router-link to="/sign-up" class="secondary-btn auth-btn">
        Create an Account
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import api from '@/api/client'

const email = ref('')
const password = ref('')

const showPassword = ref(false)

const handleSignIn = async (email: string, password: string) => {
  try {
    const res = await api.post('/auth/login', {
      email: email,
      password: password
    })
    const token = res.data.token
    if (token) {
      localStorage.setItem('jwt', token)
      alert('Login successful!')
      router.push('/') // Redirect to home
    } else {
      alert(res.data.message)
    }
  } catch (err: any) {
    console.error(err)
    alert(err.response?.data?.message || 'Login failed')
  }
}
</script>

<style scoped>
/* Page Background */
.auth-page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #fde2e4, #e0f2fe);
  padding: 1rem;
  font-family: 'Inter', sans-serif;
}

/* Card */
.auth-card {
  background: white;
  padding: 2.5rem;
  width: 100%;
  max-width: 420px;
  border-radius: 24px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

/* Logo */
.auth-logo {
  width: 60px;
  margin: 0 auto 1rem;
}

/* Headings */
h2 {
  font-size: 1.8rem;
  font-weight: 700;
  margin: 0;
}

.subtitle {
  color: #6b7280;
  font-size: 1rem;
  margin: 0 0 1rem;
}

/* Form */
.auth-form {
  display: flex;
  flex-direction: column;
  gap: 1rem; /* spacing between inputs */
}

.auth-form input {
  padding: 0.75rem 1rem;
  border-radius: 12px;
  border: 1px solid #d1d5db;
  font-size: 1rem;
}

.auth-form input:focus {
  outline: none;
  border-color: #ec4899;
  box-shadow: 0 0 0 3px rgba(236, 72, 153, 0.2);
}

/* Buttons */
.auth-btn {
  width: 100%;
  padding: 0.75rem;
  border-radius: 9999px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.primary-btn {
  background: #ec4899;
  color: white;
  border: none;
}

.primary-btn:hover {
  background: #db2777;
}

.secondary-btn {
  background: transparent;
  border: 2px solid #ec4899;
  color: #ec4899;
}

.secondary-btn:hover {
  background: #fdf2f8;
}

.password-field {
  position: relative;
  display: flex;
  align-items: center;
}

.password-field input {
  flex: 1;
  padding: 0.75rem 1rem;
  border-radius: 12px;
  border: 1px solid #d1d5db;
  font-size: 1rem;
}

.toggle-btn {
  position: absolute;
  right: 0.75rem;
  background: none;
  border: none;
  cursor: pointer;
  font-weight: 600;
  color: #ec4899;
}

/* Links */
.text-link {
  display: block;
  font-size: 0.9rem;
  margin-top: 0.5rem;
  color: #ec4899;
  text-decoration: none;
}

.text-link:hover {
  text-decoration: underline;
}

/* Divider */
.divider {
  display: flex;
  align-items: center;
  text-align: center;
  margin: 1.5rem 0;
  color: #9ca3af;
}

.divider:before,
.divider:after {
  content: "";
  flex: 1;
  border-bottom: 1px solid #e5e7eb;
}

.divider span {
  margin: 0 1rem;
}

/* Responsive */
@media (max-width: 480px) {
  .auth-card {
    padding: 2rem 1.5rem;
  }

  h2 {
    font-size: 1.6rem;
  }

  .auth-form input {
    font-size: 0.95rem;
  }
}
</style>
