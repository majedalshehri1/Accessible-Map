import AppLayout from "@/layout/AppLayout.vue";
import { createRouter, createWebHistory } from "vue-router"


const HomeView = () => import("@/views/HomeView.vue")

// simulate if user is authenticated
const isAuth = () => true;

const routes = [
  {
    path: '/',
    component: AppLayout,
    children: [
      {
        path: '',
        name: 'Home',
        component: HomeView,
      },
    ],
  },
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

// Global gurads
router.beforeEach((to, from, next) => { console.log(`checking pre route gurads from ${from.fullPath} to ${to.fullPath}`); next() });

router.afterEach((to, from) => { console.log(`checking after route gurads from ${from.fullPath} to ${to.fullPath}`); });

export default router