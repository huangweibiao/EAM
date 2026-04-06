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
          },
          {
            path: 'transfer',
            name: 'AssetTransfer',
            component: () => import('@/views/asset/Transfer.vue'),
            meta: { title: '资产调拨', requiresAuth: true }
          },
          {
            path: 'inventory',
            name: 'AssetInventory',
            component: () => import('@/views/asset/Inventory.vue'),
            meta: { title: '资产盘点', requiresAuth: true }
          }
        ]
      },
      {
        path: 'maintenance',
        name: 'Maintenance',
        meta: { title: '设备维护', requiresAuth: true },
        children: [
          {
            path: 'plan',
            name: 'MaintenancePlan',
            component: () => import('@/views/maintenance/Plan.vue'),
            meta: { title: '维护计划', requiresAuth: true }
          },
          {
            path: 'record',
            name: 'MaintenanceRecord',
            component: () => import('@/views/maintenance/Record.vue'),
            meta: { title: '维护记录', requiresAuth: true }
          }
        ]
      },
      {
        path: 'workorder',
        name: 'WorkOrder',
        meta: { title: '工单管理', requiresAuth: true },
        children: [
          {
            path: 'list',
            name: 'WorkOrderList',
            component: () => import('@/views/workorder/WorkOrder.vue'),
            meta: { title: '工单列表', requiresAuth: true }
          }
        ]
      },
      {
        path: 'spare',
        name: 'Spare',
        meta: { title: '备件库存', requiresAuth: true },
        children: [
          {
            path: 'part',
            name: 'SparePart',
            component: () => import('@/views/spare/Part.vue'),
            meta: { title: '备件列表', requiresAuth: true }
          },
          {
            path: 'inbound',
            name: 'PartInbound',
            component: () => import('@/views/spare/Inbound.vue'),
            meta: { title: '入库记录', requiresAuth: true }
          },
          {
            path: 'outbound',
            name: 'PartOutbound',
            component: () => import('@/views/spare/Outbound.vue'),
            meta: { title: '出库记录', requiresAuth: true }
          },
          {
            path: 'warning',
            name: 'InventoryWarning',
            component: () => import('@/views/spare/Warning.vue'),
            meta: { title: '库存预警', requiresAuth: true }
          }
        ]
      },
      {
        path: 'purchase',
        name: 'Purchase',
        meta: { title: '采购管理', requiresAuth: true },
        children: [
          {
            path: 'request',
            name: 'PurchaseRequest',
            component: () => import('@/views/purchase/Request.vue'),
            meta: { title: '采购申请', requiresAuth: true }
          },
          {
            path: 'order',
            name: 'PurchaseOrder',
            component: () => import('@/views/purchase/Order.vue'),
            meta: { title: '采购订单', requiresAuth: true }
          }
        ]
      },
      {
        path: 'report',
        name: 'Report',
        meta: { title: '报表分析', requiresAuth: true },
        children: [
          {
            path: 'dashboard',
            name: 'ReportDashboard',
            component: () => import('@/views/report/Dashboard.vue'),
            meta: { title: '数据看板', requiresAuth: true }
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
