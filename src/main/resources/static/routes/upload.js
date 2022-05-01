const UPLOAD_COMPONENT = {
    template:
        `
        <div class="main--upload">
            <h1 class="upload--header">Загрузка песни</h1>
            <form class="upload--form" action="/post/addSong" method="post" enctype="multipart/form-data">
                <input type="text" name="author" placeholder="Автор" class="upload--input">
                <input type="text" name="album" placeholder="Название альбома" class="upload--input">
                <input type="text" name="name" placeholder="Название песни" class="upload--input">
                <input type="file" name="file" class="upload--input">
                <button type="submit" class="upload--button clickable">Отправить</button>
            </form>
        </div>
        `,

    data() {
        return {
        }
    },

    created() {
        this.fetchData();
    },

    methods: {
        async fetchData() {
            console.log("fetching data...");
        },
    }
}