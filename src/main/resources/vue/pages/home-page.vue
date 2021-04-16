<template id="home-page">
  <app-frame>
    <app-card></app-card>
  </app-frame>
</template>
<script>
Vue.component("home-page", {
  template: "#home-page",
  data: () => ({ drawer: null }),
});
</script>

