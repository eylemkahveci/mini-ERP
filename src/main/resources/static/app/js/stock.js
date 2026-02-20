const Stock = {
  // 1. Ürünleri sadece select kutusuna doldurur (Dinleyici eklemez!)
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

    // İlk ürün seçili gelsin ve hareketleri yüklensin
    if (sel.options.length > 0) {
      sel.value = sel.options[0].value;
      Stock.loadMovements(sel.value);
    }
  },

  // 2. Hareketleri tabloya basar
  loadMovements: async (productId) => {
    if (!productId) return; // ID yoksa boşuna istek atma
    
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

  // 3. Tüm olay dinleyicilerini BURADA, SADECE BİR KEZ bağlarız
  bindForm: () => {
    // Kaydet butonu dinleyicisi
    const submitBtn = document.getElementById('btn-sm-submit');
    if (submitBtn) {
      submitBtn.addEventListener('click', Stock.submit);
    }

    // Ürün değiştirme (Change) dinleyicisi - BURAYA TAŞINDI
    const productSel = document.getElementById('sm-product');
    if (productSel) {
      productSel.addEventListener('change', (e) => {
        Stock.loadMovements(e.target.value);
      });
    }
  },

  submit: async () => {
    const prodVal = document.getElementById('sm-product').value;
    const productId = prodVal ? parseInt(prodVal, 10) : NaN;
    const type = document.getElementById('sm-type').value;
    const qtyStr = document.getElementById('sm-qty').value.trim();
    const desc = document.getElementById('sm-desc').value.trim();
    const err = document.getElementById('sm-error');
    
    err.textContent = '';
    const qty = parseInt(qtyStr, 10);
    
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
      // Başarılı işlemden sonra tabloyu yenile
      Stock.loadMovements(String(productId));
      // Ana ürün listesindeki stok miktarını da güncelle
      if (typeof Products !== 'undefined' && Products.load) {
          Products.load();
      }
    } else {
      const txt = await r.text();
      err.textContent = txt || 'İşlem başarısız';
    }
  }
};