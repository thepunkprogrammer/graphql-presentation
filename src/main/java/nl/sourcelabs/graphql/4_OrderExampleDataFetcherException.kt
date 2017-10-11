package nl.sourcelabs.graphql

import graphql.GraphQL
import graphql.Scalars
import graphql.schema.*

// graphql-java DataFetcher example with Exception in DataFetcher, see: SimpleDataFetcherExceptionHandler
class OrderExampleDataFetcherException {

    // Define a plain GraphQL Java DataFetcher
    class OrderDataFetcher : DataFetcher<Order> {
        override fun get(env: DataFetchingEnvironment): Order = throw RuntimeException("something is really wrong here!")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            // Construct a schema from code
            val orderType = GraphQLObjectType.newObject()
                    .name("Order")
                    .field(GraphQLFieldDefinition.newFieldDefinition()
                            .name("totalPrice")
                            .type(Scalars.GraphQLString)
                    )
                    .build()
            val queryType = GraphQLObjectType.newObject()
                    .name("Query")
                    .field(GraphQLFieldDefinition.newFieldDefinition()
                            .name("orderById")
                            .type(orderType)
                            .argument(GraphQLArgument.newArgument()
                                    .name("id")
                                    .type(Scalars.GraphQLInt)
                            )
                            .dataFetcher(OrderDataFetcher()) // This defines how the data is fetched for field orderById
                    )
                    .build()
            // Create the executable schema
            val graphQLSchema: GraphQLSchema = GraphQLSchema.newSchema().query(queryType).build()
            // Build GraphQL
            val graphQL = GraphQL.newGraphQL(graphQLSchema).build()
            // Execute a query
            val executionResult = graphQL.execute("""
                {
                    order(id: 123) {
                        totalPrice
                    }
                }
                """)
            println(executionResult.toSpecification())
        }
    }
}