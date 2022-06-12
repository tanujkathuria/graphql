package com.graphql.graphql.services;

import com.graphql.graphql.model.Author;
import com.graphql.graphql.model.Book;
import com.graphql.graphql.repository.AuthorRepository;
import graphql.schema.DataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class AuthorService {

    @Autowired
    AuthorRepository authorRepository;


    public Mono<String> createAuthor(String authorName, int age, UUID bookId){
        Author author = new Author();
        author.setName(authorName);
        author.setBookId(bookId);
        author.setAge(age);
        return authorRepository.createAuthor(author).map(Objects::toString);
    }

    public DataFetcher<CompletableFuture<Author>> authorDataFetcher(){
        return (env) -> {
           Book book = env.getSource();
           return authorRepository.getAuthor(book.getId()).toFuture();
        };

    }

}
