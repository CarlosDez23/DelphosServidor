/*
 * Clase seguridad, nos servirá para realizar todas las gestiones necesarias en torno
 * a cifrado y firma 
 */
package seguridad;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import modelo.Nota;

/**
 *
 * @author Carlos González
 */
public class Seguridad {

	public static PublicKey publicaServidor;
	public static SecretKey claveCifrado;

	//par de claves para la firma
	public static PublicKey mipublica;
	public static PrivateKey miprivada;

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

	public static SecretKey recomponerClaveSimetrica(byte[] clave) {

		SecretKey clave2 = new SecretKeySpec(clave, 0, clave.length, "AES");
		return clave2;

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

	public static byte[] firmar(String mensaje, PrivateKey clavepriv) {
		byte[] firma = null;
		try {
			Signature dsa = Signature.getInstance("SHA1withDSA");
			dsa.initSign(clavepriv);
			dsa.update(mensaje.getBytes());
			firma = dsa.sign();
		} catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException ex) {
			ex.printStackTrace();
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

	/**
	 * Almacenar la clave privada en disco. Será necesario codificarla en formato PKCS8 usando la clave PKCS8EncodedKeySec.
	 */
	public static void archivar_clave_privada(String nom_fichero, PrivateKey clavepriv) {
		FileOutputStream outpriv = null;
		try {
			PKCS8EncodedKeySpec pk8Spec = new PKCS8EncodedKeySpec(clavepriv.getEncoded());
			//Escribir a fichero binario la clave privada.
			outpriv = new FileOutputStream(nom_fichero);
			outpriv.write(pk8Spec.getEncoded());
			outpriv.close();

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				outpriv.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Para almacenar la clave pública es necesario codificarla con el formato X.509 usando X509EncodedKeySpec.
	 */
	public static void archivar_clave_publica(String nom_fichero, PublicKey clavepubl) {
		FileOutputStream out = null;
		try {
			X509EncodedKeySpec pkX509 = new X509EncodedKeySpec(clavepubl.getEncoded());
			//Escribir a fichero binario la clave pública.
			out = new FileOutputStream(nom_fichero);
			out.write(pkX509.getEncoded());
			out.close();

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Para recuperar las claves de los ficheros necesitamos la clase KeyFactory que proporciona métodos para conertir claves de formato criptográfico (PKCS8, X509) a especificaciones de claves y viceversa.
	 */
	/**
	 * Recuperar la clave privada del fichero "Clave.privada". Es necesario crear con KeyFactory una instancia del algoritmo DSA (el mismo que se usó para generar el par original).
	 */
	public static PrivateKey Recuperar_clave_privada(String nom_fichero) {
		FileInputStream in = null;
		PrivateKey cp = null;
		byte BufferPriv[];

		try {

			in = new FileInputStream(nom_fichero);
			BufferPriv = new byte[in.available()];//Definimos el buffer dle tamaño exacto
			in.read(BufferPriv);        //Leemos los bytes
			in.close();

			KeyFactory keyDSA = KeyFactory.getInstance("DSA");
			//Recuperamos la clave privada desde datos codificados en PKCS8
			PKCS8EncodedKeySpec clavePrivadaSpec = new PKCS8EncodedKeySpec(BufferPriv);
			cp = keyDSA.generatePrivate(clavePrivadaSpec);

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			
		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
			ex.printStackTrace();
		}
		return cp;
	}

	/**
	 * Para recuperar la clave pública almacenada en el fichero.
	 */
	public static PublicKey Recuperar_clave_publica(String nom_fichero) {
		FileInputStream inpub = null;
		PublicKey pk = null;

		try {
			//Leemos el fichero.
			inpub = new FileInputStream(nom_fichero);
			byte[] bufferPub = new byte[inpub.available()];
			inpub.read(bufferPub);
			inpub.close();

			KeyFactory keyDSA = KeyFactory.getInstance("DSA");
			//Recuperamos la clave pública desde datos codificados en X509.
			X509EncodedKeySpec clavePublicaSpec = new X509EncodedKeySpec(bufferPub);
			pk = keyDSA.generatePublic(clavePublicaSpec);

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			
		} catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		}

		return pk;
	}
}
