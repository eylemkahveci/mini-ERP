const Stock = {
  loadProducts: async () => {
    const sel = document.getElementById('sm-product');
    sel.innerHTML = '';
    const r = await API.get('/api/products');
    if (!r.ok) return;
    const data = await r.json();
    const getId = (p) => p.pId ?? p.id ?? p.productId ?? p.pid ?? null;
    data.forEach(p => {
      const id = getId(p);
      if (id == null) return;
      const opt = document.createElement('option');
      opt.value = id;
      opt.textContent = `${p.sku} - ${p.name}`;
      sel.appendChild(opt);
    });
    if (sel.options.length > 0) {
      sel.value = sel.options[0].value;
      Stock.loadMovements(sel.value);
    }
    sel.addEventListener('change', () => Stock.loadMovements(sel.value));
  },
  loadMovements: async (productId) => {
    const tbody = document.querySelector('#movements-table tbody');
    tbody.innerHTML = '';
    const r = await API.get(`/api/stock/product/${productId}`);
    if (!r.ok) return;
    const arr = await r.json();
    arr.forEach(m => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${m.movementDate || ''}</td>
        <td>${(m.product && (m.product.name || m.product.sku)) || (m.productName || '')}</td>
        <td>${m.movementType}</td>
        <td>${m.quantity}</td>
        <td>${m.description || ''}</td>
        <td>${m.createdBy || ''}</td>
      `;
      tbody.appendChild(tr);
    });
  },
  bindForm: () => {
    document.getElementById('btn-sm-submit').addEventListener('click', Stock.submit);
  },
  submit: async () => {
    const prodVal = document.getElementById('sm-product').value;
    const productId = prodVal ? parseInt(prodVal,10) : NaN;
    const type = document.getElementById('sm-type').value;
    const qtyStr = document.getElementById('sm-qty').value.trim();
    const desc = document.getElementById('sm-desc').value.trim();
    const err = document.getElementById('sm-error');
    err.textContent = '';
    const qty = parseInt(qtyStr,10);
    if (Number.isNaN(productId) || Number.isNaN(qty) || qty <= 0) {
      err.textContent = 'Ürün ve miktar zorunludur';
      return;
    }
    const body = { productId, quantity: qty, description: desc || null };
    const url = type === 'IN' ? '/api/stock/in' : '/api/stock/out';
    const r = await API.post(url, body);
    if (r.ok) {
      document.getElementById('sm-qty').value = '';
      document.getElementById('sm-desc').value = '';
      Stock.loadMovements(String(productId));
      Products.load();
    } else {
      const txt = await r.text();
      err.textContent = txt || 'İşlem başarısız';
    }
  }
};
