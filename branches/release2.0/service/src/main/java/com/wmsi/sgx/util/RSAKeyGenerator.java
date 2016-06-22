package com.wmsi.sgx.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dt84327
 */
public final class RSAKeyGenerator {

	private static final Logger log = LoggerFactory.getLogger(RSAKeyGenerator.class);

	private static final String ERROR_GENERATING_KEYS = "Error in generating key files.";

	private static final String ALGORITHAM_NAME = "RSA";
	
    /**
     * Path to store the public key
     */
    private String publicPath = null;

    /**
     * Path to store private key
     */
    private String privatePath = null;
    
    private static final int keySize = 2048;

    /**
     * BufferedReader for reading inut from the console
     */
    private BufferedReader input = null;

	/**
     * Default constructor.
     */
    private RSAKeyGenerator()
    {
        input = new BufferedReader(new InputStreamReader(System.in));

        println("RSA key generation utility" );
        println(" ");
        println("Press 'Q' at any time to quit this utility." );
        println(" ");

    }
    
	/**
	 * Size of the key modulus in bits.
	 */
	private static final int KEY_SIZE = 2048;

	public static void main(String[] args) {
        RSAKeyGenerator rkg = new RSAKeyGenerator();

        boolean done = false;
        
		// initialize fields
		rkg.reset();

		// get public path
		rkg.promptForPath(false);

		// get private path
		rkg.promptForPath(true);

		// gen the keys & store them
		rkg.generateKeys();
	}
	
    /**
     * Reset path, keysize variables.
     */
    private void reset()
    {
        privatePath = null;
        publicPath = null;
    }

    /**
     * Ask user for public/private key path.
     *
     * @param isPrivate - indicates that we are asking for a private key path.
     */
    private void promptForPath(boolean isPrivate)
    {
        boolean pathValid = false;
        boolean dirValid = false;
        String prompt;
        String path;

        if(isPrivate)
            prompt = "Enter the fully qualified path to store the private key: ";
        else
            prompt = "Enter the fully qualified path to store the public key: ";

        while(!pathValid)
        {
            println(" ");

            path = getInput(prompt);

            //get the file
            File daFile = new File(path);

            //get the dir portion
            String dirPath = daFile.getParent();
            if(dirPath != null)
            {
                File daDir = new File(dirPath);

                //if dir does not exist, prompt for create
                if(!daDir.exists())
                {
                    String resp =
                        getInput(dirPath + " does not exist. Create? (y/n): ");
                    if(resp.toLowerCase().equals("y"))
                    {
                        daDir.mkdir();
                        dirValid = true;
                    }
                    else
                        pathValid = false;
                }
                else
                    dirValid = true;
            }
            else //store in current dir
                dirValid = true;

            //if file exists, prompt for overwrite
            if(dirValid)
            {
                if(daFile.exists())
                {
                    String resp =
                        getInput(path + " exists. Overwrite? (y/n): ");
                    if(resp.toLowerCase().equals("y"))
                    {
                        if(isPrivate)
                            privatePath = path;
                        else
                            publicPath = path;
                        pathValid = true;
                    }
                    else
                        pathValid = false;
                }
                else
                {
                    if(isPrivate)
                        privatePath = path;
                    else
                        publicPath = path;
                    pathValid = true;
                }

            }

        }

    }

    /**
     * generate the RSA keypair
     */
    private void generateKeys()
    {
        println(" ");
        println("Generating RSA keypair with a " + keySize + "-bit modulus.");
        println("Please wait...");

        FileOutputStream fos = null;

        try
        {
            //Get and RSA keypair generator
            KeyPairGenerator gen =
              KeyPairGenerator.getInstance("RSA");

            //initialize the keysize
            gen.initialize(keySize);

            //generate the keys
            KeyPair pair = gen.generateKeyPair();

            PublicKey pubKey = pair.getPublic();
            PrivateKey priKey = pair.getPrivate();

            //write public
            fos = new FileOutputStream(publicPath);
            fos.write(pubKey.getEncoded());
            fos.close();


            //write private
            fos = new FileOutputStream(privatePath);
            fos.write(priKey.getEncoded());
            fos.close();

            println("RSA key generation successful!");
            println("Public key stored in: " + publicPath);
            println("Private key stored in: " + privatePath);

        }
        catch(Exception e)
        {
            println("RSA key generation failed:");
            e.printStackTrace();
        }
        finally
        {
            try
            {
              if(fos != null)
                fos.close();
            }
            catch(IOException e) {}

        }

      }


   /**
    * Output helper.
    * @param value The String value to print.
    */
   private void println(String value)
    {
        System.out.println(value);
    }

    /**
     * Output helper.
     * @param value The String value to print.
     */
    private void print(String value)
    {
        System.out.print(value);
    }

    /**
     * Read input from the console.
     * @param prompt The String value to prompt the user with.
     * @return The value entered by the user.
     */
    private String getInput(String prompt)
    {
        String value = "";

        try
        {
            System.out.print(prompt);
            value = input.readLine();

            if(value.toLowerCase().equals("q"))
            {
                println("Quitting RSA key generation tool. Press Enter to exit.");
                input.readLine();
                System.exit(-1);
            }
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }

        return value;
    }
	
	
}
