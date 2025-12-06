import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import BudgetPage from '../pages/BudgetPage.vue';
import BudgetTable from '../components/budget/BudgetTable.vue';
import HomePage from '../pages/HomePage.vue';
import Login from '../pages/LoginPage.vue';
import BudgetDetails from '../components/budget/BudgetDetails.vue';
import BudgetFromCL from '../pages/BudgetFromCL.vue';
import ForbiddenErrorPage from '../pages/err/403.vue';
import NotFoundErrorPage from '../pages/err/404.vue';

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
    meta: { requiresAuth: true, role: 'ADMIN' }
  },
  {
    path: '/budget-table-cl',
    name: 'BudgetFromCL',
    component: BudgetFromCL,
    meta: { requiresAuth: true }
  },
  {
    path: '/quotes/:id',
    name: 'quote-details',
    component: BudgetDetails,
    meta: { requiresAuth: true, role: 'ADMIN' }
  },
  {
    path: '/',
    name: 'Home',
    component: HomePage
  },
  {
    path: '/login',
    name: 'login',
    component: Login
  },
  {
    path: '/account',
    name: 'Account',
    component: () => import('../pages/AccountPage.vue'),
    meta: { requiresAuth: true }
  },
  {
  path: '/403',
  name: 'Forbidden',
  component:ForbiddenErrorPage,
  meta: { requiresAuth: false }
  },
  {
  path: '/404',
  name: 'NotFound',
  component: NotFoundErrorPage,
},
{
  path: '/:pathMatch(.*)*',
  redirect: '/404'
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

// decodifica JWT manualmente (sem lib)
function decodeJwt(token: string): any | null {
  try {
    const payload = token.split('.')[1];
    const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
    return JSON.parse(decoded);
  } catch (e) {
    console.warn('Erro ao decodificar token:', e);
    return null;
  }
}

router.beforeEach((to, from, next) => {
  const token = getTokenSafe();
  const requiresAuth = Boolean(to.meta?.requiresAuth);

  console.log('[router.beforeEach] to:', to.fullPath, 'requiresAuth:', requiresAuth, 'tokenExists:', !!token);

  if (requiresAuth && !token) {
    return next({ name: 'Home' });
  }

  // Verifica role se a rota exigir
  if (to.meta?.role) {
    const decoded = token ? decodeJwt(token) : null;

    const userRole = decoded?.role || decoded?.authorities || decoded?.roles;

    console.log('Role do usuário:', userRole, '| role necessária:', to.meta.role);

    if (!userRole || !String(userRole).includes(String(to.meta.role))) {
      return next({ name: 'Forbidden' });
    }
  }

  return next();
});

export default router;
