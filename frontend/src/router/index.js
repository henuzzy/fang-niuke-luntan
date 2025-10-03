import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/views/Layout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/Home.vue')
      },
      {
        path: '/messages',
        name: 'Messages',
        component: () => import('@/views/Messages.vue')
      },
      {
        path: '/messages/:conversationId',
        name: 'MessageDetail',
        component: () => import('@/views/MessageDetail.vue')
      },
      {
        path: '/discover',
        name: 'Discover',
        component: () => import('@/views/Discover.vue')
      },
      {
        path: '/topics',
        name: 'Topics',
        component: () => import('@/views/Topics.vue')
      },
      {
        path: '/courses',
        name: 'Courses',
        component: () => import('@/views/Courses.vue')
      },
      {
        path: '/post/:id',
        name: 'PostDetail',
        component: () => import('@/views/PostDetail.vue')
      },
      {
        path: '/profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue')
      },
      {
        path: '/settings',
        name: 'Settings',
        component: () => import('@/views/Settings.vue')
      },
      {
        path: '/user/:id',
        name: 'UserProfile',
        component: () => import('@/views/UserProfile.vue')
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router

