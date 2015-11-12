package com.network;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;
/**
 * 
 * @author Muhammad Babar Naveed
 *    Date Feb 26, 2013
 *
 */
public class FakeTrustManager implements X509TrustManager {

        private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[] {};

        
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public boolean isClientTrusted(X509Certificate[] chain) {
                return true;
        }

        public boolean isServerTrusted(X509Certificate[] chain) {
                return true;
        }

        
        public X509Certificate[] getAcceptedIssuers() {
                return _AcceptedIssuers;
        }

}
