import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import BudgetPage from '../pages/BudgetPage.vue';
import BudgetTable from '../components/budget/BudgetTable.vue';
import HomePage from '../pages/HomePage.vue';
import Login from '../pages/LoginPage.vue';
import BudgetDetails from '../components/budget/BudgetDetails.vue';

const routes: Array<RouteRecordRaw> = [
  {
    path: '/budget',
    name: 'Budget',
    component: BudgetPage,
    meta: { requiresAuth: true }
  },
  {
    path: '/budget-table',
    name: 'BudgetTable',
    component: BudgetTable,
    meta: { requiresAuth: true }
  },
  {
    path: '/quotes/:id',
    name: 'quote-details',
    component: BudgetDetails,
    meta: { requiresAuth: true }
  },
  {
    path: '/',
    name: 'Home',
    component: HomePage,
    meta: { requiresAuth: false }
  },
  {
    path: '/login',
    name: 'login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/account',
    name: 'Account',
    component: () => import('../pages/AccountPage.vue'),
    meta: { requiresAuth: true }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

function getTokenSafe(): string | null {
  const raw = localStorage.getItem('jwtToken');
  if (!raw) return null;
  const trimmed = String(raw).trim();
  if (!trimmed) return null;
  if (trimmed.toLowerCase() === 'null' || trimmed.toLowerCase() === 'undefined') return null;
  return trimmed;
}

router.beforeEach((to, from, next) => {
  const token = getTokenSafe();

  console.log('[router.beforeEach] to:', to.fullPath, 'name:', to.name, 'requiresAuth:', Boolean(to.meta?.requiresAuth), 'tokenExists:', !!token);

  if (to.meta?.requiresAuth && !token) {
    return next({ name: 'Home' });
  }

  return next();
});

export default router;