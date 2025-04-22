import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/manage/home',
      meta: { title: 'Homepage' },
    },
    {
      path: '/manage',
      name: 'manage',
      meta: { title: 'Homepage' },
      component: () => import('../views/ManageView.vue'),
      children: [
        {
          path: 'home',
          name: 'manage-home',
          meta: { title: 'User List' },
          component: () => import('../views/HomeView.vue'),
        },
        {
          path: 'account/:id',
          name: 'manage-account',
          meta: { title: 'Account Detail' },
          component: () => import('../views/AccountView.vue'),
          props: true
        },
      ],
    },
    {
      path: '/404',
      name: '404',
      meta: { title: '404 Not Found' },
      component: () => import('../views/NotFoundView.vue'),
    },
    { path: '/:pathMatch(.*)*', redirect: '/404' }, // 捕获所有路由，重定向到404页面
  ],
})

// 添加路由守卫
router.beforeEach((to, from, next) => {
  document.title = to.meta.title || 'My App'

  next()
})

export default router
