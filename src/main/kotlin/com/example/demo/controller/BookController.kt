package com.example.demo.controller

import com.example.demo.model.Book
import com.example.demo.service.AuthorService
import com.example.demo.service.BookService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class BookController(
    private val bookService: BookService,
    private val authorService: AuthorService
) {
    
    @QueryMapping
    fun hello(): String = "Hello, GraphQL!"
    
    @QueryMapping
    fun books(): List<Book> = bookService.getAllBooks()
    
    @QueryMapping
    fun book(@Argument id: String): Book? = bookService.getBookById(id)
    
    @MutationMapping
    fun createBook(@Argument("input") input: BookInput): Book {
        val book = Book(
            id = (bookService.getAllBooks().size + 1).toString(),
            title = input.title,
            authorId = input.authorId
        )
        return bookService.createBook(book)
    }
    
    @MutationMapping
    fun updateBook(
        @Argument id: String,
        @Argument("input") input: BookInput
    ): Book? {
        val book = Book(id, input.title, input.authorId)
        return bookService.updateBook(id, book)
    }
    
    @MutationMapping
    fun deleteBook(@Argument id: String): Boolean {
        bookService.deleteBook(id)
        return true
    }
    
    @SchemaMapping(typeName = "Book", field = "author")
    fun getAuthor(book: Book) = authorService.getAuthorById(book.authorId)
}

data class BookInput(
    val title: String,
    val authorId: String
) 