package services;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;


public class VertxService extends AbstractVerticle {
	VertxActions vertxActions;
	
	public static void main(String[]args) throws Exception{
		Vertx vertx = Vertx.vertx();
	    vertx.deployVerticle(VertxService.class.getName());
	    new VertxService();
	}
	
	private void enableCors(Router router){
		CorsHandler corsHandler = CorsHandler.create("http://localhost:8090");
		corsHandler.allowedMethod(HttpMethod.GET);
		corsHandler.allowedMethod(HttpMethod.POST);
		corsHandler.allowedMethod(HttpMethod.PUT);
		corsHandler.allowedMethod(HttpMethod.DELETE);
		corsHandler.allowedMethod(HttpMethod.OPTIONS);
		corsHandler.allowCredentials(true);
		corsHandler.allowedHeader("Authorization");
		corsHandler.allowedHeader("Content-Type");
		corsHandler.allowedHeader("Access-Control-Allow-Origin");
		corsHandler.allowedHeader("Access-Control-Allow-Headers");
		corsHandler.allowedHeader("Access-Control-Allow-Method");
		corsHandler.allowedHeader("Access-Control-Allow-Credentials");
		router.route().handler(corsHandler);
	}
	
	@Override
	public void start(Future<Void> future) {
		vertxActions = new VertxActions();
		vertxActions.createSomeWhiskies();
	
		Router router = Router.router(vertx);
		this.enableCors(router);
		
		//enables the reading of the request body(json data) for all routes under "/api/whiskies"
		router.route("/api/whiskies*").handler(BodyHandler.create());
		// Serve static resources from the /assets directory
		router.route("/assets/*").handler(StaticHandler.create("assets"));
	 
		router.get("/api/whiskies").handler(vertxActions::getAllWhiskies);
		router.get("/api/whiskiesMap").handler(vertxActions::getAllWhiskiesMap);
	 
		// maps POST requests on /api/whiskies to the addOne method
		router.post("/api/whiskies").handler(vertxActions::addOneWhiskie);
		 
		final String whiskiesId = "/api/whiskies/:id";
		router.get(whiskiesId).handler(vertxActions::getOneWhiskie);
		router.put(whiskiesId).handler(vertxActions::updateOneWhiskie);
		router.delete(whiskiesId).handler(vertxActions::deleteOneWhiskie);
	 
		
		 router.route("/").handler(routingContext -> {
		   HttpServerResponse response = routingContext.response();
		   response
		       .putHeader("content-type", "text/html")
		       .end("<h1>Hello from my first Vert.x 3 application</h1>");
		 });
	 

	
		 // Create the HTTP server and pass the "accept" method to the request handler.
		 vertx
		     .createHttpServer()
		     .requestHandler(router::accept)
		     .listen(
		         // Retrieve the port from the configuration, default to 8080.
		         config().getInteger("http.port", 8080),
		         result -> {
		           if (result.succeeded()) {
		             future.complete();
		           } else {
		             future.fail(result.cause());
		           }
		         }
		     );
		}
}
