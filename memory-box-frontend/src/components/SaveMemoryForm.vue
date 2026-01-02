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

<script setup>
import { ref, defineProps, defineEmits } from "vue"

// Pass the logged-in user's email as a prop
const props = defineProps({
  userEmail: { type: String, required: true }
})
const emit = defineEmits(["close"])

const title = ref("")
const content = ref("")
const saving = ref(false)
const message = ref("")

function close() {
  emit("close")
}

async function saveMemory() {
  saving.value = true
  message.value = ""

  try {
    const res = await fetch("https://your-api.example.com/memories", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        email: props.userEmail, // use the logged-in user's email
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
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 24px;
  border-radius: 8px;
  width: 400px;
  max-width: 90%;
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.close-btn {
  position: absolute;
  top: 8px;
  right: 12px;
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
}
</style>
