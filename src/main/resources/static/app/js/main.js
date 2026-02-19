const App = {
  current: 'login',
  showView: (name) => {
    document.querySelectorAll('.view').forEach(v => v.classList.add('d-none'));
    const el = document.getElementById(`view-${name}`);
    if (el) el.classList.remove('d-none');
    App.current = name;
    App.updateNav();
  },
  updateNav: () => {
    const user = API.username();
    const role = API.role();
    document.getElementById('nav-username').textContent = user ? `${user} (${role||''})` : '';
    document.querySelectorAll('[data-view]').forEach(a => {
      const allowed = a.getAttribute('data-roles');
      if (!allowed) return;
      const roles = allowed.split(',').map(s => s.trim());
      if (role && roles.includes(role)) {
        a.classList.remove('d-none');
      } else {
        a.classList.add('d-none');
      }
    });
    const createCard = document.getElementById('card-create-product');
    if (createCard) {
      if (role === 'ADMIN') createCard.classList.remove('d-none');
      else createCard.classList.add('d-none');
    }
  },
  bindNav: () => {
    document.querySelectorAll('[data-view]').forEach(a => {
      a.addEventListener('click', (e) => {
        e.preventDefault();
        const v = a.getAttribute('data-view');
        if (!API.token() && v !== 'login') return App.showView('login');
        App.showView(v);
        if (v === 'dashboard') { Dashboard.load(); }
        if (v === 'products') {
          const role = API.role();
          if (role === 'ADMIN') {
            Products.loadCategories().then(Products.load);
          } else {
            Products.load();
          }
        }
        if (v === 'stock') { Stock.loadProducts(); }
        if (v === 'categories') { Categories.load(); }
        if (v === 'users') { Users.load(); }
      });
    });
  },
  onLogin: () => {
    const role = API.role();
    if (role === 'ADMIN' || role === 'MUHASEBE') {
      App.showView('dashboard');
      Dashboard.load();
      return;
    }
    App.showView('products');
    if (role === 'ADMIN') {
      Products.loadCategories().then(Products.load);
    } else {
      Products.load();
    }
  },
  init: () => {
    Auth.bind();
    Products.bindCreate();
    Products.bindTableEvents();
    Stock.bindForm();
    Categories.bind();
    Users.bind();
    Reports.bind();
    App.bindNav();
    const t = API.token();
    if (t) {
      const role = API.role();
      if (role === 'ADMIN' || role === 'MUHASEBE') {
        App.showView('dashboard');
        Dashboard.load();
        return;
      }
      App.showView('products');
      if (role === 'ADMIN') {
        Products.loadCategories().then(Products.load);
      } else {
        Products.load();
      }
    } else {
      App.showView('login');
    }
  }
};
document.addEventListener('DOMContentLoaded', App.init);
