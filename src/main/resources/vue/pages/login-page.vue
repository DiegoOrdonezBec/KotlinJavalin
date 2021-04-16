<template id="login-page">
  <app-frame>
    <div class="signin-overlay pa-5">
      <v-card class="signin-card pa-5">
        <h1 class="mt-0 mb-5">Kotlin Admin Template</h1>
        <v-alert v-model="errorAlert" dismissible type="error" class="mb-6">{{
          errorMessage
        }}</v-alert>
        <v-text-field
          outlined
          v-model="userId"
          label="Username"
          append-icon="mdi-account"
        >
        </v-text-field>
        <v-text-field
          outlined
          v-model="password"
          label="Password"
          type="password"
          @keyup.enter="signInOrUp"
          append-icon="mdi-lock"
        >
        </v-text-field>
        <div v-if="isSignin">
          <v-btn @click="signInOrUp" depressed block large color="primary"
            >Acceder</v-btn
          >
        </div>
      </v-card>
    </div>
  </app-frame>
</template>
<script>
Vue.component("login-page", {
  template: "#login-page",
  data: () => ({
    errorAlert: false,
    errorMessage: "",
    isSignin: true,
    userId: "",
    password: "",
  }),
  methods: {
    signInOrUp() {
      let usuario = { username: this.userId, contrasena: this.password };
      fetch("http://localhost:8080/api/auth/", {
        method: "POST",
        body: JSON.stringify(usuario),
        headers: { "Content-Type": "application/json" },
      })
        .then((response) => {
          response.json();
        })
        .catch((error) => console.error("ERROR: " + error))
        .then((response) => location.href = "http://localhost:8080/");
    },
  },
});
</script>
<style>
.signin-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(125deg, #46a6c3, #008ab4);
}
.v-sheet.v-card:not(.v-sheet--outlined).signin-card {
  max-width: 380px;
  margin: 150px auto;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.3);
}
.signin-card h1 {
  font-weight: 400;
  color: #444;
  font-size: 28px;
  text-align: center;
}
</style>