const Products = {
  categories: [],
  loadCategories: async () => {
    const role = API.role();
    if (role !== 'ADMIN') {
      const sel = document.getElementById('p-category');
      if (sel) sel.innerHTML = '';
      return;
    }
    const sel = document.getElementById('p-category');
    sel.innerHTML = '';
    const r = await API.get('/api/categories');
    if (!r.ok) return;
    const data = await r.json();
    Products.categories = data;
    data.forEach(c => {
      const opt = document.createElement('option');
      opt.value = c.cId || c.id || c.cid || c.categoryId || c.id;
      opt.textContent = c.name;
      sel.appendChild(opt);
    });
  },
  load: async () => {
    const tbody = document.querySelector('#products-table tbody');
    tbody.innerHTML = '';
    const r = await API.get('/api/products');
    if (!r.ok) return;
    const arr = await r.json();
    const getId = (p) => p.pId ?? p.id ?? p.productId ?? p.pid ?? null;
    arr.forEach(p => {
      const tr = document.createElement('tr');
      const critical = p.criticalStockLevel != null ? p.criticalStockLevel : 0;
      const qty = p.quantity != null ? p.quantity : 0;
      const status = qty <= 0 ? 'TÜKENDİ' : (qty <= critical ? 'KRİTİK' : 'OK');
      const badge = status === 'OK' ? 'badge-ok' : 'badge-critical';
      const role = API.role();
      const canEdit = role === 'ADMIN' || role === 'MUHASEBE';
      const canDelete = role === 'ADMIN';
      const pid = getId(p);
      if (pid == null) {
        try { console.error('Urun ID eksik:', p); } catch(e) {}
      }
      const catName = p.categoryName || (p.category && p.category.name) || '';
      tr.innerHTML = `
        <td>${p.sku}</td>
        <td>${p.name}</td>
        <td>${p.price != null ? p.price : ''}</td>
        <td><span class="badge ${badge}">${status}</span></td>
        <td>${catName}</td>
        <td>${qty}</td>
        <td>${p.criticalStockLevel != null ? p.criticalStockLevel : ''}</td>
        <td>
          ${canEdit && pid != null ? `<button type="button" class="btn btn-sm btn-outline-secondary me-1 btn-edit" data-id="${pid}">Düzenle</button>` : ''}
          ${canDelete && pid != null ? `<button type="button" class="btn btn-sm btn-outline-danger btn-del" data-id="${pid}">Sil</button>` : ''}
        </td>
      `;
      tbody.appendChild(tr);
    });
  },
  bindTableEvents: () => {
    const tbody = document.querySelector('#products-table tbody');
    if (!tbody) return;
    tbody.addEventListener('click', async (e) => {
      const editBtn = e.target.closest('.btn-edit');
      const delBtn = e.target.closest('.btn-del');
      if (editBtn) {
        const id = editBtn.getAttribute('data-id');
        if (id && id !== 'undefined') Products.openEditModal(id);
        return;
      }
      if (delBtn) {
        const id = delBtn.getAttribute('data-id');
        if (!id || id === 'undefined') return;
        if (!confirm('Ürünü silmek istiyor musunuz?')) return;
        const resp = await API.del(`/api/products/${id}`);
        if (resp.ok) Products.load();
        return;
      }
    });
  },
  openEditModal: async (id) => {
    const res = await API.get(`/api/products/${id}`);
    if (!res.ok) return;
    const prod = await res.json();
    const role = API.role();
    const epName = document.getElementById('ep-name');
    const epSku = document.getElementById('ep-sku');
    const epPrice = document.getElementById('ep-price');
    const epQty = document.getElementById('ep-qty');
    const epCritical = document.getElementById('ep-critical');
    const epCat = document.getElementById('ep-category');
    const epErr = document.getElementById('ep-error');
    epErr.textContent = '';
    epName.value = prod.name || '';
    epSku.value = prod.sku || '';
    epPrice.value = prod.price != null ? prod.price : '';
    epQty.value = prod.quantity != null ? prod.quantity : '';
    epCritical.value = prod.criticalStockLevel != null ? prod.criticalStockLevel : '';
    // Kategori listesini sadece ADMIN yükler
    epCat.innerHTML = '';
    if (role === 'ADMIN') {
      const r = await API.get('/api/categories');
      if (r.ok) {
        const cats = await r.json();
        cats.forEach(c => {
          const opt = document.createElement('option');
          opt.value = c.cId || c.id;
          opt.textContent = c.name;
          epCat.appendChild(opt);
        });
        // Mevcut kategori ID'si yoksa, ürünün bulunduğu kategoriyi kategorilerin ürün listelerinden bul
        let currentCid = prod.categoryId || null;
        if (!currentCid) {
          const pid = prod.pId || prod.id;
          for (const c of cats) {
            if (Array.isArray(c.products) && c.products.find(pr => (pr.pId || pr.id) === pid)) {
              currentCid = c.cId || c.id;
              break;
            }
          }
        }
        if (currentCid) epCat.value = String(currentCid);
      }
    }
    // Rol bazlı alan yetkileri
    const isAdmin = role === 'ADMIN';
    const isMuh = role === 'MUHASEBE';
    epName.disabled = !isAdmin;
    epSku.disabled = !isAdmin;
    epPrice.disabled = !(isAdmin || isMuh);
    epQty.disabled = !isAdmin;
    epCritical.disabled = !isAdmin;
    epCat.disabled = !isAdmin;
    // Kaydet handler
    const saveBtn = document.getElementById('btn-ep-save');
    const modalEl = document.getElementById('productEditModal');
    const bsModal = bootstrap.Modal.getOrCreateInstance(modalEl);
    // Eski clickleri temizlemek için yeni handler ataması
    saveBtn.onclick = async () => {
      // MUHASEBE sadece fiyatı değiştirebilir; diğer alanları mevcuttan gönder
      const body = {
        name: isAdmin ? epName.value.trim() : prod.name,
        sku: isAdmin ? epSku.value.trim() : prod.sku,
        price: epPrice.value ? parseFloat(epPrice.value.replace(',', '.')) : null,
        quantity: isAdmin ? parseInt(epQty.value || '0',10) : prod.quantity,
        categoryId: null,
      };
      if (isAdmin) {
        let selVal = epCat.value && epCat.value.trim() ? parseInt(epCat.value,10) : null;
        if (selVal == null || Number.isNaN(selVal)) {
          selVal = prod.categoryId || null;
        }
        body.categoryId = selVal;
      } else {
        body.categoryId = prod.categoryId || null;
      }
      if (isAdmin) {
        if (epCritical.value) body.criticalStockLevel = parseInt(epCritical.value,10);
        else body.criticalStockLevel = null;
      } else {
        body.criticalStockLevel = prod.criticalStockLevel;
      }
      // basit doğrulama
      if (!body.name || !body.sku || body.price == null || Number.isNaN(body.price) || body.quantity == null || Number.isNaN(body.quantity) || body.categoryId == null) {
        epErr.textContent = 'Zorunlu alanları kontrol edin';
        return;
      }
      const pid = prod.pId ?? prod.id ?? prod.productId ?? id;
      try { console.debug('PUT /api/products/', pid, body); } catch(e) {}
      const resp = await API.put(`/api/products/${pid}`, body);
      if (resp.ok) {
        bsModal.hide();
        Products.load();
      } else {
        epErr.textContent = 'Güncelleme sırasında hata';
      }
    };
    bsModal.show();
  },
  bindCreate: () => {
    document.getElementById('btn-create-product').addEventListener('click', Products.create);
    document.getElementById('btn-refresh-products').addEventListener('click', () => { Products.load(); });
  },
  create: async () => {
    const role = API.role();
    if (role !== 'ADMIN') {
      const err = document.getElementById('product-error');
      err.textContent = 'Bu işlem için yetkiniz yok';
      return;
    }
    const name = document.getElementById('p-name').value.trim();
    const sku = document.getElementById('p-sku').value.trim();
    const price = document.getElementById('p-price').value.trim();
    const qty = document.getElementById('p-qty').value.trim();
    const critical = document.getElementById('p-critical').value.trim();
    const cat = document.getElementById('p-category').value;
    const err = document.getElementById('product-error');
    err.textContent = '';
    if (!name || !sku || !price || !qty || !cat) {
      err.textContent = 'Zorunlu alanları doldurunuz';
      return;
    }
    const body = {
      name,
      sku,
      price: price,
      quantity: parseInt(qty,10),
      categoryId: parseInt(cat,10)
    };
    if (critical) body.criticalStockLevel = parseInt(critical,10);
    const r = await API.post('/api/products', body);
    if (r.ok) {
      document.getElementById('p-name').value = '';
      document.getElementById('p-sku').value = '';
      document.getElementById('p-price').value = '';
      document.getElementById('p-qty').value = '';
      document.getElementById('p-critical').value = '';
      Products.load();
    } else {
      err.textContent = 'Kayıt sırasında hata';
    }
  }
};
