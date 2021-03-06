package com.cyient.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;

import org.jboss.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.multipart.MultipartFile;
import org.json.JSONObject;

import com.cyient.dao.RFIDDAO;
import com.cyient.exceptions.CustomException;
import com.cyient.model.Customer;
import com.cyient.model.Design;
import com.cyient.model.Executive;
import com.cyient.model.ExecutiveTicketInfo;
import com.cyient.model.ImageWarpper;
import com.cyient.model.Inventory;
import com.cyient.model.Taginformation;
import com.cyient.model.Ticketing;
import com.cyient.model.User;
import com.cyient.model.sensor_data;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mchange.v2.c3p0.stmt.GooGooStatementCache;
import com.mysql.jdbc.Blob;

@RestController
@RequestMapping("/Ticketing")
public class RIFDRestController {
	@Autowired
	private RFIDDAO rfidDAO;
	// add code for the "/hello" endpoint
	private static final Logger logger = Logger
			.getLogger(RIFDRestController.class);



	JSONParser parser = new JSONParser();
	Gson gson = new Gson();


	//String Error = "{ \"status\" : \"Authentication Error \"}";

	JSONObject Success_string =  new JSONObject();

	JSONObject Error = new JSONObject();
	//JSONObject Success_string = new JSONObject();

	public RIFDRestController()
	{
		Error.put("status", "Authentication Error");
		Success_string.put("status", "Customer Added");
	}


	@GetMapping(path="/getTickets",produces = "application/json")
	public String  getTickets(@RequestHeader("secret-key") String secretkey,@RequestHeader("company-id") String companyid) throws ParseException 
	{

		if(rfidDAO.Authentication(companyid, secretkey)==true)
		{

			List<Ticketing> Ticket_list = rfidDAO.getOpenTicketsData();

			//JSONArray json = (JSONArray) parser.parse();

			return gson.toJson(Ticket_list);
		}
		else

		{
			return Error.toString();
		}
	}



	@GetMapping(path="/getTickets_exec/{id}",produces = "application/json")
	public String getTickets_based_on_Executive(@PathVariable String id)
	{
		return gson.toJson(rfidDAO.getTickets_based_on_Executive(id));
	}

	@GetMapping(path="/getTickets_Executive",produces = "application/json")
	public String getTickets_based_on_Executive_header(@RequestHeader("Executive-Id") String Executive_Id,@RequestHeader("secret-key") String secretkey,@RequestHeader("company-id") String companyid)
	{
		if(rfidDAO.Authentication(companyid, secretkey)==true)
		{					
			return gson.toJson(rfidDAO.getTickets_based_on_Executive(Executive_Id));
		}
		else
		{
			return Error.toString();
		}
	}

	
	
	@GetMapping(path="/getTickets_Executive_data",produces = "application/json")
	public String getTickets_based_on_Executive_header_data(@RequestHeader("Executive-Id") String Executive_Id) throws ParseException
	{
			
			JSONObject tickets_object = new JSONObject();
			JSONArray final_array = new JSONArray(); 
			List<ExecutiveTicketInfo> tickets= rfidDAO.getTickets_based_on_Executive(Executive_Id);
			for(int i=0;i<tickets.size();i++)
			{
				JSONArray ticketsarray = new JSONArray();
				ticketsarray.add(tickets.get(i));
				ticketsarray.add(rfidDAO.getCustomer(tickets.get(i).getCustomer().getCustomerId()).get(0));
				ticketsarray.add(rfidDAO.getDesign(tickets.get(i).getCustomer().getCustomerId()).get(0));
				ticketsarray.add(rfidDAO.getInventory(tickets.get(i).getCustomer().getCustomerId()).get(0));
				tickets_object.put(tickets.get(i).getTicketNum(), ticketsarray);
				//objarray.add(ticketsarray);

			}
			final_array.add(tickets_object);
			return final_array.toString();		
	}	
	
	
	
