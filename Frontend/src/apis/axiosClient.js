import axios from "axios";

const axiosClient = axios.create({
  baseURL: "http://localhost:8081/api",
    headers: {
    "Accept": "application/json",
    "Content-Type": "application/json",
  },
});

export default axiosClient;