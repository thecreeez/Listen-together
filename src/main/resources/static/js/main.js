const eventBus = {
    data: {},

    invoke(event, data) {
        if (!this.data[event])
            this.data[event] = [];

        this.data[event].forEach((func) => {
            func(data);
        })
    },

    subscribe(event, func) {
        if (!this.data[event])
            this.data[event] = [];

        this.data[event].push(func);
    }
}

const GLOBAL_DATA = {
    isSocketConnected: false
}
const port = 1000;
const socket = new WebSocket("ws://"+window.location.hostname+":"+1000+"/lobbyws");

/**
 * EVENTS
 */
socket.onopen = (ev) => eventBus.invoke("socketOpen", {event: ev});
socket.onmessage = (ev) => eventBus.invoke("socketMessage", {event: ev});
socket.onclose = (ev) => eventBus.invoke("socketClose", {event: ev});

/**
 * SOCKETS
 */

eventBus.subscribe("socketOpen", () => GLOBAL_DATA.isSocketConnected = true);

eventBus.subscribe("socketMessage", (data) => {
    const args = data.event.data.split("//");

    //console.log(data.event.data);

    switch (args[0]) {
        case "userState":
            return eventBus.invoke("userState", args);
        case "conn":
            return eventBus.invoke("connection", args);
        case "disc":
            return eventBus.invoke("disconnection", args);
        case "player":
            return eventBus.invoke("playerControl", args);
        case "redir":
            return eventBus.invoke("redirect", args);
        default:
            return console.log(`undefined command: ${args[0]}`);
    }
})

eventBus.subscribe("socketClose", () => {
    alert("Потеряно соединение с сервером...")
    location.reload();
})

eventBus.subscribe("userState", (args) => {
    if (isUserExist(args[3])) {
        getUserById(args[3]).state = args[2];
    } else {
        //eventBus.invoke("userConnect", args);
    }
})

eventBus.subscribe("connection", (args) => {
    if (isUserExist(args[3]))
        return;

    console.log("Пользователь "+args[1]+" подключился к лобби.")

    GLOBAL_DATA.users.push({
        name: args[1],
        id: args[3],
        state: args[2]
    })
})

eventBus.subscribe("disconnection", (args) => {
    if (!isUserExist(args[3]))
        return;

    console.log("Пользователь "+args[1]+" отключился от лобби.")

    GLOBAL_DATA.users.splice(getUserIndexById(args[3]), 1);
})

eventBus.subscribe("redirect", (args) => {
    const link = args[1].split(":")[1];
    console.log(link);

    GLOBAL_DATA.vue.$router.push(link);
})

function isUserExist(id) {
    let isUserExist = false;

    if (!GLOBAL_DATA.users)
        return;

    GLOBAL_DATA.users.forEach((user) => {
        if (user.id == id)
            isUserExist = true;
    })

    return isUserExist;
}

function getUserById(id) {
    let user = undefined;

    GLOBAL_DATA.users.forEach((userCandidate) => {
        if (userCandidate.id == id)
            user = userCandidate;
    })

    return user;
}

function getUserIndexById(id) {
    let indexOut = -1;

    GLOBAL_DATA.users.forEach((userCandidate, index) => {
        if (userCandidate.id == id)
            indexOut = index;
    })

    return indexOut;
}