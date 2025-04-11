package com.example.demo.controller

import com.example.demo.model.Author
import com.example.demo.service.AuthorService
import com.example.demo.service.BookService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class AuthorController(
    private val authorService: AuthorService,
    private val bookService: BookService
) {
    
    @QueryMapping
    fun authors(): List<Author> = authorService.getAllAuthors()
    
    @QueryMapping
    fun author(@Argument id: String): Author? = authorService.getAuthorById(id)
    
    @MutationMapping
    fun createAuthor(
        @Argument firstName: String,
        @Argument lastName: String
    ): Author {
        val author = Author(
            id = (authorService.getAllAuthors().size + 1).toString(),
            firstName = firstName,
            lastName = lastName
        )
        return authorService.createAuthor(author)
    }
    
    @MutationMapping
    fun updateAuthor(
        @Argument id: String,
        @Argument firstName: String,
        @Argument lastName: String
    ): Author? {
        val author = Author(id, firstName, lastName)
        return authorService.updateAuthor(id, author)
    }
    
    @MutationMapping
    fun deleteAuthor(@Argument id: String): Boolean {
        authorService.deleteAuthor(id)
        return true
    }
    
    @SchemaMapping(typeName = "Author", field = "books")
    fun getBooks(author: Author) = bookService.getBooksByAuthorId(author.id)
} 