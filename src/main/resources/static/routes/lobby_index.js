const LOBBY_INDEX = {
    template:
        `
        <div class="main--lobby">
            <div class="main--users">
                <h3 class="main--header">Пользователи</h3>
                <ul class="main--users_list" id="userList">
                    <li v-for="user in users" class="main--users_list--item" :id="'user//id=' + user.id">
                        <div class="main--users_list--item--username"> {{ user.name }} </div>
    
                        <div class="main--users_list--item--buttons">
                            <div class="users_list--item--mutebutton">
                                <i v-if="user.state == 'LISTENING'" class="fa-solid fa-volume-high main--users_list--item--buttons--item"></i>
                                <i v-if="user.state == 'MUTED'" class="fa-solid fa-volume-xmark main--users_list--item--buttons--item"></i>
                                <i v-if="user.state == 'DISCONNECTED'" class="fa-solid fa-person-walking-arrow-right main--users_list--item--buttons--item"></i>
                                <i v-if="!user.state" class="fa-solid fa-person-walking-arrow-right main--users_list--item--buttons--item"></i>
                            </div>
                            <div v-on:click="kick(elem)" class="users_list--item--kickbutton"><i class="fa-solid fa-door-open"></i></div>
                        </div>
                    </li>
                </ul>
            </div>
    
            <div class="main--songs">
                Список песен в листе
                <!-- div class="songs--list--wrapper" th:insert="blocks/songs-list :: songs-list"></div>
                <div class="songs_list--top">
                    <h3 class="songs_list--top--author">Автор</h3>
                    <h3 class="songs_list--top--song">Название</h3>
                    <h3 class="songs_list--top--album">Альбом</h3>
                    <h3 class="songs_list--top--views">Прослушивания</h3>
                </div -->
                <ul class="main--songs_list" id="songList">
                    
                </ul>
            </div>
    
            <div class="main--controller">
                <h3 class="main--header">Управление</h3>
                <div class="controller--item">
                    <div class="controller--lobby_id">
                            <input id="codeInput" class="controller--lobby_id--input" type="text" placeholder="ID Лобби" :value="this.$route.params.id" maxlength="6" readonly>
                    </div>
                    <div class="controller--leave">
                        <i v-on:click="leaveFromLobby()" class="controller--button fa-solid fa-right-from-bracket"></i>
                    </div>
                </div>
            </div>
        </div>`,

    data() {
        return {
            users: [],
            songsInQueue: [],
        }
    },

    created() {
        if (!GLOBAL_DATA.vue.isOnLobby || GLOBAL_DATA.vue.lobbyCode != this.$route.params.id) {
            if (GLOBAL_DATA.isSocketConnected)
                this.handshakeLobby();
            else
                eventBus.subscribe("socketOpen", this.handshakeLobby);
        }

        eventBus.invoke("connectedToLobby", (GLOBAL_DATA.vue.lobbyCode));
        this.fetchData();

        GLOBAL_DATA.users = this.users;
    },

    methods: {
        async fetchData() {
            await fetch("/get/lobbyData")
                .then((response) => {
                    return response.text()
                }).then((data) => {
                    const dataJSON = JSON.parse(data);

                    dataJSON.users.forEach((user) => {
                        if (!isUserExist(user.id))
                            this.users.push(user);
                    })

                    GLOBAL_DATA.vue.songInQueueList = dataJSON.songsInQueue;
                    GLOBAL_DATA.vue.currentSong = dataJSON.currentSong;
                })
        },

        async handshakeLobby() {
            socket.send("conn//"+this.$route.params.id);
            GLOBAL_DATA.vue.fetchData();
        },

        async leaveFromLobby() {
            socket.send("disc");
            GLOBAL_DATA.vue.fetchData();
            this.$router.push("/");
        },

    }
}