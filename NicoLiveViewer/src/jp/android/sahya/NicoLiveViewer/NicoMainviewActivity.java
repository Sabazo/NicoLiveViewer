package jp.android.sahya.NicoLiveViewer;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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
		private VideoView video;
		
		private NicoMessage nicoMesssage = null;
		private NicoRequest nico ;
		private NicoSocket nicosocket;
		private int _senderID = 0;
		public void onCreate(Bundle savedInstanceState) {
		    super.onCreate(savedInstanceState);
		    	requestWindowFeature(Window.FEATURE_NO_TITLE);
		    	setContentView(R.layout.main);
				
				btnLiveNo = (Button)findViewById(R.id.btnLive);
		        btnLiveNo.setOnClickListener(this);
		        btnDisconnect = (Button)findViewById(R.id.btnDisconnect);
		        btnDisconnect.setOnClickListener(this);
		        etLiveNo = (EditText)findViewById(R.id.et_password);
		        etResponse = (EditText)findViewById(R.id.ed_response);
		        //tvPassword = (TextView)findViewById(R.id.tv_password);
		        video = (VideoView)findViewById(R.id.videoview);
		}
		
		    public void onClick(View v){
	    	switch (v.getId()) {
				case R.id.btnLive : {
					if(nicosocket.isConnected()){
						if(nicosocket.closeSockt()){
							etResponse.setText("disconnected");
							btnLiveNo.setVisibility(View.VISIBLE);
							btnDisconnect.setVisibility(View.GONE);
							video.stopPlayback();
						}
					}
					return;
				}
		case R.id.btnDisconnect : {
	    	switch (getSenderID()){
	    			
	  case R.id.btnLive : {
	    if(nicosocket.isConnected()){
	       if(nicosocket.closeSockt()){
	        etResponse.setText("disconnected");
	        btnLiveNo.setVisibility(View.VISIBLE);
	        btnDisconnect.setVisibility(View.GONE);
	        video.stopPlayback();
	   }
	     }
	  return;
	    }
	  private void playVideo(Uri uri){
	    video.requestFocus();
	    video.setMediaController(new MediaController(this));
	    video.setVideoURI(uri);
	    video.start();
	   }
	  public int getSenderID() {
	    	return this._senderID;
	    				}
	  public void setSenderID(int senderID){
	    	this._senderID = senderID;
	    			    }
	  public void onReceive(String receivedMessege){
	    	switch (this.getSenderID()){
	    		case R.id.btnLive :{
	    			etResponse.append(receivedMessege + "\n");
	    			return;
	    	}
	    	}
	  }
	  public boolean handleMessage(Message message) {
	    	switch (message.what){
	    		case R.id.btnLive : {
	    	if (nicosocket.isConnected()){
	    		new Thread(nicosocket).start();	
	    		btnLiveNo.setVisibility(View.GONE);
	    		btnDisconnect.setVisibility(View.VISIBLE);
	    	//playVideo(uri);
	    	}else{
	    		Toast.makeText(this, "�ԑg�ɐڑ��ł��܂���ł���", Toast.LENGTH_SHORT).show();
	    			    				
	    	}