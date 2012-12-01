package jp.android.sahya.NicoLiveViewer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NicoLivePlayerActivity extends Activity {
	private EditText email; 
	private EditText password;
	//�ʏ�̃��O�C��������
	private Button btnLogin;
	//�A���[�g��M�p�̃��O�C��������i�ʏ�̃��O�C�������A�J�E���g�̓��O�A�E�g���邱�Ƃ͂Ȃ��j
	private Button btnLoginAlert;
	//��ԕ\���A�R�����g�\��	
	private EditText etResponse;
	//�\����Password����ԑgID�ɏ��������Ă��܂�
	private TextView tvPassword;
	//
	private CheckBox checkBox;
	
	private NicoMessage nicoMesssage = null;
	private NicoRequest nico = null;
	private NicoSocket nicosocket = null;
	private NicoFile saveDataFile = new NicoFile("NicoLiveViewerData.dat");
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);               
        setContentView(R.layout.login);
        
        tvPassword = (TextView)findViewById(R.id.tv_password);
        email = (EditText)findViewById(R.id.et_mail);
        password = (EditText)findViewById(R.id.et_password);
        btnLogin = (Button)findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new Login());
        btnLoginAlert = (Button)findViewById(R.id.btn_loginAlert);
        btnLoginAlert.setOnClickListener(new LoginAlert());
        etResponse = (EditText)findViewById(R.id.ed_response);
        checkBox = (CheckBox) findViewById(R.id.checkbox);
        //
        nicoMesssage = new NicoMessage();
        nico = new NicoRequest(nicoMesssage);
        // �`�F�b�N�{�b�N�X��ݒ肵�܂�
        new SaveCheckBox();
    }

    class SaveCheckBox implements View.OnClickListener {
    	public SaveCheckBox(){
    		// �`�F�b�N�{�b�N�X�̃`�F�b�N��Ԃ�ݒ肵�܂�
    		if (saveDataFile.canReadFile(getApplicationContext())){
    			//���O�C���f�[�^���ۑ�����Ă���΁A�`�F�b�N�{�b�N�X�̏�Ԃ𕜌�����
    			checkBox.setChecked(((NicoInfoData)saveDataFile.openFile(getApplicationContext())).isStore);
    			//�`�F�b�N�{�b�N�X���t���ꂢ��΁A���[���ƃp�X���[�h�𕜌�����
    			if (checkBox.isChecked()){
    				email.setText(NicoCrypt.decrypt(NicoKey.getKey(),
    						((NicoInfoData)saveDataFile.openFile(getApplicationContext())).mail));
    				password.setText(NicoCrypt.decrypt(NicoKey.getKey(),
    						((NicoInfoData)saveDataFile.openFile(getApplicationContext())).password));
    			}
    		} else {
    			checkBox.setChecked(true);
    		}
    		// �`�F�b�N�{�b�N�X���N���b�N���ꂽ���ɌĂяo�����R�[���o�b�N���X�i�[��o�^���܂�
    		checkBox.setOnClickListener(this);
    	}
    	public void onClick(View v) {
    		// �`�F�b�N�{�b�N�X�̃`�F�b�N��Ԃ��擾���܂�
    		Toast.makeText(v.getContext(),
    				"onClick():" + String.valueOf(checkBox.isChecked()),
    				Toast.LENGTH_SHORT).show();
    	}
    }
    
    /**
     * Login����
     */
    class Login implements OnClickListener, Handler.Callback, Runnable {
		final Handler handler = new Handler(this);
		
    	public void onClick(View v) {
	    	//���O�C���{�^����disable�ɂ���
	    	btnLogin.setEnabled(false);
	    	btnLoginAlert.setEnabled(false);
			key();    			
			new Thread(this).start();
		}
    	public void run() {
			nico.login(email.getText().toString(),password.getText().toString());
			Message message = handler.obtainMessage(R.id.btn_login);
			handler.sendMessage(message);
		}
    	
		public boolean handleMessage(Message arg0) {
			if (nico.isLogin()){
				//�`�F�b�N���t���Ă���΃��O�C���f�[�^��ۑ�����
				saveNicoInfoData();
				
				tvPassword.setText("�ԑgID");
				password.setText("lv");
				password.setInputType(InputType.TYPE_CLASS_NUMBER);
				btnLogin.setVisibility(View.GONE);
				btnLoginAlert.setVisibility(View.GONE);
				Toast.makeText(getApplicationContext(), "���O�C�����܂���", Toast.LENGTH_SHORT).show();
				// �C���e���g�̃C���X�^���X��
				Intent intent = new Intent(getApplicationContext(), NicoMainviewActivity.class);
				// ����ʂ̃A�N�e�B�r�e�B�N��
				NicoWebView nwv = new NicoWebView(nico.getCookieStore());
				intent.putExtra("LoginCookie", nwv.getLoginCookie());
				startActivity(intent);
				finish();
			}else{
				Toast.makeText(getApplicationContext(), "���O�C���ł��܂���ł���", Toast.LENGTH_SHORT).show();
				//���O�C���{�^����enable�ɂ���
		    	btnLogin.setEnabled(true);
		    	btnLoginAlert.setEnabled(true);
			}
			
			return true;
		}
    }
    
    private void saveNicoInfoData(){
    	NicoInfoData data = new NicoInfoData();
    	
    	if (checkBox.isChecked()){    		
    		data.mail = NicoCrypt.encrypt(NicoKey.getKey(), email.getText().toString());
    		data.password = NicoCrypt.encrypt(NicoKey.getKey(), password.getText().toString());
    		data.sessionCookie = NicoCrypt.encrypt(NicoKey.getKey(), nico.getLoginCookie());
    		data.lastUrl = NicoWebView.CONNECT_URL;
    		data.isStore = checkBox.isChecked();
    	} else {
    		data.mail = "0".getBytes();
    		data.password = "0".getBytes();
    		data.sessionCookie = NicoCrypt.encrypt(NicoKey.getKey(), nico.getLoginCookie());
    		data.lastUrl = NicoWebView.CONNECT_URL;
    		data.isStore = checkBox.isChecked();
    	}
    	
    	saveDataFile.saveFile(getApplicationContext(), data);
    }

    /**
     * LoginAlert����
     */
    class LoginAlert implements OnClickListener, Handler.Callback ,OnReceiveListener, Runnable {
    	final Handler handler = new Handler(this);
    	
    	public void onClick(View v) {
    		key();
    		//���O�C���{�^����disable�ɂ���
        	btnLogin.setEnabled(false);
        	btnLoginAlert.setEnabled(false);
 
    		nicosocket = new NicoSocket(nicoMesssage);
    		nicosocket.setOnReceiveListener(this);   		
    		new Thread (this).start();
		}
    	
    	public void run() {
			nico.loginAlert(email.getText().toString(),password.getText().toString());
			nicosocket.connectCommentServer(nico.getAlertAddress(), nico.getAlertPort(), nico.getAlertThread(), "1");
			Message message = handler.obtainMessage(R.id.btn_loginAlert);
			handler.sendMessage(message);
		}
    	
    	public boolean handleMessage(Message msg) {
    		if(nicosocket.isConnected()){
    			new Thread(nicosocket.getAlertSocketRunnable()).start();
    			btnLogin.setVisibility(View.GONE);
    			btnLoginAlert.setVisibility(View.GONE);
    		}else{
    			Toast.makeText(getApplicationContext(), "�A���[�g���O�C���Ɏ��s���܂���", Toast.LENGTH_SHORT).show();
    			//���O�C���{�^����enable�ɂ���
    	    	btnLogin.setEnabled(true);
    	    	btnLoginAlert.setEnabled(true);
    		}
			return true;
		}
    	
		public void onReceive(String[] receivedMessege) {
			etResponse.append(receivedMessege[0] + ":" + receivedMessege[1] + ":" + receivedMessege[2] +"\n");
		}	
    }
    
    private void key(){
    	InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		if( intent != null ) {
			/** �����N���URL���擾����B */
			String data = intent.getDataString();
			if(data != null){
				//url�����ɂ��ɂ傲�ɂ�
			}
		}
	}
}