	@GetMapping(path="/getCustomerIds_Executive",produces = "application/json")
	public String getCustomerIds_Executive(@RequestHeader("Executive-Id") String Executive_Id) throws ParseException
	{
			
		JSONObject tickets_object = new JSONObject();
		JSONArray final_array = new JSONArray(); 
		String customerid=rfidDAO.getTickets_based_on_Executive(Executive_Id).get(0).getCustomer().getCustomerId();

			JSONArray finalObject = new JSONArray();
			List<Object> modelData = new ArrayList<Object>();


			modelData.add(rfidDAO.getCustomer(customerid).get(0));
			modelData.add(rfidDAO.getDesign(customerid).get(0));
			modelData.add(rfidDAO.getInventory(customerid).get(0));

					return gson.toJson(modelData);	
				
	}	
	
	
	
	
	
	
	
	
	@GetMapping(path="/getInventory",produces = "application/json")
	public String getInventory(@RequestHeader("customer-id") String customerid,@RequestHeader("secret-key") String secretkey,@RequestHeader("company-id") String companyid)
	{
		if(rfidDAO.Authentication(companyid, secretkey)==true)
		{
			return gson.toJson(rfidDAO.getInventory(customerid));
		}
		else
		{
			return Error.toString();
		}
	}


	@GetMapping(path="/getDesign",produces = "application/json")
	public String getDesign(@RequestHeader("customer-id") String customerid,@RequestHeader("secret-key") String secretkey,@RequestHeader("company-id") String companyid)
	{
		if(rfidDAO.Authentication(companyid, secretkey)==true)
		{
			return gson.toJson(rfidDAO.getDesign(customerid));
		}
		else
		{
			return Error.toString();
		}
	}

	@GetMapping(path="/getCustomer",produces = "application/json")
	public String getCustomer(@RequestHeader("customer-id") String customerid,@RequestHeader("secret-key") String secretkey,@RequestHeader("company-id") String companyid)
	{
		if(rfidDAO.Authentication(companyid, secretkey)==true)
		{
			return gson.toJson(rfidDAO.getCustomer(customerid));
		}
		else
		{
			return Error.toString();
		}
	}

	@GetMapping(path="/Authenticate_FE",produces = "application/json")
	public String getAuthenticate_FE(@RequestHeader("username") String username,@RequestHeader("password") String password,@RequestHeader("secret-key") String secretkey,@RequestHeader("company-id") String companyid)
	{
		if(rfidDAO.Authentication(companyid, secretkey)==true)
		{
			List<User> Authenticate_Data = rfidDAO.getAuthenticate_FE(username,password);
			JSONObject Success = new JSONObject();
			JSONObject failure = new JSONObject();

			Success.put("status","Valid Credentials");
			failure.put("status","Invalid Credentials");

			if(Authenticate_Data.size()==0)
			{
				return failure.toString();
			}
			else
			{
				return Success.toString();
	
			}
		}
		else
		{
			JSONObject obj = new JSONObject();
			obj.put("status", "Authentication Error");
			return obj.toString();
		}
	}

	@GetMapping(path="/Authenticate",produces = "application/json")
	public String getAuthenticate(@RequestHeader("username") String username,@RequestHeader("password") String password,@RequestHeader("secret-key") String secretkey,@RequestHeader("company-id") String companyid) throws ParseException
	{
		if(rfidDAO.Authentication(companyid, secretkey)==true)
		{
			List<User> Authenticate_Data = rfidDAO.getAuthenticate_FE(username,password);
			//List<Device_Assigned_to_Technician> Authenticate_Device = rfidDAO.getAuthenticate_FE(username,password);

			JSONObject Success = new JSONObject();
			JSONObject failure = new JSONObject();

			Success.put("status","Valid Credentials");
			failure.put("status","Invalid Credentials");

			if(Authenticate_Data.size()==0)
			{
				return failure.toString();
			}
			else
			{


				/*List<ExecutiveTicketInfo> list_demo =  rfidDAO.getTickets_based_on_Executive(username);
				System.out.println(username+"Kiran");

				System.out.println(list_demo.get(0).getTicketType());
				JSONParser parser = new JSONParser(); 
				JSONArray json = (JSONArray) parser.parse(gson.toJson(rfidDAO.getTickets_based_on_Executive(username)));
				System.out.println(json.get(0));
				JSONObject jobj = new JSONObject(list_demo.get(0).getCustomerId());
				jobj.put("ticketType", list_demo.get(0).getTicketType());
				System.out.println(jobj+" customer object");
				JSONObject objofobj = new JSONObject(json.get(0));
				objofobj.remove("customerId");
				System.out.println(objofobj);
				objofobj.append("customerId", jobj);
				System.out.println("after"+objofobj);*/

				return gson.toJson(rfidDAO.getTickets_based_on_Executive(username));
				//return Success.toString();
			}
		}
		else
		{
			JSONObject obj = new JSONObject();
			obj.put("status", "Authentication Error");
			return obj.toString();
		}
	}




