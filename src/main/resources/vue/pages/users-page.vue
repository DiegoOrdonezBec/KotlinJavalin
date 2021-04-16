<template id="users-page">
  <app-frame>
    <app-users></app-users>
  </app-frame>
</template>
<script>
Vue.component("users-page", {
  template: "#users-page",
  data: () => ({ drawer: null }),
});
</script>

