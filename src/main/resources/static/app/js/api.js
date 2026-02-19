const API = {
  token: () => localStorage.getItem('token') || '',
  username: () => localStorage.getItem('username') || '',
  role: () => localStorage.getItem('role') || '',
  setAuth: (t,u,r) => { localStorage.setItem('token', t); localStorage.setItem('username', u); localStorage.setItem('role', r||''); },
  clearAuth: () => { localStorage.removeItem('token'); localStorage.removeItem('username'); localStorage.removeItem('role'); },
  headers: (extra) => {
    const h = {
      'Content-Type': 'application/json',
      'Cache-Control': 'no-cache',
      'Pragma': 'no-cache'
    };
    const t = API.token();
    if (t) h['Authorization'] = 'Bearer ' + t;
    if (extra) Object.assign(h, extra);
    return h;
  },
  request: async (method, url, body, opts) => {
    const res = await fetch(url, { method, headers: API.headers(opts && opts.headers), body: body ? JSON.stringify(body) : undefined });
    if (res.status === 401 || res.status === 403) {
      API.clearAuth();
      App.showView('login');
      throw new Error('Yetkisiz');
    }
    return res;
  },
  get: (url) => API.request('GET', url),
  post: (url, body) => API.request('POST', url, body),
  put: (url, body) => API.request('PUT', url, body),
  del: (url) => API.request('DELETE', url)
};
