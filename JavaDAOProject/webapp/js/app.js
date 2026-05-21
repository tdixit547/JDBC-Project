// ── State ────────────────────────────────────────────────
let currentCartUserId = null;
let currentOrdersUserId = null;

// ── Utilities ────────────────────────────────────────────

function formatCurrency(amount) {
    return 'Rs. ' + Number(amount).toLocaleString('en-IN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}

function showToast(message, type = 'success') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;
    container.appendChild(toast);
    setTimeout(() => {
        toast.style.animation = 'slideOut 0.3s ease forwards';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

function showModal(html) {
    document.getElementById('modal').innerHTML = html;
    document.getElementById('modal-overlay').classList.remove('hidden');
}

function hideModal() {
    document.getElementById('modal-overlay').classList.add('hidden');
}

function setActiveNav(page) {
    document.querySelectorAll('.nav-links a').forEach(a => {
        a.classList.toggle('active', a.dataset.page === page);
    });
}

function stockBadge(count) {
    if (count === 0) return '<span class="badge badge-danger">Out of Stock</span>';
    if (count < 5) return `<span class="badge badge-warning">${count} left</span>`;
    if (count < 15) return `<span class="badge badge-info">${count} in stock</span>`;
    return `<span class="badge badge-success">${count} in stock</span>`;
}

// ── Router ───────────────────────────────────────────────

document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.nav-links a').forEach(link => {
        link.addEventListener('click', e => {
            e.preventDefault();
            const page = link.dataset.page;
            navigateTo(page);
        });
    });

    document.getElementById('modal-overlay').addEventListener('click', e => {
        if (e.target === e.currentTarget) hideModal();
    });

    navigateTo('dashboard');
});

function navigateTo(page) {
    setActiveNav(page);
    const content = document.getElementById('content');
    content.innerHTML = '<div class="loading">Loading...</div>';

    switch (page) {
        case 'dashboard': renderDashboard(); break;
        case 'products': renderProducts(); break;
        case 'users': renderUsers(); break;
        case 'cart': renderCart(); break;
        case 'orders': renderOrders(); break;
    }
}

// ── Dashboard ────────────────────────────────────────────

