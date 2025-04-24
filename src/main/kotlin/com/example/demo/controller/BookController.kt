package com.example.demo.controller

import com.example.demo.model.Book
import com.example.demo.service.AuthorService
import com.example.demo.service.BookService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller

@Controller
open class BookController(
    open val bookService: BookService,
    open val authorService: AuthorService
) {
    
    @QueryMapping
    @PreAuthorize("hasRole('BOOK_MANAGER') or hasRole('ADMIN')")
    fun hello(): String = "Hello, GraphQL!"
    
    @QueryMapping
    @PreAuthorize("hasRole('BOOK_MANAGER') or hasRole('ADMIN')")
    open fun books(): List<Book> = bookService.getAllBooks()
    
    @QueryMapping
    @PreAuthorize("hasRole('BOOK_MANAGER') or hasRole('ADMIN')")
    open fun book(@Argument id: String): Book? = bookService.getBookById(id)
    
    @MutationMapping
    @PreAuthorize("hasRole('BOOK_MANAGER') or hasRole('ADMIN')")
    open fun createBook(@Argument("input") input: BookInput): Book {
        val book = Book(
            id = (bookService.getAllBooks().size + 1).toString(),
            title = input.title,
            authorId = input.authorId
        )
        return bookService.createBook(book)
    }
    
    @MutationMapping
    @PreAuthorize("hasRole('BOOK_MANAGER') or hasRole('ADMIN')")
    open fun updateBook(
        @Argument id: String,
        @Argument("input") input: BookInput
    ): Book? {
        val book = Book(id, input.title, input.authorId)
        return bookService.updateBook(id, book)
    }
    
    @MutationMapping
    @PreAuthorize("hasRole('BOOK_MANAGER') or hasRole('ADMIN')")
    open fun deleteBook(@Argument id: String): Boolean {
        bookService.deleteBook(id)
        return true
    }
    
    @SchemaMapping(typeName = "Book", field = "author")
    @PreAuthorize("hasRole('AUTHOR_MANAGER') or hasRole('ADMIN')")
    open fun getAuthor(book: Book) = authorService.getAuthorById(book.authorId)
}

data class BookInput(
    val title: String,
    val authorId: String
) 