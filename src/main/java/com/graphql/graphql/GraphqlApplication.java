package com.graphql.graphql;

import com.graphql.graphql.services.AuthorService;
import com.graphql.graphql.services.BookService;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

import java.io.IOException;

@SpringBootApplication
public class GraphqlApplication {

	@Autowired
	BookService bookService;
	
	@Autowired
	AuthorService authorService;

	public static void main(String[] args) {
		SpringApplication.run(GraphqlApplication.class, args);
	}

	@Bean
	public ConnectionFactoryInitializer connectionFactoryInitializer(ConnectionFactory connectionFactory){
		ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
		initializer.setConnectionFactory(connectionFactory);
		ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator(new ClassPathResource("schema.sql"), new ClassPathResource("Data.sql"));
		initializer.setDatabasePopulator(resourceDatabasePopulator);
		return initializer;
	}

	@Bean
	public GraphQL graphQL() throws IOException {
		SchemaParser schemaParser = new SchemaParser();
		ClassPathResource classPathResource = new ClassPathResource("schema.graphql");
		TypeDefinitionRegistry registry = schemaParser.parse(classPathResource.getInputStream());
		RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
				.type(TypeRuntimeWiring.newTypeWiring("Query").dataFetcher("getBook",bookService.getBook()))
				.type(TypeRuntimeWiring.newTypeWiring("Query").dataFetcher("getBooks",bookService.getBooks()))
				.type(TypeRuntimeWiring.newTypeWiring("Mutation").dataFetcher("createBook",bookService.createBook()))
				.type(TypeRuntimeWiring.newTypeWiring("Book").dataFetcher("author",authorService.authorDataFetcher()))
				.build();

		GraphQLSchema graphQLSchema = new SchemaGenerator().makeExecutableSchema(registry,runtimeWiring);
		return GraphQL.newGraphQL(graphQLSchema).build();
	}


}
