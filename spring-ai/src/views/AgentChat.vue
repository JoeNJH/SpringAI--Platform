<template>
  <div class="ai-chat" :class="{ 'dark': isDark }">
    <div class="chat-container">
      <div class="sidebar">
        <div class="history-header">
          <h2>聊天记录</h2>
          <button class="new-chat" @click="startNewChat">
            <PlusIcon class="icon" />
            新对话
          </button>
        </div>
        <div class="history-list">
          <div
              v-for="chat in chatHistory"
              :key="chat.id"
              class="history-item"
              :class="{ 'active': currentChatId === chat.id }"
              @click="loadChat(chat.id)"
          >
            <ChatBubbleLeftRightIcon class="icon" />
            <span class="title">{{ chat.title || '新对话' }}</span>
          </div>
        </div>
      </div>

      <div class="chat-main">
        <div class="messages" ref="messagesRef">
          <ChatMessage
              v-for="(message, index) in currentMessages"
              :key="index"
              :message="message"
              :is-stream="isStreaming && index === currentMessages.length - 1"
          />
        </div>

        <div class="input-area">
          <div class="input-row">
            <textarea
                v-model="userInput"
                @keydown.enter.prevent="sendMessage"
                placeholder="输入消息..."
                rows="1"
                ref="inputRef"
            ></textarea>
            <button
                class="send-button"
                @click="sendMessage"
                :disabled="isStreaming || !userInput.trim()"
            >
              <PaperAirplaneIcon class="icon" />
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import { useDark } from '@vueuse/core'
import { useRoute, useRouter } from 'vue-router'
import {
  ChatBubbleLeftRightIcon,
  PaperAirplaneIcon,
  PlusIcon
} from '@heroicons/vue/24/outline'
import ChatMessage from '../components/ChatMessage.vue'
import { chatAPI } from '../services/api'

const isDark = useDark()
const messagesRef = ref(null)
const inputRef = ref(null)
const userInput = ref('')
const isStreaming = ref(false)
const currentChatId = ref(null)
const currentMessages = ref([])
const chatHistory = ref([])
const route = useRoute()
const router = useRouter()

// 自动调整输入框高度
const adjustTextareaHeight = () => {
  const textarea = inputRef.value
  if (textarea) {
    textarea.style.height = 'auto'
    textarea.style.height = textarea.scrollHeight + 'px'
  } else {
    textarea.style.height = '50px'
  }
}

// 滚动到底部
const scrollToBottom = async () => {
  await nextTick()
  if (messagesRef.value) {
    messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  }
}

