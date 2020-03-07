/*
 * Clase seguridad, nos servirá para realizar todas las gestiones necesarias en torno
 * a cifrado y firma 
 */
package seguridad;

import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import modelo.Nota;

/**
 *
 * @author Carlos González
 */
public class Seguridad {
	
	public static PublicKey publicaServidor;
	public static SecretKey claveCifrado;

	public static SecretKey generarClaveSimetrica() {
		SecretKey clave = null;
		try {
			KeyGenerator kg = KeyGenerator.getInstance("AES");
			kg.init(128);
			clave = kg.generateKey();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return clave;
	}

	public static SealedObject cifrarConClaveSimetrica(Object dato, SecretKey clave) {
		SealedObject objCifrado = null;
		try {
			Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
			c.init(Cipher.ENCRYPT_MODE, clave);
			objCifrado = new SealedObject((Serializable) dato, c);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException | IllegalBlockSizeException ex) {
			ex.printStackTrace();
		}
		return objCifrado;
	}

	public static Object descifrar(SecretKey clave, Object obj) {

		try {
			SealedObject c = (SealedObject) obj;
			return c.getObject(clave);

		} catch (NoSuchAlgorithmException | InvalidKeyException | IOException | ClassNotFoundException ex) {
			Logger.getLogger(Seguridad.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	public static KeyPair generarClaves() {
		KeyPair par = null;
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
			SecureRandom numero = SecureRandom.getInstance("SHA1PRNG");
			keyGen.initialize(1024, numero);
			par = keyGen.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e.getMessage());
		}
		return par;
	}

	public static byte[] firmar(String mensaje, PrivateKey clavepriv) throws InvalidKeyException {
		byte[] firma = null;
		try {
			Signature dsa = Signature.getInstance("SHA1withDSA");
			dsa.initSign(clavepriv);
			dsa.update(mensaje.getBytes());
			firma = dsa.sign();
		} catch (NoSuchAlgorithmException | SignatureException ex) {
			System.out.println(ex.getMessage());
		}
		return firma;
	}

	
	
	public static boolean verificarMensaje(Nota nota) {
		boolean valido = false;
		try {
			Signature verifica = Signature.getInstance("SHA1withDSA");
			verifica.initVerify(publicaServidor);
			verifica.update(String.valueOf(nota.getNota()).getBytes());
			valido = verifica.verify(nota.getFirma());
		} catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
		}
		return valido;
	}
}
