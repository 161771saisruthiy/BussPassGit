package org.cap.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.cap.model.LoginBean;
import org.cap.model.PassRequestForm;
import org.cap.model.Routetable;
import org.cap.model.TransactionBean;

public class LoginDAO implements ILoginDAO {
/*
 * method to check valid user
 */
	@Override
	public boolean checkUser(LoginBean loginBean) {
		String sql="select * from logintable where username=? and password=?";

		try(PreparedStatement ps =getSQLConnection().prepareStatement(sql);){

			ps.setString(1, loginBean.getUsername());
			ps.setString(2, loginBean.getPassword());
			ResultSet rs =ps.executeQuery();
			if(rs.next()) {
				return true;

			}

		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;


	}
	private Connection getSQLConnection()
	{
		Connection con=null;
		try{


			Class.forName("com.mysql.jdbc.Driver");
			con=DriverManager.getConnection
					("jdbc:mysql://localhost:3306/busreq","root","India123");
			return con;
		}catch(SQLException e)
		{
			e.printStackTrace();
		}catch(Exception e1) {
			e1.printStackTrace();
		}

		return con;
	}
	/*
	 * this method is used to create a bus pass request
	 * @see org.cap.dao.ILoginDAO#createRequest(org.cap.model.PassRequestForm)
	 */
	@Override
	public PassRequestForm createRequest(PassRequestForm passRequestBean) {
		String sql="insert into buspassrequest1(EmployeeId,firstname,lastname,gender,address,email,dateofjoin,location,pickuploc,pickuptime,status,designation)values(?,?,?,?,?,?,?,?,?,?,?,?)";
		try(PreparedStatement pst = getSQLConnection().prepareStatement(sql);){
			pst.setString(1, passRequestBean.getEmployeeid());
			pst.setString(2, passRequestBean.getFirstname());
			pst.setString(3, passRequestBean.getLastname());
			pst.setString(4, passRequestBean.getGender());
			pst.setString(5, passRequestBean.getAddress());
			pst.setString(6, passRequestBean.getEmail());
			pst.setDate(7, Date.valueOf(passRequestBean.getDoj()));
			pst.setString(8, passRequestBean.getLocation());
			pst.setString(9, passRequestBean.getPickUpLoc());
			pst.setTime(10, Time.valueOf(passRequestBean.getPickUpTime()));

			pst.setString(11, passRequestBean.getStatus());
			pst.setString(12, passRequestBean.getDesignation());

			int count=pst.executeUpdate();
			if(count>0) {
				return passRequestBean;
			}

		}catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	/*
	 *this is used to display all available routes
	 * @see org.cap.dao.ILoginDAO#listAllRoutes()
	 */
	@Override
	public List<Routetable> listAllRoutes() {
		String sql="select * from route_table";
		int routeCount=0;
		try(

				Statement statement=getSQLConnection().createStatement();

				){
			ResultSet resultSet=statement.executeQuery(sql);
			List<Routetable> routeList=new ArrayList<>();
			while(resultSet.next()){
				routeCount++;
				Routetable route=new Routetable();
				populateRoute(route,resultSet);

				routeList.add(route);

			}
			if(routeCount>0){
				return routeList;
			}else{
				return null;
			}

		}catch(SQLException e){
			e.printStackTrace();
		}
		return null;
	}
	private void populateRoute(Routetable route, ResultSet resultSet) throws SQLException {
		route.setRoute_id(resultSet.getInt(1));
		route.setRoute_path(resultSet.getString(2));
		route.setNo_of_seats_occupied(resultSet.getInt(3));
		route.setTotal_seats(resultSet.getInt(4));
		route.setBus_no(resultSet.getString(5));
		route.setDriver_name(resultSet.getString(6));
		route.setTotal_km(resultSet.getDouble(7));


	}
	/*
	 * this is used to add routes to route_table
	 * @see org.cap.dao.ILoginDAO#addRoute(org.cap.model.Routetable)
	 */
	@Override
	public Routetable addRoute(Routetable newroute) {
		String sql="insert into route_table(route_path,no_of_seats,total_seats,bus_no,driver_name,total_km) values(?,?,?,?,?,?)";
		try(PreparedStatement pst = getSQLConnection().prepareStatement(sql);){
			pst.setString(1, newroute.getRoute_path());
			pst.setInt(2, newroute.getNo_of_seats_occupied());
			pst.setInt(3, newroute.getTotal_seats());
			pst.setString(4, newroute.getBus_no());
			pst.setString(5, newroute.getDriver_name());
			pst.setDouble(6, newroute.getTotal_km());

			int n=pst.executeUpdate();
			System.out.println(n);
			if(n>0) {
				System.out.println(n);
				return newroute;
			}
			else {
				return null;
			}



		}catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	/*
	 * this is used to display only employee id whose status of req is pending
	 * @see org.cap.dao.ILoginDAO#PendingReqServlet()
	 */
	@Override
	public List<String> PendingReqServlet() {
		String sql="select EmployeeId from  buspassrequest1 where status='pending'";

		try(Statement statement =getSQLConnection().createStatement();){

			ResultSet rs= statement.executeQuery(sql);
			List<String> empList=new ArrayList<>();
			while(rs.next()) {
				empList.add(rs.getString(1));

			}
			return empList;

		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;

	}
	
	private void populateRoute(PassRequestForm bus, ResultSet rs) throws SQLException {
		bus.setEmployeeid(rs.getString(2));
		bus.setFirstname(rs.getString(3));
		bus.setLastname(rs.getString(4));
		bus.setGender(rs.getString(5));
		bus.setAddress(rs.getString(6));
		bus.setEmail(rs.getString(7));
		
		java.sql.Date sqlDate=rs.getDate(8);
		 java.util.Date utilDate = new java.util.Date(sqlDate.getTime());
		 Instant instant = Instant.ofEpochMilli(utilDate.getTime()); 
		 LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()); 
		 LocalDate localDate = localDateTime.toLocalDate();
	
		bus.setDoj(localDate);

		bus.setLocation(rs.getString(9));
		bus.setPickUpLoc(rs.getString(10));
		Time time=rs.getTime(11);
		LocalTime localTime=time.toLocalTime();
		bus.setPickUpTime(localTime);

		bus.setStatus(rs.getString(12));
		bus.setDesignation(rs.getString(13));




	}
	@Override
	public List<PassRequestForm> pendingDetails() {


		String sql="select * from  buspassrequest1 where status='pending'";
		int pendingCount=0;
		try(

				Statement statement=getSQLConnection().createStatement();

				){
			ResultSet resultSet=statement.executeQuery(sql);
			List<PassRequestForm> pendingList=new ArrayList<>();
			while(resultSet.next()){
				pendingCount++;
				PassRequestForm busBean=new PassRequestForm();
				populateRoute(busBean,resultSet);

				pendingList.add(busBean);

			}
			if(pendingCount>0){
				return pendingList;
			}else{
				return null;
			}

		}catch(SQLException e){
			e.printStackTrace();
		}
		return null;
	}
	








@Override
public List<PassRequestForm> pendingDetailsOfEmp(String empid) {
	String sql="select * from  buspassrequest1 where EmployeeId=?";
	int pendingCount=0;
	try(

			PreparedStatement preparedStatement=getSQLConnection().prepareStatement(sql);

			){
		preparedStatement.setString(1,empid);
		ResultSet resultSet=preparedStatement.executeQuery();
		List<PassRequestForm> pendingList=new ArrayList<>();
		while(resultSet.next()){
			pendingCount++;
			PassRequestForm busBean=new PassRequestForm();
			populateRoute(busBean,resultSet);

			pendingList.add(busBean);

		}
		if(pendingCount>0){
			return pendingList;
		}else{
			return null;
		}

	}catch(SQLException e){
		e.printStackTrace();
	}
	return null;


}
@Override
public Integer transaction(TransactionBean transaction) {
	String sql="insert into transaction(employeeId,transaction_date,calculated_km,monthly_fare,route_id) values(?,?,?,?,?)";
	String sql1="update  buspassrequest1 set status=? where EmployeeId=?";
	String sql2="update route_table set no_of_seats=no_of_seats+1 where route_id=?";
	try(PreparedStatement preparedStatement = getSQLConnection().prepareStatement(sql);
			PreparedStatement preparedStatement2 = getSQLConnection().prepareStatement(sql1);
			PreparedStatement preparedStatement1 = getSQLConnection().prepareStatement("select transaction_id from transaction where employeeId=?");
			PreparedStatement preparedStatement3 = getSQLConnection().prepareStatement(sql2);
			){
		preparedStatement.setString(1,transaction.getEmp_id());
		preparedStatement.setDate(2, Date.valueOf(transaction.getTransaction_date()));
		preparedStatement.setDouble(3, transaction.getTotal_km());
		preparedStatement.setInt(4, transaction.getMonthly_fare());
		preparedStatement.setInt(5, transaction.getRoute_id());
		
		preparedStatement1.setString(1,transaction.getEmp_id());
		preparedStatement2.setString(1,"Approved");
		preparedStatement2.setString(2, transaction.getEmp_id());
		
		preparedStatement3.setInt(1,transaction.getRoute_id());
		
		int n=preparedStatement.executeUpdate();
		
		if(n>0) {
			ResultSet resultSet = preparedStatement1.executeQuery();
			if(resultSet.next()) {
				Integer transaction_id = resultSet.getInt(1);
				int n1=preparedStatement2.executeUpdate();
				int n2=preparedStatement3.executeUpdate();
				if(n1>0 && n2>0)
					return transaction_id;
			}
		}
		
		
	}catch(SQLException e) {
		e.printStackTrace();
	}
	return null;
}
@Override
public List<TransactionBean> monthlyReport(LocalDate fdate, LocalDate tdate) {
	String sql="select * from transaction where transaction_date between ? and ?";
	int tCount=0;
	try(
			
			PreparedStatement preparedStatement=getSQLConnection().prepareStatement(sql);

			){
		preparedStatement.setDate(1,Date.valueOf(fdate));
		preparedStatement.setDate(2,Date.valueOf(tdate));
		
		ResultSet resultSet=preparedStatement.executeQuery();
		List<TransactionBean> tList=new ArrayList<>();
		while(resultSet.next()){
			tCount++;
			TransactionBean tBean=new TransactionBean();
			tBean.setTransaction_id(resultSet.getInt(1));
			tBean.setEmp_id(resultSet.getString(2));
			java.sql.Date sqlDate=resultSet.getDate("transaction_date");
			 java.util.Date utilDate = new java.util.Date(sqlDate.getTime());
			 Instant instant = Instant.ofEpochMilli(utilDate.getTime()); 
			 LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()); 
			 LocalDate localDate = localDateTime.toLocalDate();
			tBean.setTransaction_date(localDate);
			tBean.setTotal_km(resultSet.getDouble(4));
			tBean.setMonthly_fare(resultSet.getInt(5));
			tBean.setRoute_id(resultSet.getInt(6));
			

			tList.add(tBean);
			
		}
		if(tCount>0){
			return tList;
		}else{
			return null;
		}

	}catch(SQLException e){
		e.printStackTrace();
	}
	return null;
	
	
}
}



