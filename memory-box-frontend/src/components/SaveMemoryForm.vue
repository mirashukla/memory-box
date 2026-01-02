<template>
  <div class="modal-overlay" @click.self="close">
    <div class="modal-content">
      <button class="close-btn" @click="close">×</button>
      <h2>Save a Memory</h2>

      <label>Title</label>
      <input v-model="title" placeholder="Memory title" />

      <label>Content</label>
      <textarea
        v-model="content"
        rows="4"
        placeholder="Write your memory here..."
      ></textarea>

      <button
        :disabled="saving"
        @click="saveMemory"
        class="btn-primary"
      >
        {{ saving ? "Saving..." : "Save Memory" }}
      </button>

      <p v-if="message">{{ message }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue"

const props = defineProps<{
  userEmail: string
}>()

const emit = defineEmits<{
  (e: "close"): void
}>()

const title = ref("")
const content = ref("")
const saving = ref(false)
const message = ref("")

function close() {
  emit("close")
}

async function saveMemory() {
  if (!title.value.trim() || !content.value.trim()) {
    message.value = "Please fill in title & content"
    return
  }

  saving.value = true
  message.value = ""

  try {
    const res = await fetch("https://your-api.example.com/memories", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        email: props.userEmail,
        memoryItem: { title: title.value, content: content.value }
      })
    })

    if (!res.ok) {
      const errText = await res.text()
      throw new Error(errText || "Save failed")
    }

    message.value = "Memory saved 🎉"
    title.value = ""
    content.value = ""

    // optionally close modal after success
    // emit("close")
  } catch (err) {
    console.error(err)
    message.value = "Error saving memory 😢"
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 24px;
  border-radius: 16px;
  width: 420px;
  max-width: 90%;
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 12px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.15);
}

.close-btn {
  position: absolute;
  top: 8px;
  right: 12px;
  background: none;
  border: none;
  font-size: 22px;
  cursor: pointer;
}
</style>
