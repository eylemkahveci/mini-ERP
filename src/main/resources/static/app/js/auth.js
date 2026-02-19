const Auth = {
  bind: () => {
    document.getElementById('btn-login').addEventListener('click', Auth.login);
    document.getElementById('btn-logout').addEventListener('click', Auth.logout);
  },
  login: async () => {
    const u = document.getElementById('login-username').value.trim();
    const p = document.getElementById('login-password').value;
    const err = document.getElementById('login-error');
    err.textContent = '';
    try {
      const r = await API.post('/api/auth/login', { username: u, password: p });
      if (!r.ok) {
        err.textContent = 'Giriş başarısız';
        return;
      }
      const data = await r.json();
      API.setAuth(data.token, data.username, data.role);
      App.onLogin();
    } catch(e) {
      err.textContent = 'Giriş başarısız';
    }
  },
  logout: () => {
    API.clearAuth();
    App.showView('login');
  }
};

