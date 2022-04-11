const routes = [
    { path: "/", component: INDEX_COMPONENT },
    { path: "/auth/login", component: AUTH_LOGIN_COMPONENT },
    { path: "/auth/register", component: AUTH_REGISTER_COMPONENT }
]

const router = VueRouter.createRouter({
    history: VueRouter.createWebHashHistory(),
    routes
})

const app = Vue.createApp({
    data() {
        const usernameData = document.getElementById("username").innerText;

        let profileText = "Войти";

        if (usernameData != "null")
            profileText = usernameData;

        return {
            profileText
        };
    }
});

app.use(router);
app.mount('#app');

window.onload = () => {
}

/*
TODO:
    Фетчинг данных без thymeleaf через get-запросы https://www.youtube.com/watch?v=tWFrqiS5jqE&ab_channel=Infybuzz
    Подключение сокета и команды коннекта и дисконнекта к лобби (и последующие для контроля за лобби)
    Работающий плеер
 */