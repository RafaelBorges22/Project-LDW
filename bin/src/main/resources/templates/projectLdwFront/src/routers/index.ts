import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import BudgetPage from '../pages/BudgetPage.vue';
import HomePage from '../pages/HomePage.vue';


const routes: Array<RouteRecordRaw> = [
    {
        path: '/budget',
        name: 'Budget',
        component: BudgetPage
    },
    {
        path: '/',
        name: 'Home',
        component: HomePage
    }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;