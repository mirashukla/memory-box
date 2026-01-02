<template>
  <div class="landing-page p-6 max-w-6xl mx-auto">
    <h1 class="text-3xl font-bold mb-6 text-center">Your Memories</h1>

    <!-- Controls -->
    <div class="flex gap-3 justify-center mb-6">
      <button @click="toggleSort" class="btn-primary">
        Sort: {{ sortOrder === 'desc' ? 'Newest → Oldest' : 'Oldest → Newest' }}
      </button>

      <button @click="openModal" class="btn-primary">
        Add Memory
      </button>
    </div>

    <!-- Add Memory Modal -->
    <SaveMemoryForm
      v-if="showModal"
      :userEmail="userEmail"
      @close="showModal = false"
      @saved="() => { showModal = false; fetchMemories() }"
    />

    <!-- Loading / Error -->
    <p v-if="loading" class="text-center text-gray-500">Loading memories...</p>
    <p v-if="error" class="text-center text-red-500">{{ error }}</p>

    <!-- Placeholder if no memories -->
    <div v-if="!loading && memories.length === 0" class="placeholder">
      <img :src="leoPhotoUrl" alt="No memories yet" />
      <p class="text-gray-500">
        You haven't created any memories yet. Start adding some ✨
      </p>
    </div>

    <!-- Grid -->
    <div v-else class="memories-grid">
      <div
        v-for="(memory, index) in sortedMemories"
        :key="memory.createdAt + index"
        class="memory-card"
        @click="openMemory(index)"
      >
        <div class="image-wrapper">
          <img
            :src="placeholderImgUrl"
            alt="Memory image"
            class="memory-image"
          />
        </div>

        <h2 class="memory-title">
          {{ memory.memory.title }}
        </h2>

        <p class="memory-content">
          {{ memory.memory.content }}
        </p>

        <p class="memory-date">
          {{ formatDate(memory.createdAt) }}
        </p>
      </div>
    </div>

    <!-- Load More Button -->
    <div v-if="nextPageToken && !loading" class="flex justify-center mt-6">
      <button @click="loadMore" class="btn-primary">Load More</button>
    </div>

    <!-- Overlay / Lightbox -->
    <div v-if="activeMemory" class="overlay" @click="closeMemory">
      <div class="overlay-content" @click.stop>
        <button
          v-if="canNavigate"
          class="nav-btn left"
          @click.stop="prevMemory"
          aria-label="Previous memory"
        >
          &#10094;
        </button>

        <img
          :src="placeholderImgUrl"
          :alt="`Memory: ${activeMemory.memory.title}`"
          class="overlay-image"
        />

        <h2 class="overlay-title">{{ activeMemory.memory.title }}</h2>
        <p class="overlay-content-text">{{ activeMemory.memory.content }}</p>

        <button
          v-if="canNavigate"
          class="nav-btn right"
          @click.stop="nextMemory"
          aria-label="Next memory"
        >
          &#10095;
        </button>

        <button class="close-btn" @click="closeMemory">
          &times;
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue"
import SaveMemoryForm from "./components/SaveMemoryForm.vue"
import api from '@/api/client'

// User email
const userEmail = ref(localStorage.getItem("userEmail") || "")

// Modal
const showModal = ref(false)
function openModal() {
  showModal.value = true
}

// Placeholder images
import placeholderImgUrl from "@/images/placeholder1.jpg?url"
import leoPhotoUrl from "@/images/leoPhoto.jpeg?url"

// Types
interface MemoryItem {
  title: string
  content: string
}

interface Memory {
  createdAt: string
  memory: MemoryItem
}

interface MemoriesResponse {
  memories: Memory[]
  nextPageToken?: string
}

// State
const memories = ref<Memory[]>([])
const loading = ref(false)
const error = ref<string | null>(null)
const nextPageToken = ref<string | null>(null)
const pageSize = 10

// Fetch memories from API
async function fetchMemories(pageToken?: string) {
  loading.value = true
  error.value = null

  try {
    const res = await api.get("/memories", {
      params: {
        pageSize,
        pageToken
      }
    })

    // Axios-style response
    const data: MemoriesResponse = res.data

    if (pageToken) {
      memories.value = [...memories.value, ...data.memories]
    } else {
      memories.value = data.memories
    }

    nextPageToken.value = data.nextPageToken ?? null
  } catch (e: any) {
    error.value = e.message || "Failed to load memories"
  } finally {
    loading.value = false
  }
}

// Initial fetch
onMounted(() => fetchMemories())

// Load next page
async function loadMore() {
  if (!nextPageToken.value) return
  await fetchMemories(nextPageToken.value)
}

// Sorting
const sortOrder = ref<"asc" | "desc">("desc")
function toggleSort() {
  sortOrder.value = sortOrder.value === "desc" ? "asc" : "desc"
}

const sortedMemories = computed(() =>
  [...memories.value].sort((a, b) =>
    sortOrder.value === "asc"
      ? new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
      : new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
  )
)

// Overlay
const activeIndex = ref<number | null>(null)
function openMemory(index: number) {
  activeIndex.value = index
}
function closeMemory() {
  activeIndex.value = null
}

const activeMemory = computed(() =>
  activeIndex.value === null ? null : sortedMemories.value[activeIndex.value]
)
const canNavigate = computed(() => sortedMemories.value.length > 1)
function prevMemory() {
  if (activeIndex.value !== null)
    activeIndex.value =
      (activeIndex.value - 1 + sortedMemories.value.length) %
      sortedMemories.value.length
}
function nextMemory() {
  if (activeIndex.value !== null)
    activeIndex.value =
      (activeIndex.value + 1) % sortedMemories.value.length
}

function formatDate(dateStr: string) {
  return new Date(dateStr).toLocaleDateString(undefined, {
    year: "numeric",
    month: "short",
    day: "numeric"
  })
}
</script>

<style scoped>
.landing-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #fde2e4, #e0f2fe);
}

/* Grid layout */
.memories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 1.5rem;
}

/* Memory cards */
.memory-card {
  background: white;
  border-radius: 18px;
  padding: 1rem;
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.memory-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 16px 28px rgba(0, 0, 0, 0.12);
}

.image-wrapper {
  width: 100%;
  height: 180px;
  overflow: hidden;
  border-radius: 14px;
  margin-bottom: 1rem;
}
.memory-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.memory-title {
  font-weight: 700;
  color: #be123c;
  font-size: 1.2rem;
}
.memory-content {
  color: #374151;
  margin-top: 0.25rem;
}
.memory-date {
  color: #6b7280;
  font-size: 0.8rem;
  margin-top: 0.5rem;
}

/* Placeholder */
.placeholder {
  text-align: center;
  margin-top: 3rem;
}
.placeholder img {
  width: 240px;
  border-radius: 18px;
  margin-bottom: 1rem;
}

/* Overlay */
.overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.65);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 50;
}
.overlay-content {
  position: relative;
  background: white;
  border-radius: 18px;
  max-width: 820px;
  width: 100%;
  padding: 1rem;
  text-align: center;
}
.overlay-image {
  width: 100%;
  max-height: 520px;
  object-fit: contain;
  border-radius: 12px;
  margin-bottom: 1rem;
}
.close-btn {
  position: absolute;
  right: 1rem;
  top: 0.5rem;
  font-size: 2rem;
  border: none;
  background: none;
  cursor: pointer;
}
.nav-btn {
  position: absolute;
  top: 50%;
  font-size: 2rem;
  border: none;
  background: none;
  cursor: pointer;
}
.nav-btn.left {
  left: 0.5rem;
}
.nav-btn.right {
  right: 0.5rem;
}
</style>
