# rest-schemagen
[![Build Status](https://travis-ci.org/Mercateo/rest-schemagen.svg?branch=master)](https://travis-ci.org/Mercateo/rest-schemagen)
[![Coverage Status](https://coveralls.io/repos/Mercateo/rest-schemagen/badge.svg?branch=master&service=github)](https://coveralls.io/github/Mercateo/rest-schemagen?branch=master)


Jersey add-on for dynamic link and schema building.
This add-on is for building HATEOAS-conform responses with your jersey REST-API. Creating and managing your links and schemas of your REST-API becomes as easy as making a method call.

It also works for reverse proxies (like jetty behind apache/nginx)


# Quick start
You can find the latest artifact on [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22common.rest.schemagen%22).

In your jersey configuration simply type:
```java
LinkFactoryResourceConfig.configure(rs); 
```
Where "rs" is your jersey application ResourceConfig. Please choose Jackson as your JSON provider

In your resources you simply type:
```java
@Path("resource")
@Produces(MediaType.APPLICATION_JSON)
public class ResourceClass {
    @Inject
    private LinkMetaFactory linkMetaFactory; // security and baseURIs already injected
 
    @Path("/method/{id}")
    @GET
    @RolesAllowed("test")
    public ObjectWithSchema<Something> getSomething(@PathParam("id") String id) {
 
        Optional<Link> link = linkMetaFactory
            .createFactoryFor(ResourceClass.class)
            .forCall(Rel.SELF, r -> r.getSomething(id));
 
        final JsonHyperSchema hyperSchema = JsonHyperSchema.fromOptional(link);
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
Note, that all calls have properly typed return values. So you get code completion, call hierarchy and all other features you are used to have in your IDE.

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
        [{
            "href": "http://localhost:8081/catalog.management.rest/cvc/1",
            "rel": "self",
            "targetSchema":
                {
                    "type":"object",
                    "properties":{
                        "verNr":{"type":"integer"},
                        "legalEntityDisplayName":{"type":"string"},
                        "catalogId":{"type":"string"},
                        "date":{"type":"integer"},
                        "displayName":{"type":"string"},
                        "legalEnityId":{"type":"string"}
                    }
                },
            "method": "GET"
       }]
    }
}
```
# Allowed and default values
There is a way to manipulate allowed and default values. The central class is CallContext. With CallContext you can also bring custom values to your plugins (see next section). The following sample shows how to bring dynamic computed default/allowed values to an address (based e.g. on the country the current user comes from).

```java
Optional<Link> link = Optional.empty();
 
final Parameter.Context context = Parameter.createContext();
 
final Parameter.Builder<AddressJson> addressJsonBuilder = context.builderFor(AddressJson.class) //
        .allowValues(getAllowedAddressTypes());
 
if (!addressJsonBuilder.isEmpty()) {
    final Parameter<AddressJson> addressJson = addressJsonBuilder //
            .allowValues(getAvailableContryCodes())
            .defaultValue(getDefaultAddressValue()).build();
 
    link = linkFactoryForAddressResource.forCall(Rel.CREATE, r -> r.createAddress(addressJson.get()),
            context);
}
```
The context and the parameter, which is build by context.builderFor() are still connected.

# Plugins
There are three possibilities to customize the way the schema generation works. All of them are located in the plugin package.

### Determine if a link should be made from a method
You should bring you own implementation of the MethodCheckerForLink interface and bind it with your own factory. A common use-case would be if you want to check security roles. The implementation can be found in the plugin package.

### Determine if a field should be included in the schema
You should bring you own implementation of the FieldCheckerForSchema interface and bind it with your own factory. A common use-case is included in the package. There, you do the filtering with jackson's JsonView annotation. In the future, we plan to do the filtering based on the roles of a user. This feature depends on https://java.net/jira/browse/JERSEY-2998.

### Mapping of a field
Provide an IndividualSchemaGenerator with the PropertySchema annotation at the desired field.

# Creating your own relations
If you need your own relations you can do the following:
```java
public enum OwnRel implements RelationContainer {
 
    FOO, BAR(RelType.OTHER);
 
    private final Relation relation;
 
    OwnRel() {
        this(RelType.SELF);
    }
 
    OwnRel(RelType type) {
        relation = Relation.of(this, type);
    }
 
    public Relation getRelation() {
        return relation;
    }
}
```
