<template>
  <div class="auth-page">
    <div class="auth-card">
      <!-- Logo -->
      <router-link to="/">
        <img src="@/images/MemoryBox.png" class="auth-logo" alt="Memory Box Logo" />
      </router-link>

      <h2>Reset Password</h2>
      <p class="subtitle">Enter your email and new password below</p>

      <form class="auth-form" @submit.prevent="handleReset">
        <input type="email" v-model="email" placeholder="Email" required />
        <div class="password-field">
          <input :type="showPassword ? 'text' : 'password'" v-model="newPassword" placeholder="New Password" required />
          <button type="button" class="toggle-btn" @click="showPassword = !showPassword">
            {{ showPassword ? 'Hide' : 'Show' }}
          </button>
        </div>

        <div class="password-field">
          <input :type="showConfirmPassword ? 'text' : 'password'" v-model="confirmPassword" placeholder="Confirm New Password" required />
          <button type="button" class="toggle-btn" @click="showConfirmPassword = !showConfirmPassword">
            {{ showConfirmPassword ? 'Hide' : 'Show' }}
          </button>
        </div>

        <button type="submit" class="primary-btn auth-btn">Reset Password</button>
      </form>

      <router-link to="/sign-in" class="text-link">
        Back to Sign In
      </router-link>

      <p v-if="error" class="error-message">{{ error }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const email = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const error = ref('')

const showPassword = ref(false)
const showConfirmPassword = ref(false)

function handleReset() {
  error.value = ''
  if (newPassword.value !== confirmPassword.value) {
    error.value = 'Passwords do not match!'
    return
  }

  // TODO: replace with real password reset logic
  alert('Password reset successful!')
  router.push('/sign-in')
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
  gap: 1.5rem; /* spacing between heading, form, and links */
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
  gap: 1rem; /* spacing between inputs */
}

.auth-form input {
  padding: 0.75rem 1rem;
  border-radius: 12px;
  border: 1px solid #d1d5db;
  font-size: 1rem;
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
</style>
