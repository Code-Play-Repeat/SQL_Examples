import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;


public class Lab7 {

	public static void main(String[] args) 
	{  
		Scanner input = new Scanner (System.in);
		Connection conn = null;
		try 
		{  
			double userInput = 0; 
			boolean c = false;
			boolean t = false;
			boolean d = false;

			while (userInput != -1)
			{
				System.out.println ("What would you like to do?");
				System.out.println ("Enter 1 to connect to database\n");
				System.out.println ("Enter 2 to create table in database\n");
				System.out.println ("Enter 3 to update database\n");
				System.out.println ("Enter 4 to execute queries on database\n");
				System.out.println ("Enter 5 to disconnect from database\n");
				System.out.println ("Enter -1 to quit program\n");
			    userInput = input.nextDouble();

				if (userInput == 1)
				{
					System.out.println ("Connecting...\n");
					// Part 1 connect
					conn = connect();
					c = true;
				}
				else if (userInput == 2)
				{
					//Part 2  create table
					System.out.println ("Creating Table...\n");
					createTable(conn);
					t = true;

				}
				else if (userInput == 3)
				{
					//Part 3 input data
					System.out.println ("Inputing Data...\n");

					userEntry(conn);
				}
				else if (userInput == 4)
				{
					//Part 4 queries
					System.out.println ("Executing Queries...\n");

					executeQueries(conn);
				}
				else if (userInput == 5)
				{		         
					System.out.println ("Disconnecting...\n");
					//Part 5 disconnect
					disconnect(conn); 
					d = true;
				}

			}

		} 
		catch (Exception e) 
		{  
			e.printStackTrace();  
		}

	}  
	// Part 1
	public static Connection  connect ()
	{
		Connection conn = null;
		try
		{
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:TPCH");
			return conn;


		}
		catch (Exception e)
		{
			// if the error message is "out of memory", 
			// it probably means no database file is found
			System.err.println(e.getMessage());
		}

		return conn;
	}

	// Part 2
	public static void createTable(Connection rconn)
	{
		try 
		{
			Statement state = rconn.createStatement();
			state.executeUpdate("create table warehouse (w_warehousekey decimal(3,0) not null, w_name char(25) not null, w_supplierkey decimal(2,0) not null, w_capacity decimal(6,2) not null, w_address varchar(40) not null, w_nationkey decimal(2,0) not null)");
			state.close();
		}
		catch (Exception e)
		{
			// if the error message is "out of memory", 
			// it probably means no database file is found
			System.err.println(e.getMessage());
		}

	}

	// Part 3

	public static void userEntry(Connection rconn)
	{
		Scanner input = new Scanner (System.in);	   
		try
		{
			Statement state = rconn.createStatement();
			double i = 0;
			String quit = " ";
			while (quit != "q")
			{
				System.out.print("Name:  ");
				String name = input.nextLine();

				double warehousekey = i;
				System.out.print("Supplier name: ");
				String s_name = input.nextLine();

				System.out.print("Capacity:  ");
				double capacity = input.nextDouble();

				System.out.print("Address:  ");
				String address = input.next();

				System.out.print("Nation:  ");
				double nationkey = input.nextDouble();

				ResultSet supKey = state.executeQuery("select distinct s_suppkey from supplier where s_name = '" + s_name + "';");
				int supplierKey = supKey.getInt("s_suppkey");

				state.executeUpdate("insert into warehouse values ( '" + warehousekey + "', '" + name + "', '" +  supplierKey+ "', '" +  capacity+ "', '" +  address+ "', '" +  nationkey + "');");
				System.out.println("Do you want to quit? (enter q for quit): ");
				quit = input.next();
				supKey.close();
				i++;

			}
			state.close();


		}
		catch (Exception e)
		{
			// if the error message is "out of memory", 
			// it probably means no database file is found
			System.err.println(e.getMessage());
		}
	}

	// Part 4
	public static void executeQueries (Connection rconn)
	{
		Scanner input = new Scanner (System.in);	   
		try
		{
			Statement state = rconn.createStatement();
			// query 1
			ResultSet res = state.executeQuery("select s_name, sum(w_capacity) from warehouse, supplier where s_suppkey = w_supplierkey group by s_name;");
			while ( res.next())
			{
				System.out.println("s_name = "+ res.getString("s_name"));
				System.out.println("sum(w_capacity) = " + res.getString("sum(w_capacity)"));
			}
			// query 2
			res = state.executeQuery("select s_name from supplier where s_name in (select s_name, count(w_name) from warehouse, supplier where w_supplierkey = s_suppkey);");
			System.out.println("s_name = " + res.getString("s_name"));

			// query 3
			System.out.println("Enter min capacity of Warehouse you are looking for: ");
			int a = input.nextInt();
		    res = state.executeQuery("select s_name from warehouse, supplier, nation, region where w_capacity ="+a+" and w_supplierkey = s_suppkey and w_nationkey = n_nationkey and n_regionkey = r_regionkey and r_name = 'ASIA';");
			while(res.next())
			{
				System.out.println("s_name = "+ res.getString("s_name"));
			}

			// query 4
			System.out.println("Input the name of the supplier: ");
			String name = input.nextLine();
			res = state.executeQuery("select w_capacity, sum(ps_availqty) from warehouse, supplier, partsupp where w_supplierkey = s_suppkey and s_suppkey = ps_suppkey and s_name = '"+name+"';");
			int cap = res.getInt("w_capacity");
			int sums = res.getInt("sum(ps_availqty)");	

			if(cap >= sums)
				System.out.println("The supplies fit into the warehouse");
			else
				System.out.println("Capcity exceeded");
			res.close();
			state.close();

		}
		catch (Exception e)
		{
			// if the error message is "out of memory", 
			// it probably means no database file is found
			System.err.println(e.getMessage());
		}
	}

	// Part 5
	public static void disconnect (Connection rconn)
	{
		try
		{
			rconn.close();

		}
		catch (Exception e)
		{
			// if the error message is "out of memory", 
			// it probably means no database file is found
			System.err.println(e.getMessage());
		}

	}

}
