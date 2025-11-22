<template>
  <div class="learn-page">
    <!-- Navbar -->
      <header class="navbar">
        <router-link to="/" class="logo">
          <img src="@/images/MemoryBox.png" alt="Memory Box logo" />
          <h1>Memory Box</h1>
        </router-link>


        <router-link to="/sign-in" class="sign-in-btn">Sign In</router-link>
      </header>

    <!-- Hero Section -->
    <main class="learn-hero">
      <h2>Capture. Share. <span>Remember.</span></h2>
      <p>
        Memory Box helps you store and share your favorite memories with friends and family — all
        in one beautiful, secure place.
      </p>
    </main>

    <!-- Features Section -->
    <section class="features">
      <h3>Why Memory Box?</h3>
      <div class="feature-cards">
        <div class="card">
          <h4>Safe Storage</h4>
          <p>Keep your photos, videos, and documents safe and organized in one place.</p>
        </div>
        <div class="card">
          <h4>Share With Friends</h4>
          <p>Share your favorite memories with selected friends or groups easily.</p>
        </div>
        <div class="card">
          <h4>Beautiful Design</h4>
          <p>Memory Box is intuitive and visually pleasing, making every memory special.</p>
        </div>
      </div>
    </section>

    <!-- Call to Action -->
    <section class="cta">
      <router-link to="/sign-up" class="primary-btn">Get Started</router-link>
    </section>

    <!-- Footer -->
    <footer class="footer">
      © {{ currentYear }} Memory Box — Built for friends, by friends 💖
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const currentYear = ref(new Date().getFullYear())
</script>

<style scoped>
.learn-page {
  font-family: 'Inter', sans-serif;
  color: #1f2937;
  background: linear-gradient(135deg, #fde2e4, #e0f2fe);
  min-height: 100vh;
  display: flex;
  flex-direction: column;
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
  text-decoration: none;
  color: #111827;
}

.logo img {
  width: 40px;
  height: 40px;
}

.nav-link {
  margin-left: 1rem;
  text-decoration: none;
  color: #374151;
  font-weight: 500;
}

.nav-link.active {
  color: #ec4899;
  font-weight: 700;
}

/* Hero */
.learn-hero {
  text-align: center;
  padding: 4rem 1rem;
}

.learn-hero h2 {
  font-size: 3rem;
  font-weight: 800;
  margin-bottom: 1rem;
}

.learn-hero h2 span {
  color: #ec4899;
}

.learn-hero p {
  font-size: 1.25rem;
  max-width: 600px;
  margin: 0 auto;
  color: #4b5563;
}

/* Features */
.features {
  padding: 2rem 1rem 4rem;
  text-align: center;
}

.features h3 {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 2rem;
}

.feature-cards {
  display: flex;
  gap: 1.5rem;
  flex-wrap: wrap;
  justify-content: center;
}

.card {
  background: white;
  padding: 2rem;
  border-radius: 24px; /* match landing page */
  box-shadow: 0 10px 25px rgba(0,0,0,0.1);
  flex: 1 1 250px;
  max-width: 300px;
}

.card h4 {
  font-size: 1.25rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
}

.card p {
  font-size: 1rem;
  color: #4b5563;
}

/* Call to Action */
.cta {
  text-align: center;
  margin-bottom: 4rem;
}

.primary-btn {
  background: #ec4899;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 9999px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.primary-btn:hover {
  background: #db2777;
}

/* Footer */
.footer {
  text-align: center;
  padding: 2rem 1rem;
  color: #6b7280;
  font-size: 0.875rem;
}
</style>
