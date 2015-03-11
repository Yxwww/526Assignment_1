C_1:
    THe program should fulfill the functionality requested by assignment specification.
    This program accepts argument. It contains one main java:
        * C_1.java
            > Run the program on Sample file use command:
                - "java c_1 encrypt original1.txt aKeyNeeds8Chars"
            > The program will encrypt the file in all "EBC/CBC/OFB/CFB" and write out a report
        Table(number of identical blocks):
                File            ECB     CBC     OFB     CFB
            php.ini.default      1       0       0       0
            ssh_config~org       0       0       0       0
            nanorc               0       0       0       0
            xtab                 0       0       0       0
            man.conf             0       0       0       0
            localtime            0       0       0       0
            shells               0       0       0       0
            protocols            0       0       0       0
            mail.rc              0       0       0       0
            master.passwd~orig   0       0       0       0

        Since php.ini.defualt has 1088 building blocks and the other files are relatively smaller, I guess that's why it
        has 1 identical building block.


C_2:
    My C_2 has two main file:
        * C_2_Server.java
            > Server handles basic "GET" command, return with "HTTP 200" header contain the file.
            > Server handles basic "PUT command. Though, it doesn't handle to save payload into a file,
                instead it prints the file and send back "HTTP 200"
        * C_2_Client_Demo.java
            > Client doesn't take any argument, it is a demo program that runs to send "GET" "PUT" "SHUTDOWN" command
                to the server and writes out the response.
            > What client does:
                1, Set trustStorePath and connect socket to the server
                2, Send "GET /hello.txt HTTP/1.0" to server ,and get response with print out.
                3, Send "PUT ClientToSend/lifeishard.txt" with the file data as payload, waits response and printout.
                4, Send "SHUTDOWN" msg, to shutdown server.
                5, DONE.


SSH certificate generate section:

    #   To generate my own CA, I found some resources on
            http://blog.didierstevens.com/2008/12/30/howto-make-your-own-cert-with-openssl/
    Command that I used for generate key and certificate on server:
    -   openssl genrsa -out ca.key 4096
    -   openssl req -new -x509 -days 1826 -key ca.key -out ca.crt

    #   Then, according to http://stackoverflow.com/questions/906402/importing-an-existing-x509-certificate-and-private-key-in-java-keystore-to-use-i/8224863#8224863
            we need to convert the x509 Cert and Key to a pkcs12 file, then translate to pkcs12 file that java could read.
    Command:
    -   openssl pkcs12 -export -in ca.crt -inkey ca.key -out server.p12 -name serverP12 -CAfile ca.crt
    -   keytool -importkeystore -destkeystore server.keystore -srckeystore server.p12 -srcstoretype PKCS12 -srcstorepass yuxibruh
        * Note that my password was set to: yuxibruh for generate server.p12,
        * and my destination password is set to : yuxibruh, need to be the same password for server.

    #    To generate Client truststore according to certificate
    Command:
    -   keytool -keystore clientcert -importcert -alias clientCert -file ca.crt
    Alternatively:
    We can do:
    -   openssl s_client -connect localhost:3456
    Then we can see the server certificate and store it to a file lets say client.pem, then we can do
    -   keytool -keystore clientcert -importcert -alias clientCert -file client.pem
    To generate the client truststore