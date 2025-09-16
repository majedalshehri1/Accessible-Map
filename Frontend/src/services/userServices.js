import axiosClient from "@/apis/axiosClient";

export default {
  createNewUser(userData) {
    return axiosClient.post("/auth/register", userData);
  },
  loginUser(userData) {
    return axiosClient.post("/auth/login", userData);
  },
};
