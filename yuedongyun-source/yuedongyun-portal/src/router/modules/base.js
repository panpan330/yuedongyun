import Layout from '@/pages/layouts/index.vue';

export default [
  {
    path: '/main',
    component: Layout,
    redirect: '/main/index',
    name: 'main',
    meta: { title: '首页' },
    children: [
      {
        path: 'index',
        name: 'index',
        component: () => import('@/pages/main/index.vue'),
        meta: { title: '首页' },
      },
      {
        path: 'voucher',
        name: 'voucher',
        component: () => import('@/pages/main/voucher.vue'),
        meta: { title: '首页' },
      }
    ],
  },
  {
    path: '/search',
    component: Layout,
    name: 'search',
    redirect: '/search/index',
      meta: { title: '训练搜索' },
    children: [
      {
        path: 'index',
        name: 'search',
        component: () => import('@/pages/trainingSearch/index.vue'),
      meta: { title: '训练搜索' },
      }
    ],
  },
  {
    path: '/askDetails',
    component: Layout,
    name: 'askDetails',
    redirect: '/askDetails/index',
    meta: { title: '问题回复详情' },
    children: [
      {
        path: 'index',
        name: 'askDetails',
        component: () => import('@/pages/ask/askDetails.vue'),
        meta: { title: '问题回复详情' },
      }
    ],
  },
  {
    path: '/details',
    component: Layout,
    name: 'details',
    redirect: '/details/index',
    meta: { title: '问题详情' },
    children: [
      {
        path: 'index',
        name: 'details',
        component: () => import('@/pages/trainingDetails/index.vue'),
        meta: { title: '问题详情' },
      }
    ],
  },
  {
    path: '/workout',
    name: 'workout',
    redirect: '/workout/index',
    meta: { title: '训练' },
    children: [
      {
        path: 'index',
        name: 'workout',
        component: () => import('@/pages/workout/index.vue'),
        meta: { title: '训练' },
      }
    ],
  },
  {
    path: '/pay',
    name: 'pay',
    component: Layout,
    redirect: '/pay/settlement',
    meta: { title: '购买训练' },
    children: [
      {
        path: 'settlement',
        name: 'settlement',
        component: () => import('@/pages/pay/settlement.vue'),
        meta: { title: '训练结算页' },
      },
      {
        path: 'carts',
        name: 'carts',
        component: () => import('@/pages/pay/carts.vue'),
        meta: { title: '购物车' },
      },
      {
        path: 'payment',
        name: 'payment',
        component: () => import('@/pages/pay/payment.vue'),
        meta: { title: '支付训练订单' },
      },
      {
        path: 'success',
        name: 'success',
        component: () => import('@/pages/pay/success.vue'),
        meta: { title: '支付成功' },
      }
    ],
  },
  {
    path: '/personal',
    component: Layout,
    name: 'personal',
    redirect: '/personal/main',
    meta: { title: '个人中心' },
    children: [
      {
        path: 'main',
        name: 'personalMain',
        component: () => import('@/pages/personal/index.vue'),
        meta: { title: '个人中心首页' },
        children:[
          {
            path: 'myTraining',
            name: 'myTraining',
            component: () => import('@/pages/personal/myTraining.vue'),
            meta: { title: '我的训练', active:'myTraining', icon: '&#xe611;'},
          },
          {
            path: 'myExam',
            name: 'myExam',
            component: () => import('@/pages/personal/myExam.vue'),
            meta: { title: '我的考核', active:'myExam', icon: '&#xe615;'},
          },
          {
            path: 'myExamDetails',
            name: 'myExamDetails',
            component: () => import('@/pages/personal/myExamDetails.vue'),
            meta: { title: '我的考核', current: '考核详情', active:'myExam', hidden: true, icon: '&#xe615;'},
          },
          {
            path: 'myOrder',
            name: 'myOrder',
            component: () => import('@/pages/personal/myOrder.vue'),
            meta: { title: '我的订单', active:'myOrder', icon: '&#xe60b;'},
          },
          {
            path: 'myOrderDetails',
            name: 'myOrderDetails',
            component: () => import('@/pages/personal/myOrderDetails.vue'),
            meta: { title: '我的订单', current: '订单详情', active:'myOrder', hidden: true, icon: '&#xe60b;'},
          },
          {
            path: 'myVoucher',
            name: 'myVoucher',
            component: () => import('@/pages/personal/myVoucher.vue'),
            meta: { title: '我的商城券', active:'myVoucher', icon: '&#xe616;'},
          },
          {
            path: 'myVoucherExplain',
            name: 'myVoucherExplain',
            component: () => import('@/pages/personal/myVoucherExplain.vue'),
            meta: { title: '我的商城券', current: '商城券说明', active:'myVoucher', hidden: true, icon: '&#xe616;'},
          },
          {
            path: 'myMessage',
            name: 'myMessage',
            component: () => import('@/pages/personal/myMessage.vue'),
            meta: { title: '我的消息', active:'myMessage',hidden: true, icon: '&#xe612;'},
          },
          {
            path: 'myFitPoint',
            name: 'myFitPoint',
            component: () => import('@/pages/personal/myFitPoint.vue'),
            meta: { title: '我的积分', active:'myFitPoint', icon: '&#xe610;'},
          },
          {
            path: 'myFitPointRanking',
            name: 'myFitPointRanking',
            component: () => import('@/pages/personal/myFitPointRanking.vue'),
            meta: { title: '我的积分',current: '赛季榜', active:'myFitPoint', hidden: true, icon: '&#xe610;'},
          },
          {
            path: 'myCollect',
            name: 'myCollect',
            component: () => import('@/pages/personal/myCollect.vue'),
            meta: { title: '我的收藏', active:'myCollect', hidden: true, icon: '&#xe613;'},
          },
          {
            path: 'mySet',
            name: 'mySet',
            component: () => import('@/pages/personal/mySet.vue'),
            meta: { title: '个人设置', active:'mySet', icon: '&#xe617;'},
          }
        ]
      },
    ],
  },
];

