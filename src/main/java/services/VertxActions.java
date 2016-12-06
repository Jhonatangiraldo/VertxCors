package services;

import java.util.LinkedHashMap;
import java.util.Map;

import dao.Whisky;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class VertxActions {
	// Store our product
	private static Map<Integer, Whisky> products = new LinkedHashMap<>();
	private static final String CONTENT_TYPE = "content-type";
	private static final String TYPE_APPLICATION = "application/json; charset=utf-8";


	protected void createSomeWhiskies(){
		Whisky bowmore = new Whisky("Bowmore 15 Years Laimrig", "Scotland, Islay");
		products.put(bowmore.getId(), bowmore);
		Whisky talisker = new Whisky("Talisker 57° North", "Scotland, Island");
		products.put(talisker.getId(), talisker);
	}
	
	protected void getAllWhiskies(RoutingContext routingContext) {
		routingContext.response()
			.putHeader(CONTENT_TYPE, TYPE_APPLICATION)
			.end(Json.encodePrettily(products.values()));
	}
	
	protected void getAllWhiskiesMap(RoutingContext routingContext) {
		routingContext.response()
			.putHeader(CONTENT_TYPE, TYPE_APPLICATION)
			.end(Json.encodePrettily(products));
	}

	protected void addOneWhiskie(RoutingContext routingContext) { 
		final Whisky whisky = Json.decodeValue(routingContext.getBodyAsString(), Whisky.class);
		products.put(whisky.getId(), whisky);
		routingContext.response()
			.setStatusCode(201)
			.putHeader(CONTENT_TYPE, TYPE_APPLICATION)
			.end(Json.encodePrettily(whisky));
	}

	protected void getOneWhiskie(RoutingContext routingContext) {
		final String id = routingContext.request().getParam("id");
		if (id == null) {
			routingContext.response().setStatusCode(400).end();
		} else {
			final Integer idAsInteger = Integer.valueOf(id);
			Whisky whisky = products.get(idAsInteger);
			if (whisky == null) {
				routingContext.response().setStatusCode(404).end();
			} else {
				routingContext.response()
				.putHeader(CONTENT_TYPE, TYPE_APPLICATION)
				.end(Json.encodePrettily(whisky));
			}
		}
	}

	protected void updateOneWhiskie(RoutingContext routingContext) {
		final String id = routingContext.request().getParam("id");
		JsonObject json = routingContext.getBodyAsJson();
		if (id == null || json == null) {
			routingContext.response().setStatusCode(400).end();
		} else {
			final Integer idAsInteger = Integer.valueOf(id);
			Whisky whisky = products.get(idAsInteger);
			if (whisky == null) {
				routingContext.response().setStatusCode(404).end();
			} else {
				whisky.setName(json.getString("name"));
				whisky.setOrigin(json.getString("origin"));
				routingContext.response()
				.putHeader(CONTENT_TYPE, TYPE_APPLICATION)
				.end(Json.encodePrettily(whisky));
			}
		}
	}

	protected void deleteOneWhiskie(RoutingContext routingContext) {
		String id = routingContext.request().getParam("id");
		if (id == null) {
			routingContext.response().setStatusCode(400).end();
		} else {
			Integer idAsInteger = Integer.valueOf(id);
			products.remove(idAsInteger);
		}
		routingContext.response().setStatusCode(204).end();
	}

}
