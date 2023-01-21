
var apiUrl = (function () {
  return {
    getApiUrl: function () {
      return "http://127.0.0.1:8080/wms"
    },
    getDevApiUrl: function () {
      return "http://127.0.0.1:8081/wms"
    }
  }
})(apiUrl || {})

var apiUrlDev = (function () {
  return {
    getApiUrl: function () {
      return "http://127.0.0.1:8081/wms"
    }
  }
})(apiUrlDev || {})

