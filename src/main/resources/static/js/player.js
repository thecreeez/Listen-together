eventBus.subscribe("playerControl", (args) => {
    switch (args[1]) {
        case "isPlaying": {
            if (args[2] == "true")
                eventBus.invoke("onPlay")
            if (args[2] == "false")
                eventBus.invoke("onStop")

            break;
        }
        case "sync": {
            GLOBAL_DATA.vue.ping = Math.abs(((Number(args[2]) / 1000)-(GLOBAL_DATA.vue.audio.currentTime)));
            if (Math.abs(GLOBAL_DATA.vue.audio.currentTime - Number(args[2]) / 1000) > 0.5)
                GLOBAL_DATA.vue.audio.currentTime = Number(args[2]) / 1000;

            GLOBAL_DATA.vue.durationSliderWidth = Math.round((GLOBAL_DATA.vue.audio.currentTime / GLOBAL_DATA.vue.audio.duration) * 10000) / 100 + '%';
            break;
        }
        case "songEnded": {
            switch (args[2]) {
                case "nextSongReady": {
                    eventBus.invoke("songEnded");
                    break;
                }
                case "nextSongEmpty": {
                    eventBus.invoke("queueEmpty");
                    break;
                }
            }
        }
    }
})

eventBus.subscribe("onPlay", (vue) => {
    GLOBAL_DATA.vue.isPlaying = true;
    GLOBAL_DATA.vue.audio.play();
})

eventBus.subscribe("onStop", (vue) => {
    GLOBAL_DATA.vue.isPlaying = false;
    GLOBAL_DATA.vue.audio.pause();
})

eventBus.subscribe("getSongFromServer", (vue) => {
    console.log("Получение песни с сервера..."+" src="+"/songs/getCurrentSong?trash="+new Date().getMinutes()+new Date().getSeconds())
    GLOBAL_DATA.vue.audio.src = "/songs/getCurrentSong?trash="+new Date().getMinutes()+new Date().getSeconds();

    if (GLOBAL_DATA.vue.isPlaying)
        GLOBAL_DATA.vue.audio.play();
})

eventBus.subscribe("connectedToLobby", (vue) => {
    console.log("Подключение к лобби завершено!");
    eventBus.invoke("getSongFromServer");
})

eventBus.subscribe("songEnded", (vue) => {
    console.log("Песня закончилась...");
    eventBus.invoke("getSongFromServer");
})

eventBus.subscribe("queueEmpty", (vue) => {
    console.log("Очередь пуста")
})