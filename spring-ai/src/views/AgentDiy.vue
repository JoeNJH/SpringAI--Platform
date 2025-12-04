<template>
  <div class="agent-diy" :class="{ 'dark': isDark }">
    <div class="container">
      <h1 class="title">Agent Management</h1>

      <div class="actions-bar">
        <div class="left-actions">
          <button class="refresh-btn" @click="fetchAgents">
            Refresh Agents
          </button>
          <button class="create-btn" @click="$router.push('/agent/create')">
            Create New Agent
          </button>
        </div>
        <router-link to="/agent" class="back-btn">
          Back to Agents
        </router-link>
      </div>

      <div class="agents-table">
        <div class="table-header">
          <div class="header-cell">Agent ID</div>
          <div class="header-cell">Name</div>
          <div class="header-cell">Description</div>
          <div class="header-cell">Actions</div>
        </div>

        <div
            v-for="agent in agents"
            :key="agent.id"
            class="table-row"
        >
          <div class="cell">{{ agent.id }}</div>
          <div class="cell">{{ agent.title }}</div>
          <div class="cell">{{ agent.description }}</div>
          <div class="cell actions-cell">
            <button
                class="action-btn view-btn"
                @click="viewPrompt(agent)"
            >
              View Prompt
            </button>
            <button
                class="action-btn delete-btn"
                @click="deleteAgent(agent.id)"
                :disabled="deletingAgentId === agent.id"
            >
              {{ deletingAgentId === agent.id ? 'Deleting...' : 'Delete' }}
            </button>
          </div>
        </div>

        <div v-if="agents.length === 0" class="no-agents">
          No agents found. Click "Refresh Agents" to load.
        </div>
      </div>

      <!-- Prompt Modal -->
      <div v-if="showPromptModal" class="modal-overlay" @click="closePromptModal">
        <div class="modal-content" @click.stop>
          <div class="modal-header">
            <h2>Agent Prompt - {{ currentAgent?.title }}</h2>
            <button class="close-btn" @click="closePromptModal">&times;</button>
          </div>
          <div class="modal-body">
            <pre class="prompt-content">{{ currentPrompt }}</pre>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useDark } from '@vueuse/core'
import { chatAPI } from '../services/api'

const isDark = useDark()
const agents = ref([])
const deletingAgentId = ref(null)
const showPromptModal = ref(false)
const currentPrompt = ref('')
const currentAgent = ref(null)

// Fetch all agents
const fetchAgents = async () => {
  try {
    const agentList = await chatAPI.getAllAgents()
    if (agentList && agentList.length > 0) {
      agents.value = agentList
    } else {
      agents.value = []
    }
  } catch (error) {
    console.error('Failed to fetch agents:', error)
    agents.value = []
  }
}

// Delete an agent by ID
const deleteAgent = async (agentId) => {
  if (!confirm('Are you sure you want to delete this agent?')) {
    return
  }

  try {
    deletingAgentId.value = agentId
    await chatAPI.deleteAgent(agentId)
    // Remove deleted agent from the list
    agents.value = agents.value.filter(agent => agent.id !== agentId)
  } catch (error) {
    console.error('Failed to delete agent:', error)
    alert('Failed to delete agent. Please try again.')
  } finally {
    deletingAgentId.value = null
  }
}

// View agent prompt
const viewPrompt = (agent) => {
  currentAgent.value = agent
  currentPrompt.value = agent.prompt || 'No prompt available'
  showPromptModal.value = true
}

// Close prompt modal
const closePromptModal = () => {
  showPromptModal.value = false
  currentPrompt.value = ''
  currentAgent.value = null
}

// Load agents when component mounts
onMounted(() => {
  fetchAgents()
})
</script>

