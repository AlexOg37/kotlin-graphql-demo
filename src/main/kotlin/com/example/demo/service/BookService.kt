package com.example.demo.service

import com.example.demo.model.Book
import com.example.demo.repository.BookRepository
import org.springframework.stereotype.Service

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val authorService: AuthorService
) {
    
    fun getAllBooks(): List<Book> = bookRepository.findAll()
    
    fun getBookById(id: String): Book? = bookRepository.findById(id)
    
    fun createBook(book: Book): Book {
        // Validate that the author exists
        authorService.getAuthorById(book.authorId) ?: throw IllegalArgumentException("Author not found")
        return bookRepository.save(book)
    }
    
    fun updateBook(id: String, book: Book): Book? {
        // Validate that the author exists
        authorService.getAuthorById(book.authorId) ?: throw IllegalArgumentException("Author not found")
        return if (bookRepository.findById(id) != null) {
            bookRepository.save(book.copy(id = id))
        } else null
    }
    
    fun deleteBook(id: String) = bookRepository.deleteById(id)
    
    fun getBooksByAuthorId(authorId: String): List<Book> {
        return bookRepository.findAll().filter { it.authorId == authorId }
    }
} 