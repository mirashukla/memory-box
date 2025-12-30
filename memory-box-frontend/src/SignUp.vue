<template>
  <div class="auth-page">
    <div class="auth-card">
      <!-- Logo -->
      <router-link to="/">
        <img src="@/images/MemoryBox.png" class="auth-logo" alt="Memory Box Logo" />
      </router-link>

      <h2>Create Your Account</h2>
      <p class="subtitle">Join Memory Box and start sharing moments 🎁</p>

      <form class="auth-form" @submit.prevent="handleSignUp">
        <input type="email" v-model="email" placeholder="Email" required />

        <div class="password-field">
          <input :type="showPassword ? 'text' : 'password'" v-model="password" placeholder="Password" required />
          <button type="button" class="toggle-btn" @click="showPassword = !showPassword">
            {{ showPassword ? 'Hide' : 'Show' }}
          </button>
        </div>

        <div class="password-field">
          <input :type="showConfirmPassword ? 'text' : 'password'" v-model="confirmPassword" placeholder="Confirm Password" required />
          <button type="button" class="toggle-btn" @click="showConfirmPassword = !showConfirmPassword">
            {{ showConfirmPassword ? 'Hide' : 'Show' }}
          </button>
        </div>

        <button type="submit" class="primary-btn auth-btn">Sign Up</button>
      </form>

      <router-link to="/sign-in" class="text-link">
        Already have an account? Sign in
      </router-link>

      <p v-if="error" class="error-message">{{ error }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/api/client'

const router = useRouter()
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const error = ref('')

// Toggle states for password visibility
const showPassword = ref(false)
const showConfirmPassword = ref(false)

async function handleSignUp() {
  error.value = ''

  if (!email.value || !password.value) {
    error.value = 'Email and password are required!'
    return
  }

  if (password.value !== confirmPassword.value) {
    error.value = 'Passwords do not match!'
    return
  }

  await register(email.value, password.value)
}

const register = async (email: string, password: string) => {
  try {
    const res = await api.post('/auth/register', {
      email: email,
      password: password
    })
    console.log(res.data)
    // Redirect to sign-in page
    router.push('/sign-in')
  } catch (err: any) {
    console.log("POST /auth/register failed")
    console.error(err)
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #fde2e4, #e0f2fe);
  padding: 1rem;
  font-family: 'Inter', sans-serif;
}

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

.auth-logo {
  width: 60px;
  margin: 0 auto 1rem;
  cursor: pointer;
}

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

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
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

.error-message {
  color: #be123c;
  font-size: 0.875rem;
  margin-top: 0.5rem;
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

</style>
