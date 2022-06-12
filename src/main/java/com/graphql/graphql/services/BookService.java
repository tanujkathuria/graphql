package com.graphql.graphql.services;

import com.graphql.graphql.constant.Category;
import com.graphql.graphql.model.Author;
import com.graphql.graphql.model.Book;
import com.graphql.graphql.repository.AuthorRepository;
import com.graphql.graphql.repository.BookRepository;
import graphql.schema.DataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class BookService {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    AuthorService authorService;


    public DataFetcher<CompletableFuture<Book>> getBook(){
        return env -> {
            String bookId = env.getArgument("id");
            return bookRepository.getBook(UUID.fromString(bookId)).toFuture();
        };
    }

    public DataFetcher<CompletableFuture<List<Book>>> getBooks(){
        return env -> {
            return bookRepository.getBooks().collectList().toFuture();
        };
    }

    public DataFetcher<CompletableFuture<String>> createBook(){
        return env -> {
            String name = env.getArgument("name");
            int pages = env.getArgument("pages");
            String authorName = env.getArgument("authorName");
            Category category = Category.valueOf(env.getArgument("category"));
            int age = env.getArgument("age");
            Book book = new Book();
            book.setName(name);
            book.setPages(pages);
            book.setCategory(category);
            
            return bookRepository.createBook(book).flatMap(
                     bookId -> authorService.createAuthor(authorName,age,bookId).map(authorId -> bookId.toString()))
                     .toFuture();
        };
    }


}
