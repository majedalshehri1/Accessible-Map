import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null
  }),
  actions: {
    register(userData) {
      let users = JSON.parse(localStorage.getItem('users')) || []

      const exists = users.find(u => u.email === userData.email)
      if (exists) throw new Error('المستخدم موجود مسبقًا')

      users.push(userData)
      localStorage.setItem('users', JSON.stringify(users))

      this.user = userData
      localStorage.setItem('user', JSON.stringify(userData))
    },

    login({ email, password }) {
      const users = JSON.parse(localStorage.getItem('users')) || []
      const found = users.find(u => u.email === email && u.password === password)

      if (!found) throw new Error('بيانات الدخول غير صحيحة')

      this.user = found
      localStorage.setItem('user', JSON.stringify(found))
    },

    logout() {
      this.user = null
      localStorage.removeItem('user')
    },

    restore() {
      const user = localStorage.getItem('user')
      this.user = user ? JSON.parse(user) : null
    }
  }
})
