package med.aymen.service.Diary;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import med.aymen.service.Diary.CrossFilter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

@Path("/api")
public class DiaryService {

	private static Connection conn = null;
	private String driver = "com.mysql.jdbc.Driver";
//	private String URL = "jdbc:mysql://localhost:3306/projectdb?user=root&password=aymen11859835&autoReconnect=true";
	private String URL = "jdbc:mysql://localhost:3306/projectdb?user=root&password=1&autoReconnect=true";
	
	private void connectJDBC() throws IOException,SQLException
	{
		try {
			// Load database driver if not already loaded.
			Class.forName(driver);
			System.out.println("Driver Loaded OK");
			// Establish network connection to database.
	
				conn = DriverManager.getConnection(URL);
				System.out.println("Connected");
		
		}
		catch(SQLException sqle) {
			System.out.println("Error connecting: " + sqle);
			sqle.printStackTrace();
			}
		catch(Exception cnfe) {
			System.out.println("Error loading driver: " + cnfe);
			} 
	}
	
	public boolean isConnected()
	{
		if (conn != null)
				return true;
		else
				return false;
	}
	
	public boolean disconnect()
	{
		if (conn != null)
		{
			try {
				conn.close();
			} 
			catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}			
		}		
		return true;
	}
	  
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/getall")
	public Response getAllUsers() throws SQLException, IOException
	  {
	    connectJDBC();     
	    Statement statement = conn.createStatement();
	    String counter = "select count(*) from users";
	    ResultSet counterSet = statement.executeQuery(counter);
	    
	    
	    System.out.println("test1");
	  
	    
	    int count = 0;
	    if(counterSet.next()) 
	        count = counterSet.getInt(1);
	    String query = "SELECT id, username, password FROM users";
	    // Send query to database and store results.
	    ResultSet resultSet = statement.executeQuery(query); 
	    ArrayList<Users> list = new ArrayList<Users>();
	    
	    while(resultSet.next()){
	      Users temp = new Users();
	      temp.id = resultSet.getInt(1);
	      temp.username = resultSet.getString(2);
	      temp.password = resultSet.getString(3);
	          
	      list.add(temp);
	    }
	    disconnect();  
	    Gson gson = new Gson();
	    String returnList = gson.toJson(list);
	    
	    
	    
	    System.out.println(returnList);
	    
	    return Response.ok(returnList)
	    		.header("Access-Control-Allow-Origin", "*")
	    	      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
	    	      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	  }
	
	
	
	  
	@POST
	@Consumes({MediaType.TEXT_PLAIN,MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Produces({ MediaType.TEXT_PLAIN})
	@Path("/register")
	public String registration(String json) throws SQLException, IOException
	{   
		System.out.println("register");
		
		
		Type obj = new TypeToken<Users>(){}.getType();
		Users user  =  new Gson().fromJson(json,obj);
		System.out.println(user.username);
		System.out.println(user.password);
		Response res = null;  
//		try {
		connectJDBC();
		
		Statement statement = conn.createStatement();
		String sqls = "select username from users where username="+user.username;
		ResultSet resultSet = statement.executeQuery(sqls);
		System.out.println(sqls);
////		Gson gson = new Gson();
		
		if (resultSet.next()) {
			
			res = Response.status(201).entity("OK").build();
			System.out.println("failed");
		    return "failed";     
		}else{
		String sql = "INSERT INTO users VALUES(null," + 
		             "'" + user.username + "','" + user.password + "')";
		System.out.println(sql);
//		Gson gson = new Gson();
		
		Boolean result = statement.execute(sql);
		if(result=true){
		res = Response.status(201).entity("OK").build();
		System.out.println("success");
	    return "success";     
		}else{
		
		res = Response.status(400).entity("FAIL").build();
		System.out.println("registration failed");
		return "registration failed";
		}
//		return "register test";
		}
		}
	
		
		
	
	
	
	@GET
	@Produces({MediaType.APPLICATION_JSON })
	@Path("/get/{id}")
	public Response getAUser(@PathParam("id")int id) throws SQLException, IOException {
		connectJDBC();
		
		Statement statement = conn.createStatement();
		String counter = "select count(*) from users";
		ResultSet counterSet = statement.executeQuery(counter);
	
		int count = 0;
		if(counterSet.next())	
				count = counterSet.getInt(1);
		String sql = "SELECT * FROM users WHERE id = '" + id + "'";
		// Send query to database and store results.
		ResultSet resultSet = statement.executeQuery(sql);
	
		Users temp = new Users();
		
		while(resultSet.next())
		{
			  temp.id = resultSet.getInt(1);
		      temp.username = resultSet.getString(2);
		      
		      temp.password = resultSet.getString(3);				
		}
		disconnect();
		
		 	Gson gson = new Gson();
		    String returnCoffee = gson.toJson(temp);
	
		
	return Response.ok(returnCoffee)
		.header("Access-Control-Allow-Origin", "*")
	      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
	      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}	
	
	
	
	
	
	
	
	@POST
	@Consumes({MediaType.TEXT_PLAIN,MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Produces({ MediaType.TEXT_PLAIN})
	@Path("/login")
	public Response login(String json) throws SQLException, IOException
	{
		Type obj = new TypeToken<Users>(){}.getType();
		Users user  =  new Gson().fromJson(json,obj);
		System.out.println(user.username);
		System.out.println(user.password);
		Response res = null;  
//		try {
		connectJDBC();
		
		Statement statement = conn.createStatement();
		String sql = "select username from users where username="+user.username+" AND password="+user.password;
		ResultSet resultSet = statement.executeQuery(sql);
		System.out.println(resultSet);
////		Gson gson = new Gson();
		
		if (resultSet.next()) {
			
			res = Response.status(201).entity("OK").build();
			String success = "success";
		    return Response.ok(success).header("Access-Control-Allow-Origin", "*")
		  	      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();    
		}else{
		    String wrong = "username or password wrong!";     
		    return Response.ok(wrong).header("Access-Control-Allow-Origin", "*")
			  	      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
				      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
			}
		}

	
	
// diary part
	
//	////////////////////////////////////////
	
	@POST
	@Consumes({MediaType.TEXT_PLAIN,MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Produces({ MediaType.TEXT_PLAIN})
	@Path("/newdiary")
	public String newdiary(String json) throws SQLException, IOException{
		System.out.println(json);
		Type obj = new TypeToken<Diary>(){}.getType();
		Diary diary  =  new Gson().fromJson(json,obj);
		Response res = null;
		System.out.println(diary.Diary_text+","+diary.Diary_title);
		try {
		connectJDBC();
		System.out.println(json);
		Statement statement = conn.createStatement();
		String sql = "INSERT INTO diaryss (user_name, Diary_title, Diary_text) VALUES('"+ diary.user_name +
		             "','" + diary.Diary_title + "','" + diary.Diary_text + "')";
		System.out.println(sql);
		Gson gson = new Gson();
//		
		Boolean result = statement.execute(sql);
//		
         if (result=true) {
//			
			res = Response.status(201).entity("OK").build();
		    return "success";     
		  }else{
		    return "failed";     
		  }
         }
	
		
		finally{disconnect();}
//		System.out.println(diary.user_name);
//		return"newdiary";
		
	}
//	////////////////////////////////////////////////
	
//	
	
//	///////////////////////////////////////////
	
	@DELETE
	@Consumes({MediaType.TEXT_PLAIN,MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Produces({ MediaType.TEXT_PLAIN,MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Path("/delete/{id}")
	public String deleteADiary(@PathParam("id")int id) throws SQLException, IOException
	{
//		System.out.println(id);
//		return "id = "+id;
		Response res = null;
		try {
		connectJDBC();
		
		Statement statement = conn.createStatement();
		String sql = "DELETE FROM diaryss WHERE id = '" + id + "'";
		
		boolean result = statement.execute(sql);
		
		res = Response.status(201).entity("OK").build();
	    return "deleted";     
		}
		catch (Exception e)
		{
		res = Response.status(400).entity("FAIL").build();
		return "not deleted";
		}
		
		finally{disconnect();}
	}
	
	
	
	
//	
//get diary for the current user
	
	@GET
	@Produces({MediaType.APPLICATION_JSON })
	@Path("/getallDiary/{user_name}")
	public Response getAllUserDiary(@PathParam("user_name")String user_name) throws SQLException, IOException
	  {
	    connectJDBC();     
	    Statement statement = conn.createStatement();
	    
	    String query = "SELECT * FROM `diaryss` WHERE user_name ='"+user_name+"'";
	    		
	    // Send query to database and store results.
	    ResultSet resultSet = statement.executeQuery(query); 
	    ArrayList<Diary> list = new ArrayList<Diary>();
	    
	    while(resultSet.next()){
	      Diary temp = new Diary();
	      temp.id = resultSet.getInt(1);
	      temp.user_name = resultSet.getString(2);
	      temp.Diary_title = resultSet.getString(3);
	      temp.Diary_text = resultSet.getString(4);
	    
	          
	      list.add(temp);
	    }
	    disconnect();  
	    Gson gson = new Gson();
	    String returnList = gson.toJson(list);
	    
	    
	    
	    System.out.println("test2");
	    
	    
	   
	    return Response.ok(returnList)
	    		.header("Access-Control-Allow-Origin", "*")
	    	      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
	    	      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	  
	  }
	
	
	
	@GET
	@Produces({MediaType.APPLICATION_JSON })
	@Path("/searchdiary/{user_name}/{Diary_title}")
	public Response searchDiary(@PathParam("user_name")String user_name,@PathParam("Diary_title")String diary_title ) throws SQLException, IOException
	  {
		
//		
//	  System.out.println("username "+user_name+"  Diary title  "+diary_title);
//	  return "username "+user_name+"  Diary title  "+diary_title;
	    connectJDBC();     
	    Statement statement = conn.createStatement();
	    String counter = "select count(*) from diarys";
	    ResultSet counterSet = statement.executeQuery(counter);
	    
	    
	    System.out.println("search");
	  
	    
	    int count = 0;
	    if(counterSet.next()) 
	        count = counterSet.getInt(1);
	    String query = "SELECT * FROM diaryss WHERE user_name ="+user_name+" AND Diary_title Like '%"+diary_title+"%'";
	    System.out.println(query);
	
	    // Send query to database and store results.
	    ResultSet resultSet = statement.executeQuery(query); 
	    ArrayList<Diary> list = new ArrayList<Diary>();
	    
	    while(resultSet.next()){
	      Diary temp = new Diary();
	      temp.id = resultSet.getInt(1);
	      temp.user_name = resultSet.getString(2);
	      temp.Diary_title = resultSet.getString(3);
	      temp.Diary_text = resultSet.getString(4);
	    
	          
	      list.add(temp);
	    }
	    disconnect();  
	    Gson gson = new Gson();
	    String returnList = gson.toJson(list);
	    
	    
	    
	   
	    return Response.ok(returnList)
	    		.header("Access-Control-Allow-Origin", "*")
	    	      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
	    	      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	  
	  }
	
	
	
	
	//////web application 
	
	@GET
	@Produces({MediaType.APPLICATION_JSON })
	@Path("/userlog/{username}/{password}")
	public Response userlog(@PathParam("username")String username, @PathParam("password")String password) throws SQLException, IOException {
		connectJDBC();
		
		Statement statement = conn.createStatement();
		String counter = "select count(*) from users";
		ResultSet counterSet = statement.executeQuery(counter);
		System.out.println(counter);
	
		int count = 0;
		if(counterSet.next())	
				count = counterSet.getInt(1);
		String sql = "SELECT * FROM users WHERE username = '" + username + "' AND password = '"+password+"'";
		// Send query to database and store results.
		ResultSet resultSet = statement.executeQuery(sql);
	
//		Users temp = new Users();
		String result  = null;
		if (resultSet.next())
		{ 
//			  temp.id = resultresultSet.getInt(1);
//		      temp.username = resultSet.getString(2);
//		      temp.password = resultSet.getString(3);	
			result = "success";
		} else {
			result = "failed";

		}
		disconnect();
		
//		 	Gson gson = new Gson();
//		    String returnCoffee = gson.toJson(temp);
	
		
	return Response.ok(result)
		.header("Access-Control-Allow-Origin", "*")
	      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
	      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}	
	
	
	
	@GET
	@Produces({MediaType.APPLICATION_JSON })
	@Path("/registerwa/{username}/{password}")
	public Response registeruser(@PathParam("username")String username, @PathParam("password")String password) throws SQLException, IOException {
        System.out.println("testing");
		connectJDBC();
//		
		Statement statement = conn.createStatement();
		String sqls = "select username from users where username='"+username+"'";
		ResultSet resultSet = statement.executeQuery(sqls);
		System.out.println(sqls);
		String result =null;
		Response res = null;
		if (resultSet.next()) {
//			res = Response.status(201).entity("OK").build();
			System.out.println("failed");
			result = "failed";     
		}else{
		String sql = "INSERT INTO users VALUES(null," + 
		             "'" + username + "','" + password + "')";
		System.out.println(sql);
//		Gson gson = new Gson();
		
		Boolean results = statement.execute(sql);
		if(results=true){
//		res = Response.status(201).entity("OK").build();
		System.out.println("success");
		result = "success";     
		}else{
		
//		res = Response.status(400).entity("FAIL").build();
		System.out.println("registration failed");
		result =  "registration failed";
		}
//		return "register test";
		}
		
	return Response.ok(result)
		.header("Access-Control-Allow-Origin", "*")
	      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
	      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}	
	
	
	
	@GET
	@Consumes({MediaType.TEXT_PLAIN,MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Produces({ MediaType.TEXT_PLAIN,MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Path("/deletewa/{id}")
	public String deleteADiaryWA(@PathParam("id")int id) throws SQLException, IOException
	{
//		System.out.println(id);
//		return "id = "+id;
		connectJDBC();
//		
		Statement statement = conn.createStatement();
////		String sqls = "select * from diaryss where id='"+id+"'";
//		ResultSet resultSet = statement.executeQuery(sqls);
//		System.out.println(sqls);
		String result =null;
//		if (resultSet.next()) {
			String sql = "DELETE FROM diaryss WHERE id = '" + id + "'";
			System.out.println(sql);
			Boolean results = statement.execute(sql);
			if(results=true){
			System.out.println("success");
			result = "success";     
			}else{
			
			System.out.println("failed");
			result =  "failed";
			}
		
//	}
    return result;
    
	}
	
	@GET
	@Consumes({MediaType.TEXT_PLAIN,MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Produces({ MediaType.TEXT_PLAIN,MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Path("/creatediarywa/{user_name}/{diary_title}/{diary_text}")
	public String creatediaryWA(@PathParam("user_name")String user_name, @PathParam("diary_title")String diary_title, @PathParam("diary_text")String diary_text) throws SQLException, IOException
	{
//		System.out.println(id);
//		return "id = "+id;
		connectJDBC();
//		
		Statement statement = conn.createStatement();
////		String sqls = "select * from diaryss where id='"+id+"'";
//		ResultSet resultSet = statement.executeQuery(sqls);
//		System.out.println(sqls);
		String result =null;
//		if (resultSet.next()) {
		String sql = "INSERT INTO diaryss (user_name, Diary_title, Diary_text) VALUES('"+ user_name +
	             "','" + diary_title + "','" + diary_text + "')";
			System.out.println(sql);
			Boolean results = statement.execute(sql);
			if(results=true){
			System.out.println("success");
			result = "success";     
			}else{
			
			System.out.println("failed");
			result =  "failed";
			}
		
//	}
    return result;
	}
	
	}