	@PostMapping(path = "/add_customer", consumes = "application/json", produces = "application/json")
	public String addMember(@RequestBody Customer customer,@RequestHeader("secret-key") String secretkey,@RequestHeader("company-id") String companyid) throws ParseException
	{
		//code
		if(rfidDAO.Authentication(companyid, secretkey)==true)
		{
			String s= rfidDAO.save_customer(customer);	

			return Success_string.toString();
		}
		else
		{
			return Error.toString();
		}
	}


	@PostMapping(path = "/update_customer", consumes = "application/json", produces = "application/json")
	public String update_customer(@RequestBody Customer customer,@RequestHeader("secret-key") String secretkey,@RequestHeader("company-id") String companyid) throws ParseException
	{
		JSONObject status = new JSONObject();
		//code

		if(rfidDAO.Authentication(companyid, secretkey)==true)
		{
			if(rfidDAO.update_customer(customer)==true)	
				status.put("status","Customer Data updated successfully");
			return status.toString();
		}
		else
		{
			return Error.toString();
		}
	}

	@PostMapping(path = "/update_data", consumes = "application/json", produces = "application/json")
	public String update_customer_header(@RequestBody List <Object> stuffs,@RequestHeader("customer-id") String customerid,@RequestHeader("ticket-id") String ticketid,@RequestHeader("ticket_type") String ticket_type) throws ParseException
	{
		JSONObject status = new JSONObject();

			try{
				final ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper
				final Customer customer = mapper.convertValue(stuffs.get(0), Customer.class);
				System.out.println(rfidDAO.update_customer(customer));

				final Design desgine = mapper.convertValue(stuffs.get(1), Design.class);		
				System.out.println(rfidDAO.update_design(desgine, customerid));
				System.out.println(stuffs.get(2));
				final Inventory inventory = mapper.convertValue(stuffs.get(2), Inventory.class);					
				System.out.println(rfidDAO.update_inventory(inventory,customerid));
				final ImageWarpper image = mapper.convertValue(stuffs.get(3), ImageWarpper.class);
						
				status.put("status","Data updated successfully");
				
				Taginformation Tag_data = new Taginformation();
				if(ticket_type.equals("New"))
				{
					Tag_data.setCustomerid(customer.getCustomerId());
					//Tag_data.setFiledata(filedata);
					//Tag_data.setFileName(fileName);
					Tag_data.setStartpoint(desgine.getStartPoint());
					Tag_data.setTaguniqueid(desgine.getStartPoint()+"-"+desgine.getEndPoint()+"-"+desgine.getRack()+customerid);
					Tag_data.setTicketid(ticketid);
					Tag_data.setCusjon(gson.toJson(customer));
					Tag_data.setDesJson(gson.toJson(desgine));
					Tag_data.setInvjson(gson.toJson(inventory));
					rfidDAO.insert_taginformation(Tag_data);
				}
				else
				{
					List<Taginformation> old_unqid = rfidDAO.get_taginformation(customerid);
					rfidDAO.delete_and_insert_taginformation(customerid);
					Tag_data.setCustomerid(customer.getCustomerId());
					Tag_data.setFiledata(image.getData());
					Tag_data.setFileName(ticketid+".jpeg");
					Tag_data.setStartpoint(desgine.getStartPoint());
					Tag_data.setTaguniqueid(desgine.getStartPoint()+"-"+desgine.getEndPoint()+"-"+desgine.getRack()+customerid);
					Tag_data.setTicketid(ticketid);
					Tag_data.setCusjon(gson.toJson(customer));
					Tag_data.setDesJson(gson.toJson(desgine));
					Tag_data.setInvjson(gson.toJson(inventory));
					Tag_data.setPre_uniq(old_unqid.get(0).getTaguniqueid());
					rfidDAO.insert_taginformation(Tag_data);
				}							
			}
			catch(Exception e)
			{
				status.put("status","Failed to update ");
			}
	
	
		return status.toString();
	}


	
	@PostMapping(path = "/new_update_data", consumes = "application/json", produces = "application/json")
	public String new_update_data(@RequestBody List <Object> stuffs,@RequestHeader("customer-id") String customerid,@RequestHeader("ticket-id") String ticketid,@RequestHeader("ticket_type") String ticket_type) throws ParseException
	{
		JSONObject status = new JSONObject();

			try{
				
				final ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper
				final Customer customer = mapper.convertValue(stuffs.get(0), Customer.class);
				System.out.println(rfidDAO.update_customer(customer));

				final Design desgine = mapper.convertValue(stuffs.get(1), Design.class);		
				System.out.println(rfidDAO.update_design(desgine, customerid));
				System.out.println(stuffs.get(2));
				final Inventory inventory = mapper.convertValue(stuffs.get(2), Inventory.class);					
				System.out.println(rfidDAO.update_inventory(inventory,customerid));
				final ImageWarpper image = mapper.convertValue(stuffs.get(3), ImageWarpper.class);
				
				
				/*
				//final ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper
				final Customer customer = rfidDAO.getCustomer(customerid).get(0);
				//System.out.println(rfidDAO.update_customer(customer));

				final Design desgine =  rfidDAO.getDesign(customerid).get(0);		
				//System.out.println(rfidDAO.update_design(desgine, customerid));
				//System.out.println(stuffs.get(2));
				final Inventory inventory = rfidDAO.getInventory(customerid).get(0);	
				//System.out.println(rfidDAO.update_inventory(inventory,customerid));
				//status.put("status","Data updated successfully");
				*/

				
				Taginformation Tag_data = new Taginformation();
				if(ticket_type.equals("New"))
				{
					Tag_data.setCustomerid(customer.getCustomerId());
					Tag_data.setFiledata(image.getData());
					Tag_data.setFileName(ticketid+".jpeg");
					Tag_data.setStartpoint(desgine.getStartPoint());
					Tag_data.setTaguniqueid(desgine.getStartPoint()+"-"+desgine.getEndPoint()+"-"+desgine.getRack()+customerid);
					Tag_data.setTicketid(ticketid);
					Tag_data.setCusjon(gson.toJson(customer));
					Tag_data.setDesJson(gson.toJson(desgine));
					Tag_data.setInvjson(gson.toJson(inventory));
					rfidDAO.insert_taginformation(Tag_data);
				}
							
			}
			catch(Exception e)
			{
				status.put("status","Failed to update ");
			}
		
	
		return status.toString();
	}	
	
	
	@GetMapping(path="/getData",produces = "application/json")
	public String getData(@RequestHeader("customer-id") String customerid)
	{
	JSONArray finalObject = new JSONArray();
	List<Object> modelData = new ArrayList<Object>();


	modelData.add(rfidDAO.getCustomer(customerid).get(0));
	modelData.add(rfidDAO.getDesign(customerid).get(0));
	modelData.add(rfidDAO.getInventory(customerid).get(0));

			return gson.toJson(modelData);
		
	}
	
	
	
	
	@PostMapping(path = "/update_design", consumes = "application/json", produces = "application/json")
	public String update_design(@RequestBody Design design,@RequestHeader("secret-key") String secretkey,@RequestHeader("company-id") String companyid,@RequestHeader("customer-id") String customerid) throws ParseException
	{
		JSONObject status = new JSONObject();
		//code
		/*List<Design> old_object=rfidDAO.getDesign(customerid);
		String NewUnq_ID = "";
		String Old_ID = "";
		if(old_object.get(0).getStartPoint().equals(design.getStartPoint()) & old_object.get(0).getEndPoint().equals(design.getEndPoint()) & old_object.get(0).getRack().equals(design.getRack()))
		{
			Old_ID= design.getStartPoint()+"-"+design.getEndPoint()+"-"+design.getRack()+design.getCustomerId();
		}
		else
		{
			NewUnq_ID= old_object.get(0).getStartPoint()+"-"+old_object.get(0).getEndPoint()+"-"+old_object.get(0).getRack()+design.getCustomerId();
		}

*/

		if(rfidDAO.Authentication(companyid, secretkey)==true)
		{
			if(rfidDAO.update_design(design,customerid)==true)	
				status.put("status","Design Data updated successfully");
			return status.toString();
		}
		else
		{
			return Error.toString();
		}
	}



