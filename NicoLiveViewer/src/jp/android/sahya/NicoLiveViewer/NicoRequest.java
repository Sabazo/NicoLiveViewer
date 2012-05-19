package jp.android.sahya.NicoLiveViewer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.cookie.Cookie;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class NicoRequest {
	//���O�C��API
		private final String _nicoHost = "secure.nicovideo.jp";
		private final String _nicoPath = "/secure/login";
		//�F��API
		private final String _apiHost = "live.nicovideo.jp";
		private final String _getalertstatus = "/api/getalertstatus";
		//���[�U�[�F�؂��s��Ȃ�API
		private final String _getalertinfo = "/api/getalertinfo";
		//�ԑg���擾API
		private final String _getstreaminfo = "/api/getstreaminfo/";
		//�ԑg���
		private final String _getplayerstatus = "/api/getplayerstatus";
		//NG���X�g���擾[XML] (������̂�)
		private final String _configurengword = "/configurengword?mode=get&video=";
		//
		private SchemeRegistry schemeRegistry = new SchemeRegistry();
		private HttpParams httpParams = new BasicHttpParams();
		private NicoMessage nicoMessage = null;
		//Login -> getplayerstatus
		private CookieStore _cookieStore;
		private String _loginCookie = "";
		
		//Alert server
		private String _alertaddr = null;
		private int _alertport;
		private String _alertthread = null;

		private String _addr;
		private int _port;
		private String _thread;
		//Login
		private boolean _isLogin = false;
		//Login Alert
		private boolean _isLoginAlert = false;
		private NodeList _community_id = null;
		
		/**
		 * NicoRequest
		 * @param nicoMesssage NicoMessage�̃C���X�^���X
		 */
		public NicoRequest (NicoMessage nicoMesssage){
			this.nicoMessage = nicoMesssage;
			
			HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
			SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
		    socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
			
			schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			schemeRegistry.register(new Scheme("https", socketFactory, 443));
			
			HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);
		}
		/**
		 * �j�R�j�R����Ƀ��O�C������
		 */
		public boolean isLogin(){
			return this._isLogin;
		}
		/**
		 * �j�R�������A���[�g�Ƀ��O�C������
		 */
		public boolean isLoginAlert(){
			return this._isLoginAlert;
		}
		public CookieStore getCookieStore(){
			return this._cookieStore;
		}
		
		/**
		 * ���O�C���N�b�L�[��Ԃ��܂�
		 * @return ��juser_session=user_session_18180000_265723068462200000;domain=.nicovideo.jp;Path=/
		 */
		public String getLoginCookie(){
			if (isLogin() && _loginCookie.toString().equals("")){
				_loginCookie = getLoginCookie(this._cookieStore);
			}
			return _loginCookie;
		}
		/**
		 * ���O�C���N�b�L�[��ݒ肵�܂�
		 * ��juser_session=user_session_18180000_265723068462200000;domain=.nicovideo.jp;Path=/
		 */
		public void setLoginCookie(String loginCookie){		
			setCookieStore(loginCookie.split(";")[0].split("=")[1]);
			_loginCookie = getLoginCookie(this._cookieStore);
		}
		private void setCookieStore(String loginCookie){
			_cookieStore = new BasicCookieStore();
			_cookieStore.addCookie(getCookie(loginCookie));
		}
		private Cookie getCookie(String loginCookie){
			// Cookie���쐬
	        BasicClientCookie cookie = new BasicClientCookie("user_session", loginCookie);
	        cookie.setDomain(".nicovideo.jp");
	        cookie.setPath("/");
	        
	        return cookie;
		}
		
		/**
		 * �j�R�j�R����ւ̃��O�C������
		 * @param mail
		 * @param password
		 * @return 
		 */
		public String login (String mail, String password) {
			
			try{
				
				DefaultHttpClient client = new DefaultHttpClient(new ThreadSafeClientConnManager(httpParams, schemeRegistry), httpParams);
				
				/*
				Uri.Builder uriBuilder = new Uri.Builder();
				uriBuilder.path(_nicoPath);
				uriBuilder.appendQueryParameter("site","nicolive");
				*/
				HttpPost post = new HttpPost(_nicoPath + "?site=nicolive");//uriBuilder.build().toString());

				// POST �f�[�^�̐ݒ�
				List<BasicNameValuePair> postParams = new ArrayList<BasicNameValuePair>();
				postParams.add(new BasicNameValuePair("mail", mail));
				postParams.add(new BasicNameValuePair("password", password));
				post.setEntity(new UrlEncodedFormEntity(postParams, HTTP.UTF_8));

				client.execute(new HttpHost(_nicoHost, 443, "https"), post);
				_cookieStore = client.getCookieStore();
				
				/*HttpResponse response = 
				 * if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
					//
					if (! _cookieStore.getCookies().isEmpty()){
						return _cookieStore.getCookies().get(0).getValue();
					}
					return "";
				}*/
			}
	        catch (Exception e){
	        	_isLogin = false;
	        	return e.getMessage();//"���O�C���Ɏ��s���܂���";
	        }
			
			if (! _cookieStore.getCookies().isEmpty()) {
				_isLogin = true;
				return "���O�C�����������܂���";
			}else{
				_isLogin = false;
				return "���O�C���Ɏ��s���܂���";
			}
		}
		
		/**
		 * ���O�C���N�b�L�[��Ԃ��܂�
		 * @param cookieStore �j�R�j�R����T�C�g��Cookie
		 * @return ��juser_session=user_session_18180000_265723068462200000;domain=.nicovideo.jp;Path=/
		 */
		public String getLoginCookie(CookieStore cookieStore){
			if ( cookieStore != null ) {
				List<Cookie> cookies = cookieStore.getCookies();
				if (!cookies.isEmpty()) {
					for (int i = 0; i < cookies.size(); i++) {
						if(isNicoVideoUserSession(cookies.get(i))){
							Cookie cookie = cookies.get(i);
							return cookie.getName() + "=" + cookie.getValue() + ";domain=" + cookie.getDomain() + ";Path=/";
						}
					}
				}
			}
			return null;
		}
		private boolean isNicoVideoUserSession(Cookie cookie){
			if (cookie != null) {
				if (cookie.getDomain().equals(".nicovideo.jp") && cookie.getName().equals("user_session")){
					return true;
				}
			}
			return false;
		}
		
		/**
		 * �j�R���A���[�g�ւ̃��O�C������
		 * @param mail
		 * @param password
		 * @return 
		 */
		public String loginAlert(String mail, String password){
			String ticket = null;
			
			try{
				
				DefaultHttpClient client = new DefaultHttpClient(new ThreadSafeClientConnManager(httpParams, schemeRegistry), httpParams);
				
				//Uri.Builder uriBuilder = new Uri.Builder();
				//uriBuilder.path(_nicoPath);
				//uriBuilder.appendQueryParameter("site","nicolive_antenna");
				HttpPost post = new HttpPost(_nicoPath + "?site=nicolive_antenna");//uriBuilder.build().toString());

				// POST �f�[�^�̐ݒ�
				List<BasicNameValuePair> postParams = new ArrayList<BasicNameValuePair>();
				postParams.add(new BasicNameValuePair("mail", mail));
				postParams.add(new BasicNameValuePair("password", password));
				post.setEntity(new UrlEncodedFormEntity(postParams, HTTP.UTF_8));

				HttpResponse response = client.execute(new HttpHost(_nicoHost, 443, "https"), post);
				
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
					//
					if (response.getEntity().isStreaming()){
						ticket = nicoMessage.getNodeValue(getInputStream(response), "ticket");
					}
				}
				
				if(ticket != null){
					client = new DefaultHttpClient(new ThreadSafeClientConnManager(httpParams, schemeRegistry), httpParams);
					//uriBuilder = new Uri.Builder();
					//uriBuilder.path(_getalertstatus);
					post = new HttpPost(_getalertstatus);//uriBuilder.build().toString());
					postParams = new ArrayList<BasicNameValuePair>();
					postParams.add(new BasicNameValuePair("ticket", ticket));
					post.setEntity(new UrlEncodedFormEntity(postParams, HTTP.UTF_8));
					response = client.execute(new HttpHost(_apiHost, 80, "http"), post);
				}
				
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
					Document doc = nicoMessage.getDocument(getInputStream(response));
					this._alertaddr = nicoMessage.getNodeValue(doc, "addr");
					this.set_alertport(nicoMessage.getNodeValue(doc, "port"));
					this._alertthread = nicoMessage.getNodeValue(doc, "thread");
					this._isLoginAlert = true;
					return "�A���[�g���O�C�����܂���";
				}
				
			}catch (Exception e){
				this._isLoginAlert = false;
	        	return "errer " + e.getMessage();//"���O�C���Ɏ��s���܂���";
	        }
			
			this._isLoginAlert = false;
			return "�A���[�g���O�C���Ɏ��s���܂���";
		}
		
		private InputStream getInputStream(HttpResponse response) throws IllegalStateException, IOException{
			return response.getEntity().getContent();
		}
		
		private String getResponceContents(HttpResponse response){
			try {
				return EntityUtils.toString( response.getEntity(), "UTF-8" );
			} catch (ParseException e) {
				e.getMessage();
			} catch (IOException e) {
				e.getMessage();
			}
			
			return null;
		}

		/**
		 * @return �A���[�g�T�[�o�̃A�h���X
		 */
		public String getAlertAddress() {
			return _alertaddr;
		}
		/**
		 * @return �A���[�g�T�[�o�̃|�[�g�ԍ�
		 */
		public int getAlertPort() {
			return _alertport;
		}
		/**
		 * @return �A���[�g�T�[�o�̃X���b�h�ԍ�
		 */
		public String getAlertThread() {
			return _alertthread;
		}

		/**
		 * @param lv �����ԍ��܂��̓R�~���j�e�B�ԍ�
		 * @return PlayerStatus�̃f�[�^
		 */
		public void getPlayerStatus(String lv) {
			
			try {
				
				DefaultHttpClient client = new DefaultHttpClient(new ThreadSafeClientConnManager(httpParams, schemeRegistry), httpParams);
				client.setCookieStore(_cookieStore);
			
				HttpGet get = new HttpGet(_getplayerstatus + "?v=" + lv);

				HttpResponse response = client.execute(new HttpHost(_apiHost, 80, "http"), get);
			
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
					Document doc = nicoMessage.getDocument(getInputStream(response));
					this._addr = nicoMessage.getNodeValue(doc, "addr");
					this.set_port(nicoMessage.getNodeValue(doc, "port"));
					this._thread = nicoMessage.getNodeValue(doc, "thread");

				}
				
			}catch (Exception e){

	        }
			
		}

		/**
		 * @return �R�����g�T�[�o�̃A�h���X
		 */
		public String getAddress() {
			return _addr;
		}
		/**
		 * @return �R�����g�T�[�o�̃|�[�g�ԍ�
		 */
		public int getPort() {
			return _port;
		}
		private void set_port(String port) {
			this._port = Integer.parseInt(port);
		}
		private void set_alertport(String port) {
			this._alertport = Integer.parseInt(port);
		}
		/**
		 * @return �R�����g�T�[�o�̃X���b�h
		 */
		public String getThread() {
			return _thread;
		}
}