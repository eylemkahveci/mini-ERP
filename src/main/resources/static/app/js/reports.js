const Reports = {
  bind: () => {
    document.getElementById('btn-report-excel').addEventListener('click', Reports.excel);
    document.getElementById('btn-report-pdf').addEventListener('click', Reports.pdf);
  },
  excel: async () => {
    const r = await fetch('/api/reports/low-stock/excel', { headers: API.headers() });
    if (!r.ok) return;
    const blob = await r.blob();
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url; a.download = 'low-stock-products.xlsx';
    document.body.appendChild(a); a.click(); a.remove();
    URL.revokeObjectURL(url);
  },
  pdf: async () => {
    const r = await fetch('/api/reports/low-stock/pdf', { headers: API.headers() });
    if (!r.ok) return;
    const blob = await r.blob();
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url; a.download = 'low-stock-products.pdf';
    document.body.appendChild(a); a.click(); a.remove();
    URL.revokeObjectURL(url);
  }
};