	@PostMapping(path = "/update_inventory", consumes = "application/json", produces = "application/json")
	public String update_inventory(@RequestBody Inventory inventory,@RequestHeader("secret-key") String secretkey,@RequestHeader("customer-id") String customerid,@RequestHeader("company-id") String companyid) throws ParseException
	{
		JSONObject status = new JSONObject();
		//code

		if(rfidDAO.Authentication(companyid, secretkey)==true)
		{
			if(rfidDAO.update_inventory(inventory,customerid)==true)	
				status.put("status","Inventory Data updated successfully");
			return status.toString();
		}
		else
		{
			return Error.toString();
		}
	}

	/*@GetMapping(path = "/update_ticket", produces = "application/json")
	public String update_ticket(@RequestHeader("ticket-id") String ticketid) throws ParseException
	{
		JSONObject status = new JSONObject();

		if(rfidDAO.update_ticket(ticketid,"Closed")==true)
		{
			status.put("status", "Ticket Closed");
		}
		else
		{
			status.put("status", "Ticket Not Closed");
		}

		return status.toString();
	}*/
	
	
	@GetMapping(path = "/ticket_status", produces = "application/json")
	public String reject_ticket(@RequestHeader("ticket-id") String ticketid,@RequestHeader("status") String status_msg,@RequestHeader("Executive-Id") String Executive_Id) throws ParseException
	{
		JSONObject status = new JSONObject();

		if(rfidDAO.update_ticket(ticketid,status_msg,Executive_Id)==true)
		{
			status.put("status", "Ticket status updated");
		}
		else
		{
			status.put("status", "Sorry Contact your Admin");
		}

		return status.toString();
	}
	
