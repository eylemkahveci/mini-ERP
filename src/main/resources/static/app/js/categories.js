const Categories = {
  load: async () => {
    const tbody = document.querySelector('#categories-table tbody');
    if (!tbody) return;
    tbody.innerHTML = '';
    const r = await API.get('/api/categories');
    if (!r.ok) return;
    const arr = await r.json();
    arr.forEach(c => {
      const tr = document.createElement('tr');
      const cid = c.cId ?? c.id ?? c.categoryId ?? c.cid ?? null;
      tr.innerHTML = `
        <td>${c.name}</td>
        <td>
          <button class="btn btn-sm btn-outline-secondary me-1 btn-edit" data-id="${cid ?? ''}">Güncelle</button>
          <button class="btn btn-sm btn-outline-danger btn-del" data-id="${cid ?? ''}">Sil</button>
        </td>
      `;
      tbody.appendChild(tr);
    });
  },
  bind: () => {
    const btnCreate = document.getElementById('btn-create-category');
    const btnRefresh = document.getElementById('btn-refresh-categories');
    if (btnCreate) btnCreate.addEventListener('click', Categories.create);
    if (btnRefresh) btnRefresh.addEventListener('click', Categories.load);
    const tbody = document.querySelector('#categories-table tbody');
    if (tbody && !tbody.dataset.bound) {
      tbody.addEventListener('click', async (e) => {
        const editBtn = e.target.closest('.btn-edit');
        const delBtn = e.target.closest('.btn-del');
        if (editBtn) {
          const id = editBtn.getAttribute('data-id');
          if (!id || id === 'undefined') {
            console.error('Kategori ID eksik (edit)');
            return;
          }
          const currentName = editBtn.closest('tr')?.children?.[0]?.textContent?.trim() || '';
          const next = prompt('Yeni kategori adı', currentName);
          if (!next) return;
          if (next.trim() === currentName) return;
          const resp = await API.put(`/api/categories/${id}`, { name: next.trim() });
          if (resp.ok) Categories.load();
          return;
        }
        if (delBtn) {
          const id = delBtn.getAttribute('data-id');
          if (!id || id === 'undefined') {
            console.error('Kategori ID eksik (delete)');
            return;
          }
          if (!confirm('Kategoriyi silmek istiyor musunuz?')) return;
          const resp = await API.del(`/api/categories/${id}`);
          if (resp.ok) Categories.load();
          return;
        }
      });
      tbody.dataset.bound = '1';
    }
  },
  create: async () => {
    const name = document.getElementById('cat-name').value.trim();
    const err = document.getElementById('category-error');
    err.textContent = '';
    if (!name) {
      err.textContent = 'Kategori adı zorunludur';
      return;
    }
    const r = await API.post('/api/categories', { name });
    if (r.ok) {
      document.getElementById('cat-name').value = '';
      Categories.load();
    } else {
      err.textContent = 'Kayıt sırasında hata';
    }
  }
};
