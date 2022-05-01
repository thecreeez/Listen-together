const AUTH_LOGIN_COMPONENT = {
    template:
        `
        <div class="main--auth">
            <link rel="stylesheet" type="text/css" href="/css/auth.css">
            <h1 class="auth--center auth--header">Авторизация</h1>
            <form action="/post/login" method="post">
                <input type="text" name="username" placeholder="Имя" class="auth--input">
                <input type="password" name="password" placeholder="Пароль" class="auth--input">
                <button type="submit" class="auth--button clickable">Войти</button>
                <router-link to="/auth/register" class="auth--center clickable">Регистрация</router-link>
            </form>
        </div>`
}

const AUTH_REGISTER_COMPONENT = {
    template:
        `
        <div class="main--auth">
            <link rel="stylesheet" type="text/css" href="/css/auth.css">
            <h1 class="auth--center auth--header">Регистрация</h1>
            <form action="/post/register" method="post">
                <input type="text" name="username" placeholder="Имя" class="auth--input">
                <input type="password" name="password" placeholder="Пароль" class="auth--input">
                <button type="submit" class="auth--button clickable">Регистрация</button>
                <router-link to="/auth/login" class="auth--center clickable">У меня есть аккаунт</router-link>
            </form>
        </div>`
}