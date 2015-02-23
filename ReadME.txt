




C_2:
    #   To generate my own CA, I found some resources on
            http://blog.didierstevens.com/2008/12/30/howto-make-your-own-cert-with-openssl/
    Command that I used for generate key and certificate on server:
    -   openssl genrsa -out ca.key 4096
    -   openssl req -new -x509 -days 1826 -key ca.key -out ca.crt

    #   Then, according to http://stackoverflow.com/questions/906402/importing-an-existing-x509-certificate-and-private-key-in-java-keystore-to-use-i/8224863#8224863
            we need to convert the x509 Cert and Key to a pkcs12 file, then translate to pkcs12 file that java could read.
    Command that converts the certificate to something java could read:
    -   openssl pkcs12 -export -in ca.crt -inkey ca.key -out server.p12 -name serverP12 -CAfile ca.crt
    -   keytool -importkeystore -destkeystore server.keystore -srckeystore server.p12 -srcstoretype PKCS12 -srcstorepass yuxibruh
        * Note that my password was set to: yuxibruh


    #    To generate Client truststore according to certificate
    Command:
    -   keytool -keystore clientcert -importcert -alias clientCert -file ca.crt
    Alternatively:
    We can do:
    -   openssl s_client -connect localhost:3456
    Then we can see the server certificate and store it to a file lets say client.pem, then we can do
    -   keytool -keystore clientcert -importcert -alias clientCert -file client.pem
    To generate the client truststore