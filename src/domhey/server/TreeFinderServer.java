package domhey.server;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class TreeFinderServer {
	static java.sql.Connection conn;
	
	public static void main(String[] args) {
		ServerSocket serverSocket;
		int PORT = 4090;	
		try {
			// Creates a new server socket with the given port number
			serverSocket = new ServerSocket(PORT);
			getDBConnection("jdbc:mysql://127.0.0.1:3306/treefinder", "root", "root");
			try{
				createTables();				
			}catch(Exception e){}
			System.out.println("Server Started");
			InetAddress IP=InetAddress.getLocalHost();
			System.out.println("IP my system is := "+IP.getHostAddress());
		} catch (IOException ex) {
			System.out.println(ex);
			return;
		}

		while (true) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();
				System.out.println("new Client Connected");
				ClientHandler handler = new ClientHandler(socket);
				handler.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
	private static void getDBConnection(String url, String name, String pwd){
		try{
			// get database access with URL NAME PWD
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url,name,pwd);
			System.out.println("Connected to Database");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static void createTables(){
		String table = "CREATE TABLE `treefinder`.`data` (`latlon` VARCHAR(128) NOT NULL,`title` VARCHAR(128) NULL,`photoname` VARCHAR(512) NULL,`photo` LONGBLOB NULL,PRIMARY KEY (`latlon`),UNIQUE INDEX `latlon_UNIQUE` (`latlon` ASC));";
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(table);
			System.out.println("Created table");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
