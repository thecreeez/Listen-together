const AUTH_LOGIN_COMPONENT = {
    template:
        `<h1 class="auth--center auth--header">Авторизация</h1>
        <form action="/post/login" method="post">
            <input type="text" name="username" placeholder="Имя" class="auth--input">
            <input type="password" name="password" placeholder="Пароль" class="auth--input">
            <button type="submit" class="auth--button">Войти</button>
            <router-link to="/auth/register" class="auth--center">Регистрация</router-link>
        </form>`
}

const AUTH_REGISTER_COMPONENT = {
    template:
        `<h1 class="auth--center auth--header">Регистрация</h1>
        <form action="/post/register" method="post">
            <input type="text" name="username" placeholder="Имя" class="auth--input">
            <input type="password" name="password" placeholder="Пароль" class="auth--input">
            <button type="submit" class="auth--button">Регистрация</button>
            <router-link to="/auth/login" class="auth--center">У меня есть аккаунт</router-link>
        </form>`
}