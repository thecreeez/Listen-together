eventBus.subscribe("playerControl", (args) => {
    switch (args[1]) {
        case "isPlaying": {
            if (args[2] == "true")
                eventBus.invoke("onPlay")
            if (args[2] == "false")
                eventBus.invoke("onStop")
        }
    }
})

eventBus.subscribe("onPlay", (vue) => {
    GLOBAL_DATA.vue.isPlaying = true;
})

eventBus.subscribe("onStop", (vue) => {
    GLOBAL_DATA.vue.isPlaying = false;
})