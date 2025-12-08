<template>
  <div class="create-agent" :class="{ 'dark': isDark }">
    <div class="container">
      <h1 class="title">Create New Agent</h1>

      <div class="content-wrapper">
        <div class="form-section">
          <form @submit.prevent="submitForm" class="agent-form">
            <div class="form-group">
              <label for="name">Name *</label>
              <input
                id="name"
                v-model="formData.name"
                type="text"
                required
                placeholder="Enter agent name"
              />
            </div>

            <div class="form-group">
              <label for="age">Age *</label>
              <input
                id="age"
                v-model.number="formData.age"
                type="number"
                min="7"
                max="20"
                required
                placeholder="7-20"
              />
            </div>

            <div class="form-group">
              <label>Personality *</label>
              <div class="personality-tags">
                <span
                  v-for="trait in allPersonalityTraits"
                  :key="trait"
                  @click="togglePersonality(trait)"
                  :class="{ selected: formData.personality.includes(trait) }"
                  class="tag"
                >
                  {{ trait }}
                </span>
                <input
                  type="text"
                  v-model="newTrait"
                  @keydown.enter.prevent="handleEnterKey"
                  placeholder="Add custom trait + Enter"
                  class="custom-trait-input"
                />
              </div>
            </div>

            <div class="form-group">
              <label for="writingStyle">Writing Style *</label>
              <textarea
                id="writingStyle"
                v-model="formData.writingStyle"
                required
                rows="3"
                placeholder="Describe the writing style..."
              ></textarea>
            </div>

            <div class="form-group">
              <label for="speakingStyle">Speaking Style *</label>
              <textarea
                id="speakingStyle"
                v-model="formData.speakingStyle"
                required
                rows="3"
                placeholder="Describe the speaking style..."
              ></textarea>
            </div>

            <div class="form-group">
              <label for="currentState">Current State *</label>
              <textarea
                id="currentState"
                v-model="formData.currentState"
                required
                rows="2"
                placeholder="Describe the current state..."
              ></textarea>
            </div>

            <div class="form-group">
              <label for="goal">Goal (student-led) *</label>
              <textarea
                id="goal"
                v-model="formData.goal"
                required
                rows="2"
                placeholder="Describe the goal..."
              ></textarea>
            </div>

            <div class="form-actions">
              <button
                type="button"
                @click="$router.back()"
                class="cancel-btn"
              >
                Cancel
              </button>
              <button
                type="submit"
                :disabled="loading"
                class="submit-btn"
              >
                {{ loading ? 'Generating...' : 'Submit & Generate' }}
              </button>
            </div>
          </form>
        </div>

        <div class="preview-section">
          <h2>Generated Prompt</h2>
          <div class="prompt-controls" v-if="generatedPrompt || promptLoading">
            <button
              @click="saveAgent"
              class="save-btn"
              :disabled="promptLoading"
            >
              Save Agent
            </button>
          </div>

          <div class="prompt-preview" v-if="!promptLoading && generatedPrompt">
            <textarea
              v-model="editablePrompt"
              class="prompt-editor"
              placeholder="Generated prompt will appear here..."
            ></textarea>
            <div class="markdown-preview">
              <h3>Markdown Preview:</h3>
              <div v-html="renderedMarkdown" class="markdown-content"></div>
            </div>
          </div>

          <div v-else-if="promptLoading" class="loading-container">
            <div class="spinner"></div>
            <p>Generating prompt...</p>
          </div>

          <div v-else class="placeholder">
            Generated prompt will appear here after submission
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { useDark } from '@vueuse/core'
import { chatAPI } from '../services/api'
import { useRouter } from 'vue-router'
import MarkdownIt from 'markdown-it'

const md = new MarkdownIt()

const isDark = useDark()
const router = useRouter()
const loading = ref(false)
const promptLoading = ref(false)
const generatedPrompt = ref('')
const newTrait = ref('')
const editablePrompt = ref('')

// 计算属性用于渲染Markdown
const renderedMarkdown = computed(() => {
  return md.render(editablePrompt.value || '')
})

const formData = reactive({
  name: '',
  age: null,
  personality: [],
  writingStyle: '',
  speakingStyle: '',
  currentState: '',
  goal: ''
})