	@GetMapping(path = "/ticket_comments", produces = "application/json")
	public String ticket_comments(@RequestHeader("ticket-id") String ticketid,@RequestHeader("comments") String status_msg,@RequestHeader("Executive-Id") String Executive_Id) throws ParseException
	{
		JSONObject status = new JSONObject();

		if(rfidDAO.update_comment(ticketid,status_msg,Executive_Id)==true)
		{
			status.put("status", "Ticket Comments updated");
		}
		else
		{
			status.put("status", "Sorry Contact your Admin");
		}

		return status.toString();
	}

	/*	@PostMapping(path = "/update_ticket", consumes = "application/json", produces = "application/json")
	public String update_ticket(@RequestBody JSONArray data,@RequestHeader("secret-key") String secretkey,@RequestHeader("company-id") String companyid) throws ParseException
	{
		JSONObject status = new JSONObject();
		//code

		if(rfidDAO.Authentication(companyid, secretkey)==true)
		{

		}
		else
		{
			return Error.toString();

		}

			//if(rfidDAO.update_inventory(inventory)==true)	
			//status.put("status","Inventory Data updated successfully");			
			return data;


	}*/

	@PostMapping(path = "/upload_image", produces = "application/json")
	public String upload_file(@RequestBody ImageWarpper file,@RequestHeader("secret-key") String secretkey,@RequestHeader("company-id") String companyid,@RequestHeader("ticket-id") String ticketid) throws ParseException, IOException
	{
		JSONObject status = new JSONObject();
		try{
			if(rfidDAO.Authentication(companyid, secretkey)==true)
			{

				if( rfidDAO.upload_file(file.getData(),ticketid)==true)
				{
					System.out.println(file);
					status.put("status", "File uploaded");
				}
				else
				{
					status.put("status", "Image not updated");
				}
			}
			else
			{
				return Error.toString();
			}
		}
		catch(Exception e)
		{
			status.put("status", "Image not updated");
		}
		return status.toString();
	}



	@GetMapping("/test_header")
	public String headertest() 
	{
		List<ExecutiveTicketInfo> temp = rfidDAO.getTickets_based_on_Executive("Holly");

		return temp.toString();
	}

	@GetMapping("/recieve_ticket/{ticketid}/{region}/{timestamp}")
	public String recieve_ticket(@PathVariable("ticketid") String ticketid,@PathVariable("region") String region,@PathVariable("timestamp") long timestamp) 
	{	
		
		try {
			return rfidDAO.sendEmail(rfidDAO.get_mail(region).get(0).getEmailId(),ticketid);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Failed";
		}
	}
	

	
	/*@PostMapping(path="/update_data",consumes = "application/json")
	public String update_data(@Valid @RequestBody List <sensor_data> stuffs) {
		//Rest_Response response = new Rest_Response();		
		System.out.println("RasbiData");
		System.out.println(gson.toJson(stuffs));
        return "Done"; 
	}*/
}
