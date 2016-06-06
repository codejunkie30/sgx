Please follow the below instructions to generate the RSA key pairs.

1. Run com.wmsi.sgx.util.RSAKeyGenerator
2. It will prompt for fully qualified path to store the public key. Example path C:\mnt\sgx-login-keys\login-key.pub
3. 2. It will prompt for fully qualified path to store the private key. Example path C:\mnt\sgx-login-keys\login-key.pub
4. To quite the tool at any time we can enter Q
5. Place the key files in the servers in the specified location (Refer the rsakey.filepath.public and rsakey.filepath.private properties values corresponding to the platform to place the keys)

==============================================================================================================
Below is the example content we see in the console

RSA key generation utility
 
Press 'Q' at any time to quit this utility.
 
 
Enter the fully qualified path to store the public key: C:\mnt\sgx-login-keys\login-key.pub
C:\mnt\sgx-login-keys\login-key.pub exists. Overwrite? (y/n): y
 
Enter the fully qualified path to store the private key: C:\mnt\sgx-login-keys\login-key.pri
C:\mnt\sgx-login-keys\login-key.pri exists. Overwrite? (y/n): y
 
Generating RSA keypair with a 2048-bit modulus.
Please wait...
RSA key generation successful!
Public key stored in: C:\mnt\sgx-login-keys\login-key.pub
Private key stored in: C:\mnt\sgx-login-keys\login-key.pri