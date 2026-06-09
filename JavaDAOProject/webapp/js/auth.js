// ── ShopZone Auth Utilities ────────────────────────────────────────
const Auth = {
    SESSION_KEY: 'shopzone_session',

    setAdminSession() {
        localStorage.setItem(this.SESSION_KEY, JSON.stringify({ type: 'admin' }));
    },

    setCustomerSession(user, wallet) {
        localStorage.setItem(this.SESSION_KEY, JSON.stringify({
            type: 'customer',
            userId: user.userId,
            userName: user.name,
            userEmail: user.email,
            walletBalance: wallet.balance
        }));
    },

    getSession() {
        try {
            const raw = localStorage.getItem(this.SESSION_KEY);
            return raw ? JSON.parse(raw) : null;
        } catch { return null; }
    },

    clearSession() {
        localStorage.removeItem(this.SESSION_KEY);
    },

    // Call at top of admin.html — redirects to login if not admin
    requireAdmin() {
        const session = this.getSession();
        if (!session || session.type !== 'admin') {
            window.location.replace('/');
            return null;
        }
        return session;
    },

    // Call at top of shop.html — redirects to login if not customer
    requireCustomer() {
        const session = this.getSession();
        if (!session || session.type !== 'customer') {
            window.location.replace('/');
            return null;
        }
        return session;
    },

    logout() {
        this.clearSession();
        window.location.replace('/');
    }
};
