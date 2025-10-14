import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import BudgetPage from '../pages/BudgetPage.vue';
import HomePage from '../pages/HomePage.vue';
import Login from '../pages/LoginPage.vue';

const routes: Array<RouteRecordRaw> = [
  {
    path: '/budget',
    name: 'Budget',
    component: BudgetPage,
    meta: { requiresAuth: true }
  },
  {
    path: '/',
    name: 'Home',
    component: HomePage,
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'login',
    component: Login
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('jwtToken');

  if (to.meta.requiresAuth && !token) {
    next('/login');
  } else if (to.path === '/login' && token) {
    next('/');
  } else {
    next(); 
  }
});

export default router;
