<template>
  <section class="landing">
    <h1>{{ appName }}</h1>
    <p>Capture, share, and relive your favorite memories.</p>
    <button @click="goToSignUp">Get Started</button>
  </section>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const appName = 'Memory Box'
const router = useRouter()

function goToSignUp() {
  router.push('/signup')
}
</script>

<style scoped>
.landing {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #fbe2e2, #e0f2fe);
  text-align: center;
}

h1 {
  font-size: 3rem;
  color: #e11d48;
  margin-bottom: 0.5rem;
}

p {
  color: #374151;
  font-size: 1.25rem;
  margin-bottom: 1.5rem;
}

button {
  background-color: #e11d48;
  color: white;
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 9999px;
  cursor: pointer;
  font-size: 1rem;
  transition: background-color 0.3s;
}

button:hover {
  background-color: #be123c;
}
</style>
