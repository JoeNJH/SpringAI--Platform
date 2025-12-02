<template>
  <div class="home" :class="{ 'dark': isDark }">
    <div class="container">
      <h1 class="title">Student Assistant Simulator</h1>
      <div class="cards-grid">
        <div
            v-for="agent in agents"
            :key="agent.id"
            class="card"
            @click="selectAgent(agent)"
        >
          <div class="card-content">
            <h2>{{ agent.title }}</h2>
            <p>{{ agent.description }}</p>
          </div>
        </div>
      </div>

      <!-- 添加的RAG按钮 -->
      <div class="rag-button-container">
        <button class="admin-button" @click="goToAdmin">
          Agent Management
        </button>
        <button class="rag-button" @click="goToRag">
          RAG Knowledge Base
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useDark } from '@vueuse/core'
import { useRouter } from 'vue-router'
import { chatAPI } from '../services/api'

const isDark = useDark()
const router = useRouter()

// Add the missing agents ref declaration here
const agents = ref([])

// Fetch agents from backend API
const fetchAgents = async () => {
  try {
    const agentList = await chatAPI.getAllAgents();
    if (agentList && agentList.length > 0) {
      agents.value = agentList;
    }
  } catch (error) {
    console.error('Failed to fetch agents:', error);
  }
};

const selectAgent = (agent) => {
  router.push({
    path: '/agent-chat',
    query: {
      agentId: agent.id,
      agentName: agent.title,
      type: 'agent'
    }
  })
}

// New method: navigate to RAG page
const goToRag = () => {
  window.location.href = 'http://localhost:5173/chat-pdf'
}

// New method: navigate to admin page
const goToAdmin = () => {
  router.push('/agent-admin')
}

// Fetch agents when component mounts
onMounted(() => {
  fetchAgents();
});
</script>

<style scoped lang="scss">
.home {
  min-height: 100vh;
  padding: 0.3rem;
  background: var(--bg-color);
  transition: background-color 0.3s;

  .container {
    max-width: 1600px;
    margin: 0 auto;
    padding: 0 2rem;
  }

  .title {
    text-align: center;
    font-size: 2.5rem;
    margin-bottom: 3rem;
    background: linear-gradient(45deg, #007CF0, #00DFD8);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    animation: fadeIn 1s ease-out;
  }

  .cards-grid {
    display: grid;
    grid-template-columns: repeat(1, 1fr);
    gap: 2rem;
    justify-items: center;
    padding: 1rem;

    @media (min-width: 768px) {
      grid-template-columns: repeat(2, 1fr);
    }

    @media (min-width: 1200px) {
      grid-template-columns: repeat(4, 1fr);
    }
  }

  .card {
    position: relative;
    width: 100%;
    max-width: 320px;
    background: rgba(255, 255, 255, 0.8);
    backdrop-filter: blur(10px);
    border-radius: 20px;
    padding: 2rem;
    text-decoration: none;
    color: inherit;
    transition: all 0.3s ease;
    border: 1px solid rgba(255, 255, 255, 0.1);
    overflow: hidden;
    cursor: pointer;

    .dark & {
      background: rgba(255, 255, 255, 0.05);
      border: 1px solid rgba(255, 255, 255, 0.05);
    }

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);

      .dark & {
        box-shadow: 0 10px 20px rgba(0, 0, 0, 0.3);
      }
    }

    .card-content {
      display: flex;
      flex-direction: column;
      align-items: center;
      text-align: center;
    }

    h2 {
      font-size: 1.5rem;
      margin-bottom: 0.5rem;
    }

    p {
      color: #666;
      font-size: 1rem;

      .dark & {
        color: #999;
      }
    }
  }

  // RAG button container style
  .rag-button-container {
    display: flex;
    justify-content: center;
    gap: 2rem;
    margin-top: 2rem;
  }

  // Admin button style
  .admin-button {
    background: linear-gradient(45deg, #ff6b6b, #ffa502); // Orange/red gradient
    color: white;
    border: none;
    padding: 12px 30px;
    font-size: 1.1rem;
    border-radius: 50px;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 4px 15px rgba(255, 107, 107, 0.3);

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(255, 107, 107, 0.4);
    }

    &:active {
      transform: translateY(0);
    }
  }

  // RAG button style
  .rag-button {
    background: linear-gradient(45deg, #8a2be2, #9370db); // Purple gradient
    color: white;
    border: none;
    padding: 12px 30px;
    font-size: 1.1rem;
    border-radius: 50px;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 4px 15px rgba(138, 43, 226, 0.3);

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(138, 43, 226, 0.4);
    }

    &:active {
      transform: translateY(0);
    }
  }

  &.dark {
    background: #1a1a1a;

    .card {
      background: rgba(255, 255, 255, 0.05);

      p {
        color: #999;
      }
    }
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .home {
    padding: 1rem;

    .container {
      padding: 0 1rem;
    }

    .title {
      font-size: 2rem;
    }

    .card {
      max-width: 100%;
    }
  }
}
</style>