async function renderDashboard() {
    const content = document.getElementById('content');
    try {
        const [products, users] = await Promise.all([
            API.getProducts(),
            API.getUsers()
        ]);

        const totalValue = products.reduce((sum, p) => sum + p.price * p.count, 0);
        const lowStock = products.filter(p => p.count < 5).length;

        content.innerHTML = `
            <div class="page-header">
                <div><h1>Dashboard</h1><p>Overview of your store</p></div>
            </div>
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-label">Total Products</div>
                    <div class="stat-value accent">${products.length}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Registered Users</div>
                    <div class="stat-value accent">${users.length}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Low Stock Items</div>
                    <div class="stat-value ${lowStock > 0 ? 'warning' : 'success'}">${lowStock}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Catalog Value</div>
                    <div class="stat-value success">${formatCurrency(totalValue)}</div>
                </div>
            </div>

            <h2 style="margin-bottom:16px; font-size:1.1rem">Recent Products</h2>
            <div class="table-container">
                <table class="data-table">
                    <thead><tr><th>ID</th><th>Name</th><th>Price</th><th>Stock</th></tr></thead>
                    <tbody>
                        ${products.slice(0, 5).map(p => `
                            <tr>
                                <td>${p.productId}</td>
                                <td>${p.name}</td>
                                <td>${formatCurrency(p.price)}</td>
                                <td>${stockBadge(p.count)}</td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        `;
    } catch (err) {
        content.innerHTML = `<div class="empty-state">Failed to load dashboard: ${err.message}</div>`;
    }
}

// ── Products ─────────────────────────────────────────────

async function renderProducts() {
    const content = document.getElementById('content');
    try {
        const products = await API.getProducts();
        content.innerHTML = `
            <div class="page-header">
                <div><h1>Products</h1><p>Manage your product catalog</p></div>
                <button class="btn btn-primary" id="btn-add-product">+ Add Product</button>
            </div>
            ${products.length === 0 ? '<div class="empty-state">No products found. Add your first product.</div>' : `
            <div class="table-container">
                <table class="data-table">
                    <thead><tr><th>ID</th><th>Name</th><th>Price</th><th>Stock</th><th>Actions</th></tr></thead>
                    <tbody>
                        ${products.map(p => `
                            <tr>
                                <td>${p.productId}</td>
                                <td>${p.name}</td>
                                <td>${formatCurrency(p.price)}</td>
                                <td>${stockBadge(p.count)}</td>
                                <td class="actions">
                                    <button class="btn btn-secondary btn-sm btn-edit-stock" data-id="${p.productId}" data-count="${p.count}" data-name="${p.name}">Edit Stock</button>
                                    <button class="btn btn-danger btn-sm btn-delete-product" data-id="${p.productId}" data-name="${p.name}">Delete</button>
                                </td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>`}
        `;

        document.getElementById('btn-add-product').addEventListener('click', () => {
            showModal(`
                <h2>Add Product</h2>
                <div class="form-group"><label>Product Name</label><input id="inp-name" type="text" placeholder="e.g. Wireless Mouse"></div>
                <div class="form-group"><label>Price</label><input id="inp-price" type="number" step="0.01" placeholder="e.g. 1299.00"></div>
                <div class="form-group"><label>Stock Quantity</label><input id="inp-stock" type="number" placeholder="e.g. 50"></div>
                <div class="form-actions">
                    <button class="btn btn-secondary" onclick="hideModal()">Cancel</button>
                    <button class="btn btn-primary" id="btn-submit-product">Add Product</button>
                </div>
            `);
            document.getElementById('btn-submit-product').addEventListener('click', async () => {
                try {
                    const name = document.getElementById('inp-name').value;
                    const price = document.getElementById('inp-price').value;
                    const stock = document.getElementById('inp-stock').value;
                    await API.addProduct(name, price, stock);
                    hideModal();
                    showToast('Product added successfully');
                    renderProducts();
                } catch (err) { showToast(err.message, 'error'); }
            });
        });

        document.querySelectorAll('.btn-edit-stock').forEach(btn => {
            btn.addEventListener('click', () => {
                const id = btn.dataset.id;
                const name = btn.dataset.name;
                const currentCount = btn.dataset.count;
                showModal(`
                    <h2>Update Stock - ${name}</h2>
                    <div class="form-group"><label>Current Stock: ${currentCount}</label><input id="inp-new-stock" type="number" value="${currentCount}" placeholder="New stock count"></div>
                    <div class="form-actions">
                        <button class="btn btn-secondary" onclick="hideModal()">Cancel</button>
                        <button class="btn btn-primary" id="btn-submit-stock">Update</button>
                    </div>
                `);
                document.getElementById('btn-submit-stock').addEventListener('click', async () => {
                    try {
                        await API.updateStock(id, document.getElementById('inp-new-stock').value);
                        hideModal();
                        showToast('Stock updated');
                        renderProducts();
                    } catch (err) { showToast(err.message, 'error'); }
                });
            });
        });

        document.querySelectorAll('.btn-delete-product').forEach(btn => {
            btn.addEventListener('click', async () => {
                const name = btn.dataset.name;
                if (confirm(`Delete "${name}"? This cannot be undone.`)) {
                    try {
                        await API.deleteProduct(btn.dataset.id);
                        showToast('Product deleted');
                        renderProducts();
                    } catch (err) { showToast(err.message, 'error'); }
                }
            });
        });
    } catch (err) {
        content.innerHTML = `<div class="empty-state">Failed to load products: ${err.message}</div>`;
    }
}

// ── Users ────────────────────────────────────────────────

async function renderUsers() {
    const content = document.getElementById('content');
    try {
        const users = await API.getUsers();
        const wallets = await Promise.all(users.map(u =>
            API.getWallet(u.userId).catch(() => ({ balance: 0 }))
        ));

        content.innerHTML = `
            <div class="page-header">
                <div><h1>Users</h1><p>Manage registered users</p></div>
                <button class="btn btn-primary" id="btn-register-user">+ Register User</button>
            </div>
            ${users.length === 0 ? '<div class="empty-state">No users registered yet.</div>' : `
            <div class="table-container">
                <table class="data-table">
                    <thead><tr><th>ID</th><th>Name</th><th>Email</th><th>Wallet Balance</th><th>Actions</th></tr></thead>
                    <tbody>
                        ${users.map((u, i) => `
                            <tr>
                                <td>${u.userId}</td>
                                <td>${u.name}</td>
                                <td>${u.email}</td>
                                <td style="color: var(--success); font-weight: 600">${formatCurrency(wallets[i].balance)}</td>
                                <td class="actions">
                                    <button class="btn btn-secondary btn-sm btn-add-balance" data-id="${u.userId}" data-name="${u.name}" data-balance="${wallets[i].balance}">Add Balance</button>
                                </td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>`}
        `;

        document.getElementById('btn-register-user').addEventListener('click', () => {
            showModal(`
                <h2>Register User</h2>
                <div class="form-group"><label>Full Name</label><input id="inp-user-name" type="text" placeholder="e.g. John Doe"></div>
                <div class="form-group"><label>Email</label><input id="inp-user-email" type="email" placeholder="e.g. john@example.com"></div>
                <div class="form-actions">
                    <button class="btn btn-secondary" onclick="hideModal()">Cancel</button>
                    <button class="btn btn-primary" id="btn-submit-user">Register</button>
                </div>
            `);
            document.getElementById('btn-submit-user').addEventListener('click', async () => {
                try {
                    await API.registerUser(
                        document.getElementById('inp-user-name').value,
                        document.getElementById('inp-user-email').value
                    );
                    hideModal();
                    showToast('User registered successfully');
                    renderUsers();
                } catch (err) { showToast(err.message, 'error'); }
            });
        });

        document.querySelectorAll('.btn-add-balance').forEach(btn => {
            btn.addEventListener('click', () => {
                const id = btn.dataset.id;
                const name = btn.dataset.name;
                const balance = btn.dataset.balance;
                showModal(`
                    <h2>Add Balance - ${name}</h2>
                    <p class="text-muted" style="margin-bottom:16px">Current balance: ${formatCurrency(balance)}</p>
                    <div class="form-group"><label>Amount to Add</label><input id="inp-amount" type="number" step="0.01" placeholder="e.g. 1000"></div>
                    <div class="form-actions">
                        <button class="btn btn-secondary" onclick="hideModal()">Cancel</button>
                        <button class="btn btn-success" id="btn-submit-balance">Add Balance</button>
                    </div>
                `);
                document.getElementById('btn-submit-balance').addEventListener('click', async () => {
                    try {
                        await API.addBalance(id, document.getElementById('inp-amount').value);
                        hideModal();
                        showToast('Balance added successfully');
                        renderUsers();
                    } catch (err) { showToast(err.message, 'error'); }
                });
            });
        });
    } catch (err) {
        content.innerHTML = `<div class="empty-state">Failed to load users: ${err.message}</div>`;
    }
}

// ── Cart ─────────────────────────────────────────────────

async function renderCart() {
    const content = document.getElementById('content');
    content.innerHTML = `
        <div class="page-header">
            <div><h1>Cart</h1><p>View and manage user shopping carts</p></div>
        </div>
        <div class="inline-form">
            <div class="form-group">
                <label>User ID</label>
                <input id="inp-cart-user" type="number" placeholder="Enter user ID" value="${currentCartUserId || ''}">
            </div>
            <button class="btn btn-primary" id="btn-load-cart">Load Cart</button>
        </div>
        <div id="cart-content"></div>
    `;

    document.getElementById('btn-load-cart').addEventListener('click', () => {
        currentCartUserId = parseInt(document.getElementById('inp-cart-user').value);
        if (currentCartUserId > 0) loadCart(currentCartUserId);
        else showToast('Enter a valid User ID', 'error');
    });

    if (currentCartUserId) loadCart(currentCartUserId);
}

async function loadCart(userId) {
    const container = document.getElementById('cart-content');
    container.innerHTML = '<div class="loading">Loading cart...</div>';

    try {
        const [user, wallet, cartItems] = await Promise.all([
            API.getUser(userId),
            API.getWallet(userId),
            API.getCart(userId)
        ]);

        // Fetch product details for each cart item
        const products = await Promise.all(
            cartItems.map(item => API.getProduct(item.productId).catch(() => ({ name: 'Unknown', price: 0 })))
        );

        let totalAmount = 0;
        let totalItems = 0;
        cartItems.forEach((item, i) => {
            totalAmount += products[i].price * item.quantity;
            totalItems += item.quantity;
        });

        container.innerHTML = `
            <div class="user-info-bar">
                <div class="card">
                    <div class="stat-label">Customer</div>
                    <div style="font-weight:600;font-size:1.1rem;margin-top:4px">${user.name}</div>
                    <div class="text-muted" style="font-size:0.85rem">${user.email}</div>
                </div>
                <div class="card">
                    <div class="stat-label">Wallet Balance</div>
                    <div class="wallet-balance">${formatCurrency(wallet.balance)}</div>
                </div>
            </div>

            <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px">
                <h2 style="font-size:1.1rem">Cart Items</h2>
                <button class="btn btn-primary btn-sm" id="btn-add-to-cart">+ Add Item</button>
            </div>

            ${cartItems.length === 0 ? '<div class="empty-state">Cart is empty</div>' : `
            <div class="table-container">
                <table class="data-table">
                    <thead><tr><th>Cart ID</th><th>Product</th><th>Qty</th><th>Unit Price</th><th>Subtotal</th><th>Actions</th></tr></thead>
                    <tbody>
                        ${cartItems.map((item, i) => `
                            <tr>
                                <td>${item.cartId}</td>
                                <td>${products[i].name}</td>
                                <td>${item.quantity}</td>
                                <td>${formatCurrency(products[i].price)}</td>
                                <td style="font-weight:600">${formatCurrency(products[i].price * item.quantity)}</td>
                                <td>
                                    <button class="btn btn-danger btn-sm btn-remove-cart" data-cart-id="${item.cartId}">Remove</button>
                                </td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
            <div class="cart-summary">
                <div class="total">${totalItems} item(s) — Total: <span>${formatCurrency(totalAmount)}</span></div>
                <div class="cart-actions">
                    <button class="btn btn-danger btn-sm" id="btn-clear-cart">Clear Cart</button>
                    <button class="btn btn-success" id="btn-checkout">Checkout</button>
                </div>
            </div>`}
        `;

        // Event listeners
        document.getElementById('btn-add-to-cart').addEventListener('click', () => {
            showModal(`
                <h2>Add to Cart</h2>
                <div class="form-group"><label>Product ID</label><input id="inp-cart-product" type="number" placeholder="Product ID"></div>
                <div class="form-group"><label>Quantity</label><input id="inp-cart-qty" type="number" value="1" placeholder="Quantity"></div>
                <div class="form-actions">
                    <button class="btn btn-secondary" onclick="hideModal()">Cancel</button>
                    <button class="btn btn-primary" id="btn-submit-cart">Add to Cart</button>
                </div>
            `);
            document.getElementById('btn-submit-cart').addEventListener('click', async () => {
                try {
                    await API.addToCart(userId,
                        document.getElementById('inp-cart-product').value,
                        document.getElementById('inp-cart-qty').value
                    );
                    hideModal();
                    showToast('Item added to cart');
                    loadCart(userId);
                } catch (err) { showToast(err.message, 'error'); }
            });
        });

        document.querySelectorAll('.btn-remove-cart').forEach(btn => {
            btn.addEventListener('click', async () => {
                try {
                    await API.removeFromCart(userId, btn.dataset.cartId);
                    showToast('Item removed');
                    loadCart(userId);
                } catch (err) { showToast(err.message, 'error'); }
            });
        });

        const clearBtn = document.getElementById('btn-clear-cart');
        if (clearBtn) {
            clearBtn.addEventListener('click', async () => {
                if (confirm('Clear entire cart? Stock will be restored.')) {
                    try {
                        await API.clearCart(userId);
                        showToast('Cart cleared');
                        loadCart(userId);
                    } catch (err) { showToast(err.message, 'error'); }
                }
            });
        }

        const checkoutBtn = document.getElementById('btn-checkout');
        if (checkoutBtn) {
            checkoutBtn.addEventListener('click', async () => {
                try {
                    const result = await API.checkout(userId);
                    showToast(`Checkout successful! Bill #${result.billId}`);
                    loadCart(userId);
                } catch (err) { showToast(err.message, 'error'); }
            });
        }
    } catch (err) {
        container.innerHTML = `<div class="empty-state">Failed to load cart: ${err.message}</div>`;
    }
}

// ── Orders ───────────────────────────────────────────────

async function renderOrders() {
    const content = document.getElementById('content');
    content.innerHTML = `
        <div class="page-header">
            <div><h1>Orders</h1><p>View order history and invoices</p></div>
        </div>
        <div class="inline-form">
            <div class="form-group">
                <label>User ID</label>
                <input id="inp-orders-user" type="number" placeholder="Enter user ID" value="${currentOrdersUserId || ''}">
            </div>
            <button class="btn btn-primary" id="btn-load-orders">Load Orders</button>
        </div>
        <div id="orders-content"></div>
    `;

    document.getElementById('btn-load-orders').addEventListener('click', () => {
        currentOrdersUserId = parseInt(document.getElementById('inp-orders-user').value);
        if (currentOrdersUserId > 0) loadOrders(currentOrdersUserId);
        else showToast('Enter a valid User ID', 'error');
    });

    if (currentOrdersUserId) loadOrders(currentOrdersUserId);
}

async function loadOrders(userId) {
    const container = document.getElementById('orders-content');
    container.innerHTML = '<div class="loading">Loading orders...</div>';

    try {
        const bills = await API.getBillsByUser(userId);

        if (bills.length === 0) {
            container.innerHTML = '<div class="empty-state">No orders found for this user.</div>';
            return;
        }

        container.innerHTML = `
            <div class="table-container">
                <table class="data-table">
                    <thead><tr><th>Bill ID</th><th>Date</th><th>Total</th><th>Status</th><th>Actions</th></tr></thead>
                    <tbody>
                        ${bills.map(b => `
                            <tr>
                                <td>#${b.billId}</td>
                                <td>${b.billDate ? b.billDate.substring(0, 10) : 'N/A'}</td>
                                <td style="font-weight:600">${formatCurrency(b.totalAmount)}</td>
                                <td>${b.status === 'COMPLETED'
                                    ? '<span class="badge badge-success">COMPLETED</span>'
                                    : '<span class="badge badge-danger">' + b.status + '</span>'}</td>
                                <td>
                                    <button class="btn btn-secondary btn-sm btn-view-order" data-bill-id="${b.billId}">View Details</button>
                                </td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
            <div id="order-detail-section"></div>
        `;

        document.querySelectorAll('.btn-view-order').forEach(btn => {
            btn.addEventListener('click', () => viewOrderDetails(btn.dataset.billId));
        });
    } catch (err) {
        container.innerHTML = `<div class="empty-state">Failed to load orders: ${err.message}</div>`;
    }
}

async function viewOrderDetails(billId) {
    const section = document.getElementById('order-detail-section');
    section.innerHTML = '<div class="loading">Loading details...</div>';

    try {
        const data = await API.getBillDetails(billId);
        const bill = data.bill;
        const items = data.items;

        let grandTotal = 0;

        section.innerHTML = `
            <div class="order-detail">
                <h3>Invoice #${bill.billId}</h3>
                <p class="text-muted mb-4">Date: ${bill.billDate ? bill.billDate.substring(0, 10) : 'N/A'} | User ID: ${bill.userId}</p>
                <div class="table-container">
                    <table class="data-table">
                        <thead><tr><th>Product ID</th><th>Qty</th><th>Unit Price</th><th>Line Total</th></tr></thead>
                        <tbody>
                            ${items.map(item => {
                                const lineTotal = item.priceAtPurchase * item.quantity;
                                grandTotal += lineTotal;
                                return `
                                    <tr>
                                        <td>Product #${item.productId}</td>
                                        <td>${item.quantity}</td>
                                        <td>${formatCurrency(item.priceAtPurchase)}</td>
                                        <td style="font-weight:600">${formatCurrency(lineTotal)}</td>
                                    </tr>
                                `;
                            }).join('')}
                        </tbody>
                    </table>
                </div>
                <div class="order-total">Grand Total: <span>${formatCurrency(grandTotal)}</span></div>
            </div>
        `;
    } catch (err) {
        section.innerHTML = `<div class="empty-state">Failed to load details: ${err.message}</div>`;
    }
}
