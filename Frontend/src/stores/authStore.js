import { defineStore } from "pinia";
import api from "@/apis/axiosClient";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    user: null,    
    loading: false,
    error: null,
  }),

  actions: {
    
    async register({ username, email, password }) {
      this.loading = true; this.error = null;
      try {
        await api.post("/auth/register", { username, email, password });
        await this.refreshProfile();
        return this.user;
      } catch (err) {
        this.error =
          err.response?.status === 409 || err.response?.status === 400
            ? "المستخدم موجود مسبقًا أو بيانات غير صحيحة"
            : "حدث خطأ أثناء التسجيل";
        throw err;
      } finally {
        this.loading = false;
      }
    },

    async login({ email, password, username }) {
      this.loading = true; this.error = null;
      try {
        const body = username ? { username, email, password } : { email, password };
        await api.post("/auth/login", body);
        await this.refreshProfile();
        return this.user;
      } catch (err) {
        this.error = err.response?.status === 401
          ? "بيانات الدخول غير صحيحة"
          : "تعذّر تسجيل الدخول";
        throw err;
      } finally {
        this.loading = false;
      }
    },

    async refreshProfile() {
      try {
        const { data } = await api.get("/auth/me");
        this.user = { id: data.id, username: data.username, email: data.email };
      } catch {
        this.user = null;
      }
    },

    async logout() {
      try {
        await api.post("/auth/logout");
      } finally {
        this.user = null;
        this.error = null;
      }
    },

    restore() {
      return this.refreshProfile();
    },
  },
});
