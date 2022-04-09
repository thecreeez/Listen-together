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