// 发送消息函数
const sendMessage = async () => {
  if (isStreaming.value) return
  if (!userInput.value.trim()) return

  const messageContent = userInput.value.trim()

  // 添加用户消息
  const userMessage = {
    role: 'user',
    content: messageContent,
    timestamp: new Date()
  }
  currentMessages.value.push(userMessage)

  // 清空输入
  userInput.value = ''
  adjustTextareaHeight()
  await scrollToBottom()

  // 添加助手消息占位
  const assistantMessage = {
    role: 'assistant',
    content: '',
    timestamp: new Date()
  }
  currentMessages.value.push(assistantMessage)
  isStreaming.value = true

  try {
    // 使用Agent专用接口发送消息
    const reader = await chatAPI.sendMessageAgent(messageContent, route.query.agentId, currentChatId.value)
    const decoder = new TextDecoder('utf-8')
    let accumulatedContent = ''

    while (true) {
      try {
        const { value, done } = await reader.read()
        if (done) break

        // 累积新内容
        accumulatedContent += decoder.decode(value)

        await nextTick(() => {
          // 更新消息，使用累积的内容
          const updatedMessage = {
            ...assistantMessage,
            content: accumulatedContent
          }
          const lastIndex = currentMessages.value.length - 1
          currentMessages.value.splice(lastIndex, 1, updatedMessage)
        })
        await scrollToBottom()
      } catch (readError) {
        console.error('读取流错误:', readError)
        break
      }
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    assistantMessage.content = '抱歉，发生了错误，请稍后重试。'
  } finally {
    isStreaming.value = false
    await scrollToBottom()
  }
}

// 加载特定对话
const loadChat = async (chatId) => {
  currentChatId.value = chatId
  try {
    const messages = await chatAPI.getChatMessages(chatId, 'agent')
    currentMessages.value = messages
  } catch (error) {
    console.error('加载对话消息失败:', error)
    currentMessages.value = []
  }
}

// 加载聊天历史
const loadChatHistory = async () => {
  try {
    const history = await chatAPI.getChatHistory('agent')
    chatHistory.value = history || []
    if (history && history.length > 0) {
      await loadChat(history[0].id)
    } else {
      startNewChat()
    }
  } catch (error) {
    console.error('加载聊天历史失败:', error)
    chatHistory.value = []
    startNewChat()
  }
}

// 开始新对话
const startNewChat = () => {
  const newChatId = Date.now().toString()
  currentChatId.value = newChatId
  currentMessages.value = []

  // 添加新对话到聊天历史列表
  const newChat = {
    id: newChatId,
    title: `Agent对话 ${newChatId.slice(-6)}`
  }
  chatHistory.value = [newChat, ...chatHistory.value]
}

// 监听路由变化
watch(() => route.query, () => {
  if (route.query.type === 'agent' && route.query.agentId) {
    loadChatHistory()
  }
}, { immediate: true })

onMounted(() => {
  adjustTextareaHeight()
})
</script>

<style scoped lang="scss">
.ai-chat {
  position: fixed;
  top: 64px;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  background: var(--bg-color);
  overflow: hidden;

  .chat-container {
    flex: 1;
    display: flex;
    max-width: 1800px;
    width: 100%;
    margin: 0 auto;
    padding: 1.5rem 2rem;
    gap: 1.5rem;
    height: 100%;
    overflow: hidden;
  }

  .sidebar {
    width: 300px;
    display: flex;
    flex-direction: column;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-radius: 1rem;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);

    .history-header {
      flex-shrink: 0;
      padding: 1rem;
      display: flex;
      justify-content: space-between;
      align-items: center;

      h2 {
        font-size: 1.25rem;
      }

      .new-chat {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.5rem 1rem;
        border-radius: 0.5rem;
        background: #007CF0;
        color: white;
        border: none;
        cursor: pointer;
        transition: background-color 0.3s;

        &:hover {
          background: #0066cc;
        }

        .icon {
          width: 1.25rem;
          height: 1.25rem;
        }
      }
    }

    .history-list {
      flex: 1;
      overflow-y: auto;
      padding: 0 1rem 1rem;

      .history-item {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.75rem;
        border-radius: 0.5rem;
        cursor: pointer;
        transition: background-color 0.3s;

        &:hover {
          background: rgba(255, 255, 255, 0.1);
        }

        &.active {
          background: rgba(0, 124, 240, 0.1);
        }

        .icon {
          width: 1.25rem;
          height: 1.25rem;
        }

        .title {
          flex: 1;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }
    }
  }

  .chat-main {
    flex: 1;
    display: flex;
    flex-direction: column;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-radius: 1rem;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
    overflow: hidden;

    .messages {
      flex: 1;
      overflow-y: auto;
      padding: 2rem;
    }

    .input-area {
      flex-shrink: 0;
      padding: 1.5rem 2rem;
      background: rgba(255, 255, 255, 0.98);
      border-top: 1px solid rgba(0, 0, 0, 0.05);
      display: flex;
      flex-direction: column;
      gap: 1rem;

      .input-row {
        display: flex;
        gap: 1rem;
        align-items: flex-end;
        background: #fff;
        padding: 0.75rem;
        border-radius: 1rem;
        border: 1px solid rgba(0, 0, 0, 0.1);
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

        textarea {
          flex: 1;
          resize: none;
          border: none;
          background: transparent;
          padding: 0.75rem;
          color: inherit;
          font-family: inherit;
          font-size: 1rem;
          line-height: 1.5;
          max-height: 150px;

          &:focus {
            outline: none;
          }

          &::placeholder {
            color: #999;
          }
        }

        .send-button {
          width: 2.5rem;
          height: 2.5rem;
          display: flex;
          align-items: center;
          justify-content: center;
          border: none;
          border-radius: 0.75rem;
          background: #007CF0;
          color: white;
          cursor: pointer;
          transition: all 0.2s ease;

          &:hover:not(:disabled) {
            background: #0066cc;
            transform: translateY(-1px);
          }

          &:disabled {
            background: #ccc;
            cursor: not-allowed;
          }

          .icon {
            width: 1.25rem;
            height: 1.25rem;
          }
        }
      }
    }
  }
}

.dark {
  .sidebar {
    background: rgba(40, 40, 40, 0.95);
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.2);
  }

  .chat-main {
    background: rgba(40, 40, 40, 0.95);
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.2);

    .input-area {
      background: rgba(30, 30, 30, 0.98);
      border-top: 1px solid rgba(255, 255, 255, 0.05);

      .input-row {
        background: rgba(255, 255, 255, 0.02);
        border-color: rgba(255, 255, 255, 0.05);
        box-shadow: none;

        textarea {
          color: #fff;

          &::placeholder {
            color: #666;
          }
        }
      }
    }
  }

  .history-item {
    &:hover {
      background: rgba(255, 255, 255, 0.05) !important;
    }

    &.active {
      background: rgba(0, 124, 240, 0.2) !important;
    }
  }
}

@media (max-width: 768px) {
  .ai-chat {
    .chat-container {
      padding: 0;
    }

    .sidebar {
      display: none;
    }

    .chat-main {
      border-radius: 0;
    }
  }
}
</style>
