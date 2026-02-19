const Users = {
  load: async () => {
    const tbody = document.querySelector('#users-table tbody');
    if (!tbody) return;
    tbody.innerHTML = '';
    const r = await API.get('/api/users');
    if (!r.ok) return;
    const arr = await r.json();
    arr.forEach(u => {
      const tr = document.createElement('tr');
      const uid = u.userId ?? u.id ?? u.uid ?? null;
      tr.innerHTML = `
        <td>${uid ?? ''}</td>
        <td>${u.username}</td>
        <td>${u.role}</td>
        <td>
          <button class="btn btn-sm btn-outline-secondary me-1 btn-edit" data-id="${uid ?? ''}">Güncelle</button>
          <button class="btn btn-sm btn-outline-danger btn-del" data-id="${uid ?? ''}">Sil</button>
        </td>
      `;
      tbody.appendChild(tr);
    });
  },
  bind: () => {
    const btnCreate = document.getElementById('btn-create-user');
    const btnRefresh = document.getElementById('btn-refresh-users');
    if (btnCreate) btnCreate.addEventListener('click', Users.create);
    if (btnRefresh) btnRefresh.addEventListener('click', Users.load);
    const tbody = document.querySelector('#users-table tbody');
    if (tbody && !tbody.dataset.bound) {
      tbody.addEventListener('click', async (e) => {
        const editBtn = e.target.closest('.btn-edit');
        const delBtn = e.target.closest('.btn-del');
        if (editBtn) {
          const id = editBtn.getAttribute('data-id');
          if (!id || id === 'undefined') return;
          const row = editBtn.closest('tr');
          const currentUsername = row.children[1]?.textContent?.trim() || '';
          const currentRole = row.children[2]?.textContent?.trim() || 'ADMIN';
          const newUsername = prompt('Yeni kullanıcı adı', currentUsername);
          if (!newUsername) return;
          let newRole = prompt("Rol (ADMIN/DEPO/MUHASEBE)", currentRole);
          if (!newRole) return;
          newRole = newRole.toUpperCase().trim();
          if (!['ADMIN','DEPO','MUHASEBE'].includes(newRole)) {
            alert('Rol ADMIN, DEPO veya MUHASEBE olmalıdır.');
            return;
          }
          const newPassword = prompt('Yeni şifre (boş bırak = değiştirme)');
          const body = { username: newUsername.trim(), role: newRole, password: (newPassword || '').trim() };
          const resp = await API.put(`/api/users/${id}`, body);
          if (resp.ok) Users.load();
          return;
        }
        if (delBtn) {
          const id = delBtn.getAttribute('data-id');
          if (!id || id === 'undefined') return;
          if (!confirm('Kullanıcıyı silmek istiyor musunuz?')) return;
          const resp = await API.del(`/api/users/${id}`);
          if (resp.ok) Users.load();
          return;
        }
      });
      tbody.dataset.bound = '1';
    }
  },
  create: async () => {
    const username = document.getElementById('u-username').value.trim();
    const password = document.getElementById('u-password').value;
    const role = document.getElementById('u-role').value;
    const err = document.getElementById('user-error');
    err.textContent = '';
    if (!username || !password || !role) {
      err.textContent = 'Kullanıcı adı, şifre ve rol zorunludur';
      return;
    }
    const r = await API.post('/api/users', { username, password, role });
    if (r.ok) {
      document.getElementById('u-username').value = '';
      document.getElementById('u-password').value = '';
      document.getElementById('u-role').value = 'ADMIN';
      Users.load();
    } else {
      err.textContent = 'Kayıt sırasında hata';
    }
  }
};