const predefinedTraits = [
  'Friendly', 'Serious', 'Humorous', 'Patient',
  'Curious', 'Helpful', 'Enthusiastic', 'Thoughtful',
  'Creative', 'Analytical', 'Supportive', 'Encouraging',
  'Adventurous', 'Responsible', 'Optimistic', 'Empathetic'
]

const customTraits = ref([])

const allPersonalityTraits = computed(() => {
  return [...predefinedTraits, ...customTraits.value]
})

const togglePersonality = (trait) => {
  const index = formData.personality.indexOf(trait)
  if (index > -1) {
    formData.personality.splice(index, 1)
  } else {
    formData.personality.push(trait)
  }
}

const handleEnterKey = () => {
  const traitValue = newTrait.value.trim()
  if (!traitValue) return

  // If it's a new custom trait, add it to customTraits
  if (!predefinedTraits.includes(traitValue) && !customTraits.value.includes(traitValue)) {
    customTraits.value.push(traitValue)
  }

  // Select the trait (whether predefined or custom)
  if (!formData.personality.includes(traitValue)) {
    formData.personality.push(traitValue)
  }

  // Clear the input
  newTrait.value = ''
}

// 监听generatedPrompt变化，同步更新editablePrompt
watch(generatedPrompt, (newVal) => {
  editablePrompt.value = newVal
})

const submitForm = async () => {
  try {
    loading.value = true
    promptLoading.value = true

    // Prepare persona object as requested
    const persona = {
      name: formData.name,
      age: formData.age,
      personality: formData.personality.join(', '),
      writingStyle: formData.writingStyle,
      speakingStyle: formData.speakingStyle,
      currentState: formData.currentState,
      goal: formData.goal
    }

    // Call API to generate prompt
    const prompt = await chatAPI.generateAgentPrompt(persona)
    generatedPrompt.value = prompt
  } catch (error) {
    console.error('Error generating prompt:', error)
    alert('Failed to generate prompt. Please try again.')
  } finally {
    loading.value = false
    promptLoading.value = false
  }
}

// 保存编辑后的prompt
const saveAgent = async () => {
  try {
    await chatAPI.saveAgent(formData.name, editablePrompt.value);
    alert('Agent saved successfully!');
    router.push('/agent-admin'); // 保存成功后跳转到 agents 管理页面
  } catch (error) {
    console.error('Error saving agent:', error);
    alert('Failed to save agent. Please try again.');
  }
}
</script>

