// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router';
import Home from '../views/Home.vue';
import LoveApp from '../views/LoveApp.vue';
import Manus from '../views/Manus.vue';

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/love-app',
    name: 'LoveApp',
    component: LoveApp
  },
  {
    path: '/manus',
    name: 'Manus',
    component: Manus
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;