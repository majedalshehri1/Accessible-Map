import axiosClient from "@/apis/axiosClient";

export default {
  getIsExisting(userId) {
    console.log(userId);
    return axiosClient.get(`/survey/exists/${userId}`);
  },

  postSurveyResponses(surveyData) {
    return axiosClient.post("/survey/create", surveyData);
  },
};