<style scoped lang="scss">
.create-agent {
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

  .content-wrapper {
    display: grid;
    grid-template-columns: 1fr 2fr; /* 左侧1/3，右侧2/3 */
    gap: 2rem;

    @media (max-width: 1024px) {
      grid-template-columns: 1fr;
    }
  }

  .form-section {
    background: white;
    border-radius: 10px;
    padding: 1.5rem;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

    .dark & {
      background: #2d2d2d;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
    }
  }

  .preview-section {
    background: white;
    border-radius: 10px;
    padding: 1.5rem;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    display: flex;
    flex-direction: column;
    position: relative;

    .dark & {
      background: #2d2d2d;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
    }

    h2 {
      margin-top: 0;
      color: #333;

      .dark & {
        color: #fff;
      }
    }
  }

  .agent-form {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }

  .form-group {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;

    label {
      font-weight: bold;
      color: #333;

      .dark & {
        color: #fff;
      }
    }

    input, textarea {
      padding: 0.75rem;
      border: 1px solid #ddd;
      border-radius: 4px;
      font-family: inherit;
      font-size: 1rem;
      background: white;
      color: #333;

      .dark & {
        background: #3d3d3d;
        border-color: #555;
        color: #fff;
      }

      &:focus {
        outline: none;
        border-color: #007CF0;
        box-shadow: 0 0 0 2px rgba(0, 124, 240, 0.2);
      }

      &:disabled {
        background: #f5f5f5;
        cursor: not-allowed;
      }
    }

    textarea {
      resize: vertical;
      min-height: 60px;
    }
  }

  .personality-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;
    align-items: center;

    .tag {
      padding: 0.4rem 0.8rem;
      background: #e9ecef;
      border-radius: 20px;
      cursor: pointer;
      user-select: none;
      transition: all 0.2s;
      font-size: 0.9rem;

      .dark & {
        background: #3d3d3d;
        color: #fff;
      }

      &:hover {
        background: #dee2e6;

        .dark & {
          background: #4d4d4d;
        }
      }

      &.selected {
        background: #007CF0;
        color: white;

        &:hover {
          background: #0066cc;
        }
      }
    }

    .custom-trait-input {
      flex: 1;
      min-width: 120px;
      margin: 0;
    }
  }

  .form-actions {
    display: flex;
    gap: 1rem;
    margin-top: 1rem;

    .cancel-btn, .submit-btn {
      padding: 0.75rem 1.5rem;
      border: none;
      border-radius: 4px;
      font-size: 1rem;
      cursor: pointer;
      transition: all 0.3s;
      flex: 1;
    }

    .cancel-btn {
      background: #6c757d;
      color: white;

      &:hover {
        background: #5a6268;
      }
    }

    .submit-btn {
      background: linear-gradient(45deg, #007CF0, #00DFD8);
      color: white;

      &:hover:not(:disabled) {
        transform: translateY(-2px);
        box-shadow: 0 4px 10px rgba(0, 124, 240, 0.3);
      }

      &:disabled {
        opacity: 0.7;
        cursor: not-allowed;
        transform: none;
        box-shadow: none;
      }
    }
  }

  .prompt-controls {
    position: absolute;
    top: 1.5rem;
    right: 1.5rem;
    margin-bottom: 1rem;
    z-index: 10;

    .save-btn {
      padding: 0.5rem 1rem;
      background: #28a745;
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;

      &:hover {
        background: #218838;
      }

      &:disabled {
        background: #6c757d;
        cursor: not-allowed;
      }
    }
  }

  .prompt-preview {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 1rem;
    margin-top: 2rem;
  }

  .prompt-editor {
    width: 100%;
    min-height: 200px;
    padding: 1rem;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-family: monospace;
    font-size: 0.9rem;
    resize: vertical;
    background: #f8f9fa;
    color: #333;

    .dark & {
      background: #3d3d3d;
      border-color: #555;
      color: #fff;
    }
  }

  .markdown-preview {
    flex: 1;
    background: #f8f9fa;
    border-radius: 4px;
    padding: 1rem;
    overflow-y: auto;
    min-height: 200px;

    .dark & {
      background: #3d3d3d;
    }

    h3 {
      margin-top: 0;
      color: #333;

      .dark & {
        color: #fff;
      }
    }
  }

  .markdown-content {
    line-height: 1.5;

    h1, h2, h3, h4, h5, h6 {
      margin-top: 1rem;
      margin-bottom: 0.5rem;
      color: #333;

      .dark & {
        color: #fff;
      }
    }

    p {
      margin-bottom: 1rem;
      color: #333;

      .dark & {
        color: #ccc;
      }
    }

    ul, ol {
      padding-left: 1.5rem;
      margin-bottom: 1rem;

      li {
        margin-bottom: 0.25rem;
        color: #333;

        .dark & {
          color: #ccc;
        }
      }
    }

    code {
      padding: 0.2rem 0.4rem;
      background: #e9ecef;
      border-radius: 4px;
      font-family: monospace;
      font-size: 0.875rem;

      .dark & {
        background: #4d4d4d;
      }
    }

    pre {
      padding: 1rem;
      background: #e9ecef;
      border-radius: 4px;
      overflow-x: auto;

      .dark & {
        background: #4d4d4d;
      }

      code {
        padding: 0;
        background: none;
      }
    }

    blockquote {
      margin: 0 0 1rem;
      padding: 0.5rem 1rem;
      border-left: 4px solid #007CF0;
      background: #e9ecef;

      .dark & {
        background: #4d4d4d;
      }

      p {
        margin-bottom: 0;
      }
    }
  }

  .placeholder {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #6c757d;
    font-style: italic;
    text-align: center;
    min-height: 300px;

    .dark & {
      color: #aaa;
    }
  }

  .loading-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    min-height: 300px;

    .spinner {
      width: 50px;
      height: 50px;
      border: 5px solid rgba(0, 124, 240, 0.3);
      border-radius: 50%;
      border-top-color: #007CF0;
      animation: spin 1s linear infinite;
      margin-bottom: 1rem;
    }

    p {
      color: #6c757d;
      font-style: italic;

      .dark & {
        color: #aaa;
      }
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

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 768px) {
  .create-agent {
    padding: 1rem;

    .container {
      padding: 0 1rem;
    }

    .title {
      font-size: 2rem;
    }

    .content-wrapper {
      gap: 1rem;
    }
  }
}
</style>
