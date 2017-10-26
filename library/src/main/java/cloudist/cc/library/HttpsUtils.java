package cloudist.cc.library;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by cloudist on 2017/8/15.
 */
public class HttpsUtils {
    public static class SSLParams {
        public SSLSocketFactory sSLSocketFactory;
        public X509TrustManager trustManager;
    }

    public static SSLParams getSslSocketFactory(InputStream certificates, InputStream bksFile, String password) {
        SSLParams sslParams = new SSLParams();
        try {
            KeyManager[] keyManagers = prepareKeyManager(bksFile, password);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            //  自己创建一个trustManager
            X509TrustManager trustManager = LocalTrustManager.getLocalTrustManager(certificates);
            if (trustManager == null) {
                trustManager = new UnSafeTrustManager();
            }
            sslContext.init(keyManagers, new TrustManager[]{trustManager}, null);
            sslParams.sSLSocketFactory = sslContext.getSocketFactory();
            sslParams.trustManager = trustManager;
            return sslParams;
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        } catch (KeyManagementException e) {
            throw new AssertionError(e);
        } catch (KeyStoreException e) {
            throw new AssertionError(e);
        } catch (CertificateException e) {
            throw new AssertionError(e);
        }
    }

    public static SSLParams getSslSocketFactoryWithKeyStore(InputStream certificate, InputStream bksFile, String password) {
        SSLParams sslParams = new SSLParams();
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            SSLContext sslContext = SSLContext.getInstance("TLS");
            //  使用keyStore生成TrustManager
            keyStore.load(null);
            X509TrustManager trustManager;
            if (certificate != null) {
                keyStore.setCertificateEntry("cert", certificateFactory.generateCertificate(certificate));
                certificate.close();
                TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                factory.init(keyStore);
                trustManager = chooseTrustManager(factory.getTrustManagers());
            } else {
                trustManager = new UnSafeTrustManager();
            }

            KeyManager[] keyManagers = prepareKeyManager(bksFile, password);
            sslContext.init(keyManagers, new TrustManager[]{trustManager}, null);
            sslParams.sSLSocketFactory = sslContext.getSocketFactory();
            sslParams.trustManager = trustManager;
            return sslParams;
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        } catch (IOException e) {
            throw new AssertionError(e);
        } catch (CertificateException e) {
            throw new AssertionError(e);
        } catch (KeyStoreException e) {
            throw new AssertionError(e);
        } catch (KeyManagementException e) {
            throw new AssertionError(e);
        }
    }

    private static X509TrustManager chooseTrustManager(TrustManager[] trustManagers) {
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        return null;
    }

    private static class UnSafeTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[]{};
        }
    }

    private static KeyManager[] prepareKeyManager(InputStream bksFile, String password) {
        try {
            if (bksFile == null || password == null) return null;

            KeyStore clientKeyStore = KeyStore.getInstance("BKS");
            clientKeyStore.load(bksFile, password.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeyStore, password.toCharArray());
            return keyManagerFactory.getKeyManagers();

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class LocalTrustManager implements X509TrustManager {

        X509Certificate severCertificate;

        private LocalTrustManager(InputStream certificate) throws NoSuchAlgorithmException, KeyStoreException, CertificateException {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            severCertificate = (X509Certificate) certificateFactory.generateCertificate(certificate);
            try {
                certificate.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static LocalTrustManager getLocalTrustManager(InputStream certificates) throws NoSuchAlgorithmException, KeyStoreException, CertificateException {
            if (certificates == null) return null;
            return new LocalTrustManager(certificates);
        }


        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            if (chain == null || chain.length < 0) {
                throw new CertificateException();
            }

            for (X509Certificate certificate : chain) {
                certificate.checkValidity();

                try {
                    certificate.verify(severCertificate.getPublicKey());
                } catch (NoSuchAlgorithmException e) {
                    throw new AssertionError(e);
                } catch (SignatureException e) {
                    throw new AssertionError(e);
                } catch (NoSuchProviderException e) {
                    throw new AssertionError(e);
                } catch (InvalidKeyException e) {
                    throw new AssertionError(e);
                }
            }
        }


        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    public static HttpsUtils.SSLParams getSslSocketFactory(Context appContext, String fileName) throws IOException {
        return getSslSocketFactory(appContext.getAssets().open(fileName), null, null);
    }
}

