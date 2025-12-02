import { createRouter, createWebHistory } from 'vue-router'
import GameChat from '../views/GameChat.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/ai-chat',
    name: 'AIChat',
    component: () => import('../views/AIChat.vue')
  },
  {
    path: '/comfort-simulator',
    name: 'ComfortSimulator',
    component: () => import('../views/ComfortSimulator.vue')
  },
  {
    path: '/customer-service',
    name: 'CustomerService',
    component: () => import('../views/CustomerService.vue')
  },
  {
    path: '/chat-pdf',
    name: 'ChatPDF',
    component: () => import('../views/ChatPDF.vue')
  },
  {
    path: '/game',
    name: 'game',
    component: () => import('../views/GameChat.vue')
  },
  {
    path: '/agent',
    name: 'agent',
    component: () => import('../views/Agent.vue')
  },
  {
    path: '/agent-chat',
    name: 'AgentChat',
    component: () => import('../views/AgentChat.vue')
  },
  {
    path: '/agent-admin',
    name: 'AgentDiy',
    component: () => import('../views/AgentDiy.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router 