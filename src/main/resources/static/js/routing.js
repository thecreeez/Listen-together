const routes = [
    { path: "/", component: INDEX_COMPONENT, props: true },
    { path: "/auth/login", component: AUTH_LOGIN_COMPONENT, props: true },
    { path: "/auth/register", component: AUTH_REGISTER_COMPONENT, props: true },
    { path: "/upload", component: UPLOAD_COMPONENT, props: true},
    { path: "/lobby/:id", component: LOBBY_INDEX, props: true}
]

const router = VueRouter.createRouter({
    history: VueRouter.createWebHashHistory(),
    routes
})


const app = Vue.createApp({
    data() {
        return {
            username: null,
            isAuth: false,
            users: new Map(),
            ping: 0,
            state: STATE.START,

            volume: 0.8,
            isAwaitingSync: false,

            isOnLobby: false,
            lobbyCode: null,

            isPlaying: false,

            audio: new Audio("/songs/getCurrentSong"),

            songInQueueList: [],
            currentSongId: -1,

            durationSliderWidth: 0+'%'
        };
    },

    created() {
        this.fetchData();
        this.eventBusSubscribe();

        if (this.isOnLobby)
            eventBus.invoke("connectedToLobby");
    },

    methods: {
        async fetchData() {
            const data = parseData(document.getElementById("data").innerText);

            if (data.get("username") !== "null") {
                this.username = data.get("username");
                this.isAuth = data.get("isAuth");

                if (data.get("lobbyCode"))
                    this.lobbyCode = data.get("lobbyCode");
                this.isOnLobby = data.get("isOnLobby");
            }

            if (this.lobbyCode)
                this.getLobbyData();

            GLOBAL_DATA.vue = this;
        },

        async getLobbyData() {
            await fetch("/get/lobbyData")
                .then((response) => {
                    return response.text()
                }).then(async (data) => {
                    const dataJSON = JSON.parse(data);

                    if (dataJSON.playing) {
                        this.isPlaying = true;
                        eventBus.invoke("getSongFromServer");
                    } else {
                        eventBus.invoke("onStop");
                    }

                    console.log("Getted data: ",dataJSON);
                })
        },

        async leave() {
            await fetch("/auth/leave");

            location.reload();
        },

        eventBusSubscribe() {
            console.log("Подписка на события EventBus...");

            eventBus.subscribe("socketOpen", this.connectedToSocket);
            eventBus.subscribe("userConnect", this.userConnect);
        },

        async connectedToSocket() {

            console.log("Подключено к сокету! " + this.lobbyCode);
        },

        playstop() {
            eventBus.invoke("playstopInvoking");
        },

        moveDuration(event) {
            const MAX_CURRENT_AUDIO_LENGTH = this.audio.duration * 1000;
            const PLAYER_WIDTH = event.target.className == "player--duration--slider--done" ? event.path[1].offsetWidth : event.target.offsetWidth;
            const CLICKED_X = event.offsetX < 0 ? 0 : event.offsetX;

            let time = Math.round((CLICKED_X / PLAYER_WIDTH) * MAX_CURRENT_AUDIO_LENGTH);

            socket.send("pcontrol//movetime//"+time);
        },

        skipsong() {
            console.log("Пропуск песни...")
            socket.send("pcontrol//skipsong");
        },

        prevsong() {
            console.log("Включение предыдущей песни...")
            socket.send("pcontrol//prevsong");
        },

        repeat() {
            socket.send("pcontrol//nextrepeat");
        }
    }
});

app.use(router);
app.mount('#app');

window.onload = () => {
}

function parseData(data) {
    const res = new Map();

    data = data
        .slice(1, -1)
        .split(", ");

    data.forEach(elem => {
        const args = elem.split("=");

        if (args.length === 2) {
            let name = args[0];
            let value = args[1];

            switch (args[1]) {
                case "true": value = true; break;
                case "false": value = false; break;
            }

            res.set(name, value);
        }
    })

    return res;
}

eventBus.subscribe("setState", (data) => {
    console.log("Смена состояния сайта на "+data.state+". Причина: "+data.reason);
    GLOBAL_DATA.vue.state = data.state;
})

eventBus.subscribe("connectingToLobby", (code) => {
    if (code == "") {
        eventBus.invoke("setState", {state: STATE.AWAITING_LOBBY, reason: "Создание лобби"});
    } else {
        eventBus.invoke("setState", {state: STATE.AWAITING_LOBBY, reason: "Подключение к лобби"});
    }
})

eventBus.subscribe("connectedToLobby", (code) => {
    eventBus.invoke("setState", {state: STATE.ON_LOBBY, reason: "Завершено подключение к лобби"});
})

/*
TODO:
    Фетчинг данных без thymeleaf через get-запросы https://www.youtube.com/watch?v=tWFrqiS5jqE&ab_channel=Infybuzz
    Подключение сокета и команды коннекта и дисконнекта к лобби (и последующие для контроля за лобби)
    Работающий плеер
 */