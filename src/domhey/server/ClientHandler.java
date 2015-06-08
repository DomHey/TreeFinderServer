package domhey.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import networkMessages.Request;
import networkMessages.Response;
import networkMessages.ServerResponse;


public class ClientHandler extends Thread {
	private Socket clientsocket;
	private ObjectInputStream in = null;
	private ObjectOutputStream out = null;
	private ArrayList<Response> list = new ArrayList<Response>();

	public ClientHandler(Socket clientSocket) {
		this.clientsocket = clientSocket;
		try {
			this.in= new ObjectInputStream(clientsocket.getInputStream());
			this.out= new ObjectOutputStream(clientsocket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			Object o = in.readObject();
				if(o instanceof Response){
					// Save Tree to database
				Response res = (Response) o;
				String sql = "INSERT INTO data (latlon,title,photoname,photo) VALUES (?,?,?,?)";
				try {
					PreparedStatement prep = TreeFinderServer.conn.prepareStatement(sql);
					prep.setString(1, res.getLatLon());
					prep.setString(2, res.getTitle());
					prep.setLong(3, res.getPhotoName());
					prep.setBytes(4, res.getPhoto());
					prep.executeUpdate();
					System.out.println("saved Tree");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}else if(o instanceof Request){
					System.out.println("Requested Trees");
					Request req = (Request)o;
					
					String sql = "SELECT latlon,title,photoname,photo FROM data WHERE PHOTONAME > " + req.getLastUpdated();
					Statement stmt;
					try {
						stmt = TreeFinderServer.conn.createStatement();
						ResultSet rs = stmt.executeQuery(sql);
						while(rs.next()){
							list.add(new Response(rs.getString("latlon"), rs.getBytes("photo"), rs.getLong("photoname"), rs.getString("title")));
						}
						System.out.println("listsize: " + list.size());
						out.writeObject(new ServerResponse(list));
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

}
	

