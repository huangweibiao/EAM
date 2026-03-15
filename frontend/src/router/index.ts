import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/views/Layout.vue'),
    meta: { requiresAuth: true },
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '仪表盘', requiresAuth: true }
      },
      {
        path: 'system',
        name: 'System',
        meta: { title: '系统管理', requiresAuth: true },
        children: [
          {
            path: 'user',
            name: 'User',
            component: () => import('@/views/system/User.vue'),
            meta: { title: '用户管理', requiresAuth: true }
          },
          {
            path: 'role',
            name: 'Role',
            component: () => import('@/views/system/Role.vue'),
            meta: { title: '角色管理', requiresAuth: true }
          },
          {
            path: 'menu',
            name: 'Menu',
            component: () => import('@/views/system/Menu.vue'),
            meta: { title: '菜单管理', requiresAuth: true }
          },
          {
            path: 'dept',
            name: 'Dept',
            component: () => import('@/views/system/Dept.vue'),
            meta: { title: '部门管理', requiresAuth: true }
          }
        ]
      },
      {
        path: 'asset',
        name: 'Asset',
        meta: { title: '资产管理', requiresAuth: true },
        children: [
          {
            path: 'list',
            name: 'AssetList',
            component: () => import('@/views/asset/AssetList.vue'),
            meta: { title: '资产台账', requiresAuth: true }
          },
          {
            path: 'category',
            name: 'AssetCategory',
            component: () => import('@/views/asset/Category.vue'),
            meta: { title: '资产分类', requiresAuth: true }
          }
        ]
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard
router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth !== false)

  if (requiresAuth) {
    // Check if user is authenticated
    const token = localStorage.getItem('access_token')
    if (!token) {
      next('/login')
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
