<template>
  <div class="landing-page p-6 max-w-5xl mx-auto">
    <h1 class="text-3xl font-bold mb-6 text-center">Your Memories</h1>

    <!-- Sort button -->
    <div class="flex justify-center mb-6">
      <button @click="toggleSort" class="sort-btn">
        Sort: {{ sortOrder === 'desc' ? 'Newest → Oldest' : 'Oldest → Newest' }}
      </button>
    </div>

    <!-- Placeholder if no memories -->
    <div v-if="memories.length === 0" class="placeholder">
      <img :src="leoPhotoUrl" alt="No memories yet" />
      <p class="text-gray-500">You haven't created any memories yet. Start adding some ✨</p>
    </div>

    <!-- Grid of memories with drag-and-drop -->
    <draggable
      v-else
      v-model="memories"
      item-key="title"
      animation="200"
      class="memories-grid"
    >
      <template #item="{ element, index }">
        <div class="memory-card" @click="openMemory(index)">
          <img :src="element.imageUrl" alt="Memory image" class="memory-image" />
          <h2 class="memory-title">{{ element.title }}</h2>
          <p class="memory-content">{{ element.content }}</p>
          <p class="memory-date">Created at: {{ element.createdAt }}</p>
        </div>
      </template>
    </draggable>

    <!-- Overlay / Lightbox -->
    <div v-if="activeIndex !== null" class="overlay" @click="closeMemory">
      <div class="overlay-content" @click.stop>
        <button class="nav-btn left" @click.stop="prevMemory">&#10094;</button>
        <img :src="memories[activeIndex].imageUrl" alt="Memory image" class="overlay-image" />
        <h2 class="overlay-title">{{ memories[activeIndex].title }}</h2>
        <p class="overlay-content-text">{{ memories[activeIndex].content }}</p>
        <button class="nav-btn right" @click.stop="nextMemory">&#10095;</button>
        <button class="close-btn" @click="closeMemory">&times;</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import draggable from 'vuedraggable'

// Vite-compatible local images
import placeholderImgUrl1 from '@/images/placeholder1.jpg?url'
import placeholderImgUrl2 from '@/images/placeholder2.jpg?url'
import placeholderImgUrl3 from '@/images/placeholder3.jpg?url'
import leoPhotoUrl from '@/images/leoPhoto.jpeg?url'

interface Memory {
  title: string
  content: string
  createdAt: string
  imageUrl: string
}

// Local memories array
const memories = ref<Memory[]>([
  {
    title: 'Memory 1',
    content: 'This is my first memory.',
    createdAt: '2025-01-15T12:00:00Z',
    imageUrl: placeholderImgUrl1
  },
  {
    title: 'Memory 2',
    content: 'Another lovely memory.',
    createdAt: '2025-02-01T09:30:00Z',
    imageUrl: placeholderImgUrl2
  },
  {
    title: 'Memory 3',
    content: 'Yet another memory!',
    createdAt: '2025-03-10T18:45:00Z',
    imageUrl: placeholderImgUrl3
  }
])

// Sorting
const sortOrder = ref<'asc' | 'desc'>('desc')
function toggleSort() {
  sortOrder.value = sortOrder.value === 'desc' ? 'asc' : 'desc'
  memories.value.sort((a, b) => {
    if (sortOrder.value === 'asc') {
      return new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
    } else {
      return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
    }
  })
}

// Lightbox overlay
const activeIndex = ref<number | null>(null)
function openMemory(index: number) {
  activeIndex.value = index
}
function closeMemory() {
  activeIndex.value = null
}
function prevMemory() {
  if (activeIndex.value !== null) {
    activeIndex.value =
      (activeIndex.value - 1 + memories.value.length) % memories.value.length
  }
}
function nextMemory() {
  if (activeIndex.value !== null) {
    activeIndex.value = (activeIndex.value + 1) % memories.value.length
  }
}
</script>

<style scoped>
.landing-page {
  min-height: 100vh;
  font-family: 'Inter', sans-serif;
  color: #1f2937;
  background: linear-gradient(135deg, #fde2e4, #e0f2fe);
}

/* Sort button */
.sort-btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 9999px;
  background-color: #ec4899;
  color: white;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.3s;
}
.sort-btn:hover {
  background-color: #db2777;
}

/* Grid layout */
.memories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1.5rem;
}

/* Memory cards */
.memory-card {
  position: relative;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px);
  border-radius: 24px;
  padding: 1rem;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
  cursor: grab;
}

.memory-card:active {
  cursor: grabbing;
}

.memory-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 16px;
  margin-bottom: 1rem;
}

.memory-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: #be123c;
  margin-bottom: 0.5rem;
}

.memory-content {
  font-size: 1rem;
  color: #374151;
  margin-bottom: 0.5rem;
}

.memory-date {
  font-size: 0.75rem;
  color: #6b7280;
}

/* Placeholder */
.placeholder {
  text-align: center;
  margin-top: 4rem;
}

.placeholder img {
  width: 250px;
  max-width: 80%;
  margin-bottom: 1rem;
  border-radius: 16px;
  opacity: 0.8;
}

.placeholder p {
  font-size: 1rem;
  color: #6b7280;
}

/* Overlay / Lightbox */
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 50;
  padding: 1rem;
}

.overlay-content {
  position: relative;
  background: white;
  border-radius: 16px;
  max-width: 800px;
  width: 100%;
  padding: 1rem;
  text-align: center;
}

.overlay-image {
  width: 100%;
  max-height: 500px;
  object-fit: contain;
  border-radius: 12px;
  margin-bottom: 1rem;
}

.overlay-title {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
  color: #be123c;
}

.overlay-content-text {
  font-size: 1rem;
  color: #374151;
  margin-bottom: 1rem;
}

.close-btn {
  position: absolute;
  top: 0.5rem;
  right: 1rem;
  font-size: 2rem;
  background: none;
  border: none;
  cursor: pointer;
  color: #374151;
}

/* Navigation arrows */
.nav-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  font-size: 2rem;
  background: none;
  border: none;
  cursor: pointer;
  color: #374151;
  padding: 0 0.5rem;
}

.nav-btn.left {
  left: 0.5rem;
}

.nav-btn.right {
  right: 0.5rem;
}
</style>
