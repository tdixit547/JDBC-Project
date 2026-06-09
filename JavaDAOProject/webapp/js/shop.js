// ── State ─────────────────────────────────────────────────────────
const State = {
    currentUser: null,
    currentWallet: null,
    products: [],
    cartItems: [],
    cartProducts: {},   // productId -> product details cache
    quantities: {},     // productId -> selected qty on store page
};

// ── Product emoji mapping ──────────────────────────────────────────
const PRODUCT_BGRADS = [
    'linear-gradient(135deg,#1a1040,#2d1b69)',
    'linear-gradient(135deg,#0f2a1a,#1a4a2e)',
    'linear-gradient(135deg,#1a100a,#3d2010)',
    'linear-gradient(135deg,#0a1628,#104060)',
    'linear-gradient(135deg,#1a0a2a,#3d1060)',
];

function getImage(product, size = 400) {
    const url = product.imageUrl && product.imageUrl.trim() !== '' ? product.imageUrl : '/img/placeholder.png';
    return `<img src="${url}" alt="${product.name}" style="width:100%;height:100%;object-fit:cover;">`;
}

function getBg(product) {
    return PRODUCT_BGRADS[product.productId % PRODUCT_BGRADS.length];
}

// ── Currency Format ────────────────────────────────────────────────
function fmt(amount) {
    return '₹' + Number(amount).toLocaleString('en-IN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}

// ── Toast ──────────────────────────────────────────────────────────
function toast(message, type = 'success') {
    const container = document.getElementById('toast-container');
    const el = document.createElement('div');
    const icons = { success: '✓', error: '✕', info: 'ℹ' };
    el.className = `toast toast-${type}`;
    el.innerHTML = `<span>${icons[type] || '✓'}</span><span>${message}</span>`;
    container.appendChild(el);
    setTimeout(() => {
        el.style.animation = 'slideDown 0.3s ease forwards';
        setTimeout(() => el.remove(), 300);
    }, 3200);
}

// ── Navigation ─────────────────────────────────────────────────────
function showPage(pageId) {
    document.querySelectorAll('.page').forEach(p => p.classList.add('hidden'));
    document.getElementById(pageId).classList.remove('hidden');

    document.querySelectorAll('.nav-link').forEach(a => {
        a.classList.toggle('active', a.dataset.page === pageId.replace('-page', ''));
    });
}

document.querySelectorAll('.nav-link[data-page]').forEach(link => {
    link.addEventListener('click', e => {
        e.preventDefault();
        const page = link.dataset.page;
        if (page === 'store') {
            showPage('store-page');
        } else if (page === 'orders') {
            showPage('orders-page');
            loadOrders();
        }
    });
});

// ── Navbar User Display ────────────────────────────────────────────
function updateNavUser() {
    const { currentUser, currentWallet } = State;
    if (!currentUser) return;
    const pill = document.getElementById('user-pill');
    pill.classList.remove('hidden');
    document.getElementById('user-avatar-char').textContent = currentUser.name.charAt(0).toUpperCase();
    document.getElementById('nav-user-name').textContent = currentUser.name;
    document.getElementById('nav-wallet-balance').textContent = fmt(currentWallet?.balance || 0);
}

async function refreshWallet() {
    try {
        State.currentWallet = await API.getWallet(State.currentUser.userId);
        updateNavUser();
        document.getElementById('cart-wallet').textContent = fmt(State.currentWallet.balance);
    } catch {}
}

// ── Products ───────────────────────────────────────────────────────
async function loadProducts() {
    const grid = document.getElementById('products-grid');
    grid.innerHTML = `<div class="skeleton-grid">
        ${[1,2,3,4].map(() => '<div class="skeleton-card"></div>').join('')}
    </div>`;

    try {
        State.products = await API.getProducts();
        document.getElementById('product-count-label').textContent =
            `${State.products.length} product${State.products.length !== 1 ? 's' : ''}`;

        if (State.products.length === 0) {
            grid.innerHTML = `<div style="grid-column:1/-1;text-align:center;padding:60px;color:var(--text-3)">No products available yet.</div>`;
            return;
        }

        // Initialize quantities
        State.products.forEach(p => { State.quantities[p.productId] = 1; });

        grid.innerHTML = State.products.map(p => renderProductCard(p)).join('');
        attachProductCardEvents();

        // Update floating cards
        if (State.products.length >= 3) {
            const fc1 = document.querySelector('.fc1');
            const fc2 = document.querySelector('.fc2');
            const fc3 = document.querySelector('.fc3');
            if (fc1) { fc1.querySelector('img').src = State.products[0].imageUrl || '/img/placeholder.png'; fc1.querySelector('span').textContent = State.products[0].name; }
            if (fc2) { fc2.querySelector('img').src = State.products[1].imageUrl || '/img/placeholder.png'; fc2.querySelector('span').textContent = State.products[1].name; }
            if (fc3) { fc3.querySelector('img').src = State.products[2].imageUrl || '/img/placeholder.png'; fc3.querySelector('span').textContent = State.products[2].name; }
        }
    } catch (err) {
        grid.innerHTML = `<div style="grid-column:1/-1;text-align:center;padding:60px;color:var(--text-3)">Failed to load products: ${err.message}</div>`;
    }
}

function renderProductCard(p) {
    const outOfStock = p.count === 0;
    const stockClass = p.count === 0 ? 'stock-out' : p.count < 5 ? 'stock-low' : 'stock-good';
    const stockText = p.count === 0 ? 'Out of stock' : p.count < 5 ? `${p.count} left` : `${p.count} in stock`;
    const imageHtml = getImage(p);
    const bg = getBg(p);

    return `
    <div class="product-card ${outOfStock ? 'out-of-stock' : ''}" data-product-id="${p.productId}">
        <div class="product-card-img" style="background: ${bg}">
            ${imageHtml}
        </div>
        <div class="product-card-body">
            <div class="product-card-name">${p.name}</div>
            <div class="product-card-price">${fmt(p.price)}</div>
        </div>
        <div class="product-card-footer">
            <div class="stock-info ${stockClass}">${stockText}</div>
            <div class="add-to-cart-row">
                ${outOfStock ? '' : `
                <div class="qty-control">
                    <button class="qty-btn btn-qty-dec" data-pid="${p.productId}">−</button>
                    <span class="qty-value" id="qty-${p.productId}">1</span>
                    <button class="qty-btn btn-qty-inc" data-pid="${p.productId}">+</button>
                </div>
                <button class="btn btn-primary btn-sm btn-add-cart" data-pid="${p.productId}" data-max="${p.count}">
                    Add
                </button>
                `}
            </div>
        </div>
    </div>`;
}

function attachProductCardEvents() {
    // Quantity controls
    document.querySelectorAll('.btn-qty-dec').forEach(btn => {
        btn.addEventListener('click', () => {
            const pid = parseInt(btn.dataset.pid);
            if (State.quantities[pid] > 1) {
                State.quantities[pid]--;
                document.getElementById(`qty-${pid}`).textContent = State.quantities[pid];
            }
        });
    });

    document.querySelectorAll('.btn-qty-inc').forEach(btn => {
        btn.addEventListener('click', () => {
            const pid = parseInt(btn.dataset.pid);
            const max = parseInt(btn.closest('.product-card-footer').querySelector('.btn-add-cart').dataset.max);
            if (State.quantities[pid] < max) {
                State.quantities[pid]++;
                document.getElementById(`qty-${pid}`).textContent = State.quantities[pid];
            } else {
                toast('Not enough stock available', 'error');
            }
        });
    });

    // Add to cart
    document.querySelectorAll('.btn-add-cart').forEach(btn => {
        btn.addEventListener('click', async () => {
            const pid = parseInt(btn.dataset.pid);
            const qty = State.quantities[pid] || 1;

            btn.disabled = true;
            btn.textContent = '...';

            try {
                await API.addToCart(State.currentUser.userId, pid, qty);
                toast(`Added to cart!`, 'success');
                State.quantities[pid] = 1;
                document.getElementById(`qty-${pid}`).textContent = '1';
                await loadCartState();
                await loadProducts(); // refresh stock
            } catch (err) {
                toast(err.message, 'error');
            } finally {
                btn.disabled = false;
                btn.textContent = 'Add';
            }
        });
    });
}

// ── Cart State ─────────────────────────────────────────────────────
async function loadCartState() {
    if (!State.currentUser) return;
    try {
        State.cartItems = await API.getCart(State.currentUser.userId);

        // Fetch product details for cart items
        const productFetches = State.cartItems.map(item =>
            State.cartProducts[item.productId]
                ? Promise.resolve(State.cartProducts[item.productId])
                : API.getProduct(item.productId).then(p => { State.cartProducts[item.productId] = p; return p; })
        );
        await Promise.all(productFetches.map(f => f.catch(() => null)));

        updateCartCount();
        updateCartSidebar();
    } catch {}
}

function updateCartCount() {
    const count = State.cartItems.reduce((s, i) => s + i.quantity, 0);
    const badge = document.getElementById('cart-count');
    if (count > 0) {
        badge.textContent = count > 99 ? '99+' : count;
        badge.classList.remove('hidden');
    } else {
        badge.classList.add('hidden');
    }
}

function updateCartSidebar() {
    const container = document.getElementById('cart-items-container');
    const footer = document.getElementById('cart-footer');

    if (State.cartItems.length === 0) {
        container.innerHTML = `
            <div class="cart-empty-state">
                <p>Your cart is empty</p>
                <span>Add some products to get started</span>
            </div>`;
        footer.classList.add('hidden');
        return;
    }

    let total = 0;
    container.innerHTML = State.cartItems.map(item => {
        const prod = State.cartProducts[item.productId];
        const name = prod ? prod.name : `Product #${item.productId}`;
        const price = prod ? prod.price : 0;
        const subtotal = price * item.quantity;
        total += subtotal;
        const imageHtml = prod ? getImage(prod, 100) : '';
        return `
        <div class="cart-item" data-cart-id="${item.cartId}">
            <div class="cart-item-icon" style="border-radius:6px;overflow:hidden">${imageHtml}</div>
            <div class="cart-item-details">
                <div class="cart-item-name">${name}</div>
                <div class="cart-item-price">${fmt(price)} × ${item.quantity}</div>
            </div>
            <div class="cart-item-right">
                <div class="cart-item-total">${fmt(subtotal)}</div>
                <button class="cart-item-remove btn-remove-item" data-cart-id="${item.cartId}">Remove</button>
            </div>
        </div>`;
    }).join('');

    document.getElementById('cart-subtotal').textContent = fmt(total);
    document.getElementById('cart-wallet').textContent = fmt(State.currentWallet?.balance || 0);
    footer.classList.remove('hidden');

    // Remove buttons
    document.querySelectorAll('.btn-remove-item').forEach(btn => {
        btn.addEventListener('click', async () => {
            const cartId = parseInt(btn.dataset.cartId);
            try {
                await API.removeFromCart(State.currentUser.userId, cartId);
                toast('Item removed', 'info');
                await loadCartState();
                await refreshWallet();
                await loadProducts();
            } catch (err) {
                toast(err.message, 'error');
            }
        });
    });
}

// ── Cart Sidebar Toggle ────────────────────────────────────────────
document.getElementById('btn-cart').addEventListener('click', openCart);
document.getElementById('btn-close-cart').addEventListener('click', closeCart);
document.getElementById('cart-overlay').addEventListener('click', closeCart);

function openCart() {
    if (!State.currentUser) { toast('Please log in first', 'error'); return; }
    document.getElementById('cart-sidebar').classList.add('open');
    document.getElementById('cart-overlay').classList.remove('hidden');
    setTimeout(() => document.getElementById('cart-overlay').classList.add('visible'), 10);
}

function closeCart() {
    document.getElementById('cart-sidebar').classList.remove('open');
    document.getElementById('cart-overlay').classList.remove('visible');
    setTimeout(() => document.getElementById('cart-overlay').classList.add('hidden'), 300);
}

// ── Checkout ───────────────────────────────────────────────────────
document.getElementById('btn-checkout').addEventListener('click', async () => {
    const btn = document.getElementById('btn-checkout');
    btn.disabled = true;
    btn.textContent = 'Processing...';

    try {
        const result = await API.checkout(State.currentUser.userId);
        closeCart();

        // Compute total from cart
        let total = State.cartItems.reduce((sum, item) => {
            const prod = State.cartProducts[item.productId];
            return sum + (prod ? prod.price * item.quantity : 0);
        }, 0);

        document.getElementById('success-bill-id').textContent = `Order #${result.billId} confirmed`;
        document.getElementById('success-amount').textContent = fmt(total);
        document.getElementById('success-modal').classList.remove('hidden');

        await loadCartState();
        await refreshWallet();
        await loadProducts();
    } catch (err) {
        toast(err.message, 'error');
    } finally {
        btn.disabled = false;
        btn.innerHTML = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 12V22H4V12"/><path d="M22 7H2v5h20V7z"/><path d="M12 22V7"/><path d="M12 7H7.5a2.5 2.5 0 0 1 0-5C11 2 12 7 12 7z"/><path d="M12 7h4.5a2.5 2.5 0 0 0 0-5C13 2 12 7 12 7z"/></svg> Place Order`;
    }
});

// Clear Cart
document.getElementById('btn-clear-cart').addEventListener('click', async () => {
    if (!confirm('Clear your entire cart? Stock will be restored.')) return;
    try {
        await API.clearCart(State.currentUser.userId);
        toast('Cart cleared', 'info');
        await loadCartState();
        await refreshWallet();
        await loadProducts();
    } catch (err) {
        toast(err.message, 'error');
    }
});

// ── Success Modal ──────────────────────────────────────────────────
document.getElementById('btn-success-close').addEventListener('click', () => {
    document.getElementById('success-modal').classList.add('hidden');
});

document.getElementById('btn-view-orders').addEventListener('click', () => {
    document.getElementById('success-modal').classList.add('hidden');
    closeCart();
    showPage('orders-page');
    loadOrders();
});

// ── Orders Page ────────────────────────────────────────────────────
async function loadOrders() {
    const container = document.getElementById('orders-list');
    container.innerHTML = `<div style="text-align:center;padding:40px;color:var(--text-3)">Loading orders...</div>`;

    try {
        const bills = await API.getBillsByUser(State.currentUser.userId);

        if (bills.length === 0) {
            container.innerHTML = `
                <div class="empty-orders">
                    <p>No orders yet</p>
                    <span>Your completed orders will appear here</span>
                </div>`;
            return;
        }

        // Sort newest first
        bills.sort((a, b) => b.billId - a.billId);

        container.innerHTML = bills.map(b => `
            <div class="order-card" data-bill-id="${b.billId}">
                <div class="order-card-header">
                    <div class="order-meta">
                        <div class="order-id">Order #${b.billId}</div>
                        <div class="order-date">${b.billDate ? b.billDate.substring(0, 10) : 'N/A'}</div>
                        <span class="order-badge badge-completed">✓ Completed</span>
                    </div>
                    <div class="order-right">
                        <div class="order-total">${fmt(b.totalAmount)}</div>
                        <div class="order-chevron">▾</div>
                    </div>
                </div>
                <div class="order-items" id="order-items-${b.billId}">
                    <div style="color:var(--text-3);font-size:0.85rem">Loading items...</div>
                </div>
            </div>
        `).join('');

        // Toggle expand on click
        document.querySelectorAll('.order-card-header').forEach(header => {
            header.addEventListener('click', () => {
                const card = header.closest('.order-card');
                const billId = card.dataset.billId;
                const isExpanded = card.classList.contains('expanded');

                if (!isExpanded) {
                    card.classList.add('expanded');
                    loadOrderItems(billId);
                } else {
                    card.classList.remove('expanded');
                }
            });
        });

    } catch (err) {
        container.innerHTML = `<div style="text-align:center;padding:40px;color:var(--text-3)">Failed to load orders: ${err.message}</div>`;
    }
}

async function loadOrderItems(billId) {
    const container = document.getElementById(`order-items-${billId}`);
    try {
        const data = await API.getBillDetails(billId);
        const items = data.items;

        if (!items || items.length === 0) {
            container.innerHTML = '<div style="color:var(--text-3);font-size:0.85rem">No items found.</div>';
            return;
        }

        container.innerHTML = items.map(item => {
            const lineTotal = item.priceAtPurchase * item.quantity;
            return `
            <div class="order-item-row">
                <span class="order-item-name">Product #${item.productId}</span>
                <span class="order-item-qty">× ${item.quantity}</span>
                <span style="color:var(--text-2);font-size:0.8rem">${fmt(item.priceAtPurchase)} each</span>
                <span class="order-item-price">${fmt(lineTotal)}</span>
            </div>`;
        }).join('');
    } catch (err) {
        container.innerHTML = `<div style="color:var(--red);font-size:0.85rem">Failed to load: ${err.message}</div>`;
    }
}

// ── Init ───────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
    const session = Auth.getSession();
    if (session && session.type === 'customer') {
        State.currentUser = { userId: session.userId, name: session.userName, email: session.userEmail };
        State.currentWallet = { balance: session.walletBalance };
        updateNavUser();
        loadProducts();
        loadCartState();
    }
});
