package com.example.demo.repository

import com.example.demo.model.Book
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class InMemoryBookRepository : BookRepository {
    private val books = ConcurrentHashMap<String, Book>()

    init {
        // Initialize with some sample data
        save(Book("1", "The Hobbit", "1"))  // J.R.R. Tolkien
        save(Book("2", "1984", "2"))        // George Orwell
    }

    override fun findAll(): List<Book> = books.values.toList()

    override fun findById(id: String): Book? = books[id]

    override fun save(book: Book): Book {
        books[book.id] = book
        return book
    }

    override fun deleteById(id: String) {
        books.remove(id)
    }
} 