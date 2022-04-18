const LOBBY_INDEX = {
    template:
        `
        <div class="main--lobby">
            <div class="main--users">
                <h3 class="main--header">Пользователи</h3>
                <ul class="main--users_list" id="userList">
                    ааааа
                </ul>
            </div>
    
            <div class="main--songs">
                Добавление песни в лист
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
                        <i class="controller--button fa-solid fa-right-from-bracket"></i>
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
        this.fetchData();
    },

    methods: {
        async fetchData() {
            console.log("fetching lobby data...");
            console.log(this.$route.params);
        },
    }
}