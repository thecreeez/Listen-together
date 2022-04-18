const code = document.getElementById("lobbyId").innerText;
const userList = document.getElementById("userList");

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