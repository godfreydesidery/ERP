
var apiUrl = (function () {
  return {
    getApiUrl: function () {
      return "http://127.0.0.1:8080/api"
    },
    getDevApiUrl: function () {
      return "http://127.0.0.1:8081/api"
    }
  }
})(apiUrl || {})

var apiUrlDev = (function () {
  return {
    getApiUrl: function () {
      return "http://127.0.0.1:8081/api"
    }
  }
})(apiUrlDev || {})

