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
            // Если разница больше чем пол секунды то синхронизирует
            if (Math.abs(GLOBAL_DATA.vue.audio.currentTime - Number(args[2]) / 1000) > 0.5)
                GLOBAL_DATA.vue.audio.currentTime = Number(args[2]) / 1000;

            if (GLOBAL_DATA.vue.isAwaitingSync) {
                GLOBAL_DATA.vue.isAwaitingSync = false;
                GLOBAL_DATA.vue.audio.volume = GLOBAL_DATA.vue.volume;
            }

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
            break;
        }
        case "songUpdated": {
            eventBus.invoke("songEnded");
            GLOBAL_DATA.vue.currentSongId = args[2];
            break;
        }
        case "changeQueueState": {
            GLOBAL_DATA.vue.queueState = QUEUE_STATE[args[2]];
            break;
        }
        case "songListUpdated": {
            fetch("/get/lobbyData")
                .then((response) => {
                    return response.text()
                }).then((data) => {
                    const dataJSON = JSON.parse(data);

                    GLOBAL_DATA.songsInQueue.length = 0;

                    dataJSON.songsInQueue.forEach((songInQueue) => {
                        console.log(songInQueue);
                        GLOBAL_DATA.songsInQueue.push(songInQueue);
                    })
                })
        }
        default: {
            console.log(args);
        }
    }
})


/*
    ЛОГИКА ОСТАНОВКИ ПРОИГРЫВАНИЯ
*/

eventBus.subscribe("playstopInvoking", () => {
    socket.send("pcontrol//playstop");
})

eventBus.subscribe("onPlay", (vue) => {
    GLOBAL_DATA.vue.isPlaying = true;
    GLOBAL_DATA.vue.audio.play();
})

eventBus.subscribe("onStop", (vue) => {
    GLOBAL_DATA.vue.isPlaying = false;
    GLOBAL_DATA.vue.audio.pause();
})

eventBus.subscribe("setSongInvoking", (siq_id) => {
    socket.send("pcontrol//setCurrentSongInList//"+siq_id);
})

eventBus.subscribe("getSongFromServer", (vue) => {
    console.log("Получение песни с сервера..."+" src="+"/songs/getCurrentSong?trash="+new Date().getMinutes()+new Date().getSeconds())
    GLOBAL_DATA.vue.audio.src = "/songs/getCurrentSong?trash="+new Date().getMinutes()+new Date().getSeconds();

    GLOBAL_DATA.vue.isAwaitingSync = true;
    GLOBAL_DATA.vue.audio.volume = 0;

    if (GLOBAL_DATA.vue.isPlaying)
        GLOBAL_DATA.vue.audio.play();
})

eventBus.subscribe("connectedToLobby", (vue) => {
    if (GLOBAL_DATA.vue.audio.paused)
        eventBus.invoke("getSongFromServer");
})

eventBus.subscribe("songEnded", (vue) => {
    console.log("Песня закончилась...");
    eventBus.invoke("getSongFromServer");
})

eventBus.subscribe("queueEmpty", (vue) => {
    console.log("Очередь пуста")
})

eventBus.subscribe("setVolume", (volume) => {
    if (volume < 0 && volume > 1)
        return;

    if (volume == 0)
        //Сделать чтоб отправлялось типа я замутился идите нахуй
        socket.send("")

    if (volume > 0)
        //Сделать типа я не замутился если че долбаебы
        socket.send("")

    GLOBAL_DATA.vue.volume = volume;
    GLOBAL_DATA.vue.audio.volume = volume;
})

navigator.mediaSession.setActionHandler('play', () => console.log('play'));
navigator.mediaSession.setActionHandler('pause', () => console.log('pause'));
navigator.mediaSession.setActionHandler('seekbackward', () => console.log('seekbackward'));
navigator.mediaSession.setActionHandler('seekforward', () => console.log('seekforward'));
navigator.mediaSession.setActionHandler('previoustrack', () => console.log('previoustrack'));
navigator.mediaSession.setActionHandler('nexttrack', () => console.log('nexttrack'));