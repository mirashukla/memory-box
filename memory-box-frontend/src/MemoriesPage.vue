<template>
<div class="p-6 max-w-3xl mx-auto">
<h1 class="text-2xl font-bold mb-4">Your Memories</h1>


<div v-if="loading" class="text-gray-600">Loading memories...</div>
<div v-else-if="error" class="text-red-600">{{ error }}</div>


<div v-else-if="memories.length === 0" class="placeholder">
  <img src="@/images/leoPhoto.jpeg" alt="No memories yet" />
  <p class="text-gray-500">You haven't created any memories yet. Start adding some ✨</p>
</div>


<div v-else>
<div v-for="(memory, index) in memories" :key="index" class="memory-card">
  <h2 class="memory-title">{{ memory.memory.title }}</h2>
  <p class="memory-content">{{ memory.memory.content }}</p>
  <p class="memory-date">Created at: {{ memory.createdAt }}</p>
</div>



<div v-if="nextPageToken" class="mt-4 flex justify-center">
<button
  @click="loadMemories(true)"
  class="load-more-btn"
>
  <span v-if="!loadingMore">Load more</span>
  <span v-else>Loading...</span>
</button>
</div>
</div>
</div>
</template>


<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/api/client'


interface MemoryItem {
title: string
content: string
}


interface Memory {
email: string
createdAt: string
memory: MemoryItem
}


interface PaginatedMemoriesResponse {
memories: Memory[]
nextPageToken?: string
}


const router = useRouter()
const memories = ref<Memory[]>([])
const loading = ref(true)
const loadingMore = ref(false)
const error = ref('')
const pageSize = 10
let nextPageToken: string | null = null


async function loadMemories(isLoadMore = false) {
  if (isLoadMore) loadingMore.value = true
  else loading.value = true

  try {
    const token = localStorage.getItem('jwt')
    if (!token) {
      router.push('/sign-in')
      return
    }

    const response = await api.get<PaginatedMemoriesResponse>('/memories', {
      params: {
        pageSize,
        pageToken: nextPageToken || undefined
      },
      headers: {
        Authorization: `Bearer ${token}`
      }
    })

    if (isLoadMore) {
      memories.value.push(...response.data.memories)
    } else {
      memories.value = response.data.memories
    }

    nextPageToken = response.data.nextPageToken ?? null
  } catch (err: any) {
    console.error(err)
    error.value = err.response?.data?.message || 'Failed to load memories.'
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

</script>

<style scoped>
.landing-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #fde2e4, #e0f2fe);
  font-family: 'Inter', sans-serif;
  color: #1f2937;
}

h1 {
  font-size: 2.5rem;
  font-weight: 800;
  color: #111827;
  text-align: center;
  margin-bottom: 2rem;
}

.memories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
}

.memory-card {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(8px);
  border-radius: 24px;
  padding: 1.5rem;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.memory-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.15);
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
  margin-bottom: 0.75rem;
}

.memory-date {
  font-size: 0.75rem;
  color: #6b7280;
}

.load-more-btn {
  display: inline-block;
  padding: 0.75rem 1.5rem;
  background: #ec4899;
  color: white;
  border: none;
  border-radius: 9999px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.3s;
  margin: 2rem auto 0;
}

.load-more-btn:hover {
  background: #db2777;
}

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
</style>