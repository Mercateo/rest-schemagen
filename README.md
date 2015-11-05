# rest-schemagen
Jersey add-on for dynamic link and schema building
This add-on is for building HATEOAS - conform responses with your jersey-driven REST - api. Creating and managing your links and schemas of your REST-API becomes as easy as making a method call.

It also works for reverse proxies (like jetty behind apache/ngix)


# Quick start
In your jersey configuration simply type:
```java
LinkFactoryResourceConfig.configure(rs); 
```
Where rs is your jersey application ResourceConfig. Please choose Jackson as your JSON provider

In your resources you simply type:
```java
@Path("resource")
@Produces(MediaType.APPLICATION_JSON)
public class ResourceClass {
    @Inject
    private LinkMetaFactory linkMetaFactory; //security and baseURIs already injected
 
    @Path("/method/{id}")
    @GET
    @RolesAllowed("test")
    public ObjectWithSchema<Something> getSomething(@PathParam("id") String id) {
 
        Optional<Link> link = linkMetaFactory
            .createFactoryFor(ResourceClass.class)
            .forCall(Rel.SELF, r -> r.getSomething(id));
 
        final JsonHyperSchema hyperSchema = JsonHyperSchema.fromOptional(result);
        return ObjectWithSchema.create(json, hyperSchema);
    }
}
```
or if you have subresources:
```java
Optional<Link> link = linkMetaFactory
    .createFactoryFor(ParentResourceClass.class)
    .subResource(p -> p.getSubResourceInParentResource()), ResourceClass.class)
    .subResource(p -> p.getSubResourceInResource()), SubResourceClass.class)
    .forCall(Rel.SELF, r -> r.getSomething(id));
```
Note, that all calls have properly eyped return values, so you have code completion, call hierarchy and all other feature you are used to have in your IDE.

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
# Plugins
There are three possibilities to customize the way the schema generation works. All of them are located in the plugin package

## Determine if a link should be made out of a method
You should bring you own implementation of the MethodCheckerForLink interface and bind it with your own factory. A common use case would be if you want to check securoty roles. This usecase is in the included in the package.

## Determine if a field should be included in the schema
You should bring you own implementation of the FieldCheckerForSchema interface and bind it with your own factory. A common use case is included in the package. There you do the filtering with jackson's JsonView annotation. In the future, we plan to do the filtering based on the roles of a user. This feature depends on https://java.net/jira/browse/JERSEY-2998.

## Do the mapping of a field by your own
Provide a IndividualSchemaGenerator with the PropertySchema annotation at the desired field.
