<template id="app-users">
  <v-card>
    <v-card-title>
      <v-text-field
        v-model="search"
        append-icon="mdi-magnify"
        label="Search"
        single-line
        hide-details
      ></v-text-field>
    </v-card-title>
    <v-data-table
      :headers="headers"
      :items="filteredUsers"
      sort-by="username"
      class="elevation-1"
    >
      <template v-slot:top>
        <v-toolbar flat>
          <v-toolbar-title>Usuarios</v-toolbar-title>
          <v-divider class="mx-4" inset vertical></v-divider>
          <v-spacer></v-spacer>
          <v-dialog v-model="dialog" max-width="500px">
            <template v-slot:activator="{ on, attrs }">
              <v-btn color="primary" dark class="mb-2" v-bind="attrs" v-on="on">
                New usuario
              </v-btn>
            </template>
            <v-card>
              <v-card-title>
                <span class="headline">{{ formTitle }}</span>
              </v-card-title>

              <v-card-text>
                <v-container>
                  <v-row>
                    <v-col cols="12" sm="6" md="4">
                      <v-text-field
                        v-model="editedItem.username"
                        label="Username"
                      ></v-text-field>
                    </v-col>
                    <v-col cols="12" sm="6" md="4">
                      <v-text-field
                        v-model="editedItem.contrasena"
                        :type="'password'"
                        label="Contrasena"
                      ></v-text-field>
                    </v-col>
                    <v-col cols="12" sm="6" md="4">
                      <v-select
                        v-model="editedItem.rolename"
                        :items="roles"
                        label="Role"
                      >
                      </v-select>
                    </v-col>
                    <v-col cols="12" sm="6" md="4">
                      <v-select
                        v-model="editedItem.enabled"
                        :items="enabledOptions"
                        label="Enabled"
                      >
                      </v-select>
                    </v-col>
                  </v-row>
                </v-container>
              </v-card-text>

              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="blue darken-1" text @click="close">
                  Cancel
                </v-btn>
                <v-btn color="blue darken-1" text @click="save"> Save </v-btn>
              </v-card-actions>
            </v-card>
          </v-dialog>
          <v-dialog v-model="dialogDelete" max-width="500px">
            <v-card>
              <v-card-title class="headline"
                >Are you sure you want to delete this item?</v-card-title
              >
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="blue darken-1" text @click="closeDelete"
                  >Cancel</v-btn
                >
                <v-btn color="blue darken-1" text @click="deleteItemConfirm"
                  >OK</v-btn
                >
                <v-spacer></v-spacer>
              </v-card-actions>
            </v-card>
          </v-dialog>
        </v-toolbar>
      </template>
      <template v-slot:item.actions="{ item }">
        <v-icon small class="mr-2" @click="editItem(item)"> mdi-pencil </v-icon>
        <v-icon small @click="deleteItem(item)"> mdi-delete </v-icon>
      </template>
    </v-data-table>
  </v-card>
</template>

<script>
Vue.component("app-users", {
  template: "#app-users",
  data: () => ({
    dialog: false,
    search: "",
    roles: ["USER", "ADMIN"],
    enabledOptions: ["T", "F"],
    dialogDelete: false,
    headers: [
      {
        text: "Username",
        align: "start",
        sortable: true,
        value: "username",
      },
      { text: "Contrasena", value: "contrasena" },
      { text: "Role", value: "rolename" },
      { text: "Enabled", value: "enabled" },
      { text: "Actions", value: "actions", sortable: false },
    ],
    users: [],
    editedIndex: -1,
    editedItem: {
      id: "",
      username: "",
      contrasena: "",
      rolename: "",
      enabled: "",
    },
    defaultItem: {
      id: "",
      username: "",
      contrasena: "",
      rolename: "",
      enabled: "",
    },
  }),

  computed: {
    formTitle() {
      return this.editedIndex === -1 ? "New Item" : "Edit Item";
    },
    filteredUsers() {
      return this.users.filter((user) => {
        return user.username
          .normalize("NFD")
          .replace(/[\u0300-\u036f]/g, "")
          .toLowerCase()
          .startsWith(
            this.search
              .normalize("NFD")
              .replace(/[\u0300-\u036f]/g, "")
              .toLowerCase()
          );
      });
    },
  },

  watch: {
    dialog(val) {
      val || this.close();
    },
    dialogDelete(val) {
      val || this.closeDelete();
    },
  },

  created() {
    this.initialize();
  },

  methods: {
    initialize() {
      this.updateTable();
    },

    updateTable() {
      fetch("http://localhost:8080/api/user/")
        .then((response) => response.json())
        .then((result) => (this.users = result));

      this.users = this.users.sort((i1, i2) =>
        new Intl.Collator("es", { sensitivity: "base" }).compare(
          i1.username,
          i2.username
        )
      );
    },

    editItem(item) {
      this.editedIndex = this.users.indexOf(item);
      this.editedItem = Object.assign({}, item);
      this.dialog = true;
    },

    deleteItem(item) {
      this.editedIndex = this.users.indexOf(item);
      this.editedItem = Object.assign({}, item);
      this.dialogDelete = true;
    },

    deleteItemConfirm() {
      fetch("http://localhost:8080/api/user/" + this.editedItem.id, {
        method: "DELETE",
        headers: { "Content-Type": "application/json" },
      })
        .catch((error) => {
          console.error("ERROR: " + error);
          this.updateTable();
        })
        .then((response) => this.updateTable());

      this.closeDelete();
    },

    close() {
      this.dialog = false;
      this.$nextTick(() => {
        this.editedItem = Object.assign({}, this.defaultItem);
        this.editedIndex = -1;
      });
    },

    closeDelete() {
      this.dialogDelete = false;
      this.$nextTick(() => {
        this.editedItem = Object.assign({}, this.defaultItem);
        this.editedIndex = -1;
      });
    },

    save() {
      if (this.editedIndex > -1) {
        fetch("http://localhost:8080/api/user/" + this.editedItem.id, {
          method: "PATCH",
          body: JSON.stringify(this.editedItem),
          headers: { "Content-Type": "application/json" },
        })
          .catch((error) => {
            console.error("ERROR: " + error);
            this.updateTable();
          })
          .then((response) => this.updateTable());
      } else {
        fetch("http://localhost:8080/api/user/", {
          method: "POST",
          body: JSON.stringify(this.editedItem),
          headers: { "Content-Type": "application/json" },
        })
          .then((response) => {
            response.json();
          })
          .catch((error) => {
            console.error("ERROR: " + error);
            this.updateTable();
          })
          .then((response) => this.updateTable());
      }
      this.close();
    },
  },
});
</script>
