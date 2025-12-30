<script setup lang="ts">
import { ref } from 'vue'

// reactive value for current year in footer
const currentYear = ref(new Date().getFullYear())

// placeholder for navigation (e.g., later link to login/signup)
function handleGetStarted() {
  alert('🚀 This will take you to sign-up soon!')
}
</script>

<template>
  <div class="landing-page">
    <!-- Navbar -->
    <header class="navbar">
      <div class="logo">
        <img src="@/images/MemoryBox.png" alt="Memory Box logo" />
        <h1>Memory Box</h1>
      </div>
      <router-link to="/sign-in" class="sign-in-btn">
        Sign In
      </router-link>
    </header>

    <!-- Hero Section -->
    <main class="hero">
      <h2>Capture. Share. <span>Remember.</span></h2>
      <p>
        Memory Box lets you and your friends store photos, videos, and documents — keeping your
        favorite moments safe and beautifully shared.
      </p>
      <div class="hero-buttons">
        <div class="hero-buttons">
          <router-link to="/sign-up" class="primary-btn">Get Started</router-link>
          <router-link to="/learn-more" class="secondary-btn">Learn More</router-link>
          <router-link to="/memories" class="secondary-btn">Memories bypass</router-link>
        </div>
      </div>

      <div class="mockup">
        <img src="@/images/leoPhoto.jpeg"alt="Memory Box preview" />
      </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
      © {{ currentYear }} Memory Box — Built for friends, by friends 💖
    </footer>
  </div>
</template>

<style scoped>
.landing-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #fde2e4, #e0f2fe);
  font-family: 'Inter', sans-serif;
  color: #1f2937;
}

/* Navbar */
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.25rem 2rem;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(8px);
  position: sticky;
  top: 0;
}

.logo {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.logo img {
  width: 40px;
  height: 40px;
}

.logo h1 {
  font-size: 1.5rem;
  font-weight: 700;
  color: #111827;
}

nav a {
  margin: 0 1rem;
  color: #374151;
  text-decoration: none;
  transition: color 0.2s;
}

nav a:hover {
  color: #be123c;
}

.sign-in-btn {
  background: #ec4899;
  color: white;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 9999px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.3s;
}

.sign-in-btn:hover {
  background: #db2777;
}

/* Hero */
.hero {
  text-align: center;
  padding: 4rem 1rem;
  flex-grow: 1;
}

.hero h2 {
  font-size: 3rem;
  font-weight: 800;
  color: #111827;
}

.hero h2 span {
  color: #ec4899;
}

.hero p {
  margin-top: 1rem;
  font-size: 1.25rem;
  color: #4b5563;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.hero-buttons {
  margin-top: 2rem;
  display: flex;
  justify-content: center;
  gap: 1rem;
}

.primary-btn,
.secondary-btn {
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
  font-weight: 600;
  border-radius: 9999px;
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
  border: 2px solid #ec4899;
  background: transparent;
  color: #ec4899;
}

.secondary-btn:hover {
  background: #fdf2f8;
}

.mockup {
  margin-top: 3rem;
  display: flex;
  justify-content: center;
}

.mockup img {
  width: 60%;
  max-width: 350px;
  min-width: 200px;
  height: auto;
  border-radius: 24px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

/* Footer */
.footer {
  text-align: center;
  padding: 1.5rem 0;
  font-size: 0.875rem;
  color: #6b7280;
}
</style>

/* Sign In */
.sign-in-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  text-decoration: none;
}
