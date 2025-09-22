import axios from "axios";

// const BASE = process.env.VITE_API_BASE_URL;
const BASE = import.meta.env.VITE_API_BASE_URL


const api = axios.create({
  baseURL: `${BASE}/api`,
  withCredentials: true,
  headers: { Accept: "application/json", "Content-Type": "application/json" },
});
export default api;