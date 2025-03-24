import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import HomeView from '../views/HomeView.vue';
import LoginView from '../views/LoginView.vue';
import RegisterView from '../views/RegisterView.vue';
import ChatView from '../views/ChatView.vue';
import NewChatRoomView from '../views/NewChatRoomView.vue';
import ProfileView from '../views/ProfileView.vue';
import { useAuthStore } from '@/store/auth';

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView
  },
  {
    path: '/chat',
    name: 'chat',
    component: ChatView,
    meta: { requiresAuth: true }
  },
  {
    path: '/chat/new',
    name: 'newChatRoom',
    component: NewChatRoomView,
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'profile',
    component: ProfileView,
    meta: { requiresAuth: true }
  },
  {
    path: '/about',
    name: 'about',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/AboutView.vue')
  }
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
});

// 인증이 필요한 라우트에 대한 네비게이션 가드
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  
  if (to.matched.some(record => record.meta.requiresAuth) && !authStore.isLoggedIn) {
    // 인증이 필요한 페이지이고 로그인되어 있지 않은 경우 로그인 페이지로 리디렉션
    next({ name: 'login' });
  } else {
    // 그 외의 경우 정상적으로 라우팅
    next();
  }
});

export default router; 