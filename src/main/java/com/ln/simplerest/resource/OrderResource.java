package com.ln.simplerest.resource;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ln.simplerest.model.Order;
import com.ln.simplerest.service.OrderService;
import com.ln.simplerest.support.MyJob;
import com.ln.simplerest.support.QuartzManager;

@Singleton
@Path("orders")
public class OrderResource extends AbstractResource {

	public static String JOB_NAME = "动态任务调度";
	public static String TRIGGER_NAME = "动态任务触发器";
	public static String JOB_GROUP_NAME = "XLXXCC_JOB_GROUP";
	public static String TRIGGER_GROUP_NAME = "XLXXCC_JOB_GROUP";

	@Inject
	private OrderService orderService;

	@Inject
	private QuartzManager quartzManager;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllOrder() {
		try {
			List<Order> orderList = orderService.listAllOrder();
			return Response.ok(orderList).build();
		} catch (Exception e) {
			Map<String, String> errorEntity = getResponseEntity("error", e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorEntity).build();
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addOrder(Order order) {
		try {
			orderService.saveOrder(order);
			Map<String, String> result = getResponseEntity("orderId", order.getId());
			return Response.ok().entity(result).build();
		} catch (Exception e) {
			Map<String, String> errorEntity = getResponseEntity("error", e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorEntity).build();
		}
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrderById(@PathParam("id") String id) {
		try {
			Order order = orderService.getById(id);
			return Response.ok(order).build();
		} catch (Exception e) {
			Map<String, String> errorEntity = getResponseEntity("error", e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorEntity).build();
		}
	}

	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeOrder(@PathParam("id") String id) {
		try {
			int rows = orderService.removeOrder(id);
			if (rows > 0) {
				return Response.ok().build();
			}
			return Response.status(Response.Status.NOT_FOUND).build();
		} catch (Exception e) {
			Map<String, String> errorEntity = getResponseEntity("error", e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorEntity).build();
		}
	}

	@GET
	@Path("/test/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response testQuartz(@PathParam("id") String id) {
		try {
System.out.println("test........");
			System.out.println("【系统启动】开始(每1秒输出一次)...");
			quartzManager.addJob(JOB_NAME, JOB_GROUP_NAME, TRIGGER_NAME, TRIGGER_GROUP_NAME, MyJob.class, "0/1 * * * * ?");
			return Response.ok().build();
		} catch (Exception e) {
			Map<String, String> errorEntity = getResponseEntity("error", e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorEntity).build();
		}
	}

}
