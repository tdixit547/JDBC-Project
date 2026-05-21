const API = {
    BASE: '',

    async request(method, path, body = null) {
        const options = {
            method,
            headers: { 'Content-Type': 'application/json' }
        };
        if (body) options.body = JSON.stringify(body);
        const response = await fetch(this.BASE + path, options);
        const data = await response.json();
        if (!response.ok || data.status === 'error') {
            throw new Error(data.message || 'Request failed');
        }
        return data;
    },

    // Products
    getProducts() { return this.request('GET', '/api/products'); },
    getProduct(id) { return this.request('GET', `/api/products/${id}`); },
    addProduct(name, price, stock) { return this.request('POST', '/api/products', { name, price, stock }); },
    updateStock(id, count) { return this.request('PUT', `/api/products/${id}/stock`, { count }); },
    deleteProduct(id) { return this.request('DELETE', `/api/products/${id}`); },

    // Users
    getUsers() { return this.request('GET', '/api/users'); },
    getUser(id) { return this.request('GET', `/api/users/${id}`); },
    registerUser(name, email) { return this.request('POST', '/api/users', { name, email }); },
    getWallet(userId) { return this.request('GET', `/api/users/${userId}/wallet`); },
    addBalance(userId, amount) { return this.request('PUT', `/api/users/${userId}/wallet`, { amount }); },

    // Cart
    getCart(userId) { return this.request('GET', `/api/cart/${userId}`); },
    addToCart(userId, productId, quantity) { return this.request('POST', `/api/cart/${userId}`, { productId, quantity }); },
    removeFromCart(userId, cartId) { return this.request('DELETE', `/api/cart/${userId}/${cartId}`); },
    clearCart(userId) { return this.request('DELETE', `/api/cart/${userId}`); },

    // Bills
    getBillsByUser(userId) { return this.request('GET', `/api/bills/user/${userId}`); },
    getBillDetails(billId) { return this.request('GET', `/api/bills/${billId}`); },
    checkout(userId) { return this.request('POST', `/api/checkout/${userId}`); }
};
