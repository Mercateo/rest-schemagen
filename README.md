# rest-schemagen
Jersey add-on for dynamic link and schema building
This add-on is for building HATEOAS - conform responses with your jersey-driven REST - api. Creating and managing your links and schemas of your REST-API becomes as easy as making a method call.


# Quick start
In your jersey configuration simply type:
```java
LinkFactoryResourceConfig.configure(rs); 
```
Where rs is your jersey application ResourceConfig. Pleas choose Jackson as your JSON provider

In your resources you simply type:
```java
@Path("resource")
@Produces(MediaType.APPLICATION_JSON)
public class ResourceClass {
    @Inject
    private LinkMetaFactory linkMetaFactory; //security and baseURIs already injected
 
    @Path("/method/{id}")
    @GET
    @HasInstancePermission(idParameterName = "id", instanceAction = "GET", instanceType = "Something")
    @RolesAllowed("test")
    public ObjectWithSchema<Something> getSomething(@PathParam("id") String id) {
 
        Optional<Link> link = linkMetaFactory
            .createFactoryFor(ResourceClass.class)
            .forCall(Rel.SELF, r -> r.getSomething(id));
 
        return new ObjectWithSchema<Something>(new Something(), link);
    }
}
```


Responses will look like this:
```json
{
  "displayName": "display_name",
  "legalEnityId": "00000000",
  "legalEntityDisplayName": "mercateo",
  "catalogId": "CAT_ID",
  "verNr": 1000,
  "date": 1321892112432,
  "_schema":
    {
      "links":
        [{"href": "http://localhost:8081/catalog.management.rest/cvc/1",
      "rel": "self",
      "targetSchema":
        "{\"type\":\"object\",\"properties\":{\"verNr\":{\"type\":\"integer\"},\"legalEntityDisplayName\":{\"type\":\"string\"},\"catalogId\":{\"type\":\"string\"},\"date\":{\"type\":\"integer\"},\"displayName\":{\"type\":\"string\"},\"legalEnityId\":{\"type\":\"string\"}}}",
      "method": "GET"
       }]
    }
}
```
