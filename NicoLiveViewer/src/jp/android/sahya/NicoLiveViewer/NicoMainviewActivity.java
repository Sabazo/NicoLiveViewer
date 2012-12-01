package jp.android.sahya.NicoLiveViewer;

import java.util.Timer;
import java.util.TimerTask;

import jp.android.sahya.NicoLiveViewer.NicoSocket.NicoLiveComment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NicoMainviewActivity extends Activity {

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
	//�r�f�I�\��
	private WebView video;
	private String _url = "";
	private String _liveID = "";
	//Comment Post
	private EditText etCommentPost;
	private Button btnCommentPost;
	private NicoLiveComment nicoLiveComment;

	private NicoMessage nicoMessage = null;
	private NicoRequest nicoRequest = null;

	private int Rbstar = 0;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		etLiveNo = (EditText)findViewById(R.id.et_password);
		etResponse = (EditText)findViewById(R.id.ed_response);
		video = (WebView)findViewById(R.id.webView1);
		etCommentPost = (EditText)findViewById(R.id.et_commentpost);
		btnCommentPost = (Button)findViewById(R.id.btn_commentpost);
		
		btnCommentPost.setOnClickListener(new SendComment());

		nicoMessage = new NicoMessage();
		nicoRequest = new NicoRequest(nicoMessage);
		//�N�b�L�[���󂯎��
		nicoRequest.setLoginCookie(getIntent().getStringExtra("LoginCookie"));
		NicoWebView nwv = new NicoWebView(nicoRequest.getLoginCookie(), video);

		//�j�R���y�[�W�����[�h����
		nwv.loadUrl();
		_url = NicoWebView.CONNECT_URL;
		//WebView���y�[�W��ǂݍ��݂��J�n�������̃C�x���g�ʒm�n���h����ݒ�
		nwv.setOnPageStartedHandler(new Handler(new ChangedUrlHandler()));
	}
	/**
	 * WebView��URL�ύX���̏���
	 */
	class ChangedUrlHandler implements Handler.Callback {
		public boolean handleMessage(Message message) {
			switch (message.what){
			case NicoWebView.ON_PAGE_STARTED:
				if (isChangedUrl(message.obj.toString())){
					new GetComment().getComment();
					return true;
				}
				break;
			}
			return false;
		}
		private boolean isChangedUrl(String url){
			if (!_url.equals(url)){
				_url = url;
				return true;
			}
			return false;
		}
	}
	

	/**
	 * �����y�[�W�̃R�����g�擾����
	 */
	class GetComment implements Runnable, Handler.Callback, OnReceiveListener {
		final Handler handler = new Handler(this);

		private void getComment() {
			_liveID = nicoMessage.getLiveID(_url);
			if (_liveID.equals("")){ return; }
			new Thread(this).start();
		}
		
		public void run() {
			nicoRequest.getPlayerStatus(_liveID);
			NicoSocket nicoSocket = new NicoSocket(nicoMessage);
            nicoSocket.setOnReceiveListener(this);
			nicoLiveComment = nicoSocket.startNicoLiveComment(nicoRequest, _liveID);
			Message message = handler.obtainMessage();
			handler.sendMessage(message);
		}
		
		public boolean handleMessage(Message msg) {
			if (nicoLiveComment.isConnected()){
				new Thread(nicoLiveComment).start();	
			}else{
				etResponse.setText("�ԑg�ɐڑ��ł��܂���ł���");
			}
			return true;
		}

		public void onReceive(String[] receivedMessege){
			if (receivedMessege[0].equals("chatresult")) {
				etCommentPost.setText(nicoLiveComment.getComment());
			}
        	else {
        		etResponse.append(receivedMessege[0] + ":" +  receivedMessege[1] + "\n" + receivedMessege[2] + "\n");
        	}
		}
	}
	class SendComment implements OnClickListener, Runnable, Handler.Callback {
		final Handler handler = new Handler(this);
		
		public void onClick(View v) {
			new Thread(this).start();
		}
		
		public void run() {
			if (nicoLiveComment == null || nicoLiveComment.isConnected() == false){
         		return;
         	}
         	nicoLiveComment.send(etCommentPost.getText().toString());
			Message message = handler.obtainMessage();
			handler.sendMessage(message);
		}
		
		public boolean handleMessage(Message msg) {
			return true;
		}

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
	
	
	//�߂�{�^�����^�b�v����ƏI������
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
		if(keyCode == KeyEvent.KEYCODE_BACK){  

			//�_�C�A���O�̕\��  
			AlertDialog.Builder ad=new AlertDialog.Builder(this);  
			ad.setMessage(getString(R.string.dialog_message01));  
			ad.setPositiveButton(getString(R.string.Yes),
					new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int whichButton) {  
					//OK�Ȃ�Activity�I��  
					finish(); 
					
				}     
			});  
			ad.setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int whichButton) {  
					//NO�Ȃ炻�̂܂܉������Ȃ�  

				}     
			});  
			ad.create();  
			ad.show();  
			return false;  
		} 
		return true;
	}  

}
