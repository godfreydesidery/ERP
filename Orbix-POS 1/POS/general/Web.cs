using System;
using System.Text;
using Newtonsoft.Json;
using System.Net;
using System.IO;
using System.Windows.Forms;

namespace POS.general
{
    class Web
    {

        public static Object login(string username, string password)
        {
            try
            {
                string baseUrl = "http://" + Env.SERVER_ADDRESS;
                UTF8Encoding encoding = new UTF8Encoding();
                HttpWebRequest request = (HttpWebRequest)WebRequest.Create(baseUrl + "/api/login?username="+username+"&password="+password);
                request.Method = "POST";
                request.KeepAlive = true;
                request.Referer = baseUrl;
                request.ContentType = "application/x-www-form-urlencoded";       
                Stream requestStream = request.GetRequestStream();
                requestStream.Close();
                HttpWebResponse postResponse;
                postResponse = (HttpWebResponse)request.GetResponse();
                StreamReader requestuestReader = new StreamReader(postResponse.GetResponseStream(), new UTF8Encoding());
                Object responseFromServer = requestuestReader.ReadToEnd();
                return responseFromServer;
            }
            catch (System.Net.WebException)
            {
                //MessageBox.Show(e.ToString(), "Error");
                return null;
            }
            catch (Exception)
            {
                //MessageBox.Show(e.ToString(), "Error");
                return null;
            }
        }

        public static object post(object data, string url)
        {
            try
            {         
                string baseUrl ="http://"+ Env.SERVER_ADDRESS;
                UTF8Encoding encoding = new UTF8Encoding();
                Byte[] byteData = encoding.GetBytes(JsonConvert.SerializeObject(data, Formatting.Indented));
                HttpWebRequest postReq = (HttpWebRequest)WebRequest.Create(baseUrl + "/api/" + url);
                postReq.Method = "POST";
                postReq.KeepAlive = true;
                postReq.ContentType = "application/json";
                postReq.Referer = baseUrl;
                postReq.Headers.Add("Authorization", "Bearer "+User.accessToken); //put auth token
                postReq.ContentLength = byteData.Length;
                Stream postReqStream = postReq.GetRequestStream();
                postReqStream.Write(byteData, 0, byteData.Length);
                postReqStream.Close();
                HttpWebResponse postResponse;
                postResponse = (HttpWebResponse)postReq.GetResponse();
                StreamReader postRequestReader = new StreamReader(postResponse.GetResponseStream(), new UTF8Encoding());
                object responseFromServer = postRequestReader.ReadToEnd();
                return responseFromServer;
            }catch(System.Net.WebException e)
            {
                MessageBox.Show(e.ToString(), "Error");
                return null;
            }
            catch (Exception e)
            {
                MessageBox.Show(e.ToString(), "Error");
                return null;
            }           
        }

        public static object patch(object data, string url)
        {
            try
            {
                string baseUrl = "http://" + Env.SERVER_ADDRESS;
                UTF8Encoding encoding = new UTF8Encoding();
                Byte[] byteData = encoding.GetBytes(JsonConvert.SerializeObject(data, Formatting.Indented));
                HttpWebRequest request = (HttpWebRequest)WebRequest.Create(baseUrl + "/api/" + url);
                request.Method = "PATCH";
                request.KeepAlive = true;
                request.ContentType = "application/json";
                request.Referer = baseUrl;
                request.Headers.Add("Authorization", User.accessToken); //put auth token
                request.ContentLength = byteData.Length;
                Stream requestStream = request.GetRequestStream();
                requestStream.Write(byteData, 0, byteData.Length);
                requestStream.Close();
                HttpWebResponse response;
                response = (HttpWebResponse)request.GetResponse();
                StreamReader postRequestReader = new StreamReader(response.GetResponseStream(), new UTF8Encoding());
                object responseFromServer = postRequestReader.ReadToEnd();
                return responseFromServer;
            }
            catch (System.Net.WebException e)
            {
                MessageBox.Show(e.Message, "Error");
                return null;
            }
            catch (Exception e)
            {
                MessageBox.Show(e.Message, "Error");
                return null;
            }
        }

        public static object delete(object data, string url)
        {
            try
            {
                string baseUrl = "http://" + Env.SERVER_ADDRESS;
                UTF8Encoding encoding = new UTF8Encoding();
                HttpWebRequest request = (HttpWebRequest)WebRequest.Create(baseUrl + "/api/" + url);
                request.Method = "DELETE";
                request.Referer = baseUrl;
                request.Headers.Add("Authorization", User.accessToken); //put auth token
                Stream reqStream = request.GetRequestStream();
                reqStream.Close();
                HttpWebResponse response;
                response = (HttpWebResponse)request.GetResponse();
                StreamReader requestReader = new StreamReader(response.GetResponseStream(), new UTF8Encoding());
                object responseFromServer = requestReader.ReadToEnd();
                return responseFromServer;
            }
            catch (System.Net.WebException e)
            {
                MessageBox.Show(e.Message, "Error");
                return null;
            }
            catch (Exception e)
            {
                MessageBox.Show(e.Message, "Error");
                return null;
            }
        }

        public static object get_(string url)
        {
            try
            {
                string baseUrl = "http://" + Env.SERVER_ADDRESS;
                //Create initial request
                HttpWebRequest request = (HttpWebRequest)WebRequest.Create(baseUrl + "/api/" + url);
                request.Proxy = null;
                request.UserAgent = "";
                request.Headers.Add("Authorization", User.accessToken); //put auth token

                //Create the response and reader
                HttpWebResponse response = (HttpWebResponse) request.GetResponse();
                System.IO.Stream responseStream = response.GetResponseStream();
                //Create a new stream reader
                System.IO.StreamReader streamReader = new System.IO.StreamReader(responseStream);
                object responseFromServer = streamReader.ReadToEnd();
                streamReader.Close();
                return responseFromServer;
            }
            catch(System.Net.WebException)
            {
                //MessageBox()
                return null;
            }
            catch(Exception e)
            {
                MessageBox.Show(e.Message, "Error");
                return null;

            }
        }
        public static Object put(object data, string url)
        {
            try
            {
                string baseUrl = "http://" + Env.SERVER_ADDRESS;
                UTF8Encoding encoding = new UTF8Encoding();
                Byte[] byteData = encoding.GetBytes(JsonConvert.SerializeObject(data, Formatting.Indented));
                HttpWebRequest postReq = (HttpWebRequest)WebRequest.Create(baseUrl + "/api/" + url);
                postReq.Method = "POST";
                postReq.KeepAlive = true;
                postReq.ContentType = "application/json";
                postReq.Referer = baseUrl;
                postReq.Headers.Add("Authorization", "Bearer " + User.accessToken); //put auth token
                postReq.ContentLength = byteData.Length;
                Stream postReqStream = postReq.GetRequestStream();
                postReqStream.Write(byteData, 0, byteData.Length);
                postReqStream.Close();
                HttpWebResponse postResponse;
                postResponse = (HttpWebResponse)postReq.GetResponse();
                StreamReader postRequestReader = new StreamReader(postResponse.GetResponseStream(), new UTF8Encoding());
                object responseFromServer = postRequestReader.ReadToEnd();
                return responseFromServer;
            }
            catch (System.Net.WebException e)
            {
                MessageBox.Show(e.ToString(), "Error");
                return null;
            }
            catch (Exception e)
            {
                MessageBox.Show(e.ToString(), "Error");
                return null;
            }
        }
    }   
}
