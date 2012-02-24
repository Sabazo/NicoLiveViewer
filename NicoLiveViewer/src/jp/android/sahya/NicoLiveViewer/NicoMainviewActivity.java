package jp.android.sahya.NicoLiveViewer;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NicoMainviewActivity extends Activity implements OnClickListener, OnReceiveListener, Handler.Callback {

	private EditText email; 
	private EditText password;
	//�ʏ�̃��O�C��������
	private Button btnLogin;
	//�A���[�g��M�p�̃��O�C��������i�ʏ�̃��O�C�������A�J�E���g�̓��O�A�E�g���邱�Ƃ͂Ȃ��j
	private Button btnLoginAlert;
	//�ԑgID:lv000000000����ԑg�����擾���ăR�����g�T�[�o�ɐڑ����܂�
	//����A����Video���擾������
	private Button btnLiveNo;
	//�R�����g�T�[�o�܂��̓A���[�g�R�����g�T�[�o����̐ڑ���؂�܂�
	private Button btnDisconnect;
	//�ԑgID���͗��A���̓p�X���[�h�����ė��p���Ă��܂�
	private EditText etLiveNo;
	//��ԕ\���A�R�����g�\��	
	private EditText etResponse;
	//�\����Password����ԑgID�ɏ��������Ă��܂�
	private TextView tvPassword;
	//�r�f�I�\��������
	private WebView video;

	private NicoMessage nicoMesssage = null;
	private NicoRequest nico ;
	private NicoSocket nicosocket;
	private int _senderID = 0;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		
		etLiveNo = (EditText)findViewById(R.id.et_password);
		etResponse = (EditText)findViewById(R.id.ed_response);
		video = (WebView)findViewById(R.id.webView1);
		
		new NicoWebView(getIntent().getStringExtra("LoginCookie"), video).loadUrl();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState){
		video.restoreState(savedInstanceState);
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		video.saveState(outState);
	}

	public void onClick(View v){

	}

	public int getSenderID() {
		return this._senderID;
	}
	public void setSenderID(int senderID){
		this._senderID = senderID;
	}
	public void onReceive(String receivedMessege){
		
	}
	public boolean handleMessage(Message message) {
		return false;
	}
}