<style scoped lang="scss">
.agent-diy {
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
    margin-bottom: 2rem;
    background: linear-gradient(45deg, #007CF0, #00DFD8);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    animation: fadeIn 1s ease-out;
  }

  .actions-bar {
    display: flex;
    justify-content: space-between;
    margin-bottom: 2rem;

    .left-actions {
      display: flex;
      gap: 1rem;
    }

    .refresh-btn, .back-btn, .create-btn {
      background: linear-gradient(45deg, #007CF0, #00DFD8);
      color: white;
      border: none;
      padding: 10px 20px;
      font-size: 1rem;
      border-radius: 50px;
      cursor: pointer;
      transition: all 0.3s ease;
      text-decoration: none;
      display: inline-block;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 10px rgba(0, 124, 240, 0.3);
      }

      &:active {
        transform: translateY(0);
      }
    }

    .back-btn {
      background: linear-gradient(45deg, #8a2be2, #9370db);

      &:hover {
        box-shadow: 0 4px 10px rgba(138, 43, 226, 0.3);
      }
    }

    .create-btn {
      background: linear-gradient(45deg, #28a745, #20c997);
      color: white;
      border: none;
      padding: 10px 20px;
      font-size: 1rem;
      border-radius: 50px;
      cursor: pointer;
      transition: all 0.3s ease;
      text-decoration: none;
      display: inline-block;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 10px rgba(40, 167, 69, 0.3);
      }

      &:active {
        transform: translateY(0);
      }
    }
  }

  .agents-table {
    border-radius: 10px;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

    .dark & {
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
    }
  }

  .table-header {
    display: grid;
    grid-template-columns: 1fr 1fr 1.5fr 1fr;
    background: linear-gradient(45deg, #007CF0, #00DFD8);
    color: white;
    font-weight: bold;

    .header-cell {
      padding: 1rem;
    }
  }

  .table-row {
    display: grid;
    grid-template-columns: 1fr 1fr 1.5fr 1fr;
    border-bottom: 1px solid #eee;
    transition: background-color 0.2s;

    .dark & {
      border-bottom: 1px solid #333;
    }

    &:nth-child(even) {
      background-color: rgba(0, 124, 240, 0.05);

      .dark & {
        background-color: rgba(255, 255, 255, 0.05);
      }
    }

    &:hover {
      background-color: rgba(0, 124, 240, 0.1);

      .dark & {
        background-color: rgba(255, 255, 255, 0.1);
      }
    }
  }

  .cell {
    padding: 1rem;
    display: flex;
    align-items: center;

    &.actions-cell {
      gap: 0.5rem;
    }
  }

  .action-btn {
    padding: 0.5rem 1rem;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 0.9rem;
    transition: all 0.2s;

    &.view-btn {
      background: linear-gradient(45deg, #00b09b, #96c93d);
      color: white;

      &:hover {
        opacity: 0.9;
        transform: translateY(-2px);
      }
    }

    &.delete-btn {
      background: linear-gradient(45deg, #ff416c, #ff4b2b);
      color: white;

      &:hover:not(:disabled) {
        opacity: 0.9;
        transform: translateY(-2px);
      }

      &:disabled {
        opacity: 0.6;
        cursor: not-allowed;
      }
    }
  }

  .no-agents {
    grid-column: 1 / -1;
    text-align: center;
    padding: 3rem;
    color: #666;
    font-size: 1.2rem;

    .dark & {
      color: #999;
    }
  }

  .modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.7);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
  }

  .modal-content {
    background: white;
    border-radius: 10px;
    width: 90%;
    max-width: 800px;
    max-height: 80vh;
    overflow: hidden;
    display: flex;
    flex-direction: column;

    .dark & {
      background: #2d2d2d;
    }
  }

  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem 1.5rem;
    border-bottom: 1px solid #eee;

    .dark & {
      border-bottom: 1px solid #444;
    }

    h2 {
      margin: 0;
      font-size: 1.5rem;
    }

    .close-btn {
      background: none;
      border: none;
      font-size: 2rem;
      cursor: pointer;
      color: #666;

      .dark & {
        color: #aaa;
      }

      &:hover {
        color: #333;

        .dark & {
          color: #fff;
        }
      }
    }
  }

  .modal-body {
    padding: 1.5rem;
    overflow-y: auto;
    flex: 1;
  }

  .prompt-content {
    white-space: pre-wrap;
    word-break: break-word;
    margin: 0;
    font-family: monospace;
    font-size: 1rem;
    line-height: 1.5;
    color: #333;

    .dark & {
      color: #ccc;
    }
  }

  &.dark {
    background: #1a1a1a;
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
  .agent-diy {
    padding: 1rem;

    .container {
      padding: 0 1rem;
    }

    .title {
      font-size: 2rem;
    }

    .actions-bar {
      flex-direction: column;
      gap: 1rem;
    }

    .table-header, .table-row {
      grid-template-columns: 1fr 1fr;
    }

    .table-header .header-cell:nth-child(3),
    .table-header .header-cell:nth-child(4),
    .table-row .cell:nth-child(3),
    .table-row .cell:nth-child(4) {
      display: none;
    }

    .table-header .header-cell,
    .table-row .cell {
      padding: 0.75rem;
    }
  }
}
</style>
