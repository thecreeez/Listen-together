const INDEX_COMPONENT = {
    template:
        `
        <div class="main--index">
            <div class="main--index--lobby">
                <input class="lobby--input" placeholder="ID Лобби" type="text" maxlength="6" id="lobbyCode">
                <button v-on:click="connectToLobby()" class="lobby--btn">GO</button>
            </div>
    
            <div class="main--info">
                <p>Для создания лобби оставьте поле пустым</p>
                <p>Лобби: {{ lobbies }} Песен: {{ songs }} Пользователи: {{ users }}</p>
            </div>
        </div>`,

    data() {
        return {
            lobbies: 0,
            songs: 0,
            users: 0
        }
    },

    created() {
        this.fetchData();
    },

    methods: {
        async fetchData() {
            await fetch("/get/stats")
                .then((response) => {
                    return response.text()
                }).then((data) => {
                    const dataJSON = JSON.parse(data);

                    this.lobbies = dataJSON.lobbies;
                    this.songs = dataJSON.songs;
                    this.users = dataJSON.users;
                })
        },

        async connectToLobby() {
            const code = document.getElementById("lobbyCode").value.toUpperCase();

            socket.send("conn//"+code);

            eventBus.invoke("connectingToLobby", code);
            GLOBAL_DATA.vue.fetchData();
        }
    }
}