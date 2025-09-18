import AppLayout from "@/layout/AppLayout.vue";
import InfoAccountView from "@/views/InfoAccountView.vue";
import RequestPlaceView from "@/views/RequestPlaceView.vue";
import LoginView from "@/views/LoginView.vue";
import RegisterView from "@/views/RegisterView.vue";
import { createRouter, createWebHistory } from "vue-router"

// import pinia and auth store
import { useAuthStore } from '@/stores/authStore'
import { storeToRefs } from 'pinia'



const HomeView = () => import("@/views/HomeView.vue")

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
      {
        path: '/newPlace',
        name: 'AddPlace',
        component: RequestPlaceView,
      },
      {
        path: '/InfoAccount',
        name: 'InfoAccount', 
        component: InfoAccountView,
      },
      {
        path: '/login',
        name: 'login', 
        component: LoginView,
      },
      {
        path: '/register',
        name: 'register', 
        component: RegisterView,
      },
    ],
  },
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

// Global gurads
router.beforeEach((to, from, next) => {
  console.log(`checking before route guards from ${from.fullPath} to ${to.fullPath}`);

  const authStore = useAuthStore();
  const { user } = storeToRefs(authStore);

  // If user is logged in and trying to access login or register, redirect to home
  if (user.value && (to.name === 'login' || to.name === 'register')) {
    next({ name: 'Home' });
    return;
  }

  // Allow access to home for everyone
  if (to.path === '/') {
    next();
    return;
  }

  // Allow access to login and register for non-authenticated users
  if (!user.value && (to.name === 'login' || to.name === 'register')) {
    next();
    return;
  }

  // For all other routes, require login
  if (!user.value) {
    next({ name: 'login' });
  } else {
    next();
  }
});


export default router