<template id="app-frame">
  <v-app>
    <v-navigation-drawer dark dense v-model="drawer" app>
      <v-list>
        <v-list-item v-for="item in items" :key="item.title" :href="item.link">
          <v-list-item-icon>
            <v-icon>{{ item.icon }}</v-icon>
          </v-list-item-icon>
          <v-list-item-content>
            <v-list-item-title>{{ item.title }}</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
        <v-list-item @click="signOut">Salir</v-list-item>
      </v-list>
    </v-navigation-drawer>

    <v-app-bar app dense>
      <v-app-bar-nav-icon @click="drawer = !drawer"></v-app-bar-nav-icon>

      <v-toolbar-title>Application</v-toolbar-title>
    </v-app-bar>

    <v-main>
      <slot></slot>
    </v-main>
  </v-app>
</template>

<script>
Vue.component("app-frame", {
  template: "#app-frame",
  data: () => ({
    drawer: null,
    search: "",
    items: [
      { title: "Card", icon: "mdi-card", link: "/" },
      { title: "Table", icon: "mdi-table", link: "table" },
      { title: "Users", icon: "mdi-table", link: "users" },
    ],
  }),
  methods: {
    signOut() {
      fetch("http://localhost:8080/api/auth/signout", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
      })
        .then((response) => {
          response.json();
        })
        .catch((error) => console.error("ERROR: " + error))
        .then((response) => location.reload());
    },
  },
});
</script>
