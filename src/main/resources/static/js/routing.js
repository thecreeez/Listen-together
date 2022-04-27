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

            isOnLobby: false,
            lobbyCode: null,

            isPlaying: false,

            audio: new Audio("/songs/getCurrentSong"),

            songInQueueList: [],
            currentSong: null,

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

            console.log("data from back: ",data);

            if (data.get("username") !== "null") {
                this.username = data.get("username");
                this.isAuth = data.get("isAuth");

                if (data.get("lobbyCode"))
                    this.lobbyCode = data.get("lobbyCode");
                this.isOnLobby = data.get("isOnLobby");
            }

            GLOBAL_DATA.vue = this;
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
            socket.send("pcontrol//playstop");
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

/*
TODO:
    Фетчинг данных без thymeleaf через get-запросы https://www.youtube.com/watch?v=tWFrqiS5jqE&ab_channel=Infybuzz
    Подключение сокета и команды коннекта и дисконнекта к лобби (и последующие для контроля за лобби)
    Работающий плеер
 */