const Dashboard = {
  compute: (products) => {
    const totalProducts = products.length;
    let low = 0, out = 0;
    let totalValue = 0;
    products.forEach(p => {
      const qty = p.quantity || 0;
      const crit = p.criticalStockLevel || 0;
      const price = Number(p.price || 0);
      if (qty <= 0) out++;
      else if (qty <= crit) low++;
      totalValue += price * qty;
    });
    document.getElementById('db-total-products').textContent = String(totalProducts);
    document.getElementById('db-low-stock').textContent = String(low);
    document.getElementById('db-out-stock').textContent = String(out);
    document.getElementById('db-total-value').textContent = new Intl.NumberFormat('tr-TR', { style: 'currency', currency: 'TRY' }).format(totalValue);
  },
  load: async () => {
    const r = await API.get('/api/products');
    if (!r.ok) return;
    const arr = await r.json();
    Dashboard.compute(arr);
  },
  bind: () => {
    const btn = document.getElementById('btn-db-refresh');
    if (btn) btn.addEventListener('click', Dashboard.load);
  }
};

