const LOBBY_INDEX = {
    template:
        `
        <div class="main--lobby">
            <div class="main--users">
                <h3 class="main--header">Пользователи</h3>
                <ul class="main--users_list" id="userList">
                    <li v-for="user in users" class="main--users_list--item">
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
                <div class="main--songs_list--item">
                        <div class="songs_list--item--play">#</div>
                        <div class="songs_list--item--author">Автор</div>
                        <div class="songs_list--item--name">Название</div>
                        <div class="songs_list--item--views">Прослуш.</div>
                        <div class="songs_list--item--album">Альбом</div>
                </div>
                <ul class="main--songs_list" id="songList">
                    <li v-for="song in songsInQueue" class="main--songs_list--item" :id="'siq=' + song.queueId">
                        <div v-on:click="playstop" class="songs_list--item--play">
                            <i v-if="song.queueId == global.vue.currentSongId && global.vue.isPlaying" class="fa-solid fa-pause"></i>
                            <i v-else class="fa-solid fa-play"></i>
                        </div>
                        <div class="songs_list--item--author">{{ song.author.name }}</div>
                        <div class="songs_list--item--name">{{ song.name }}</div>
                        <div class="songs_list--item--views">{{ song.views }}</div>
                        <div class="songs_list--item--album">{{ song.album.name }}</div>
                    </li>
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
            global: undefined
        }
    },

    async created() {
        console.log()
        if (!GLOBAL_DATA.vue.isOnLobby || GLOBAL_DATA.vue.lobbyCode != this.$route.params.id) {
            if (GLOBAL_DATA.isSocketConnected)
                this.handshakeLobby();
            else
                eventBus.subscribe("socketOpen", this.handshakeLobby);
        }

        eventBus.invoke("connectedToLobby", (GLOBAL_DATA.vue.lobbyCode));
        this.fetchData();

        this.global = GLOBAL_DATA;

        GLOBAL_DATA.songsInQueue = this.songsInQueue;
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

                    dataJSON.songsInQueue.forEach((songInQueue) => {
                        console.log(songInQueue);
                        this.songsInQueue.push(songInQueue);
                    })

                    GLOBAL_DATA.vue.currentSongId = dataJSON.currentSongId;

                    console.log("Getted data: ",dataJSON);
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

        playstop(elem) {
            const SIQ_ID = elem.path[2].id.split("=")[1];
            const CURRENT_SONG_ID = GLOBAL_DATA.vue.currentSongId;

            if (!SIQ_ID)
                return;

            if (CURRENT_SONG_ID == SIQ_ID)
                return eventBus.invoke("playstopInvoking");

            eventBus.invoke("setSongInvoking", SIQ_ID);
        }
    }
}