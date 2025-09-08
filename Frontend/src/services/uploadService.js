import axios from "axios";

// data from cloudinary dashboard
const CLOUD_NAME = "dl3vyqptj";
const UPLOAD_PRESET = "images";

const url = `https://api.cloudinary.com/v1_1/${CLOUD_NAME}/image/upload`;

export const uploadImages = async (files) => {
  try {
    const fileArray = Array.from(files);

    const uploadRequests = fileArray.map((file) => {
      const formData = new FormData();
      formData.append("file", file);
      formData.append("upload_preset", UPLOAD_PRESET);

      return axios.post(url, formData); // return promises
    });

    const results = await Promise.all(uploadRequests);
    return results.map((res) => res.data.secure_url);
  } catch (error) {
    console.error(
      "Error uploading multiple images:",
      error.response?.data || error.message
    );
    throw error;
  }
};
