import java.awt.RenderingHints.Key;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;


public class Client{
	public static Socket connectionSocket= null;
	public static DataOutputStream outToServer = null;
	public static BufferedReader inFromServer = null;
	public static InputStream in=null;

	public static Scanner KeyboardInput = new Scanner(System.in);

	public static void main(String [] args){
		try {
			connectionSocket = new Socket("localhost",6789);
			outToServer = new DataOutputStream(connectionSocket.getOutputStream());
			inFromServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			in = connectionSocket.getInputStream();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		int inputFromUser;
		do{
			System.out.println("Welcome to Dropbox\nPlease Enter Your Choice:\n1.Register a new Account\n2.Login using existing account\n3.Exit");
			inputFromUser = KeyboardInput.nextInt();
			switch(inputFromUser){
				case 1: regNewAccount();
					break;
				case 2: logIntoAccount();
					break;
				case 3:printExit();
				try {
					KeyboardInput.close();
					outToServer.close();
					
					inFromServer.close();
					connectionSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					break;
				default:System.out.println("Wrong Entry Try Again");
					break;
			}
		}while(inputFromUser!=3);
	}
	
	public static void regNewAccount(){
		String Email="",Password="";
		try {
			outToServer.writeBytes("regNew()\n");
			outToServer.flush();
			System.out.println("New User Name:");
			Email = KeyboardInput.next();
			outToServer.writeBytes(Email+"\n");
			outToServer.flush();
			
			String bool=inFromServer.readLine();
			if(bool.equals("false")){
				System.out.println("Email Already Exist try with different email");
				return;
			}
			else{
				System.out.println("Password for "+Email+":");
				Password = KeyboardInput.next();
				outToServer.writeBytes(Password+"\n");
				outToServer.flush();
				
				String bool1=inFromServer.readLine();
				
				if(bool1.equals("true")){
					System.out.println("\n\nRegistration successful...!!!\n\n");
					
				}
				else{
					System.out.println("Something went wrong try again...");
					return;
				}
			}
			}catch(IOException e){
				e.printStackTrace();
			}
	}

	public static void logIntoAccount(){
		int choice;
		boolean isTerminate = true;
		String Email="",Password="";
		try {
			outToServer.writeBytes("login()\n");
			outToServer.flush();
			System.out.println("Enter existing User Name:");
			Email = KeyboardInput.next();
			outToServer.writeBytes(Email+"\n");
			outToServer.flush();
		
			String bool=inFromServer.readLine();
			if(bool.equals("true")){
				System.out.println("Password for "+Email+":");
				Password = KeyboardInput.next();
				outToServer.writeBytes(Password+"\n");
				outToServer.flush();
				
				String bool1=inFromServer.readLine();
			
				if(bool1.equals("true")){
					System.out.println("\nLogin successfull...!!!\n\n\n");
					File folder = new File(Email);
					if (!folder.exists()) {
						boolean isCreated = folder.mkdir();
						if (isCreated) {
							System.out.println(Email + " folder created successfully.");
						} else {
							System.out.println(Email+" failed to create the folder.");
						}
						} else {
							System.out.println(Email + " folder already exists.");
						}
					do{
						System.out.println("1.See Your File\n2.Upload file to Dropbox\n3.Download File from Dropbox\n4.Share File\n5.Delete File\n6.Logout or Exit");
						System.out.println("\nPlease enter your choice:\n");
						choice = KeyboardInput.nextInt();
						switch(choice){
							case 1:
								outToServer.writeBytes("seeFiles()\n");
								outToServer.flush();
								seeFiles(Email);
								break;
							case 2:
								System.out.println("In main function before upload");
								uploadFiles();
								System.out.println("Here in main function");
								break;
							case 3:downloadFile(Email);
								break;
							case 4:
								shareFile();
								break;
							case 5:
								deleteFile();
								break;
							case 6:
								logout();
								break;
						}
					}while(choice!=6);
					
				}
				else{
					System.out.println("Username  Pass combination not working...");
					return;
				}
			}
			else{
				System.out.println("E-mail doesn't exist");
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	
	
	

	public static void deleteFile(){
		Scanner Input = new Scanner(System.in);
		try {
			outToServer.writeBytes("deleteFiles()\n");
			outToServer.flush();
			System.out.println("Enter Name of file you want to delete:");
			String fileName = Input.next();
			outToServer.writeBytes(fileName+"\n");
			outToServer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	public static void shareFile(){
		Scanner Input = new Scanner(System.in);
		System.out.println("Inside File sharing");
		try {
			outToServer.writeBytes("fileShare()\n");
			outToServer.flush();
			System.out.println("Enter Name of User with whom you want to share file:");
			String userName = Input.next();
			outToServer.writeBytes(userName+"\n");
			outToServer.flush();
			System.out.println("Enter File name that you want to share");
			String fileName = Input.next();
			outToServer.writeBytes(fileName+"\n");
			outToServer.flush();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static void downloadFile(String dirName){
		Scanner Input = new Scanner(System.in);
		String fileName = null;
		byte[] Bytes = new byte[1000];
		try {
			outToServer.writeBytes("download()\n");
			outToServer.flush();
			System.out.println("Client send download()");
		
			System.out.println("Client called see files");
			System.out.println("\n\n Enter file name you want to download: ");
			fileName = Input.next();
			System.out.println("You have entered "+fileName);
			outToServer.writeBytes(fileName.concat("\n"));
			outToServer.flush();
			System.out.println("Client sent file name to download");
			String bool = inFromServer.readLine();
			System.out.println("CLient received bool with "+bool);
			if(bool.equals("true")){
				System.out.println("in if");
			
				String path = System.getProperty("user.dir");
				path = path.replaceAll("\\\\", "/");
    			path = path.concat("/");
				path = path.concat(dirName);
				path = path.concat("/");
				path = path.concat(fileName);
				File fout = new File(path);
				System.out.println("");
			
				FileOutputStream fileWriter = new FileOutputStream(fout);
				int count =0;
				String slength = inFromServer.readLine();
				System.out.println("Client received file length");
				System.out.println("Length is: " +slength);
				double length = Double.parseDouble(slength);
				System.out.println("length in souble is: "+length);


				if(length!=0){
					System.out.println("Inside if");
					int counter = 0;
					count = in.read(Bytes);
					while(count>=0 &&length>0 ){
						
						counter++;
						System.out.println("Receiving : "+count);
						fileWriter.write(Bytes,0,count);
						
						System.out.println("Receiving : "+count);
						length = length - count;
					
						if(length==0){
							break;
						}
						else
							count = in.read(Bytes);
					}
			
					fileWriter.flush();
					fileWriter.close();
				}
				System.out.println("After loop");
			}
			else
			{
				System.out.println("else");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void seeFiles(String dirName){
		try{
			
			String slength = null;
			slength = inFromServer.readLine();
		
			int length = Integer.parseInt(slength);
			
			ArrayList<String> fileNames = new ArrayList<>();
			while(length>0){
				fileNames.add(inFromServer.readLine());
			
				length--;
			}
			
			System.out.println("\n\nFiles on Server Side are: \n");
			for(int i =0;i<fileNames.size();i++){
				System.out.println(i+1+". "+fileNames.get(i));
			}
		
			System.out.println("____________________________________________________\n\n");
		}catch(IOException e){
			e.printStackTrace();
			System.out.println("Something wrong happen while creating streams");
		}
	}


	public static void uploadFiles(){
		byte[] bytes = new byte[100];
		Scanner KeyboardInput = new Scanner(System.in);
		OutputStream outToServerByte = null;
		try{
			outToServerByte = connectionSocket.getOutputStream();
			outToServer.writeBytes("uploadFiles()\n");
			outToServer.flush();
			System.out.println("Server sent upload files files");
			System.out.println("Enter the name of file you want to upload");
			String fileName = KeyboardInput.nextLine();
			outToServer.writeBytes(fileName+"\n");
			outToServer.flush();
			System.out.println("Sending Data to Server...");
			int count = 0;
			File file = new File(fileName);
			double length = file.length();
			String slength = Double.toString(length);
			System.out.println("length is: "+slength);
			outToServer.writeBytes(slength+"\n");
			outToServer.flush();
			InputStream fis = new FileInputStream(file);
			while ((count = fis.read(bytes)) >= 0&& count!=-1) {
	    		System.out.println("Sending " + count);
	    		
	    		outToServerByte.write(bytes, 0, count);
	    		outToServerByte.flush();
	    	}

			System.out.println("Out side loop");
			outToServer.flush();
			System.out.println("File sent returning to main function");
		}catch(IOException e){
			System.out.println("Something wrong happen while creating streams");
		}
	}
	

	public static void printExit(){
		try {
			outToServer.writeBytes("exit\n");
			outToServer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Thanks for visiting Dropbox\n\n");
	}


	public static void logout(){
		String response = null;
		try {
			outToServer.writeBytes("logout()\n");
			outToServer.flush();
			response = inFromServer.readLine();
			if(response.equals("true")){
				System.out.println("You are successfully logged out of your Account...\n\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
