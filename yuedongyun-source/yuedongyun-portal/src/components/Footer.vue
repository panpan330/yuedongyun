<!-- 公用底部部组件 -->
<template>
  <footer>
    <div class="container">
      <div class="links">
          <span v-for="item in Links" :key="item.title" @click="goDetails(item.link)">{{item.title}}</span>
        </div>
        <div class="about fx-sb">
          <div class="info">
      <p>悦动云致力于打造更轻量的在线健身与互动社区体验，提供训练内容、跟练记录、赛季排行与运动商城服务。</p>
            <p>&#169 2026 YueDongYun Inc. 保留所有权利</p>
          </div>
          <div class="logo">
            <img src="@/assets/logo.png" alt="" srcset="">
          </div>
        </div>
    </div>
  </footer>
</template>
<script setup>
  // 站内-友情链接
  const Links = [
    {title: '关于我们', link: '/'},
      {title: '训练内容', link: '/'},
    {title: '训练营', link: '/'},
    {title: '会员服务', link: '/'},
    {title: '帮助中心', link: '/'},
    {title: '新手指南', link: '/'},
    {title: '训练计划', link: '/'},
    {title: '社区互动', link: '/'},
    {title: '商务合作', link: '/'}
  ]
  // 转入详情页
  const goDetails = (link) => {
    return false;
  }
</script>
<style lang="scss" scoped>
footer {
  width: 100%;
  background-color: var(--color-white);
  border-top: 1px solid #EEEEEE;
  text-align: left;
  padding: 35px 0;
  font-size: 14px;
  .links {
    margin-bottom: 25px;
    cursor: pointer;
    span{
      margin-right: 30px;
    }
  }
  .about{
    line-height: 30px;
    color: var(--color-font2);
    img{
      width: 144px;
      height: 48px;
    }
  }
}
</style>
