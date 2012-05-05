package jp.android.sahya.NicoLiveViewer;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class NicoCrypt {
	/*
	 * http://java.sun.com/j2se/1.4/ja/docs/ja/guide/security/jce/JCERefGuide.html#AppA
 	Cipher �̃C���X�^���X��v������ꍇ�A���̖��O��ϊ����� algorithm �R���|�[�l���g�Ƃ��Ďw��ł��܂��B
	AES - Advanced Encryption Standard �Ƃ��āANIST �ɂ���� FIPS �h���t�g�Ɏw�肳��܂����B Joan Daemen�AVincent Rijmen �����ɂ�� Rijndael �A���S���Y���Ɋ�Â��� 128 �r�b�g�̃u���b�N�Í��ł���A128 �r�b�g�A192 �r�b�g�A256 �r�b�g�̌����T�|�[�g���܂��B
	Blowfish - Bruce Schneier ���̐݌v�ɂ��u���b�N�Í��ł��B
	DES - �f�[�^�Í����K�i�ł� (FIPS PUB 46-2 �Œ�`)�B
	DESede - �g���v�� DES �Í����ł� (DES-EDE)�B
	PBEWith<digest>And<encryption> �܂��� PBEWith<prf>And<encryption> - �p�X���[�h�x�[�X�̈Í����A���S���Y�� (PKCS #5) �ł��B�w�肳�ꂽ���b�Z�[�W�_�C�W�F�X�g (<digest>) �܂��͋[�������_���֐� (<prf>) �ƈÍ����A���S���Y�� (<encryption>) ���g�p���܂��B���ɗ�������܂��B
	PBEWithMD5AndDES - 1993 �N 11 ���ARSA Laboratories �́uPKCS #5: Password-Based Encryption Standard�v�o�[�W���� 1.5 �ɒ�`���ꂽ�p�X���[�h�x�[�X�̈Í����A���S���Y���ł��B ���̃A���S���Y���ł́ACBC �͈Í����[�h�APKCS5Padding �̓p�f�B���O�����Ƃ���Ă��܂��B���̈Í����[�h��p�f�B���O�����Ŏg�p���邱�Ƃ͂ł��܂���B
	PBEWithHmacSHA1AndDESede - 1999 �N 3 ���ARSA Laboratories �́uPKCS #5: Password-Based Encryption Standard�v�o�[�W���� 2.0 �ɒ�`���ꂽ�p�X���[�h�x�[�X�̈Í����A���S���Y���ł��B
	RC2�ARC4�A����� RC5 - RSA Data Security, Inc �� Ron Rivest �ɂ��J�����ꂽ�σL�[�T�C�Y�Í����A���S���Y���ł��B
	RSA - PKCS #1 �ɒ�`����Ă��� RSA �Í����A���S���Y���ł��B
	 */
	private static String algorithm = "AES";

	public static byte[] encrypt(String key, String text)
	{
		try {
			SecretKeySpec sksSpec =  new SecretKeySpec(key.getBytes(), algorithm);
			Cipher cipher =   Cipher.getInstance(algorithm);
			cipher.init(Cipher.ENCRYPT_MODE, sksSpec);
			return cipher.doFinal(text.getBytes());
		} catch (Exception e) {
			return "".getBytes();
		}
	}

	public static String decrypt(String key, byte[] encrypted)
	{
		try {
			SecretKeySpec sksSpec =  new SecretKeySpec(key.getBytes(), algorithm);
			Cipher cipher =  Cipher.getInstance(algorithm);
			cipher.init(Cipher.DECRYPT_MODE, sksSpec);
			return new String(cipher.doFinal(encrypted));
		} catch (Exception e) {
			return null;
		}
	}
}
