package com.realestate.utils.net;

import com.realestate.utils.Common;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
/**
 * Created on 19/07/2015
 * Description:
 */
public class TrustCASSLContext {
	private static SSLContext caSslContext = null;
	private static String athexCertUrl = "http://publicbeta.inbroker.com/ibl/libs/downloads/";

	private static String athexGeneralCert = "athex_general";
	private static String athexRootCert = "athex_root_ca";
	private static String athexGeneralCertUrlFile = "athex_general.crt";
	private static String athexRootCertUrlFile = "athex_root_ca.crt";


	public static synchronized SSLContext getInstance() {
		if (caSslContext == null) {
			caSslContext = getTrustedCAContext();
		}
		return caSslContext;
	}

	/*
	 * for modifiers
	 * check: http://stackoverflow.com/a/215505
	 */
	private static SSLContext getTrustedCAContext(){

		CertificateFactory certFactory = null;
		InputStream certAuthInput = null;
		Certificate cert = null;
		String keyStoreType = KeyStore.getDefaultType();
		KeyStore keyStore = null;
		String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
		TrustManagerFactory tmf = null;
		SSLContext context = null;
		HttpURLConnection urlConnection = null;
		try {
			// Create a KeyStore containing our trusted CAs
			keyStore = KeyStore.getInstance(keyStoreType);
			keyStore.load(null, null);
			// Create certificate factory with X.509 standard, http://en.wikipedia.org/wiki/X.509
			certFactory = CertificateFactory.getInstance("X.509");
			// Load CAs from an InputStream
			// (could be from a resource or ByteArrayInputStream or ...)

//athexRootCert
			urlConnection = (HttpURLConnection) new URL(athexCertUrl + athexRootCertUrlFile).openConnection();
			certAuthInput = new BufferedInputStream(urlConnection.getInputStream());
			cert = certFactory.generateCertificate(certAuthInput);
			Common.log("CA (Certificate Authority): " + ((X509Certificate) cert).getSubjectDN());
			certAuthInput.close();
			//insert certificate in keystore
			keyStore.setCertificateEntry(athexRootCert, cert);

//athexGeneralCert
			urlConnection = (HttpURLConnection) new URL(athexCertUrl + athexGeneralCertUrlFile).openConnection();
			certAuthInput = new BufferedInputStream(urlConnection.getInputStream());
			cert = certFactory.generateCertificate(certAuthInput);
			Common.log("CA (Certificate Authority): " + ((X509Certificate) cert).getSubjectDN());
			certAuthInput.close();
			//insert certificate in keystore
			keyStore.setCertificateEntry(athexGeneralCert, cert);

			// Create a TrustManager that trusts the CAs in our KeyStore
			tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
			tmf.init(keyStore);
			// Create an SSLContext that uses our TrustManager
			context = SSLContext.getInstance("TLS");
			context.init(null, tmf.getTrustManagers(), null);
		} catch (CertificateException e) {
			Common.logError("CertificateException @ getTrustedCAContext:"+e.getMessage());
			e.printStackTrace();
		} catch (FileNotFoundException e){
			Common.logError("FileNotFoundException @ getTrustedCAContext:"+e.getMessage());
			e.printStackTrace();
		} catch(IOException e){
			Common.logError("IOException @ getTrustedCAContext:"+e.getMessage());
			e.printStackTrace();
		} catch(KeyStoreException e){
			Common.logError("KeyStoreException @ getTrustedCAContext:"+e.getMessage());
			e.printStackTrace();
		} catch(NoSuchAlgorithmException e){
			Common.logError("NoSuchAlgorithmException @ getTrustedCAContext:"+e.getMessage());
			e.printStackTrace();
		} catch(KeyManagementException e){
			Common.logError("KeyManagementException @ getTrustedCAContext:"+e.getMessage());
			e.printStackTrace();
		}

		return context;
	}
}
