package jp.android.sahya.NicoLiveViewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class NicoSocket implements Runnable{
	protected final String SERVER_ERR_MESSAGE = "�G���[���e�F�T�[�o�[�Ƃ̐ڑ��Ɏ��s���܂����B";
	protected final String SERVER_MESSAGE = "�T�[�o�[����̃��b�Z�[�W�F�T�[�o�[�Ƃ̐ڑ��ɐ������܂����B";
	private Socket socket = null;
	private BufferedReader reader = null;
    private OutputStreamWriter osw = null;
    private NicoMessage nicoMesssage = null;
	private OnReceiveListener onReceiveListener;
    
	public NicoSocket(NicoMessage nicoMesssage){
		this.nicoMesssage = nicoMesssage;
	}
	
	protected Socket getSocket() {
		return socket;
	}
	protected void setSocket(Socket socket) {
		this.socket = socket;
	}
	protected BufferedReader getCommentStream(){
		return this.reader;
	}
	protected void setCommentStream(BufferedReader reader){
		this.reader = reader;
	}
	
	public String connectCommentServer(String addr, int port, String thread) {
        
        try {
            // �T�[�o�[�֐ڑ�
            socket = new Socket(addr, port);
            
            osw = new OutputStreamWriter(socket.getOutputStream());
			osw.write(nicoMesssage.getChatMessage(thread));
			osw.flush();

            // ���b�Z�[�W�擾�I�u�W�F�N�g�̃C���X�^���X��
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
 
        } catch (UnknownHostException e) {
            return SERVER_ERR_MESSAGE;
        } catch (IOException e) {
            return SERVER_ERR_MESSAGE;
        }

        return SERVER_MESSAGE;
    }

	public void run(){
        while (socket.isConnected()){
            try{
            	nicoMesssage.getCommentMessage(reader, onReceiveListener);
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }
	public Runnable getAlertSocketRun(){
		return new Runnable(){
			public void run() {
				while (socket.isConnected()){
		            try{
		            	nicoMesssage.getAlertMessage(reader, onReceiveListener);
		            }
		            catch(Exception ex){
		                ex.printStackTrace();
		            }
		        }
			}
		};
	}
	
	public void setOnReceiveListener(final OnReceiveListener onReceiveListener){
		this.onReceiveListener = onReceiveListener;
	}
	protected OnReceiveListener getOnReceive() {
		return this.onReceiveListener;
	}

	public boolean isConnected() {
		if (socket == null){
			return false;
		}
		return this.socket.isConnected();
	}
	
	public boolean closeSockt(){
		 try {
             // �ڑ��I������
			 osw.close();
             reader.close();
             socket.close();
         } catch (IOException e) {
         	return false;
         }
		 
		return true;
	}
}