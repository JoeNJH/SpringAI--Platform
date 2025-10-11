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
            <img
                :src="getAgentIcon(agent.title)"
                :alt="agent.title"
                class="agent-image"
            />
            <h2>{{ agent.title }}</h2>
            <p>{{ agent.description }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useDark } from '@vueuse/core'
import { useRouter } from 'vue-router'

const isDark = useDark()
const router = useRouter()

const agents = ref([
  {
    id: 1,
    title: 'Avery',
    description: 'A student with many ideas but cannot decide what to write about'
  },
  {
    id: 2,
    title: 'Jasmine',
    description: 'A student who is sensitive to writing feedback'
  },
  {
    id: 3,
    title: 'Kai',
    description: 'A student who is distracted and off-task'
  },
  {
    id: 4,
    title: 'Miles',
    description: 'A student who does not like writing and does not want to participate'
  }
])

const getAgentIcon = (name) => {
  return `/StudentIcon/${name}.png`
}

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
</script>

<style scoped lang="scss">
.home {
  min-height: 100vh;
  padding: 2rem;
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

    .agent-image {
      width: 100%;
      height: 300px;
      object-fit: cover;
      margin-bottom: 1rem;
      border-radius: 10px;
      transition: transform 0.3s ease;
    }

    &:hover .agent-image {
      transform: scale(1.05);
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
