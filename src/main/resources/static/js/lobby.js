const code = document.getElementById("lobbyId").innerText;
const userList = document.getElementById("userList");
const socket = new WebSocket("ws://@localhost:8080/lobbyws");

/**
 * EVENTS
 */
socket.onopen = (ev) => eventBus.invoke("socketOpen", {event: ev});
socket.onmessage = (ev) => eventBus.invoke("socketMessage", {event: ev});
socket.onclose = (ev) => eventBus.invoke("socketClose", {event: ev});

/**
 * SOCKETS
 */
eventBus.subscribe("socketOpen", (ev) => socket.send(code));

eventBus.subscribe("socketMessage", (data) => {
    const args = data.event.data.split("//");

    const user = {
        name: args[1],
        state: args[2],
        uuid: args[3]
    }

    switch (args[0]) {
        case "conn":
            return eventBus.invoke("userConnect", user);
        case "disc":
            return eventBus.invoke("userDisconnect", user);
        default:
            return console.log(`undefined command: ${args[0]}`);
    }
})

eventBus.subscribe("socketClose", (data) => {
    console.log("Сессия закрыта: ",data.event);
})

eventBus.subscribe("userConnect", (data) => {
    const user = data;

    const UserState = {
        null: `<i class="fa-solid fa-person-walking-arrow-right main--users_list--item--buttons--item"></i>`,
        DISCONNECTED: `<i class="fa-solid fa-person-walking-arrow-right main--users_list--item--buttons--item"></i>`,
        MUTED: `<i class="fa-solid fa-volume-xmark main--users_list--item--buttons--item"></i>`,
        LISTENING: `<i class="fa-solid fa-volume-high main--users_list--item--buttons--item"></i>`
    }

    const userTemplate = `
        <li class="main--users_list--item" id="user//id=${user.uuid}">
            <div class="main--users_list--item--username">${user.name}</div>

            <div class="main--users_list--item--buttons">
                <div class="users_list--item--mutebutton"">
                    ${UserState[user.state]}
                </div>
                <div class="users_list--item--kickbutton"><i class="fa-solid fa-door-open"></i></div>
            </div>
        </li>`;

    if (document.getElementById(`user//id=${user.uuid}`))
        return;

    userList.innerHTML += userTemplate;
})

eventBus.subscribe("userConnect", (data) => {
    console.log(`Подключен пользователь: `, data);
})

eventBus.subscribe("userDisconnect", (data) => {
    const user = data;

    const userHTML = document.getElementById(`user//id=${user.uuid}`);

    if (!userHTML)
        return;

    userList.removeChild(userHTML);
})

eventBus.subscribe("userDisconnect", (data) => {
    console.log("Отключен пользователь: ", data);
})